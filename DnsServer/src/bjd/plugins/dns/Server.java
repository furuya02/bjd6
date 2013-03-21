package bjd.plugins.dns;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import bjd.Kernel;
import bjd.log.LogKind;
import bjd.net.Ip;
import bjd.net.OneBind;
import bjd.option.Conf;
import bjd.option.Dat;
import bjd.option.OneDat;
import bjd.option.OneOption;
import bjd.server.OneServer;
import bjd.sock.SockObj;
import bjd.sock.SockUdp;
import bjd.util.Util;

public final class Server extends OneServer {

	//キャッシュ
	private RrDb rootCache;
	private ArrayList<RrDb> cacheList = null;

	private Kernel kernel;

	public Server(Kernel kernel, Conf conf, OneBind oneBind) {
		super(kernel, "Dns", conf, oneBind);

		this.kernel = kernel;

	}

	@Override
	protected void onStopServer() {

	}

	@Override
	protected boolean onStartServer() {
		//ルートキャッシュの初期化
		rootCache = null;
		String namedCaPath = String.format("%s\\%s", kernel.getProgDir(), getConf().get("rootCache"));
		if ((new File(namedCaPath)).exists()) {
			try {
				//named.ca読み込み用コンストラクタ
				int expire = (int) getConf().get("soaExpire");
				rootCache = new RrDb(namedCaPath, expire);
				getLogger().set(LogKind.DETAIL, null, 6, namedCaPath);

			} catch (IOException e) {
				getLogger().set(LogKind.ERROR, null, 2, String.format("filename=%s", namedCaPath));
			}
		} else {
			getLogger().set(LogKind.ERROR, null, 3, namedCaPath);
		}

		//設定したドメイン情報を初期化する
		if (cacheList != null) {
			cacheList.clear();
		}
		cacheList = new ArrayList<RrDb>();
		OneOption op = kernel.getListOption().get("DnsDomain");
		if (op != null) {
			Dat domainList = (Dat) op.getValue("domainList");
			if (domainList != null) {
				for (OneDat o : domainList) {
					if (o.isEnable()) {
						//ドメインごとのリソースの読込
						String domainName = o.getStrList().get(0);
						OneOption res = kernel.getListOption().get("Resource-" + domainName);
						if (res != null) {
							Dat resource = (Dat) res.getValue("resourceList");
							RrDb rrDb = new RrDb(getLogger(), getConf(), resource, domainName);
							cacheList.add(rrDb);
							getLogger().set(LogKind.DETAIL, null, 21, "Resource-" + domainName);
						}
					}
				}
			}
		}

		return true;
	}

	//リクエストのドメイン名を取得する
	String initRequestDomain(String requestName, DnsType dnsType) {

		String name = "";

		//.が存在する場合、.以降をデフォルト値として仮置きする
		int index = requestName.indexOf('.');
		if (index != -1 && index < requestName.length() - 1) {
			name = requestName.substring(index + 1);
		}

		if (dnsType == DnsType.A || dnsType == DnsType.Aaaa || dnsType == DnsType.Cname) {
			// （ドメイン名自身にアドレスが指定されている可能性が有る）
			// A CNAME の場合、リクエスト名がホスト名を含まないドメイン名である可能性があるため
			// 対象ドメインのキャッシュからＡレコードが存在するかどうかの確認を行う
			for (RrDb cache : cacheList) {
				if (cache.getDomainName().equals(requestName)) {
					if (cache.find(requestName, DnsType.A)) {
						name = requestName;
					}
				}

			}
		} else if (dnsType == DnsType.Mx || dnsType == DnsType.Ns || dnsType == DnsType.Soa) {
			//MX NS SOA リクエストの場合亜h、requestName自体がドメイン名となる
			name = requestName;
		}

		if (requestName.toUpperCase().equals("LOCALHOST.")) {
			name = "localhost.";
		}
		return name;

	}

