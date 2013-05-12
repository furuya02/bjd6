package bjd.plugins.dns;

public final class RrPtr extends OneRr {

	public RrPtr(String name, int ttl, String ptr) {
		super(name, DnsType.Ptr, ttl, DnsUtil.str2DnsName(ptr));
	}

	public RrPtr(String name, int ttl, byte[] data) {
		super(name, DnsType.Ptr, ttl, data);
	}

	public String getPtr() {
		return DnsUtil.dnsName2Str(this.getData());
	}

	@Override
	public String toString() {
		return String.format("%s %s TTL=%d %s", getDnsType(), getName(), getTtl(), getPtr());
	}
	
}
