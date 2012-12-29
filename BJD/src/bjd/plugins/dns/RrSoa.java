package bjd.plugins.dns;

import bjd.packet.Conv;
import bjd.util.Bytes;

public final class RrSoa extends OneRr {

	public RrSoa(String name, int ttl, String n1, String n2, int serial, int refresh, int retry, int expire, int minimum) {
		super(name, DnsType.Soa, ttl, Bytes.create(DnsUtil.str2DnsName(n1), DnsUtil.str2DnsName(n2), Conv.getBytes(serial), Conv.getBytes(refresh), Conv.getBytes(retry), Conv.getBytes(expire),
				Conv.getBytes(minimum)));
	}

	public RrSoa(String name, int ttl, byte[] data) {
		super(name, DnsType.Soa, ttl, data);
	}

	public String getNameServer() {
		return DnsUtil.dnsName2Str(getData());
	}

	public String getPostMaster() {
		return DnsUtil.dnsName2Str(getData(getNameServer().length() + 1));
	}

	int getInt(int offset) {
		int p = getNameServer().length() + getPostMaster().length() + 2;
		return Conv.getInt(getData(), p + offset);
	}

	public int getSerial() {
		return getInt(0);
	}

	public int getRefresh() {
		return getInt(4);
	}

	public int getRetry() {
		return getInt(8);
	}

	public int getExpire() {
		return getInt(12);
	}

	public int getMinimum() {
		return getInt(16);
	}

	@Override
	public String toString() {
		return String
				.format("%s %s TTL=%d %s %s %08x %08x %08x %08x %08x", getDnsType(), getName(), getTtl(), getNameServer(), getPostMaster(), getSerial(), getRefresh(), getRetry(), getExpire(),
						getMinimum());
	}

}
