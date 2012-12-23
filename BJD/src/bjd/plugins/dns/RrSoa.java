package bjd.plugins.dns;

import bjd.packet.Conv;
import bjd.util.Bytes;

public final class RrSoa extends OneRr {

	public RrSoa(String name, int ttl, String n1, String n2, int serial, int refresh, int retry, int expire, int minimum) {
		super(name, DnsType.Soa, ttl, Bytes.create(DnsUtil.str2DnsName(n1), DnsUtil.str2DnsName(n2), Conv.getBytes(serial), Conv.getBytes(refresh), Conv.getBytes(retry), Conv.getBytes(expire),
				Conv.getBytes(minimum)));
	}

//	public short getPreference() {
//		return Conv.getShort(this.getData(0, 2));
//	}
//
//	public String getMailExchangeHost() {
//		return DnsUtil.dnsName2Str(this.getData(2));
//	}

	@Override
	public String toString() {
		return String.format("%s %s TTL=%d", getDnsType(), getName(), getTtl());
	}

}
