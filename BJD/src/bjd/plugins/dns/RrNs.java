package bjd.plugins.dns;

public final class RrNs extends OneRr {

	public RrNs(String name, int ttl, String nsName) {
		super(name, DnsType.Ns, ttl, DnsUtil.str2DnsName(nsName));
	}

	public RrNs(String name, int ttl, byte[] data) {
		super(name, DnsType.Ns, ttl, data);
	}

	public String getNsName() {
		return DnsUtil.dnsName2Str(this.getData());
	}

	@Override
	public String toString() {
		return String.format("%s %s TTL=%d %s", getDnsType(), getName(), getTtl(), getNsName());
	}
	
}
