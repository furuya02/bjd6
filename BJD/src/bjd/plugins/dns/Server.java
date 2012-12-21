package bjd.plugins.dns;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import bjd.Kernel;
import bjd.log.LogKind;
import bjd.log.Logger;
import bjd.net.Ip;
import bjd.net.OneBind;
import bjd.option.Conf;
import bjd.option.Dat;
import bjd.option.OneDat;
import bjd.option.OneOption;
import bjd.server.OneServer;
import bjd.sock.SockObj;
import bjd.sock.SockUdp;
import bjd.util.BitConverter;

public final class Server extends OneServer {

	//キャッシュ
	private DnsCache _rootCache;
	private ArrayList<DnsCache> _cacheList = new ArrayList<DnsCache>();

	public Server(Kernel kernel, Conf conf, OneBind oneBind) {
		super(kernel, "Dns", conf, oneBind);
		
		//ルートキャッシュ
        String filename = String.format("%s\\%s",kernel.getProgDir(),getConf().get("rootCache"));
        if((new File(filename)).exists()){
            try {
                //named.ca読み込み用コンストラクタ
				_rootCache = new DnsCache(conf, filename);
	            getLogger().set(LogKind.DETAIL, null, 6, filename);
			} catch (IOException e) {
				getLogger().set(LogKind.ERROR,null, 2,String.format("filename=%s",filename));
			}
        } else {
            getLogger().set(LogKind.ERROR, null, 3, filename);
        }

        //自己の管理するドメインの一覧を取得する
        OneOption op = kernel.getListOption().get("DnsDomain");
        //ArrayList<String> domainList = new ArrayList<String>();
        for (OneDat o : (Dat)op.getValue("domainList")) {
            if (o.isEnable()) {
                //ドメインごとのリソースの読込
                String domainName = o.getStrList().get(0);
                OneOption res = kernel.getListOption().get("Resource-" + domainName);
                //Assembly asm = Assembly.GetExecutingAssembly();
                //)op = (OneOption)asm.CreateInstance("DnsServer.OptionDnsResource", true, BindingFlags.Default, null, new Object[] { kernel, "Resource-" + domainName }, null, null);

                Dat resource = (Dat)res.getValue("resourceList");
                _cacheList.add(new DnsCache(getLogger(), conf, resource, domainName + "."));//.zone読み込み用コンストラクタ
            }
        }
	}

	@Override
	protected void onStopServer() {

	}

	@Override
	protected boolean onStartServer() {
		return true;
	}

