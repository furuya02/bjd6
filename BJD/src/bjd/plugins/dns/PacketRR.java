package bjd.plugins.dns;

import java.io.IOException;

import bjd.packet.Packet;
import bjd.util.Util;

public final class PacketRR extends Packet {
	// Name 不定幅のため、このクラスでは取り扱わない
	// short type
	// short class
	// int ttl
	// byte DataLength
	// byte[] RData

	private final int pTYPE = 0;
	private final int pCLS = 2;
	private final int pTTL = 4;
	private final int pDLEN = 8;
	private final int pDATA = 10;

	// デフォルトコンストラクラの隠蔽
	private PacketRR() {
		super(new byte[0], 0);
	}

	/**
	 * パケットを生成する場合のコンストラクタ
	 * @param dlen
	 */
	public PacketRR(int dlen) {
		super(new byte[10 + dlen], 0);
	}

	/**
	 * パケットを解析するためのコンストラクタ
	 * @param data
	 * @param offset
	 */
	public PacketRR(byte[] data, int offset) {
		super(data, offset);
	}

	public DnsType getType() throws IOException {
		short d = getShort(pTYPE);
		return short2DnsType(d);
	}
	public void setType(DnsType val) throws IOException {
		short n = dnsType2Short(val);
		setShort(n, pTYPE);
	}

	public short getCls() throws IOException {
		return getShort(pCLS);
	}

	public void setCls(short val) throws IOException {
		setShort(val, pCLS);
	}

	public int getTtl() throws IOException {
		return getInt(pTTL);
	}

	public void setTtl(int val) throws IOException {
		setInt(val, pTTL);
	}

	public short getDLen() throws IOException {
		return getShort(pDLEN);
	}

	public byte[] getData() throws IOException {
		return getBytes(pDATA, getDLen());
	}

	@Override
	public int length() {
		try {
			int dataLen = getDLen();
			return 2 + 2 + 4 + dataLen + 1;
		} catch (IOException e) {
			Util.runtimeException(this, e);
		}
		return 0;
	}

	private DnsType short2DnsType(short d) {
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

	private static short dnsType2Short(DnsType dnsType) {
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

}
