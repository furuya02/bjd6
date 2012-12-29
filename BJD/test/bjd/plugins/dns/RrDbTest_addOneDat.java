package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Test;

import bjd.option.OneDat;

public final class RrDbTest_addOneDat {
	private boolean[] isSecret = new boolean[] { false, false, false, false, false };
	private String domainName = "aaa.com.";

	@Test
	public void Aレコードを読み込んだ時_A及びPTRが保存される() throws Exception {

		//setUp
		RrDb sut = new RrDb();
		OneDat oneDat = new OneDat(true, new String[] { "0", "www", "alias", "192.168.0.1", "10" }, isSecret);
		//exercise
		RrDbTest.addOneDat(sut, domainName, oneDat);
		//sut.addOneDat(domainName, oneDat);

		//verify count
		assertThat(RrDbTest.size(sut), is(2)); //A,PTR

		//verify A
		RrA a = (RrA) RrDbTest.get(sut, 0);
		assertThat(a.getDnsType(), is(DnsType.A));
		assertThat(a.getName(), is("www.aaa.com."));
		assertThat(a.getIp().toString(), is("192.168.0.1"));

		//verify PTR
		RrPtr p = (RrPtr) RrDbTest.get(sut, 1);
		assertThat(p.getDnsType(), is(DnsType.Ptr));
		assertThat(p.getName(), is("www.aaa.com."));
		assertThat(p.getPtr(), is("1.0.168.192.in-addr.arpa."));

	}

	@Test
	public void AAAAレコードを読み込んだ時_AAAA及びPTRが保存される() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		OneDat oneDat = new OneDat(true, new String[] { "4", "www", "alias", "fe80::f509:c5be:437b:3bc5", "10" }, isSecret);
		//exercise
		RrDbTest.addOneDat(sut, domainName, oneDat);

		//verify count
		assertThat(RrDbTest.size(sut), is(2)); //AAAA,PTR

		//verify AAAA
		RrAaaa a = (RrAaaa) RrDbTest.get(sut, 0);
		assertThat(a.getDnsType(), is(DnsType.Aaaa));
		assertThat(a.getName(), is("www.aaa.com."));
		assertThat(a.getIp().toString(), is("fe80::f509:c5be:437b:3bc5"));