	@Override
	protected void onSubThread(SockObj sockObj) {
		SockUdp sockUdp = (SockUdp) sockObj;
		//セッションごとの情報
		//Session session = new Session((SockTcp) sockObj);

		PacketDns rp; //受信パケット
		try {
			//パケットの読込(受信パケットrp)            
			rp = new PacketDns(sockUdp.getRecvBuf());
		} catch (IOException e) {
			//データ解釈に失敗した場合は、処理なし
			getLogger().set(LogKind.SECURE, sockUdp, 4, ""); //不正パケットの可能性あり 
			return;
		}
		//リクエストのドメイン名を取得する
		String domainName = initRequestDomain(rp.getRequestName(), rp.getDnsType());

		//リクエスト解釈完了
		getLogger().set(LogKind.NORMAL, sockUdp, 8,
				String.format("%s %s domain=%s", rp.getDnsType(), rp.getRequestName(), domainName)); //Query

		boolean aa = false; // ドメインオーソリティ(管理ドメインかそうでないか)
		boolean ra = true; //再帰可能

		RrDb targetCache = rootCache; //デフォルトはルートキャッシュ

		if (rp.getDnsType() == DnsType.Ptr) {
			if (rp.getRequestName().toUpperCase().equals("1.0.0.127.IN-ADDR.ARPA.")
					|| rp.getRequestName().toUpperCase()
							.equals("1.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.IP6.ARPA.")
					|| rp.getRequestName().toUpperCase().equals("LOCALHOST.")) {
				//キャッシュはデフォルトであるルートキャッシュが使用される
				aa = true;
				getLogger().set(LogKind.DETAIL, sockUdp, 9, ""); //"request to a domain under auto (localhost)"
			} else {
				for (RrDb cache : cacheList) {
					if (cache.find(rp.getRequestName(), DnsType.Ptr)) {
						targetCache = cache;
						aa = true;
						getLogger().set(LogKind.DETAIL, sockUdp, 10,
								String.format("Resource=%s", targetCache.getDomainName())); //"request to a domain under management"
						break;
					}
				}
			}
		} else { //A
			if (rp.getRequestName().toUpperCase().equals("LOCALHOST.")) {
				//キャッシュはデフォルトであるルートキャッシュが使用される
				aa = true;
				getLogger().set(LogKind.DETAIL, sockUdp, 11, ""); //"request to a domain under auto (localhost)"
			} else {
				for (RrDb cache : cacheList) {
					if (cache.getDomainName().toUpperCase().equals(domainName.toUpperCase())) { //大文字で比較される
						targetCache = cache;
						aa = true;
						getLogger().set(LogKind.DETAIL, sockUdp, 12, String.format("Resource=%s", domainName)); //"request to a domain under management"
						break;
					}
				}

			}
		}

		//管理するドメインでなく、かつ 再帰要求が無い場合は、処理を終わる
		if (!(aa) && !(rp.getRd())) {
			return;
		}

		//aa ドメインオーソリティ
		//rs 再帰可能
		//rd 再起要求あり

		// (A)「ヘッダ」作成
		boolean qr = true; //応答

		//********************************************************
		//パケットの生成(送信パケットsp)            
		//********************************************************
		PacketDns sp = new PacketDns(rp.getId(), qr, aa, rp.getRd(), ra);

		// (B)「質問セクション」の追加
		appendRr(sp, RrKind.QD, new RrQuery(rp.getRequestName(), rp.getDnsType()));//質問フィールドの追加
		if (!aa) {
			//ドメインオーソリティ（権威サーバ）で無い場合
			//ルートキャッシュにターゲットのデータが蓄積されるまで、再帰的に検索する
			int depth = 0;
			Ip ip = new Ip(sockUdp.getRemoteAddress());
			try {
				searchLoop(rp.getRequestName(), rp.getDnsType(), depth, ip);
			} catch (IOException e) {
				// ここはどうやって扱えばいいか？？？
				e.printStackTrace();
			}
		}

		// (B)「回答セクション」作成
		ArrayList<OneRr> ansList = targetCache.getList(rp.getRequestName(), rp.getDnsType());
		getLogger().set(LogKind.DETAIL, sockUdp, 13,
				String.format("%s ansList.Count=%d", rp.getDnsType(), ansList.size())); //"Create Response (AN)"

		if (0 < ansList.size()) { //検索でヒットした場合
			for (OneRr oneRR : ansList) {
				//Java fix
				appendRr(sp, RrKind.AN,
						DnsUtil.createRr(rp.getRequestName(), rp.getDnsType(), oneRR.getTtl(), oneRR.getData()));
				if (rp.getDnsType() == DnsType.Mx || rp.getDnsType() == DnsType.Cname || rp.getDnsType() == DnsType.Ns) {

					String targetName = "";
					if (rp.getDnsType() == DnsType.Mx) {
						targetName = ((RrMx) oneRR).getMailExchangeHost();
					} else if (rp.getDnsType() == DnsType.Ns) {
						targetName = ((RrNs) oneRR).getNsName();
					} else if (rp.getDnsType() == DnsType.Cname) {
						targetName = ((RrCname) oneRR).getCName();
					} else {
						Util.runtimeException("not implement [Server.onSubThread()]");
					}

					//追加情報が必要な場合 （Aレコード）をパケットに追加する
					List<OneRr> rr = targetCache.getList(targetName, DnsType.A);
					for (OneRr r : rr) {
						appendRr(sp, RrKind.AR, new RrA(targetName, r.getTtl(), r.getData()));
					}

					//追加情報が必要な場合 （AAAAレコード）をパケットに追加する
					rr = targetCache.getList(targetName, DnsType.Aaaa);
					for (OneRr r : rr) {
						appendRr(sp, RrKind.AR, new RrAaaa(targetName, r.getTtl(), r.getData()));
					}
				}
			}
		} else {
			//検索でヒットしない場合
			if (rp.getDnsType() == DnsType.A) {
				// CNAMEに定義されていないかどうかを確認する
				List<OneRr> cnameList = targetCache.getList(rp.getRequestName(), DnsType.Cname);
				for (OneRr o : cnameList) {
					String cname = ((RrCname) o).getCName();
					List<OneRr> aList = targetCache.getList(cname, DnsType.A);
					for (OneRr a : aList) {
						getLogger().set(LogKind.DETAIL, sockUdp, 16, o.toString()); //"Append RR"
						appendRr(sp, RrKind.AN, o);
						getLogger().set(LogKind.DETAIL, sockUdp, 16, a.toString()); //"Append RR"
						appendRr(sp, RrKind.AN, a);
					}
				}

			}
		}

		if (rp.getDnsType() == DnsType.A || rp.getDnsType() == DnsType.Aaaa || rp.getDnsType() == DnsType.Soa
				|| rp.getDnsType() == DnsType.Cname) {
			// (C)「権威セクション」「追加情報セクション」作成
			List<OneRr> nsList = targetCache.getList(domainName, DnsType.Ns);
			//Java fix 13->22
			getLogger()
					.set(LogKind.DETAIL, sockUdp, 22, String.format("%s nsList.Count=%d", DnsType.Ns, nsList.size())); // Create Response (AR)
			for (OneRr o : nsList) {
				RrNs ns = (RrNs) o;

				//Java fix
				appendRr(sp, RrKind.NS, new RrNs(ns.getName(), ns.getTtl(), ns.getData()));

				if (!domainName.toUpperCase().equals("LOCALHOST.")) {
					//localhost検索の場合は、追加情報はない
					//「追加情報」
					List<OneRr> addList = targetCache.getList(ns.getNsName(), DnsType.A);
					for (OneRr rr : addList) {
						//Java fix
						appendRr(sp, RrKind.AR, new RrA(ns.getNsName(), rr.getTtl(), rr.getData()));
					}
					addList = targetCache.getList(ns.getNsName(), DnsType.Aaaa);
					for (OneRr rr : addList) {
						//Java fix
						appendRr(sp, RrKind.AR, new RrAaaa(ns.getNsName(), rr.getTtl(), rr.getData()));
					}
				}
			}
		}

		sockUdp.send(sp.getBytes()); //送信
		//sockUdp.Close();UDPソケット(sockUdp)はクローンなのでクローズしても、処理されない※Close()を呼び出しても問題はない
		sockUdp.close();
	}

