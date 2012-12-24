package bjd.plugins.dns;

import java.util.ArrayList;
import java.util.Arrays;

import bjd.ValidObjException;
import bjd.log.LogKind;
import bjd.net.InetKind;
import bjd.net.Ip;
import bjd.option.OneDat;
import bjd.util.Util;

public final class DnsUtil {

	//デフォルトコンストラクタの隠蔽
	private DnsUtil() {

	}

	/**
	 * 文字列とDNS形式の名前(.の所に文字数が入る）の変換
	 * @param data
	 * @return
	 */
	public static String dnsName2Str(byte[] data) {
		byte[] tmp = new byte[data.length - 1];
		for (int src = 0, dst = 0; src < data.length - 1;) {
			int c = data[src++];
			if (c == 0) {
				//var buf = new byte[dst];
				//Buffer.BlockCopy(tmp, 0, buf, 0, dst);
				byte[] buf = Arrays.copyOfRange(tmp, 0, dst);

				tmp = buf;
				break;
			}
			for (int i = 0; i < c; i++) {
				tmp[dst++] = data[src++];
			}
			tmp[dst++] = (byte) '.';
		}
		//return Encoding.ASCII.GetString(tmp);
		return new String(tmp);
	}

	/**
	 * 文字列とDNS形式の名前(.の所に文字数が入る）の変換
	 * @param name
	 * @return
	 */
	public static byte[] str2DnsName(String name) {
		if (name.charAt(name.length() - 1) == '.') {
			name = name.substring(0, name.length() - 1);
		}
		String[] tmp = name.split("\\.");
		//最初の文字カウントと最後の'\0'分を追加（途中の.は文字カウント分として使用される）
		//www.nifty.com  -> 03www05nifty03com00
		byte[] data = new byte[name.length() + 2];
		int d = 0;
		for (String t : tmp) {
			data[d++] = (byte) t.length();
			byte[] dd = t.getBytes(); //Encoding.ASCII.GetBytes(t);
			for (int e = 0; e < t.length(); e++) {
				data[d++] = dd[e];
			}
		}
		return data;
	}

	public static DnsType short2DnsType(short d) {
		switch (d) {
			case 0x0001:
				return DnsType.A;
			case 0x0002:
				return DnsType.Ns;
			case 0x0005:
				return DnsType.Cname;
			case 0x0006:
				return DnsType.Soa;
				//case 0x0007:
				//    return DnsType.Mb;
				//case 0x0008:
				//    return DnsType.Mg;
				//case 0x0009:
				//    return DnsType.Mr;
				//case 0x000a:
				//    return DnsType.Null;
				//case 0x000b:
				//    return DnsType.Wks;
			case 0x000c:
				return DnsType.Ptr;
				//case 0x000d:
				//    return DnsType.Hinfo;
				//case 0x000e:
				//    return DnsType.Minfo;
			case 0x000f:
				return DnsType.Mx;
				//case 0x0010:
				//    return DnsType.Txt;
			case 0x001c:
				return DnsType.Aaaa;
			default:
				Util.runtimeException("short2DnsType() unknown data");
				break;
		}
		return DnsType.Unknown;
	}

	public static short dnsType2Short(DnsType dnsType) {
		switch (dnsType) {
			case A:
				return 0x0001;
			case Ns:
				return 0x0002;
			case Cname:
				return 0x0005;
			case Soa:
				return 0x0006;
				//case DNS_TYPE.MB:
				//    return 0x0007;
				//case DNS_TYPE.MG:
				//    return 0x0008;
				//case DNS_TYPE.MR:
				//    return 0x0009;
				//case DNS_TYPE.NULL:
				//    return 0x000a;
				//case DNS_TYPE.WKS:
				//    return 0x000b;
			case Ptr:
				return 0x000c;
				//case DNS_TYPE.HINFO:
				//    return 0x000d;
				//case DNS_TYPE.MINFO:
				//    return 0x000e;
			case Mx:
				return 0x000f;
				//case DNS_TYPE.TXT:
				//    return 0x0010;
			case Aaaa:
				return 0x001c;
			default:
				Util.runtimeException("dnsType2Short() unknown data");
				break;
		}
		return 0x0000;
	}

	public static OneRr createRr(String name, DnsType dnsType, int ttl, byte[] data) {
		switch (dnsType) {
			case A:
				return new RrA(name, ttl, data);
			case Aaaa:
				return new RrAaaa(name, ttl, data);
			case Ns:
				return new RrNs(name, ttl, data);
			case Mx:
				return new RrMx(name, ttl, data);
			case Soa:
				return new RrSoa(name, ttl, data);
			case Ptr:
				return new RrPtr(name, ttl, data);
			case Cname:
				return new RrCname(name, ttl, data);
			default:
				Util.runtimeException(String.format("DnsUtil.creaetRr() not implement. DnsType=%s", dnsType));
		}
		return null; //これが返されることはない
	}

	/**
	 * OneDatをArrayList<OneRr>に追加する 
	 * @param domainName
	 * @param o
	 * @return
	 * @throws ValidObjException
	 */
	public static void readOneDat(String domainName, OneDat o, ArrayList<OneRr> ar) throws ValidObjException {
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
				String ptrName = String.format("%d.%d.%d.%d.in-addr.arpa.", (ip.getIpV4()[3]&0xff), (ip.getIpV4()[2]&0xff), (ip.getIpV4()[1]&0xff), (ip.getIpV4()[0]&0xff));
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
					ar.add(new RrPtr(name , ttl , sb + "ip6.arpa."));
				}
			}
		}
	}

	/*



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
	*/

}
