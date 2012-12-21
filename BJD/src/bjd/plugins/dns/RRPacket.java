package bjd.plugins.dns;

import java.io.IOException;

import bjd.packet.Packet;
import bjd.util.Util;

public final class RRPacket extends Packet {
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

	public RRPacket(byte[] data, int offset) {
		super(data, offset);
	}

	public DnsType getType() throws IOException {
		short d = getShort(pTYPE);
		return short2DnsType(d);
	}

	public short getCls() throws IOException {
		return getShort(pCLS);
	}

	public int getTtl() throws IOException {
		return getInt(pTTL);
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
	
	private  DnsType short2DnsType(short d) {
		switch (d) {
			case 0x0001:
				return DnsType.A;
			case 0x0002:
				return DnsType.Ns;
			case 0x0005:
				return DnsType.Cname;
			case 0x0006:
				return DnsType.Soa;
				//case 0x0700:
				//    return DnsType.Mb;
				//case 0x0800:
				//    return DnsType.Mg;
				//case 0x0900:
				//    return DnsType.Mr;
				//case 0x0a00:
				//    return DnsType.Null;
				//case 0x0b00:
				//    return DnsType.Wks;
			case 0x000c:
				return DnsType.Ptr;
				//case 0x0d00:
				//    return DnsType.Hinfo;
				//case 0x0e00:
				//    return DnsType.Minfo;
			case 0x000f:
				return DnsType.Mx;
				//case 0x1000:
				//    return DnsType.Txt;
			case 0x001c:
				return DnsType.Aaaa;
			default:
				Util.runtimeException("short2DnsType() unknown data");
				break;
		}
		return DnsType.Unknown;
	}
	

}
