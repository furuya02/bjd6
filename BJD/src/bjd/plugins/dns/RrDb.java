package bjd.plugins.dns;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import bjd.ValidObjException;
import bjd.log.LogKind;
import bjd.log.Logger;
import bjd.net.InetKind;
import bjd.net.Ip;
import bjd.net.IpKind;
import bjd.option.Conf;
import bjd.option.Dat;
import bjd.option.OneDat;
import bjd.util.Util;

public final class RrDb {

	private Object lock = new Object(); //排他制御
	private ArrayList<OneRr> ar = new ArrayList<>();
	private String domainName = "ERROR";

	public String getDomainName() {
		return domainName;
	}

	/**
	 * プロダクトでは使用しないが、テストのためにあえて公開している
	 * @param index
	 * @return
	 */
	public RrDb() {
		//ドメイン名の初期化
		setDomainName("example.com.");//テスト用ドメイン名
	}

	/**
	 * コンストラクタ<br>
	 * リソース定義（Dat)で初期化する場合
	 */
	public RrDb(Logger logger, Conf conf, Dat dat, String domainName) {
		//ドメイン名の初期化
		setDomainName(domainName);

		//Datの読み込み
		if (dat != null) {
			for (OneDat o : dat) {
				if (o.isEnable()) {
					try {
						addOneDat(domainName, o);
					} catch (ValidObjException e) {
						logger.set(LogKind.ERROR, null, 19, String.format("domain=%s %s", domainName, e.getMessage()));
					}
				}
			}
		}
		if (conf != null) {
			//SOAレコードの追加
			String mail = (String) conf.get("soaMail");
			int serial = (int) conf.get("soaSerial");
			int refresh = (int) conf.get("soaRefresh");
			int retry = (int) conf.get("soaRetry");
			int expire = (int) conf.get("soaExpire");
			int minimum = (int) conf.get("soaMinimum");
			if (!initSoa(domainName, mail, serial, refresh, retry, expire, minimum)) {
				logger.set(LogKind.ERROR, null, 20, String.format("domain=%s", domainName));
			}
		}
	}

	/**
	 * コンストラクタ<br>
	 * named.caで初期化する場合
	 * @throws IOException 
	 */
	public RrDb(String namedCaPath) throws IOException {
		//ドメイン名の初期化
		setDomainName(".");

		//named.caの読み込み
		if (namedCaPath != null) {
			File file = new File(namedCaPath);
			if (file.exists()) {
				ArrayList<String> lines = Util.textFileRead(file);
				String tmpName = ""; //全行のNAMEを保持する　NAMEは前行と同じ場合省略が可能
				for (String str : lines) {
					tmpName = addNamedCaLine(tmpName, str);
				}
			} else {
				throw new IOException(String.format("file not found [%s]", namedCaPath));
			}
		}
		//locaohostレコードの追加
		initLocalHost();
	}

