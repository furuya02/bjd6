package bjd.plugins.dns;

public final class RrQuery extends OneRr {

	public RrQuery(String name, DnsType dnsType) {
		super(name, dnsType, 0, new byte[0]);
	}
	
	@Override
	public String toString() {
		return String.format("Query %s %s", getDnsType(), getName());
	}
	
}