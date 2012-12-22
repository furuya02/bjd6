package bjd.plugins.dns;

import bjd.net.Ip;
import bjd.packet.Conv;

public final class RrA extends OneRR {

	public RrA(String name, int ttl, Ip ip) {
		//super(name, DnsType.A, ttl, Conv.getBytes(ip.getAddrV4()));
		super(name, DnsType.A, ttl, ip.getIpV4());
	}

	public Ip getIp() {
		return new Ip(Conv.getInt(this.getData()));
	}
}