	private String addNamedCaLine(String tmpName, String str) throws IOException {
		//rootCacheは有効期限なし
		int ttl = 0;

		String name = "";
		//String Class = "IN";
		DnsType dnsType = DnsType.Unknown;
		//;以降はコメントとして削除する
		int i = str.indexOf(";");
		if (i != -1) {
			str = str.substring(0, i);
		}

		//空の行は処理しない
		if (str.length() == 0) {
			return tmpName;
		}

		//空白・タブを削除して、パラメータをtmp2へ取得する
		//var tmp = str.Split(new[] { ' ', '\t' });
		//var tmp2 = tmp.Where(s => s != "").ToList();
		String[] tmp = str.split("[ \t]");
		ArrayList<String> tmp2 = new ArrayList<>();
		for (String s : tmp) {
			if (!s.equals("")) {
				tmp2.add(s);
			}
		}

		//************************************************
		//タイプだけは省略することができないので、それを基準にサーチする
		//************************************************
		int typeCol = 0;
		for (; typeCol < tmp2.size(); typeCol++) {
			//for (DnsType t : Enum.GetValues(typeof(DnsType))) {
			for (DnsType t : DnsType.values()) {
				if (!tmp2.get(typeCol).equals(t.toString().toUpperCase())) {
					continue;
				}
				dnsType = t;
				break;
			}
			if (dnsType != DnsType.Unknown) {
				break;
			}
		}
		if (dnsType == DnsType.Unknown) {
			throw new IOException(String.format("ルートサーバ情報の読み込みに失敗しました (タイプ名に矛盾があります [str=%s])", str));
		}

		//タイプの次がDATAとなる
		if (typeCol + 1 >= tmp2.size()) {
			throw new IOException(String.format("ルートサーバ情報の読み込みに失敗しました  (タイプの次にカラム（DATA）が存在しない [str=%s])", str));
		}
		String dataStr = tmp2.get(typeCol + 1);

		//************************************************
		//クラス(IN)が含まれているかどうかをサーチする
		//************************************************
		int classCol = 0;
		boolean find = false;
		for (; classCol < tmp2.size(); classCol++) {
			if (!tmp2.get(classCol).equals("IN")) {
				continue;
			}
			find = true;
			break;
		}
		if (!find) {
			classCol = -1;
		}
		//クラスが含まれた場合、そのカラムはclassColに保存されている
		//含まれていない場合 classCol=-1

		if (typeCol == 1) {
			if (classCol == -1) { //INが無い場合
				//０番目はNAME若しくはTTLとなる
				if (str.substring(0, 1).equals(" ") || str.substring(0, 1).equals("\t")) {
					//名前は省略されているので
					//ttl = Convert.ToUInt32(tmp2[0]);
					ttl = Integer.valueOf(tmp2.get(0));
					//ttl = Util.htonl(ttl);
				} else {
					name = tmp2.get(0);
				}
			} else { //INが有る場合
				//0番目はINであるので、名前もTTLも省略されている
				if (classCol != 0) {
					throw new IOException(String.format("ルートサーバ情報の読み込みに失敗しました (INの位置に矛盾がありま [str=%s])", str));
				}
			}
		} else if (typeCol == 2) {
			if (classCol == -1) { //INが無い場合
				//０番目はNAME、1番目はTTLとなる
				name = tmp2.get(0);
				//ttl = Convert.ToUInt32(tmp2[1]);
				ttl = Integer.valueOf(tmp2.get(1));
				//ttl = Util.htonl(ttl);
			} else { //INが有る場合
				if (classCol != 1) {
					throw new IOException(String.format("ルートサーバ情報の読み込みに失敗しました  (INの位置に矛盾がありま [str=%s])", str));
				}
				//０番目はNAME若しくはTTLとなる
				if (str.substring(0, 1).equals(" ") || str.substring(0, 1).equals("\t")) {
					//名前は省略されているので
					//ttl = Convert.ToUInt32(tmp2[0]);
					ttl = Integer.valueOf(tmp2.get(0));
					//ttl = Util.htonl(ttl);
				} else {
					name = tmp2.get(0);
				}
			}
		} else if (typeCol == 3) {
			if (classCol == -1) { //INが無い場合
				throw new IOException(String.format("ルートサーバ情報の読み込みに失敗しました  (カラムが不足している [str=%s])", str));
			}
			//INが有る場合
			if (classCol != 2) {
				throw new IOException(String.format("ルートサーバ情報の読み込みに失敗しました  (INの位置に矛盾がありま [str=%s])", str));
			}
			//０番目はNAME、1番目はTTLとなる
			name = tmp2.get(0);
			//ttl = Convert.ToUInt32(tmp2[1]);
			ttl = Integer.valueOf(tmp2.get(1));
			//ttl = Util.htonl(ttl);
		}

		//*********************************************
		//nameの補完
		//*********************************************
		if (name.equals("@")) { //@の場合、ドメイン名に置き換えられる
			name = domainName;
		} else if (name.lastIndexOf(".") != name.length() - 1) { //最後に.がついていない場合、ドメイン名を追加する
			name = name + "." + domainName;
		} else if (name.equals("")) {
			name = tmpName; //前行と同じ
		}
		tmpName = name; //前行分として記憶する

		//*********************************************
		//String sataStr を変換してデータベースに追加
		//*********************************************
		if (dnsType == DnsType.A) {
			try {
				Ip ipV4 = new Ip(dataStr);
				if (ipV4.getInetKind() != InetKind.V4) {
					throw new IOException(String.format("ルートサーバ情報の読み込みに失敗しました (AレコードにIPv4でないアドレスが指定されました [ip=%s str=%s])", dataStr, str));
				}
				add(new RrA(name, ttl, ipV4));
			} catch (ValidObjException e) {
				throw new IOException(String.format("ルートサーバ情報の読み込みに失敗しました (Ipアドレスに矛盾があります [ip=%s str=%s])", dataStr, str));
			}
		} else if (dnsType == DnsType.Aaaa) {
			try {
				Ip ipV6 = new Ip(dataStr);
				if (ipV6.getInetKind() != InetKind.V6) {
					throw new IOException(String.format("ルートサーバ情報の読み込みに失敗しました (AAAAレコードにIPv6でないアドレスが指定されました [ip=%s str=%s])", dataStr, str));
				}
				add(new RrAaaa(name, ttl, ipV6));
			} catch (ValidObjException e) {
				throw new IOException(String.format("ルートサーバ情報の読み込みに失敗しました (Ipアドレスに矛盾があります [ip=%s str=%s])", dataStr, str));
			}
		} else if (dnsType == DnsType.Ns) {
			add(new RrNs(name, ttl, dataStr));
		} else {
			throw new IOException(String.format("ルートサーバ情報の読み込みに失敗しました (タイプA,AAAA及びNS以外は使用できません [str=%s])", str));
		}
		return tmpName;
	}