	//レスポンス情報追加をまとめて記述
	void appendRr(PacketDns packetDns, RrKind rrKind, OneRr oneRr) {
		getLogger().set(LogKind.DETAIL, null, 23, String.format("[%s] %s", rrKind, oneRr.toString())); //"Append RR"
		packetDns.addRR(rrKind, oneRr);
	}

	//addrは通常オーダで指定されている
	//private PacketDns Lookup(Ip ip, String requestName, DNS_TYPE dnsType,RemoteInfo remoteInfo) {
	private PacketDns lookup(Ip ip, String requestName, DnsType dnsType, Ip remoteAddr) throws IOException {

		//Ip ip = new Ip(addr);
		getLogger().set(LogKind.DETAIL, null, 17,
				String.format("%s Server=%s Type=%s", requestName, ip.toString(), dnsType)); //"Lookup"

		//受信タイムアウト
		int timeout = 3;

		//		var random = new Random(Environment.TickCount);
		//		var id = (ushort) random.Next(0xFFFF);//識別子をランダムに決定する
		Random random = new Random();
		short id = (short) random.nextInt(0xFFFF);
		boolean qr = false; //要求
		boolean aa = false; //権威なし
		boolean rd = (boolean) getConf().get("useRD"); //再帰要求を使用するかどうか
		boolean ra = false; //再帰無効

		//リクエストパケットの生成
		PacketDns sp = new PacketDns(id, qr, aa, rd, ra);
		sp.addRR(RrKind.QD, new RrQuery(requestName, dnsType)); //QR(質問)フィールド追加

		int port = 53;
		//SockUdp sockUdp = new UdpObj(Kernel, getLogger(), ip, port);
		byte[] sendBuf = null;
		sendBuf = sp.getBytes();
		SockUdp sockUdp = new SockUdp(ip, port, null, sendBuf); //送信

		//この辺のロジックを動作確認する必要がある
		byte[] recvBuf = sockUdp.recv(timeout);
		if (recvBuf != null && 12 <= recvBuf.length) {

			try {
				PacketDns rp = new PacketDns(recvBuf);

				String str = String.format("requestName=%s count[%d,%d,%d,%d] rcode=%s AA=%s", requestName
						, rp.getCount(RrKind.QD)
						, rp.getCount(RrKind.AN)
						, rp.getCount(RrKind.NS)
						, rp.getCount(RrKind.AR)
						, rp.getRcode()
						, rp.getAA());
				getLogger().set(LogKind.DETAIL, sockUdp, 18, str); //"Lookup"

				//質問フィールの以外のリソースデータをキャッシュする
				//for (int rr = 1; rr < 4; rr++) {
				for (RrKind rr : RrKind.values()) {

					if (rr == RrKind.QD) {
						continue; //質問フィールの以外のリソースデータをキャッシュする
					}

					int m = rp.getCount(rr);
					for (int n = 0; n < m; n++) {
						OneRr oneRR = rp.getRR(rr, n);
						rootCache.add(oneRR);
						getLogger().set(LogKind.DETAIL, sockUdp, 24,
								String.format("%s rootCache.Count=%d", oneRR.toString(), rootCache.count())); //_rootCache.Add

					}
				}
				return rp;
			} catch (IOException e) {
				//ここでのエラーログも必要？
				return null;
			}
		}

		getLogger().set(LogKind.ERROR, sockUdp, 5,
				String.format("addr=%s requestName=%s dnsType=%s", remoteAddr, requestName, dnsType)); //Lookup() パケット受信でタイムアウトが発生しました。
		return null;
	}

