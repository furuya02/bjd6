package bjd.plugins.dns;

import bjd.net.Ip;
import bjd.packet.Conv;

public final class RrAaaa extends OneRR {

	public RrAaaa(String name, int ttl, Ip ip) {
		super(name, DnsType.Aaaa, ttl, ip.getIpV6());
	}

	public Ip getIp() {
		byte[] buf = this.getData();

		long v6h = Conv.getLong(buf, 0);
		long v6l = Conv.getLong(buf, 8);
		return new Ip(v6h, v6l);
	}
}