	private void initLocalHost() {
		int ttl = 0; //rootCacheは有効期限なし
		Ip ip = new Ip(IpKind.V4_LOCALHOST);
		add(new RrA("localhost.", ttl, ip));
		add(new RrPtr("1.0.0.127.in-addr.arpa.", ttl, "localhost"));

		ip = new Ip(IpKind.V6_LOCALHOST);
		add(new RrAaaa("localhost.", ttl, ip));
		add(new RrPtr("1.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.IP6.ARPA.", ttl, "localhost"));
	}
	
	/**
	 * ドメイン名の設定<br>
	 * 必ず、最後がドットになるように補完される<br>
	 * @param str
	 */
	private void setDomainName(String str){
		//最後に.がついていない場合、追加する
		if (str.lastIndexOf('.') != str.length() - 1) {
			str = str + ".";
		}
		this.domainName = str;
	}

	public void ttlClear() {
		long now = Calendar.getInstance().getTimeInMillis();
		// 排他制御
		synchronized (lock) {
			for (int i = ar.size() - 1; i > 0; i--) {
				if (!ar.get(i).isEffective(now)) {
					ar.remove(i);
				}
			}
		} // 排他制御
	}

	//データが存在するかどうかだけの確認
	public boolean find(String name, DnsType dnsType) {
		ArrayList<OneRr> list = getList(name, dnsType);
		if (list.size() != 0) {
			return true;
		}
		return false;
	}

