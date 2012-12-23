package bjd.plugins.dns;

import bjd.net.Ip;
import bjd.packet.Conv;

public final class RrA extends OneRr {

	public RrA(String name, int ttl, Ip ip) {
		//super(name, DnsType.A, ttl, Conv.getBytes(ip.getAddrV4()));
		super(name, DnsType.A, ttl, ip.getIpV4());
	}

	public RrA(String name, int ttl, byte[] data) {
		super(name, DnsType.A, ttl, data);
	}

	public Ip getIp() {
		return new Ip(Conv.getInt(this.getData()));
	}
	
	@Override
	public String toString() {
		return String.format("%s %s TTL=%d %s", getDnsType(), getName(), getTtl(), getIp().toString());
	}
	
}
