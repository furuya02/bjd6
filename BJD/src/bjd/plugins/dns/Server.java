package bjd.plugins.dns;

import java.util.ArrayList;

import bjd.Kernel;
import bjd.log.LogKind;
import bjd.log.Logger;
import bjd.net.OneBind;
import bjd.option.Conf;
import bjd.option.Dat;
import bjd.option.OneDat;
import bjd.option.OneOption;
import bjd.server.OneServer;
import bjd.sock.SockObj;
import bjd.sock.SockUdp;

public final class Server extends OneServer {

    //キャッシュ
    private DnsCache _rootCache;
    private ArrayList<DnsCache> _cacheList = new ArrayList<DnsCache>();

    public Server(Kernel kernel, Conf conf, OneBind oneBind) {
		super(kernel, "Dns", conf, oneBind);

		//ルートキャッシュ
        String filename = String.format("%s\\%s",kernel.getProgDir(),OneOption.getValue("rootCache"));
        if (File.Exists(filename)) {
            _rootCache = new DnsCache( OneOption, filename);//named.ca読み込み用コンストラクタ
            getLogger().set(LogKind.DETAIL, null, 6, filename);
        } else {
            getLogger().set(LogKind.ERROR, null, 21, filename);
        }

        //自己の管理するドメインの一覧を取得する
        OneOption op = kernel.getListOption().get("DnsDomain");
        //List<String> domainList = new List<String>();
        for (OneDat o : (Dat)op.getValue("domainList")) {
            if (o.isEnable()) {
                //ドメインごとのリソースの読込
                String domainName = o.StrList[0];
                OneOption res = kernel.getListOption().get("Resource-" + domainName);
                //Assembly asm = Assembly.GetExecutingAssembly();
                //op = (OneOption)asm.CreateInstance("DnsServer.OptionDnsResource", true, BindingFlags.Default, null, new Object[] { kernel, "Resource-" + domainName }, null, null);

                Dat resource = (Dat)res.getValue("resourceList");
                _cacheList.add(new DnsCache(getLogger(), OneOption, resource, domainName + "."));//.zone読み込み用コンストラクタ
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
        var rp = new PacketDns(Logger);
        if (!rp.Read(udpObj.UdpBuffer)) {
            return; //データ解釈に失敗した場合は、処理なし
        }

        // ドメイン名取得
        var domainName = "";
        //.が存在する場合、.以降をデフォルトのドメイン名として取得する
        int index = rp.RequestName.IndexOf('.');
        if (index != -1)
            domainName = rp.RequestName.SubString(index + 1);

        //Ver5.0.0-a9
        //if (rp.DnsType == DNS_TYPE.A || rp.DnsType == DNS_TYPE.CNAME) {
        if (rp.DnsType == DnsType.A || rp.DnsType == DnsType.Aaaa || rp.DnsType == DnsType.Cname) {
            // （ドメイン名自身にアドレスが指定されている可能性が有る）
            // A CNAME の場合、リクエスト名がホスト名を含まないドメイン名である可能性があるため
            // 対象ドメインのキャッシュからＡレコードが存在するかどうかの確認を行う
            foreach (DnsCache cache in _cacheList) {
                if (cache.DomainName == rp.RequestName) {
                    if (cache.Find(rp.RequestName, DnsType.A)) {
                        domainName = rp.RequestName;
                    }
                }

            }
        } else if (rp.DnsType == DnsType.Mx || rp.DnsType == DnsType.Ns || rp.DnsType == DnsType.Soa) {
            //MX NS SOA リクエストの場合亜h、requestName自体がドメイン名となる
            domainName = rp.RequestName;
        }
        //ログ出力（リクエスト解釈完了）
        getLogger().set(LogKind.Normal,udpObj,8,String.Format("{0} {1} (domain={2})",rp.DnsType,rp.RequestName,domainName));//Query

        var aa = false; // ドメインオーソリティ(管理ドメインかそうでないか)
        const bool ra = true; //再帰可能

        targetCache = _rootCache;//デフォルトはルートキャッシュ

        if (rp.DnsType == DnsType.Ptr) {
            if (rp.RequestName.ToUpper() == "1.0.0.127.IN-ADDR.ARPA.") {
                //キャッシュはデフォルトであるルートキャッシュが使用される
                aa = true;
                getLogger().set(LogKind.Detail,udpObj,9,"");//"request to a domain under auto (localhost)"
            } else {
                foreach (DnsCache cache in _cacheList) {
                    if (cache.Find(rp.RequestName, DnsType.Ptr)) {
                        targetCache = cache;
                        aa = true;
                        getLogger().set(LogKind.Detail,udpObj,10,String.Format("Resource={0}",targetCache.DomainName));//"request to a domain under management"
                        break;
                    }
                }
            }
        } else {//A
            if (rp.RequestName.ToUpper() == "LOCALHOST") {
                //キャッシュはデフォルトであるルートキャッシュが使用される
                aa = true;
                getLogger().set(LogKind.Detail,udpObj,11,"");//"request to a domain under auto (localhost)"
            } else {
                foreach (DnsCache cache in _cacheList) {
                    if (cache.DomainName.ToUpper() == domainName.ToUpper()) {//大文字で比較される
                        targetCache = cache;
                        aa = true;
                        getLogger().set(LogKind.Detail,udpObj,12,String.Format("Resource={0}",domainName));//"request to a domain under management"
                        break;
                    }
                }

            }
        }

        if (targetCache != null)
            targetCache.TtlClear();// 有効時間を過ぎたデータを削除する

        //管理するドメインでなく、かつ 再帰要求が無い場合は、処理を終わる
        if (!(aa) && !(rp.Rd))
            return;

        //aa ドメインオーソリティ
        //rs 再帰可能
        //rd 再起要求あり

        //********************************************************
        //パケットの生成(送信パケットsp)            
        //********************************************************
        var sp = new PacketDns(Logger);
        
        // (A)「ヘッダ」作成
        const bool qr = true; //応答
        sp.CreateHeader(rp.Id,qr,aa,rp.Rd,ra);//ヘッダの作成
        
        // (B)「質問セクション」の追加
        sp.AddRR(RRKind.QD, rp.RequestName,rp.DnsType,0,new byte[0]);//質問フィールドの追加
        if (!aa){
            //ドメインオーソリティ（権威サーバ）で無い場合
            //ルートキャッシュにターゲットのデータが蓄積されるまで、再帰的に検索する
            const int depth = 0;
            SearchLoop(rp.RequestName, rp.DnsType, depth, udpObj.RemoteAddr);
        }

        // (B)「回答セクション」作成
        var ansList = targetCache.Search(rp.RequestName, rp.DnsType);
        getLogger().set(LogKind.Detail,udpObj,13,String.Format("Answer Resurce ({0}) Max={1}",rp.DnsType,ansList.Count));//"Search LocalCache"
        if (0 < ansList.Count) {//検索でヒットした場合
            foreach (var oneRR in ansList) {
                getLogger().set(LogKind.Detail,udpObj,14,String.Format("{0} {1}",rp.DnsType,oneRR));//"Answer"
                
                sp.AddRR(RRKind.AN,rp.RequestName,rp.DnsType,oneRR.Ttl2,oneRR.Data);
                if (rp.DnsType == DnsType.Mx || rp.DnsType == DnsType.Cname || rp.DnsType == DnsType.Ns) {


                    //追加情報が必要な場合 （Aレコード）をパケットに追加する
                    List<OneRR> rr = targetCache.Search(oneRR.N1,DnsType.A);
                    foreach (OneRR r in rr) {
                        sp.AddRR(RRKind.AR, oneRR.N1, DnsType.A, r.Ttl2, r.Data);
                    }

                    //追加情報が必要な場合 （AAAAレコード）をパケットに追加する
                    rr = targetCache.Search(oneRR.N1,DnsType.Aaaa);
                    foreach (OneRR r in rr) {
                        sp.AddRR(RRKind.AR,oneRR.N1,DnsType.Aaaa,r.Ttl2,r.Data);
                    }
                }
            }
        } else {//検索でヒットしない場合
            if (rp.DnsType == DnsType.A){
                // CNAMEに定義されていないかどうかを確認する
                int loop = 0;
                List<OneRR> rrList = targetCache.Search(rp.RequestName, DnsType.Cname);
                //Ver5.7.3 みずき氏から情報提供いただきました
                //getLogger().set(LogKind.Detail,udpObj,15,String.Format("({0}) Max={1}",DnsType.Cname,rrList.Count));//"Search LocalCache"
                //foreach (var oneRR in rrList) {
                //    getLogger().set(LogKind.Detail,udpObj,16,String.Format("{0}",oneRR));//"Answer CNAME"
                //    sp.AddRR(RRKind.AN,rp.RequestName,DnsType.Cname,oneRR.Ttl2,oneRR.Data);
                //    var rr = targetCache.Search(oneRR.N1,DnsType.A);


                //    foreach (OneRR r in rr) {
                //        sp.AddRR(RRKind.AN,r.Name, DnsType.A, r.Ttl2, r.Data);
                //    }
                //}
                Loop:
                if (loop++ <= 15){
                    getLogger().set(LogKind.Detail, udpObj, 15,String.Format("({0}) Max={1} Loop={2}", DnsType.Cname, rrList.Count, loop));//"Search LocalCache"
                    foreach (var oneRR in rrList){
                        getLogger().set(LogKind.Detail, udpObj, 16, String.Format("{0}", oneRR)); //"Answer CNAME"
                        sp.AddRR(RRKind.AN, oneRR.Name, DnsType.Cname, oneRR.Ttl2, oneRR.Data);
                        var rr = targetCache.Search(oneRR.N1, DnsType.A);
                        if (rr.Count == 0){
                            rrList = targetCache.Search(oneRR.N1, DnsType.Cname);
                            goto Loop;
                        }
                        foreach (OneRR r in rr){
                            sp.AddRR(RRKind.AN, r.Name, DnsType.A, r.Ttl2, r.Data);
                        }
                    }
                }
            }
        }

        if (rp.DnsType == DnsType.A || rp.DnsType == DnsType.Aaaa || rp.DnsType == DnsType.Soa) {
            // (C)「権威セクション」「追加情報セクション」作成
            List<OneRR> authList = targetCache.Search(domainName,DnsType.Ns);
            getLogger().set(LogKind.Detail,udpObj,13,String.Format("Authority Resurce({0}) Max={1}",DnsType.Ns,authList.Count));
            foreach (OneRR oneRR in authList) {
                sp.AddRR(RRKind.NS,oneRR.Name,DnsType.Ns,oneRR.Ttl2,oneRR.Data);
                //「追加情報」
                var addList = targetCache.Search(oneRR.N1,DnsType.A);
                foreach (OneRR rr in addList) {
                    sp.AddRR(RRKind.AR,oneRR.N1,DnsType.A,rr.Ttl2,rr.Data);
                }
                addList = targetCache.Search(oneRR.N1,DnsType.Aaaa);
                foreach (OneRR rr in addList) {
                    sp.AddRR(RRKind.AR,oneRR.N1,DnsType.Aaaa,rr.Ttl2,rr.Data);
                }

            }
        }

        udpObj.SendTo(sp.Get());//送信
        //udpObj.Close();UDPソケット(udpObj)はクローンなのでクローズしても、処理されない※Close()を呼び出しても問題はない
		sockUdp.close();
	}
	


     //ルートキャッシュにターゲットのデータが蓄積されるまで、再帰的に検索する
     bool SearchLoop(String requestName,DnsType dnsType,int depth,Ip remoteAddr) {

         if (depth > 15)
             return false;

         String domainName = requestName;
         int index = requestName.IndexOf('.');
         if (index != -1)
             domainName = requestName.SubString(index + 1);

         // ネームサーバ情報取得
         //ターゲットドメインのネームサーバを検索する
         var nsList = new List<String>();
         var rrList = _rootCache.Search(domainName, DnsType.Ns);
         if (0 < rrList.Count) {
             nsList.AddRange(rrList.Select(t => t.N1));
         } else {//キャッシュに存在しない場合は、ルートサーバをランダムにセットする
             rrList = _rootCache.Search(".", DnsType.Ns);

             var random = new Random(Environment.TickCount);
             int center = random.Next(rrList.Count);//センタ位置をランダムに決定する
             for (int i = center; i < rrList.Count; i++)//センタ以降の一覧を取得
                 nsList.Add(rrList[i].N1);
             for (int i = 0; i < center; i++)//センタ以前の一覧をコピー
                 nsList.Add(rrList[i].N1);
         }

         while (true) {
             //rootCacheにターゲットのデータがキャッシュ（蓄積）されているかどうかを確認
             if (_rootCache.Find(requestName, dnsType))
                 return true;//検索完了
             if (dnsType == DnsType.A) {
                 //DNS_TYPE.Aの場合、CNAME及びそのAレコードがキャッシュされている場合、蓄積完了となる
                 rrList = _rootCache.Search(requestName, DnsType.Cname);
                 var find = rrList.Any(t => _rootCache.Find(t.N1, DnsType.A));
                 if (find)
                     break;
                 //Ver5.5.4 CNAMEが発見された場合は、そのIPアドレス取得に移行する
                 if (rrList.Count > 0) {
                     requestName = rrList[0].N1;
                 }
             }
             
             //ネームサーバ一覧から、そのアドレスの一覧を作成する
             var nsAddrList = new List<Ip>();
             foreach (String ns in nsList) {
                 rrList = _rootCache.Search(ns,DnsType.A);
                 if (dnsType == DnsType.Aaaa){
                 //AAAAでの検索の場合、AAAAのアドレス情報も有効にする
                     var tmpList = _rootCache.Search(domainName,DnsType.Aaaa);
                     rrList.AddRange(tmpList);
                 }


                 if (rrList.Count == 0) {
                     if (!SearchLoop(ns, dnsType, depth + 1, remoteAddr))//再帰処理
                         return false;
                     rrList = _rootCache.Search(ns,dnsType);
                     if (rrList.Count == 0)
                         return false;
                 }

                 foreach (OneRR oneRR in rrList) {
                     uint addr = Util.htonl(BitConverter.ToUInt32(oneRR.Data, 0));
                     var tmpIp = new Ip(addr);

                     if (oneRR.DnsType == DnsType.Aaaa) {
                         UInt64 v6H = BitConverter.ToUInt64(oneRR.Data,0);
                         UInt64 v6L = BitConverter.ToUInt64(oneRR.Data,8);
                         tmpIp = new Ip(Util.htonl(v6H),Util.htonl(v6L));
                     }
                     var find = nsAddrList.Any(ip => ip == tmpIp);
                     if (!find) {
                         nsAddrList.Add(tmpIp);
                     }
                 }

             }
             nsList.Clear();

             //ネームサーバのアドレスが取得できない場合、処理の継続はできない（検索不能）
             if (nsAddrList.Count == 0)
                 return false;
                 
             //0:検索中 1:発見 2:権威サーバ取得 (ドメインオーソリティからの名前エラーの場合は処理停止goto end)
             int state = 0;
             foreach (Ip ip in nsAddrList) {

                 //PacketDns rp = Lookup(ip.AddrV4, requestName, dnsType);
                 PacketDns rp = Lookup(ip,requestName,dnsType,remoteAddr);
                 if (rp != null) {
                     if (rp.GetAA()) {//権威サーバの応答の場合
                         //ホストが存在しない　若しくは　回答フィールドが0の場合、処理停止
                         if (rp.GetRcode() == 3 || rp.GetCount(RRKind.AN) == 0) {
                             goto end;
                         }
                     }
                     if (0 < rp.GetCount(RRKind.AN)) {//回答フィールドが存在する場合
                         return true;
                         //break;
                     }
                     // 求めている回答は得ていないが、権威サーバを教えられた場合
                     // ネームサーバのリストを差し替える
                     for (int n = 0; n < rp.GetCount(RRKind.NS); n++) {
                         OneRR oneRR = rp.GetRR(RRKind.NS, n);
                         if (oneRR.DnsType == DnsType.Ns) {
                             nsList.Add(oneRR.N1);
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
     private PacketDns Lookup(Ip ip, String requestName, DnsType dnsType,Ip remoteAddr) {

         //Ip ip = new Ip(addr);
         getLogger().set(LogKind.Detail,null, 17, String.Format("{0} Server={1} Type={2}", requestName, ip, dnsType));//"Lookup"

         //受信タイムアウト
         const int timeout = 3;

         //リクエストパケットの生成
         var sp = new PacketDns(Logger);
         var random = new Random(Environment.TickCount);
         var id = (ushort)random.Next(0xFFFF);//識別子をランダムに決定する
         const bool qr = false; //要求
         const bool aa = false; //権威なし
         var rd = (bool)OneOption.GetValue("useRD");//再帰要求を使用するかどうか
         const bool ra = false; //再帰無効
         sp.CreateHeader(id, qr, aa, rd, ra);//ヘッダ生成
         sp.AddRR(RRKind.QD,requestName,dnsType,0,new byte[0]);//QR(質問)フィールド追加

         const int port = 53;
         var udpObj = new UdpObj(Kernel, Logger, ip, port);

         udpObj.SendTo(sp.Get());//送信
         if (udpObj.ReceiveFrom(timeout)) {//受信

             var rp = new PacketDns(Logger);
             rp.Read(udpObj.UdpBuffer);

             String str = String.Format("requestName={0} count[{1},{2},{3},{4}] rcode={5} AA={6}", requestName
                                         , rp.GetCount(RRKind.QD)
                                         , rp.GetCount(RRKind.AN)
                                         , rp.GetCount(RRKind.NS)
                                         , rp.GetCount(RRKind.AR)
                                         , rp.GetRcode()
                                         , rp.GetAA());
             getLogger().set(LogKind.Detail,udpObj,18,str);//"Lookup"

             //質問フィールの以外のリソースデータをキャッシュする
             for (int rr = 1; rr < 4; rr++) {
                 int m = rp.GetCount((RRKind)rr);
                 for (int n = 0; n < m; n++) {
                     var oneRR = rp.GetRR((RRKind)rr, n);
                     _rootCache.Add(oneRR);
                 }
             }
             return rp;
         }

         getLogger().set(LogKind.Error,udpObj,5,String.Format("addr={0} requestName={1} dnsType={2}",remoteAddr,requestName,dnsType));//Lookup() パケット受信でタイムアウトが発生しました。
         return null;
     }

	public String getMsg(int messageNo) {
		switch (messageNo) {
			case 0:
				return isJp() ? "標準問合(OPCODE=0)以外のリクエストには対応できません" : "Because I am different from 0 in OPCODE,can't process it.";
			case 1:
				return isJp() ? "質問エントリーが１でないパケットは処理できません" : "Because I am different from 1 a question entry,can't process it.";
			case 2:
				return isJp() ? "パケットのサイズに問題があるため、処理を継続できません" : "So that size includes a problem,can't process it.";
			case 3:
				return isJp() ? "パケットのサイズに問題があるため、処理を継続できません" : "So that size includes a problem,can't process it.";
			case 4:
				return isJp() ? "パケットのサイズに問題があるため、処理を継続できません" : "So that size includes a problem,can't process it.";
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
			case 21:
				return isJp() ? "ルートキャッシュが見つかりません" : "Root chace is not found";
			case 22:
				return isJp() ? "バイト計算に矛盾が生じています" : "Contradiction produces it in a byte calculation";
			default:
				return "unknown";

		}
	}
}
