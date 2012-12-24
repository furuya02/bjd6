package bjd.plugins.dns;

import java.util.ArrayList;

import bjd.ValidObjException;
import bjd.log.LogKind;
import bjd.log.Logger;
import bjd.net.InetKind;
import bjd.net.Ip;
import bjd.option.Conf;
import bjd.option.Dat;
import bjd.option.OneDat;

public final class RrDb {
	private ArrayList<OneRr> ar = new ArrayList<>();

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

	public int size() {
		return ar.size();
	}

	/**
	 * 本来は必要ないが、テストのためにあえてメソッドにしている
	 * @param index
	 * @return
	 */
	private OneRr get(int index) {
		return ar.get(index);
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
	public boolean initSoa(String domainName, String mail, int serial, int refresh, int retry, int expire, int minimum) {

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