		//verify PTR
		RrPtr p = (RrPtr) RrDbTest.get(sut, 1);
		assertThat(p.getDnsType(), is(DnsType.Ptr));
		assertThat(p.getName(), is("www.aaa.com."));
		assertThat(p.getPtr(), is("5.c.b.3.b.7.3.4.e.b.5.c.9.0.5.f.0.0.0.0.0.0.0.0.0.0.0.0.0.8.e.f.ip6.arpa."));

	}

	@Test
	public void MXレコードを読み込んだ時_MX_A及びPTRが保存される() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		OneDat oneDat = new OneDat(true, new String[] { "2", "smtp", "alias", "210.10.2.250", "15" }, isSecret);
		//exercise
		RrDbTest.addOneDat(sut, domainName, oneDat);

		//verify count
		assertThat(RrDbTest.size(sut), is(3)); //MX,A

		//verify MX
		RrMx m = (RrMx) RrDbTest.get(sut, 0);
		assertThat(m.getDnsType(), is(DnsType.Mx));
		assertThat(m.getName(), is("aaa.com."));
		assertThat(m.getPreference(), is((short) 15));
		assertThat(m.getMailExchangeHost(), is("smtp.aaa.com."));

		//verify A
		RrA a = (RrA) RrDbTest.get(sut, 1);
		assertThat(a.getDnsType(), is(DnsType.A));
		assertThat(a.getName(), is("smtp.aaa.com."));
		assertThat(a.getIp().toString(), is("210.10.2.250"));

		//verify PTR
		RrPtr p = (RrPtr) RrDbTest.get(sut, 2);
		assertThat(p.getDnsType(), is(DnsType.Ptr));
		assertThat(p.getName(), is("smtp.aaa.com."));
		assertThat(p.getPtr(), is("250.2.10.210.in-addr.arpa."));
	}

	@Test
	public void NSレコードを読み込んだ時_NS_A及びPTRが保存される() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		OneDat oneDat = new OneDat(true, new String[] { "1", "ns", "alias", "111.3.255.0", "0" }, isSecret);
		//exercise
		RrDbTest.addOneDat(sut, domainName, oneDat);

		//verify count
		assertThat(RrDbTest.size(sut), is(3)); //Ns,A,Ptr

		//verify NS
		RrNs n = (RrNs) RrDbTest.get(sut, 0);
		assertThat(n.getDnsType(), is(DnsType.Ns));
		assertThat(n.getName(), is("aaa.com."));
		assertThat(n.getNsName(), is("ns.aaa.com."));

		//verify A
		RrA a = (RrA) RrDbTest.get(sut, 1);
		assertThat(a.getDnsType(), is(DnsType.A));
		assertThat(a.getName(), is("ns.aaa.com."));
		assertThat(a.getIp().toString(), is("111.3.255.0"));

		//verify PTR
		RrPtr p = (RrPtr) RrDbTest.get(sut, 2);
		assertThat(p.getDnsType(), is(DnsType.Ptr));
		assertThat(p.getName(), is("ns.aaa.com."));
		assertThat(p.getPtr(), is("0.255.3.111.in-addr.arpa."));
	}

	@Test
	public void CNAMEレコードを読み込んだ時_CNAMEが保存される() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		OneDat oneDat = new OneDat(true, new String[] { "3", "cname", "alias", "255.254.253.252", "0" }, isSecret);
		//exercise
		RrDbTest.addOneDat(sut, domainName, oneDat);

		//verify count
		assertThat(RrDbTest.size(sut), is(1)); //Cname

		//verify CNAME
		RrCname c = (RrCname) RrDbTest.get(sut, 0);
		assertThat(c.getDnsType(), is(DnsType.Cname));
		assertThat(c.getName(), is("alias.aaa.com."));
		assertThat(c.getCName(), is("cname.aaa.com."));

	}
	
	@Test(expected = Exception.class)
	public void enable_falseのデータを追加すると例外が発生する() throws Exception {
		//実際に発生するのはValidObjExceptionだが、privateメソッドの制約のためExceptionの発生をテストする
		
		//setUp
		RrDb sut = new RrDb();
		OneDat oneDat = new OneDat(false, new String[] { "0", "www", "alias", "192.168.0.1", "10" }, isSecret);
		//exercise
		RrDbTest.addOneDat(sut, domainName, oneDat);

		//verify
		Assert.fail("ここが実行されたらテスト失敗");
	}
	
	@Test(expected = Exception.class)
	public void 無効なAレコードを読み込むと例外が発生する() throws Exception {
		//実際に発生するのはValidObjExceptionだが、privateメソッドの制約のためExceptionの発生をテストする

		//setUp
		RrDb sut = new RrDb();
		//IPv6のAレコード
		OneDat oneDat = new OneDat(true, new String[] { "0", "www", "alias", "::1", "0" }, isSecret);
		//exercise
		RrDbTest.addOneDat(sut, domainName, oneDat);
	
		//verify
		Assert.fail("ここが実行されたらテスト失敗");

	}

	@Test(expected = Exception.class)
	public void 無効なAAAAレコードを読み込むと例外が発生する() throws Exception {
		//実際に発生するのはValidObjExceptionだが、privateメソッドの制約のためExceptionの発生をテストする

		//setUp
		RrDb sut = new RrDb();
		//IPv4のAAAAレコード
		OneDat oneDat = new OneDat(true, new String[] { "4", "www", "alias", "127.0.0.1", "0" }, isSecret);
		//exercise
		RrDbTest.addOneDat(sut, domainName, oneDat);
	
		//verify
		Assert.fail("ここが実行されたらテスト失敗");

	}
	
	@Test(expected = Exception.class)
	public void 無効なタイプのレコードを読み込むと例外が発生する() throws Exception {
		//実際に発生するのはValidObjExceptionだが、privateメソッドの制約のためExceptionの発生をテストする

		//setUp
		RrDb sut = new RrDb();
		//タイプは0~4まで
		OneDat oneDat = new OneDat(true, new String[] { "5", "www", "alias", "127.0.0.1", "0" }, isSecret);
		//exercise
		RrDbTest.addOneDat(sut, domainName, oneDat);
	
		//verify
		Assert.fail("ここが実行されたらテスト失敗");

	}

}
