package bjd.plugins.dns;

import bjd.packet.Conv;
import bjd.util.Bytes;

public final class RrMx extends OneRr {

	public RrMx(String name, int ttl, short preference, String mailExchangerHost) {
		super(name, DnsType.Mx, ttl, Bytes.create(Conv.getBytes(preference), DnsUtil.str2DnsName(mailExchangerHost)));
	}

	public RrMx(String name, int ttl, byte[] data) {
		super(name, DnsType.Mx, ttl, data);
	}

	public short getPreference() {
		return Conv.getShort(this.getData(0, 2));
	}

	public String getMailExchangeHost() {
		return DnsUtil.dnsName2Str(this.getData(2));
	}

	@Override
	public String toString() {
		return String.format("%s %s TTL=%d %d %s", getDnsType(), getName(), getTtl(), getPreference(), getMailExchangeHost());
	}
}
