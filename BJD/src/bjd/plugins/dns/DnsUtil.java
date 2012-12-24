package bjd.plugins.dns;

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

}