	//ルートキャッシュにターゲットのデータが蓄積されるまで、再帰的に検索する
	boolean searchLoop(String requestName, DnsType dnsType, int depth, Ip remoteAddr) throws IOException {

		if (depth > 15) {
			return false;
		}

		//リクエスト名からドメイン名を取得する
		String domainName = getDomainName(requestName);

		//対象ドメインのNSサーバ一覧を取得する(存在しない場合は、ルートNSの一覧となる)
		ArrayList<String> nsList = getNsList(domainName);

		while (true) {
			//検索が完了しているかどうかの確認
			//rootCacheにターゲットのデータがキャッシュ（蓄積）されているか
			if (rootCache.find(requestName, dnsType)) {
				return true; //検索完了
			}
			if (dnsType == DnsType.A) {
				//DNS_TYPE.Aの場合、CNAMEがキャッシュされている場合、蓄積完了となる
				ArrayList<OneRr> rrList = rootCache.getList(requestName, DnsType.Cname);
				for (OneRr o : rrList) {
					if (rootCache.find(((RrCname) o).getCName(), DnsType.A)) {
						return true;
					}
				}
			}
			//ネームサーバ一覧から、そのアドレスの一覧を作成する
			ArrayList<Ip> nsIpList = getIpList(nsList, depth, remoteAddr);
			//ネームサーバのアドレスが取得できない場合、処理の継続はできない（検索不能）
			if (nsIpList.size() == 0) {
				return false;
			}
			nsList.clear();

			//ターゲットの取得成功  return true
			//権威サーバから回答なし return false
			//NSリストが取得できな場合 nsListを作成してして break
			for (Ip ip : nsIpList) {

				PacketDns rp = lookup(ip, requestName, dnsType, remoteAddr);
				if (rp != null) {
					if (rp.getAA()) {
						//権威サーバの応答の場合
						//ホストが存在しない　若しくは　回答フィールドが0の場合、処理停止
						if (rp.getRcode() == 3 || rp.getCount(RrKind.AN) == 0) {
							return false;
						}
					}
					if (0 < rp.getCount(RrKind.AN)) {
						//回答フィールドが存在する場合
						return true;
					}
					// 求めている回答は得ていないが、権威サーバを教えられた場合
					// ネームサーバのリストを差し替える
					for (int n = 0; n < rp.getCount(RrKind.NS); n++) {
						OneRr oneRr = rp.getRR(RrKind.NS, n);
						if (oneRr.getDnsType() == DnsType.Ns) {
							nsList.add(((RrNs) oneRr).getNsName());
						}
					}
					if (0 < nsList.size()) {
						break; //ネームサーバリストを取得した
					}
				}
			}

		}
	}

