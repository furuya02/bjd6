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
import bjd.option.OneOption;
import bjd.util.Bytes;
import bjd.util.Util;

/**
 * RRレコードのデータベース<br>
 * @author SIN
 *
 */
public final class ListRr {

	private Object lock = new Object(); //排他制御

	//コンストラクタでファイルを読み込んで初期化する
	//コンストラクタは、named.ca用と.zone用の２種類がある
	private ArrayList<OneRr> db = new ArrayList<>();

	private int soaExpire; //終了時間（オプションで指定された有効時間）
	private String domainName;

	public String getDomainName() {
		return domainName;
	}

	/**
	 * named.caで初期化する場合のコンストラクタ
	 * @param soaExpire
	 * @param fileName
	 * @throws IOException IllegalArgumentException
	 */
	public ListRr(int soaExpire, String fileName) throws IOException {
		this.soaExpire = soaExpire;
		int ttl = 0; //rootCacheは有効期限なし
		domainName = ".";

		//this.defaultExpire = defaultExpire;
		File file = new File(fileName);

		if (file.exists()) {
			ArrayList<String> lines = Util.textFileRead(file);
			String tmpName = ""; //全行のNAMEを保持する　NAMEは前行と同じ場合省略が可能
			for (String str : lines) {
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
					continue;
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
					throw new IllegalArgumentException(String.format("タイプ名に矛盾があります [file=%s str=%s]", fileName, str));
				}

				//タイプの次がDATAとなる
				if (typeCol + 1 >= tmp2.size()) {
					throw new IllegalArgumentException(String.format("タイプの次にカラム（DATA）が存在しない [file=%s str=%s]", fileName, str));
				}
				String dataStr = tmp2.get(typeCol + 1);

				//************************************************
				//クラス(IN)が含まれているかどうかをサーチする
				//************************************************
				int classCol = 0;
				boolean find = false;
				for (; classCol < tmp2.size(); classCol++) {
					if (!tmp2.get(classCol).equals("")) {
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
							ttl = Util.htonl(ttl);
						} else {
							name = tmp2.get(0);
						}
					} else { //INが有る場合
						//0番目はINであるので、名前もTTLも省略されている
						if (classCol != 0) {
							throw new IllegalArgumentException(String.format("INの位置に矛盾がありま [file=%s str=%s]", fileName, str));
						}
					}
				} else if (typeCol == 2) {
					if (classCol == -1) { //INが無い場合
						//０番目はNAME、1番目はTTLとなる
						name = tmp2.get(0);
						//ttl = Convert.ToUInt32(tmp2[1]);
						ttl = Integer.valueOf(tmp2.get(1));
						ttl = Util.htonl(ttl);
					} else { //INが有る場合
						if (classCol != 1) {
							throw new IllegalArgumentException(String.format("INの位置に矛盾がありま [file=%s str=%s]", fileName, str));
						}
						//０番目はNAME若しくはTTLとなる
						if (str.substring(0, 1).equals(" ") || str.substring(0, 1).equals("\t")) {
							//名前は省略されているので
							//ttl = Convert.ToUInt32(tmp2[0]);
							ttl = Integer.valueOf(tmp2.get(0));
							ttl = Util.htonl(ttl);
						} else {
							name = tmp2.get(0);
						}
					}
				} else if (typeCol == 3) {
					if (classCol == -1) { //INが無い場合
						throw new IllegalArgumentException(String.format("カラムが不足している [file=%s str=%s]", fileName, str));
					}
					//INが有る場合
					if (classCol != 2) {
						throw new IllegalArgumentException(String.format("INの位置に矛盾がありま [file=%s str=%s]", fileName, str));
					}
					//０番目はNAME、1番目はTTLとなる
					name = tmp2.get(0);
					//ttl = Convert.ToUInt32(tmp2[1]);
					ttl = Integer.valueOf(tmp2.get(1));
					ttl = Util.htonl(ttl);
				}

				//*********************************************
				//nameの補完
				//*********************************************
				if (name.equals("@")) { //@の場合
					name = domainName;
				} else if (name.lastIndexOf(".") != name.length() - 1) { //最後に.がついていない場合、ドメイン名を追加する
					name = name + "." + domainName + ".";
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
						add(new RrA(name, ttl, ipV4));
					} catch (ValidObjException e) {
						throw new IllegalArgumentException(String.format("Ipアドレスに矛盾があります [ip=%s file=%s str=%s]", dataStr, fileName, str));
					}
				} else if (dnsType == DnsType.Ns) {
					add(new RrNs(name, ttl, dataStr));
				} else if (dnsType == DnsType.Aaaa) {
					try {
						Ip ipV6 = new Ip(dataStr);
						add(new RrAaaa(name, ttl, ipV6));
					} catch (ValidObjException e) {
						throw new IllegalArgumentException(String.format("Ipアドレスに矛盾があります [ip=%s file=%s str=%s]", dataStr, fileName, str));
					}
				} else {
					throw new IllegalArgumentException(String.format("name.caには、タイプA,AAAA及びNS以外は使用できません [file=%s str=%s]", fileName, str));
				}
			}
		}
		//locaohostレコードの追加
		Ip ip = new Ip(IpKind.V4_LOCALHOST);
		add(new RrA("localhost.", ttl, ip));
		add(new RrPtr("1.0.0.127.in-addr.arpa.", ttl, "localhost"));

		ip = new Ip(IpKind.V6_LOCALHOST);
		add(new RrAaaa("localhost.", ttl, ip));
		add(new RrPtr("1.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.IP6.ARPA.", ttl, "localhost"));
	}

	//リソース定義（Dat)で初期化する場合
	public ListRr(Logger logger, Conf conf, Dat dat, String dName) {
		int ttl = 0; //有効期限なし
		String ns = ""; //SOA追加時に使用するため、NSレコードを見つけたときにサーバ名を保存しておく

		//オプションを読み込んで、ローカルデータを初期化する
		//this.oneOption = oneOption;
		soaExpire = (int) conf.get("soaExpire");

		domainName = dName;

		for (OneDat o : dat) {
			if (o.isEnable()) {
				//var type = Convert.ToInt32(o.StrList[0]);
				int type = Integer.valueOf(o.getStrList().get(0));
				String name = o.getStrList().get(1);
				String alias = o.getStrList().get(2);
				Ip ip = null;
				try {
					ip = new Ip(o.getStrList().get(3));
				} catch (ValidObjException e1) {
					Util.runtimeException(this, e1);
				}
				//var n = Convert.ToInt32(o.StrList[4]);
				int n = Integer.valueOf(o.getStrList().get(4));

				DnsType dnsType = DnsType.A;
				if (type == 1) {
					dnsType = DnsType.Ns;
				} else if (type == 2) {
					dnsType = DnsType.Mx;
				} else if (type == 3) {
					dnsType = DnsType.Cname;
				} else if (type == 4) {
					dnsType = DnsType.Aaaa;
				}
				short priority = (short) n;
				//uint addr = ip.AddrV4;        //class Ip -> uint;

				//最後に.がついていない場合、ドメイン名を追加する
				if (name.lastIndexOf('.') != name.length() - 1) {
					name = name + "." + domainName;
				}
				if (alias.lastIndexOf('.') != alias.length() - 1) {
					alias = alias + "." + domainName;
				}

				//CNAME以外は、PTRレコードを自動的に生成する
				if (dnsType != DnsType.Cname) {
					//PTR名を作成 [例] 192.168.0.1 -> 1.0.168.192.in-addr.arpa;
					if (ip.getInetKind() == InetKind.V4) { //IPv4
						String ptrName = String.format("%d.%d.%d.%d.in-addr.arpa.", ip.getIpV4()[3], ip.getIpV4()[2], ip.getIpV4()[1], ip.getIpV4()[0]);
						add(new RrPtr(ptrName, ttl, name));
					} else { //IPv6
						StringBuilder sb = new StringBuilder();
						for (byte a : ip.getIpV6()) {
							sb.append(String.format("%4d", a));
						}
						String ipStr = sb.toString();
						if (ipStr.length() == 32) {
							sb = new StringBuilder();
							for (int e = 31; e >= 0; e--) {
								sb.append(ipStr.charAt(e));
								sb.append('.');
							}
							add(new RrPtr(sb + "ip6.arpa.", ttl, name));
						}
					}
				}

				//データベースへの追加
				if (dnsType == DnsType.A) {
					if (ip.getInetKind() == InetKind.V4) {
						add(new RrA(name, ttl, ip));
					} else {
						logger.set(LogKind.ERROR, null, 19, String.format("address %s", ip.toString()));
					}
				} else if (dnsType == DnsType.Aaaa) {
					if (ip.getInetKind() == InetKind.V6) {
						add(new RrAaaa(name, ttl, ip));
					} else {
						logger.set(LogKind.ERROR, null, 20, String.format("address %s", ip.toString()));
					}
				} else if (dnsType == DnsType.Ns) {
					ns = name; //SOA追加時に使用するため、ネームサーバの名前を保存する

					// A or AAAAレコードも追加
					if (ip.getInetKind() == InetKind.V4) {
						add(new RrA(name, ttl, ip));
					} else { //IPv6
						add(new RrAaaa(name, ttl, ip));
					}

					add(new RrNs(domainName, ttl, name));
				} else if (dnsType == DnsType.Mx) {
					// A or AAAAレコードも追加
					add(new RrA(name, ttl, ip));

					//プライオリィ
					//					byte[] dataName = DnsUtil.str2DnsName(name); //DNS名前形式に変換
					//					byte[] data = Bytes.create(Util.htons(priority), dataName);
					add(new RrMx(domainName, ttl, priority, name));
				} else if (dnsType == DnsType.Cname) {
					add(new RrCname(alias, ttl, name));
				}
			}

			//SOAレコードの追加
			if (!ns.equals("")) { //NSサーバ名が必須
				String soaMail = (String) conf.get("soaMail");
				soaMail = soaMail.replace('@', '.'); //@を.に置き換える
				soaMail = soaMail + "."; //最後に.を追加する
				int soaSerial = (int) conf.get("soaSerial");
				int soaRefresh = (int) conf.get("soaRefresh");
				int soaRetry = (int) conf.get("soaRetry");
				int soaExpire = (int) conf.get("soaExpire");
				int soaMinimum = (int) conf.get("soaMinimum");

				//				byte[] data = Bytes.create(DnsUtil.str2DnsName(ns), DnsUtil.str2DnsName(soaMail), Util.htonl(soaSerial), Util.htonl(soaRefresh), Util.htonl(soaRetry), Util.htonl(soaExpire),
				//						Util.htonl(soaMinimum));

				add(new RrSoa(domainName, ttl, ns, soaMail, soaSerial, soaRefresh, soaRetry, soaExpire, soaMinimum));
			}
		}
	}

	//指定したname及びDNS_TYPEにヒットするデータを取得する
	public ArrayList<OneRr> search(String name, DnsType dnsType) {
		ArrayList<OneRr> rrList = new ArrayList<OneRr>();
		long now = Calendar.getInstance().getTimeInMillis();

		// 排他制御
		synchronized (lock) {
			for (OneRr oneRr : db) {
				if (oneRr.getDnsType() != dnsType) {
					continue;
				}
				if (!oneRr.isEffective(now)) {
					continue; //生存時間超過データは使用しない
				}
				if (!oneRr.getName().toUpperCase().equals(name.toUpperCase())) {
					continue; //大文字で比較される
				}

				//boolean find = rrList.Any(o => o.Data == oneRR.Data);//データが重複していない場合だけ、リストに追加する
				//データが重複していない場合だけ、リストに追加する
				boolean find = false;
				for (OneRr o : rrList) {
					if (o.getData() == o.getData()) {
						find = true;
						break;
					}
				}
				if (find) {
					continue;
				}
				int ttl = Util.htonl(soaExpire);

				rrList.add(oneRr.clone(ttl));
			}
		} // 排他制御
		return rrList;
	}

	//データが存在するかどうかだけの確認
	public boolean find(String name, DnsType dnsType) {
		long now = Calendar.getInstance().getTimeInMillis();
		boolean ret = false;
		// 排他制御
		synchronized (lock) {
			for (OneRr oneRR : db) {
				if (oneRR.getDnsType() != dnsType) {
					continue;
				}
				if (!oneRR.isEffective(now)) {
					continue; //生存時間超過データは使用しない
				}
				if (!oneRR.getName().toUpperCase().equals(name.toUpperCase())) {
					continue; //大文字で比較される
				}
				ret = true; //存在する
				break;
			}
		} // 排他制御
		return ret;
	}

	//リソースの追加
	public boolean add(OneRr oneRR) {
		// 排他制御
		synchronized (lock) {
			for (OneRr t : db) {
				if (t.getDnsType() != oneRR.getDnsType()) {
					continue;
				}
				if (!t.getName().equals(oneRR.getName())) {
					continue;
				}
				//TTL=0のデータは普遍であるため、書き換えはしない
				if (t.getTtl() == 0) {
					return false;
				}
				//まったく同じデータが既に有る場合、書き換えはしない
				if (!t.getName().equals(oneRR.getName())) {
					continue;
				}
				if (t.getDnsType() != oneRR.getDnsType()) {
					continue;
				}
				//boolean flg = !oneRR.Data.Where((t1, n) => t.Data[n] != t1).Any();
				boolean flg = true;
				for (int n = 0; n < oneRR.getData().length; n++) {
					if (t.getData()[n] != oneRR.getData()[n]) {
						flg = false;
						break;
					}
				}
				if (!flg) {
					continue;
				}
				return false;
			}
			db.add(oneRR);
		}
		return true;
	}

	public void ttlClear() {
		long now = Calendar.getInstance().getTimeInMillis();
		// 排他制御
		synchronized (lock) {
			for (int i = db.size() - 1; i > 0; i--) {
				if (!db.get(i).isEffective(now)) {
					db.remove(i);
				}
			}
		} // 排他制御
	}
}
