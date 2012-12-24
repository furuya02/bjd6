package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Test;

import bjd.option.OneDat;

public final class ReadOneDatTest {
	private boolean[] isSecret = new boolean[] { false, false, false, false, false };

	@Test
	public void Aレコードを読み込んだ時_A及びPTRが保存される() throws Exception {
		//setUp
		String domainName = "aaa.com";
		ArrayList<OneRr> ar = new ArrayList<>();
		OneDat oneDat = new OneDat(true, new String[] { "0", "www", "alias", "192.168.0.1", "10" }, isSecret);
		//exercise
		DnsUtil.readOneDat(domainName, oneDat, ar);

		//verify count
		assertThat(ar.size(), is(2)); //A,PTR

		//verify A
		RrA a = (RrA) ar.get(0);
		assertThat(a.getDnsType(), is(DnsType.A));
		assertThat(a.getName(), is("www.aaa.com"));
		assertThat(a.getIp().toString(), is("192.168.0.1"));

		//verify PTR
		RrPtr p = (RrPtr) ar.get(1);
		assertThat(p.getDnsType(), is(DnsType.Ptr));
		assertThat(p.getName(), is("www.aaa.com"));
		assertThat(p.getPtr(), is("1.0.168.192.in-addr.arpa."));

	}

	@Test
	public void AAAAレコードを読み込んだ時_AAAA及びPTRが保存される() throws Exception {
		//setUp
		String domainName = "aaa.com";
		ArrayList<OneRr> ar = new ArrayList<>();
		OneDat oneDat = new OneDat(true, new String[] { "4", "www", "alias", "fe80::f509:c5be:437b:3bc5", "10" }, isSecret);
		//exercise
		DnsUtil.readOneDat(domainName, oneDat, ar);

		//verify count
		assertThat(ar.size(), is(2)); //AAAA,PTR

		//verify AAAA
		RrAaaa a = (RrAaaa) ar.get(0);
		assertThat(a.getDnsType(), is(DnsType.Aaaa));
		assertThat(a.getName(), is("www.aaa.com"));
		assertThat(a.getIp().toString(), is("fe80::f509:c5be:437b:3bc5"));

		//verify PTR
		RrPtr p = (RrPtr) ar.get(1);
		assertThat(p.getDnsType(), is(DnsType.Ptr));
		assertThat(p.getName(), is("www.aaa.com"));
		assertThat(p.getPtr(), is("5.c.b.3.b.7.3.4.e.b.5.c.9.0.5.f.0.0.0.0.0.0.0.0.0.0.0.0.0.8.e.f.ip6.arpa."));

	}

	@Test
	public void MXレコードを読み込んだ時_AAAA及びPTRが保存される() throws Exception {
		Assert.fail("未実装");
	}

	@Test
	public void NSレコードを読み込んだ時_AAAA及びPTRが保存される() throws Exception {
		Assert.fail("未実装");
	}

	@Test
	public void CNAMEレコードを読み込んだ時_AAAA及びPTRが保存される() throws Exception {
		Assert.fail("未実装");
	}

}