	//ネームサーバ一覧から、そのアドレスの一覧を作成する
	ArrayList<Ip> getIpList(ArrayList<String> nsList, int depth, Ip remoteAddr) throws IOException {
		ArrayList<Ip> ipList = new ArrayList<Ip>();
		for (String ns : nsList) {
			ArrayList<OneRr> rrList = rootCache.getList(ns, DnsType.A);

			//IP情報が無い場合、再帰検索
			if (rrList.size() == 0) {
				searchLoop(ns, DnsType.A, depth, remoteAddr);
				rrList = rootCache.getList(ns, DnsType.A);
			}

			rrList.addAll(rootCache.getList(ns, DnsType.Aaaa));

			for (OneRr o : rrList) {
				Ip ip = null;
				if (o.getDnsType() == DnsType.A) {
					ip = ((RrA) o).getIp();
				} else if (o.getDnsType() == DnsType.Aaaa) {
					ip = ((RrAaaa) o).getIp();
				}
				//重複は追加しない
				if (ipList.indexOf(ip) == -1) {
					ipList.add(ip);
				}
			}
		}
		return ipList;
	}

	//リクエスト名からドメイン名を取得する
	String getDomainName(String requestName) {
		String domainName = requestName;
		int index = requestName.indexOf('.');
		if (index != -1) {
			domainName = requestName.substring(index + 1);
		}
		return domainName;
	}

	//対象ドメインのNSサーバ一覧を取得する(存在しない場合は、ルートNSの一覧となる)
	ArrayList<String> getNsList(String domainName) {
		ArrayList<String> nsList = new ArrayList<String>();
		ArrayList<OneRr> rrList = rootCache.getList(domainName, DnsType.Ns);
		if (0 < rrList.size()) {
			for (OneRr o : rrList) {
				nsList.add(((RrNs) o).getNsName());
			}
		} else { //キャッシュに存在しない場合
			//ルートNSサーバをランダムに一覧セットする
			nsList = getRootNsList();
		}
		return nsList;
	}

	//ルートNSサーバをランダムに一覧セットする
	ArrayList<String> getRootNsList() {
		ArrayList<String> nsList = new ArrayList<String>();
		ArrayList<OneRr> rrList = rootCache.getList(".", DnsType.Ns);

		int center = 0;
		if (rrList.size() > 0) {
			Random random = new Random();
			center = random.nextInt(rrList.size()); //センタ位置をランダムに決定する
		}

		for (int i = center; i < rrList.size(); i++) {
			//センタ以降の一覧を取得
			//Java fix
			//nsList.add(rrList[i].Name);
			nsList.add(((RrNs) rrList.get(i)).getNsName());
		}
		for (int i = 0; i < center; i++) {
			//センタ以前の一覧をコピー
			//Java fix
			//nsList.add(rrList[i].Name);
			nsList.add(((RrNs) rrList.get(i)).getNsName());
		}
		return nsList;
	}

	public String getMsg(int messageNo) {
		switch (messageNo) {
		//case 0:
		//	return isJp() ? "標準問合(OPCODE=0)以外のリクエストには対応できません" : "Because I am different from 0 in OPCODE,can't process it.";
		//case 1:
		//	return isJp() ? "質問エントリーが１でないパケットは処理できません" : "Because I am different from 1 a question entry,can't process it.";
		case 2:
			return isJp() ? "ルートキャッシュの読み込みに失敗しました" : "Failed in reading of route cash.";
		case 3:
			return isJp() ? "ルートキャッシュ(ファイル)が見つかりません" : "Root chace (file) is not found";
		case 4:
			return isJp() ? "パケットの解釈に失敗しました。正常なDNSリクエストでない可能性があります。"
					: "Failed in interpretation of a packet.It may not be a normal DNS request.";
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
			//case 14://Java fix
			//    return "Append RR";
		case 15:
			return "Create Response (AN.CNAME)";
			//case 16://Java fix
			//    return "Append RR";
		case 17:
			return "Lookup send";
		case 18:
			return "Lookup recv";
		case 19:
			return isJp() ? "リソースデータの読み込みに失敗しました" : "Failed in reading of resource data";
		case 20:
			return isJp() ? "リソース(SOA)は追加されませんでした" : "Resource (SOA) was not added";
		case 21:
			return isJp() ? "ドメインのリソース定義を読み込みました" : "Read a resource definition of a domain";
		case 22:
			return "Create Response (AR)";
		case 23:
			return "Append RR";
		case 24:
			return "_rootCache.Add";
		default:
			return "unknown";

		}
	}
}