	@Override
	protected void onSubThread(SockObj sockObj) {
		SockUdp sockUdp = (SockUdp) sockObj;
		//セッションごとの情報
		//Session session = new Session((SockTcp) sockObj);

		//このコネクションの間、１つづつインクメントしながら使用される
		//本来は、切断したポート番号は再利用可能なので、インクリメントの必要は無いが、
		//短時間で再利用しようとするとエラーが発生する場合があるので、これを避ける目的でインクリメントして使用している

		//while (isLife()) {
		//このループは最初にクライアントからのコマンドを１行受信し、最後に、
		//sockCtrl.LineSend(resStr)でレスポンス処理を行う
		//continueを指定した場合は、レスポンスを返さずに次のコマンド受信に入る（例外処理用）
		//breakを指定した場合は、コネクションの終了を意味する（QUIT ABORT 及びエラーの場合）
		//}
		
        DnsCache targetCache;

        //パケットの読込(受信パケットrp)            
        
        PacketDns rp = new PacketDns(getLogger());
        if (!rp.Read(sockUdp.UdpBuffer)) {
            return; //データ解釈に失敗した場合は、処理なし
        }

        // ドメイン名取得
        String domainName = "";
        //.が存在する場合、.以降をデフォルトのドメイン名として取得する
        int index = rp.getRequestName().indexOf('.');
        if (index != -1)
            domainName = rp.getRequestName().substring(index + 1);

        //Ver5.0.0-a9
        //if (rp.getDnsType() == DNS_TYPE.A || rp.getDnsType() == DNS_TYPE.CNAME) {
        if (rp.getDnsType() == DnsType.A || rp.getDnsType() == DnsType.Aaaa || rp.getDnsType() == DnsType.Cname) {
            // （ドメイン名自身にアドレスが指定されている可能性が有る）
            // A CNAME の場合、リクエスト名がホスト名を含まないドメイン名である可能性があるため
            // 対象ドメインのキャッシュからＡレコードが存在するかどうかの確認を行う
            for (DnsCache cache : _cacheList) {
                if (cache.getDomainName().equals(rp.getRequestName())) {
                    if (cache.Find(rp.getRequestName(), DnsType.A)) {
                        domainName = rp.getRequestName();
                    }
                }

            }
        } else if (rp.getDnsType() == DnsType.Mx || rp.getDnsType() == DnsType.Ns || rp.getDnsType() == DnsType.Soa) {
            //MX NS SOA リクエストの場合亜h、requestName自体がドメイン名となる
            domainName = rp.getRequestName();
        }
        //ログ出力（リクエスト解釈完了）
        getLogger().set(LogKind.NORMAL,sockUdp,8,String.format("%s %s (domain=%s)",rp.getDnsType(),rp.getRequestName(),domainName));//Query

        boolean aa = false; // ドメインオーソリティ(管理ドメインかそうでないか)
        boolean ra = true; //再帰可能

        targetCache = _rootCache;//デフォルトはルートキャッシュ

        if (rp.getDnsType() == DnsType.Ptr) {
            if (rp.getRequestName().toUpperCase().equals("1.0.0.127.IN-ADDR.ARPA.")) {
                //キャッシュはデフォルトであるルートキャッシュが使用される
                aa = true;
                getLogger().set(LogKind.DETAIL,sockUdp,9,"");//"request to a domain under auto (localhost)"
            } else {
                for(DnsCache cache : _cacheList) {
                    if (cache.Find(rp.getRequestName(), DnsType.Ptr)) {
                        targetCache = cache;
                        aa = true;
                        getLogger().set(LogKind.DETAIL,sockUdp,10,String.format("Resource={0}",targetCache.getDomainName()));//"request to a domain under management"
                        break;
                    }
                }
            }
        } else {//A
            if (rp.getRequestName().toUpperCase().equals("LOCALHOST")) {
                //キャッシュはデフォルトであるルートキャッシュが使用される
                aa = true;
                getLogger().set(LogKind.DETAIL,sockUdp,11,"");//"request to a domain under auto (localhost)"
            } else {
                for (DnsCache cache : _cacheList) {
                    if (cache.getDomainName().toUpperCase().equals(domainName.toUpperCase())) {//大文字で比較される
                        targetCache = cache;
                        aa = true;
                        getLogger().set(LogKind.DETAIL,sockUdp,12,String.format("Resource=%s",domainName));//"request to a domain under management"
                        break;
                    }
                }

            }
        }

        if (targetCache != null)
            targetCache.TtlClear();// 有効時間を過ぎたデータを削除する

        //管理するドメインでなく、かつ 再帰要求が無い場合は、処理を終わる
        if (!(aa) && !(rp.getRd())){
            return;
        }

        //aa ドメインオーソリティ
        //rs 再帰可能
        //rd 再起要求あり

        //********************************************************
        //パケットの生成(送信パケットsp)            
        //********************************************************
        PacketDns sp = new PacketDns(getLogger());
        
        // (A)「ヘッダ」作成
        boolean qr = true; //応答
        sp.createHeader(rp.getId(),qr,aa,rp.getRd(),ra);//ヘッダの作成
        
        // (B)「質問セクション」の追加
        sp.addRR(RRKind.QD, rp.getRequestName(),rp.getDnsType(),0,new byte[0]);//質問フィールドの追加
        if (!aa){
            //ドメインオーソリティ（権威サーバ）で無い場合
            //ルートキャッシュにターゲットのデータが蓄積されるまで、再帰的に検索する
            int depth = 0;
            SearchLoop(rp.getRequestName(), rp.getDnsType(), depth, sockUdp.getRemoteAddress());
        }

        // (B)「回答セクション」作成
        ArrayList<OneRR> ansList = targetCache.Search(rp.getRequestName(), rp.getDnsType());
        getLogger().set(LogKind.DETAIL,sockUdp,13,String.format("Answer Resurce ({0}) Max={1}",rp.getDnsType(),ansList.size()));//"Search LocalCache"
        if (0 < ansList.size()) {//検索でヒットした場合
            for (OneRR oneRR : ansList) {
                getLogger().set(LogKind.DETAIL,sockUdp,14,String.format("{0} {1}",rp.getDnsType(),oneRR));//"Answer"
                
                sp.addRR(RRKind.AN,rp.getRequestName(),rp.getDnsType(),oneRR.getTtl2(),oneRR.getData());
                if (rp.getDnsType() == DnsType.Mx || rp.getDnsType() == DnsType.Cname || rp.getDnsType() == DnsType.Ns) {


                    //追加情報が必要な場合 （Aレコード）をパケットに追加する
                    ArrayList<OneRR> rr = targetCache.Search(oneRR.getName(),DnsType.A);
                    for (OneRR r : rr) {
                        sp.addRR(RRKind.AR, oneRR.getName(), DnsType.A, r.getTtl2(), r.getData());
                    }

                    //追加情報が必要な場合 （AAAAレコード）をパケットに追加する
                    rr = targetCache.Search(oneRR.getName(),DnsType.Aaaa);
                    for (OneRR r : rr) {
                        sp.addRR(RRKind.AR,oneRR.getName(),DnsType.Aaaa,r.getTtl2(),r.getData());
                    }
                }
            }
        } else {//検索でヒットしない場合
            if (rp.getDnsType() == DnsType.A){
                // CNAMEに定義されていないかどうかを確認する
                int loop = 0;
                ArrayList<OneRR> rrList = targetCache.Search(rp.getRequestName(), DnsType.Cname);
                //Ver5.7.3 みずき氏から情報提供いただきました
                //getLogger().set(LogKind.DETAIL,sockUdp,15,String.format("({0}) Max={1}",DnsType.Cname,rrList.size()));//"Search LocalCache"
                //foreach (var oneRR in rrList) {
                //    getLogger().set(LogKind.DETAIL,sockUdp,16,String.format("{0}",oneRR));//"Answer CNAME"
                //    sp.addRR(RRKind.AN,rp.getRequestName(),DnsType.Cname,oneRR.getTtl2(),oneRR.getData());
                //    var rr = targetCache.Search(oneRR.getName(),DnsType.A);


                //    foreach (OneRR r in rr) {
                //        sp.addRR(RRKind.AN,r.Name, DnsType.A, r.Ttl2, r.Data);
                //    }
                //}
                Loop:
                if (loop++ <= 15){
                    getLogger().set(LogKind.DETAIL, sockUdp, 15,String.format("({0}) Max={1} Loop={2}", DnsType.Cname, rrList.size(), loop));//"Search LocalCache"
                    for (OneRR oneRR : rrList){
                        getLogger().set(LogKind.DETAIL, sockUdp, 16, String.format("{0}", oneRR)); //"Answer CNAME"
                        sp.addRR(RRKind.AN, oneRR.getName(), DnsType.Cname, oneRR.getTtl2(), oneRR.getData());
                        ArrayList<OneRR> rr = targetCache.Search(oneRR.getName(), DnsType.A);
                        if (rr.size() == 0){
                            rrList = targetCache.Search(oneRR.getName(), DnsType.Cname);
                            goto Loop;
                        }
                        for (OneRR r : rr){
                            sp.addRR(RRKind.AN, r.getName(), DnsType.A, r.getTtl2(), r.getData());
                        }
                    }
                }
            }
        }

        if (rp.getDnsType() == DnsType.A || rp.getDnsType() == DnsType.Aaaa || rp.getDnsType() == DnsType.Soa) {
            // (C)「権威セクション」「追加情報セクション」作成
            ArrayList<OneRR> authList = targetCache.Search(domainName,DnsType.Ns);
            getLogger().set(LogKind.DETAIL,sockUdp,13,String.format("Authority Resurce({0}) Max={1}",DnsType.Ns,authList.Count));
            for (OneRR oneRR : authList) {
                sp.addRR(RRKind.NS,oneRR.getName(),DnsType.Ns,oneRR.getTtl2(),oneRR.getData());
                //「追加情報」
                ArrayList<OneRR> addList = targetCache.Search(oneRR.getName(),DnsType.A);
                for (OneRR rr : addList) {
                    sp.addRR(RRKind.AR,oneRR.getName(),DnsType.A,rr.getTtl2(),rr.getData());
                }
                addList = targetCache.Search(oneRR.getName(),DnsType.Aaaa);
                for (OneRR rr : addList) {
                    sp.addRR(RRKind.AR,oneRR.getName(),DnsType.Aaaa,rr.getTtl2(),rr.getData());
                }

            }
        }

        sockUdp.SendTo(sp.get());//送信
        //sockUdp.Close();UDPソケット(sockUdp)はクローンなのでクローズしても、処理されない※Close()を呼び出しても問題はない
		sockUdp.close();
	}