	/**
	 * リソースの検索<br>
	 * 指定したname及びDNS_TYPEにヒットするデータを取得する<br>
	 * クライアントへの送信に使用する場合は、TTLをexpireで書き換える必要がある<br>
	 * @param name
	 * @param dnsType
	 * @return 検索で見つからなかった場合は、空の配列を返す
	 */
	public ArrayList<OneRr> getList(String name, DnsType dnsType) {

		ArrayList<OneRr> list = new ArrayList<OneRr>();
		//検索中に期限の過ぎたリソースがあった場合は、このリストに追加しておいて最後に削除する
		ArrayList<OneRr> removeList = new ArrayList<OneRr>();

		long now = Calendar.getInstance().getTimeInMillis();

		// 排他制御
		synchronized (lock) {
			for (OneRr o : ar) {
				if (o.getDnsType() != dnsType) {
					continue;
				}
				if (!o.isEffective(now)) {
					removeList.add(o);
					continue; //生存時間超過データは使用しない
				}
				if (!o.getName().toUpperCase().equals(name.toUpperCase())) {
					continue; //大文字で比較される
				}

				//boolean find = rrList.Any(o => o.Data == oneRR.Data);//データが重複していない場合だけ、リストに追加する
				//データが重複していない場合だけ、リストに追加する
				//boolean find = false;
				//for (OneRr o : rrList) {
				//	if (o.getData() == o.getData()) {
				//		find = true;
				//		break;
				//	}
				//}
				//if (find) {
				//	continue;
				//}
				//int ttl = Util.htonl(soaExpire);
				//	
				list.add(o);
			}
			//期限の過ぎたリソースの削除
			for (OneRr o : removeList) {
				ar.remove(o);
			}
		} // 排他制御
		return list;
	}

	/**
	 * リソースの追加<br>
	 * 同一のリソース（TTL以外）は上書きされる<br>
	 * ただしTTL=0のデータは上書きされない<br>
	 * @param oneRR
	 * @return
	 */
	public boolean add(OneRr oneRR) {
		// 排他制御
		synchronized (lock) {
			OneRr target = null; //書き換え対象のリソース
			//TTL以外が全て同じのソースを検索する
			for (OneRr o : ar) {
				if (o.getDnsType() == oneRR.getDnsType() && o.getName().equals(oneRR.getName())) {
					//データ内容の確認	
					boolean isSame = true;
					for (int n = 0; n < o.getData().length; n++) {
						if (o.getData()[n] != oneRR.getData()[n]) {
							isSame = false;
							break;
						}
					}
					if (isSame) {
						if (o.getTtl() == 0) {
							//TTL=0のデータは普遍であるため、書き換えはしない
							return false;
						}
						target = o;
						break;
					}
				}
			}
			//書き換えの対象が見つかっている場合は、削除する
			if (target != null) {
				ar.remove(target);
			}
			ar.add(oneRR);
		}
		return true;
	}

	/**
	 * プロダクトでは使用しないが、テストのためにあえてメソッドにしている
	 * @param index
	 * @return
	 */
	@SuppressWarnings("unused")
	private OneRr get(int index) {
		return ar.get(index);
	}

	/**
	 * プロダクトでは使用しないが、テストのためにあえてメソッドにしている
	 * @return
	 */
	@SuppressWarnings("unused")
	private int size() {
		return ar.size();
	}

