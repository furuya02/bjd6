package bjd.plugins.dns;

import java.util.ArrayList;
import java.util.Calendar;

import bjd.ValidObjException;
import bjd.log.LogKind;
import bjd.log.Logger;
import bjd.net.InetKind;
import bjd.net.Ip;
import bjd.option.Conf;
import bjd.option.Dat;
import bjd.option.OneDat;
import bjd.util.Util;

public final class RrDb {

	private Object lock = new Object(); //排他制御
	private ArrayList<OneRr> ar = new ArrayList<>();

	/**
	 * プロダクトでは使用しないが、テストのためにあえて公開している
	 * @param index
	 * @return
	 */
	public RrDb() {

	}

	/**
	 * コンストラクタ<br>
	 * リソース定義（Dat)で初期化する場合
	 */
	public RrDb(Logger logger, Conf conf, Dat dat, String domainName) {
		//Datの読み込み
		for (OneDat o : dat) {
			if (o.isEnable()) {
				try {
					addOneDat(domainName, o);
				} catch (ValidObjException e) {
					logger.set(LogKind.ERROR, null, 19, String.format("domain=%s %s", domainName, e.getMessage()));
				}
			}
		}
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
	private OneRr get(int index) {
		return ar.get(index);
	}

	/**
	 * プロダクトでは使用しないが、テストのためにあえてメソッドにしている
	 * @return
	 */
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