	//ルートキャッシュにターゲットのデータが蓄積されるまで、再帰的に検索する
	boolean SearchLoop(String requestName,DnsType dnsType,int depth,Ip remoteAddr) {

         if (depth > 15){
             return false;
         }

         String domainName = requestName;
         int index = requestName.indexOf('.');
         if (index != -1){
             domainName = requestName.substring(index + 1);
         }

         // ネームサーバ情報取得
         //ターゲットドメインのネームサーバを検索する
         ArrayList<String> nsList = new ArrayList<String>();
         ArrayList<OneRR> rrList = _rootCache.Search(domainName, DnsType.Ns);
         if (0 < rrList.size()) {
             nsList.AddRange(rrList.Select(t => t.getName()));
         } else { //キャッシュに存在しない場合は、ルートサーバをランダムにセットする
             rrList = _rootCache.Search(".", DnsType.Ns);

             var random = new Random(Environment.TickCount);
             int center = random.Next(rrList.size());//センタ位置をランダムに決定する
             for (int i = center; i < rrList.size(); i++){//センタ以降の一覧を取得
                 nsList.add(rrList.get(i).getName());
             }
             for (int i = 0; i < center; i++){//センタ以前の一覧をコピー
                 nsList.add(rrList.get(i).getName());
             }
         }

         while (true) {
             //rootCacheにターゲットのデータがキャッシュ（蓄積）されているかどうかを確認
             if (_rootCache.Find(requestName, dnsType))
                 return true;//検索完了
             if (dnsType == DnsType.A) {
                 //DNS_TYPE.Aの場合、CNAME及びそのAレコードがキャッシュされている場合、蓄積完了となる
                 rrList = _rootCache.Search(requestName, DnsType.Cname);
                 boolean find = rrList.Any(t => _rootCache.Find(t.getName(), DnsType.A));
                 if (find){
                     break;
                 }
                 //Ver5.5.4 CNAMEが発見された場合は、そのIPアドレス取得に移行する
                 if (rrList.size() > 0) {
                     requestName = rrList.get(0).getName();
                 }
             }
             
             //ネームサーバ一覧から、そのアドレスの一覧を作成する
             ArrayList<Ip> nsAddrList = new ArrayList<Ip>();
             for (String ns : nsList) {
                 rrList = _rootCache.Search(ns,DnsType.A);
                 if (dnsType == DnsType.Aaaa){
                 //AAAAでの検索の場合、AAAAのアドレス情報も有効にする
                	 ArrayList<OneRR> tmpList = _rootCache.Search(domainName,DnsType.Aaaa);
                     rrList.AddRange(tmpList);
                 }


                 if (rrList.size() == 0) {
                     if (!SearchLoop(ns, dnsType, depth + 1, remoteAddr))//再帰処理
                         return false;
                     rrList = _rootCache.Search(ns,dnsType);
                     if (rrList.size() == 0)
                         return false;
                 }

                 for (OneRR oneRR : rrList) {
                     uint addr = Util.htonl(BitConverter.ToUInt32(oneRR.getData(), 0));
                     Ip tmpIp = new Ip(addr);

                     if (oneRR.getDnsType() == DnsType.Aaaa) {
                         long v6H = BitConverter.ToUInt64(oneRR.getData(),0);
                         long v6L = BitConverter.ToUInt64(oneRR.getData(),8);
                         tmpIp = new Ip(Util.htonl(v6H),Util.htonl(v6L));
                     }
                     boolean find = nsAddrList.Any(ip => ip == tmpIp);
                     if (!find) {
                         nsAddrList.add(tmpIp);
                     }
                 }

             }
             nsList.clear();

             //ネームサーバのアドレスが取得できない場合、処理の継続はできない（検索不能）
             if (nsAddrList.size() == 0){
                 return false;
             }
                 
             //0:検索中 1:発見 2:権威サーバ取得 (ドメインオーソリティからの名前エラーの場合は処理停止goto end)
             int state = 0;
             for (Ip ip : nsAddrList) {

                 //PacketDns rp = Lookup(ip.AddrV4, requestName, dnsType);
                 PacketDns rp = Lookup(ip,requestName,dnsType,remoteAddr);
                 if (rp != null) {
                     if (rp.getAA()) {//権威サーバの応答の場合
                         //ホストが存在しない　若しくは　回答フィールドが0の場合、処理停止
                         if (rp.getRcode() == 3 || rp.getCount(RRKind.AN) == 0) {
                             goto end;
                         }
                     }
                     if (0 < rp.getCount(RRKind.AN)) {//回答フィールドが存在する場合
                         return true;
                         //break;
                     }
                     // 求めている回答は得ていないが、権威サーバを教えられた場合
                     // ネームサーバのリストを差し替える
                     for (int n = 0; n < rp.getCount(RRKind.NS); n++) {
                         OneRR oneRR = rp.getRR(RRKind.NS, n);
                         if (oneRR.getDnsType() == DnsType.Ns) {
                             nsList.add(oneRR.getName());
                             state = 2;//ネームサーバリストを取得した
                         }
                     }
                 }//Lookupが成功した場合の処理
                 if (state != 0)
                     break;

             }//nsAddrListのループ処理（state==0の間のみ）
         }
end:
         return false;
     }