	/**
	 * OneDatを追加する <br>
	 * @param domainName
	 * @param o
	 * @throws ValidObjException
	 */
	private void addOneDat(String domainName, OneDat o) throws ValidObjException {
		if (!o.isEnable()) {
			throw new ValidObjException("isEnable=false");
		}

		int type = Integer.valueOf(o.getStrList().get(0));
		String name = o.getStrList().get(1);
		String alias = o.getStrList().get(2);
		Ip ip = new Ip(o.getStrList().get(3));
		int priority = Integer.valueOf(o.getStrList().get(4));
		int ttl = 0; //有効期限なし

		//最後に.がついていない場合、ドメイン名を追加する
		if (name.lastIndexOf('.') != name.length() - 1) {
			name = name + "." + domainName;
		}
		if (alias.lastIndexOf('.') != alias.length() - 1) {
			alias = alias + "." + domainName;
		}

		DnsType dnsType = DnsType.Unknown;
		switch (type) {
			case 0:
				dnsType = DnsType.A;
				if (ip.getInetKind() != InetKind.V4) {
					throw new ValidObjException("IPv6 cannot address it in an A(PTR) record");
				}
				ar.add(new RrA(name, ttl, ip));
				break;
			case 1:
				dnsType = DnsType.Ns;
				ar.add(new RrNs(domainName, ttl, name));
				break;
			case 2:
				dnsType = DnsType.Mx;
				ar.add(new RrMx(domainName, ttl, (short) priority, name));
				break;
			case 3:
				dnsType = DnsType.Cname;
				ar.add(new RrCname(alias, ttl, name));
				break;
			case 4:
				dnsType = DnsType.Aaaa;
				if (ip.getInetKind() != InetKind.V6) {
					throw new ValidObjException("IPv4 cannot address it in an AAAA record");
				}
				ar.add(new RrAaaa(name, ttl, ip));
				break;
			default:
				throw new ValidObjException(String.format("unknown type (%d)", type));
		}

		//MX及びNSの場合は、A or AAAAも追加する
		if (dnsType == DnsType.Mx || dnsType == DnsType.Ns) {
			if (ip.getInetKind() == InetKind.V4) {
				ar.add(new RrA(name, ttl, ip));
			} else {
				ar.add(new RrAaaa(name, ttl, ip));
			}
		}
		//CNAME以外は、PTRレコードを自動的に生成する
		if (dnsType != DnsType.Cname) {
			//PTR名を作成 [例] 192.168.0.1 -> 1.0.168.192.in-addr.arpa;
			if (ip.getInetKind() == InetKind.V4) { //IPv4
				String ptrName = String.format("%d.%d.%d.%d.in-addr.arpa.", (ip.getIpV4()[3] & 0xff), (ip.getIpV4()[2] & 0xff), (ip.getIpV4()[1] & 0xff), (ip.getIpV4()[0] & 0xff));
				ar.add(new RrPtr(name, ttl, ptrName));
			} else { //IPv6
				StringBuilder sb = new StringBuilder();
				for (byte a : ip.getIpV6()) {
					sb.append(String.format("%02x", a));
				}
				String ipStr = sb.toString();
				if (ipStr.length() == 32) {
					sb = new StringBuilder();
					for (int e = 31; e >= 0; e--) {
						sb.append(ipStr.charAt(e));
						sb.append('.');
					}
					ar.add(new RrPtr(name, ttl, sb + "ip6.arpa."));
				}
			}
		}
	}

	/**
	 * SOAレコードの追加<br>
	 * OneDatでデータを読みこんだ後、このメソッドでSOAレコードを追加する<br>
	 * 既に対象ドメインのSOAレコードが有る場合は、TTL=0で処理なし TTL!=0で置換<br>
	 * 対象ドメインのNSレコードが無い場合、処理なし（NSサーバの情報が無いため）<br>
	 * @param domainName
	 * @param mail
	 * @param serial
	 * @param refresh
	 * @param retry
	 * @param expire
	 * @param minimum
	 * @return 追加した場合 true
	 */
	private boolean initSoa(String domainName, String mail, int serial, int refresh, int retry, int expire, int minimum) {

		//NSサーバ
		String nsName = null;

		//DB上の対象ドメインのNSレコードを検索
		for (OneRr o : ar) {
			//DB上に対象ドメインのNSレコードが有る場合
			if (o.getDnsType() == DnsType.Ns && o.getName().equals(domainName)) {
				nsName = ((RrNs) o).getNsName();
				break;
			}
		}
		if (nsName == null) {
			//NSサーバの情報が無い場合は、SOAの追加はできない
			return false;
		}

		//DB上の対象ドメインのSOAレコードを検索
		for (int i = 0; i < ar.size(); i++) {
			if (ar.get(i).getDnsType() == DnsType.Soa) {
				RrSoa soa = (RrSoa) ar.get(i);
				if (soa.getName().equals(domainName)) {
					if (soa.getTtl() == 0) {
						//既存情報のTTLが0の場合、処置できない
						return false;
					} else {
						ar.remove(i); //削除
						break;
					}
				}
			}
		}
		//SOAレコードの追加
		int ttl = 0; //有効期限なし
		String soaMail = mail.replace('@', '.'); //@を.に置き換える
		soaMail = soaMail + "."; //最後に.を追加する
		ar.add(new RrSoa(domainName, ttl, nsName, soaMail, serial, refresh, retry, expire, minimum));
		return true;
	}

}
