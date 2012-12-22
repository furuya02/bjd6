package bjd.plugins.dns;

import bjd.packet.Conv;

public final class RrNs extends OneRR {

	public RrNs(String name, int ttl, String nsName) {
		//super(name, DnsType.A, ttl, Conv.getBytes(ip.getAddrV4()));
		super(name, DnsType.Ns, ttl, DnsUtil.str2DnsName(nsName));
	}

	public String getNsName() {
		return DnsUtil.dnsName2Str(this.getData());
	}
}