	//addrは通常オーダで指定されている
	//private PacketDns Lookup(Ip ip, String requestName, DNS_TYPE dnsType,RemoteInfo remoteInfo) {
	private PacketDns Lookup(Ip ip, String requestName, DnsType dnsType, Ip remoteAddr) {

		//Ip ip = new Ip(addr);
		getLogger().set(LogKind.DETAIL, null, 17, String.format("{0} Server={1} Type={2}", requestName, ip, dnsType));//"Lookup"

		//受信タイムアウト
		int timeout = 3;

		//リクエストパケットの生成
		PacketDns sp = new PacketDns(getLogger());
		var random = new Random(Environment.TickCount);
		var id = (ushort) random.Next(0xFFFF);//識別子をランダムに決定する
		boolean qr = false; //要求
		boolean aa = false; //権威なし
		boolean rd = (boolean) getConf().get("useRD");//再帰要求を使用するかどうか
		boolean ra = false; //再帰無効
		sp.CreateHeader(id, qr, aa, rd, ra);//ヘッダ生成
		sp.addRR(RRKind.QD, requestName, dnsType, 0, new byte[0]);//QR(質問)フィールド追加

		int port = 53;
		//SockUdp sockUdp = new UdpObj(Kernel, getLogger(), ip, port);
		SockUdp sockUdp = new SockUdp(kernel, getLogger(), ip, port);

		sockUdp.send(sp.get());//送信
		if (sockUdp.ReceiveFrom(timeout)) {//受信

			PacketDns rp = new PacketDns(getLogger());
			rp.Read(sockUdp.UdpBuffer);

			String str = String.format("requestName=%s count[%d,%d,%d,%d] rcode=%s AA=%s", requestName
					, rp.getCount(RRKind.QD)
					, rp.getCount(RRKind.AN)
					, rp.getCount(RRKind.NS)
					, rp.getCount(RRKind.AR)
					, rp.getRcode()
					, rp.getAA());
			getLogger().set(LogKind.DETAIL, sockUdp, 18, str);//"Lookup"

			//質問フィールの以外のリソースデータをキャッシュする
			//for (int rr = 1; rr < 4; rr++) {
			for (RRKind rr : RRKind.values()) {
				int m = rp.getCount(rr);
				for (int n = 0; n < m; n++) {
					OneRR oneRR = rp.getRR(rr, n);
					_rootCache.Add(oneRR);
				}
			}
			return rp;
		}

		getLogger().set(LogKind.ERROR, sockUdp, 5, String.format("addr=%s requestName=%s dnsType=%s", remoteAddr, requestName, dnsType));//Lookup() パケット受信でタイムアウトが発生しました。
		return null;
	}

