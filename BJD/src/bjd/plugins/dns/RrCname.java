package bjd.plugins.dns;

public final class RrCname extends OneRr {

	public RrCname(String name, int ttl, String cname) {
		super(name, DnsType.Cname, ttl, DnsUtil.str2DnsName(cname));
	}

	public RrCname(String name, int ttl, byte[] data) {
		super(name, DnsType.Cname, ttl, data);
	}

	public String getCName() {
		return DnsUtil.dnsName2Str(this.getData());
	}

	@Override
	public String toString() {
		return String.format("%s %s TTL=%d %s", getDnsType(), getName(), getTtl(), getCName());
	}
	
}