	public String getMsg(int messageNo) {
		switch (messageNo) {
			case 0:
				return isJp() ? "標準問合(OPCODE=0)以外のリクエストには対応できません" : "Because I am different from 0 in OPCODE,can't process it.";
			case 1:
				return isJp() ? "質問エントリーが１でないパケットは処理できません" : "Because I am different from 1 a question entry,can't process it.";
			case 2:
				return isJp() ? "ルートキャッシュの読み込みに失敗しました" : " Failed in reading of route cash.";
			case 3:
				return isJp() ? "ルートキャッシュが見つかりません" : "Root chace is not found";
//			case 4:
//				return isJp() ? "パケットのサイズに問題があるため、処理を継続できません" : "So that size includes a problem,can't process it.";
			case 5:
				return isJp() ? "Lookup() パケット受信でタイムアウトが発生しました。" : "Timeout occurred in Lookup()";
			case 6:
				return isJp() ? "ルートキャッシュを読み込みました" : "root cache database initialised.";
			case 7:
				return "zone database initialised.";
			case 8:
				return "Query";
			case 9:
				return "request to a domain under auto (localhost)";
			case 10:
				return "request to a domain under management";
			case 11:
				return "request to a domain under auto (localhost)";
			case 12:
				return "request to a domain under management";
			case 13:
				return "Search LocalCache";
			case 14:
				return "Answer";
			case 15:
				return "Search LocalCache";
			case 16:
				return "Answer CNAME";
			case 17:
				return "Lookup";
			case 18:
				return "Lookup";
			case 19:
				return isJp() ? "A(PTR)レコードにIPv6アドレスを指定できません" : "IPv6 cannot address it in an A(PTR) record";
			case 20:
				return isJp() ? "AAAAレコードにIPv4アドレスを指定できません" : "IPv4 cannot address it in an AAAA record";
			//case 22:
			//	return isJp() ? "バイト計算に矛盾が生じています" : "Contradiction produces it in a byte calculation";
			default:
				return "unknown";

		}
	}
}
