package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Test;

import bjd.option.OneDat;
import bjd.util.Util;

public final class RrDbTest_addOneDat {
	private boolean[] isSecret = new boolean[] { false, false, false, false, false };
	private String domainName = "aaa.com.";

	/**
	 * 共通メソッド
	 * リソースレコードのtoString()
	 * @param o
	 * @return
	 */
	private String print(OneRr o) {
		switch (o.getDnsType()) {
			case A:
				return ((RrA) o).toString();
			case Aaaa:
				return ((RrAaaa) o).toString();
			case Ns:
				return ((RrNs) o).toString();
			case Mx:
				return ((RrMx) o).toString();
			case Ptr:
				return ((RrPtr) o).toString();
			case Soa:
				return ((RrSoa) o).toString();
			case Cname:
				return ((RrCname) o).toString();
			default:
				Util.runtimeException("not implement.");
				break;
		}
		return "";
	}

	@Test
	public void Aレコードを読み込んだ時_A及びPTRが保存される() throws Exception {

		//setUp
		RrDb sut = new RrDb();
		OneDat oneDat = new OneDat(true, new String[] { "0", "www", "alias", "192.168.0.1", "10" }, isSecret);
		//exercise
		RrDbTest.addOneDat(sut, domainName, oneDat);

		//verify
		assertThat(RrDbTest.size(sut), is(2)); //A,PTR
		assertThat(print(RrDbTest.get(sut, 0)), is("A www.aaa.com. TTL=0 192.168.0.1"));
		assertThat(print(RrDbTest.get(sut, 1)), is("Ptr 1.0.168.192.in-addr.arpa. TTL=0 www.aaa.com."));

	}

	@Test
	public void AAAAレコードを読み込んだ時_AAAA及びPTRが保存される() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		OneDat oneDat = new OneDat(true, new String[] { "4", "www", "alias", "fe80::f509:c5be:437b:3bc5", "10" }, isSecret);
		//exercise
		RrDbTest.addOneDat(sut, domainName, oneDat);

		//verify
		assertThat(RrDbTest.size(sut), is(2)); //AAAA,PTR
		assertThat(print(RrDbTest.get(sut, 0)), is("Aaaa www.aaa.com. TTL=0 fe80::f509:c5be:437b:3bc5"));
		assertThat(print(RrDbTest.get(sut, 1)), is("Ptr 5.c.b.3.b.7.3.4.e.b.5.c.9.0.5.f.0.0.0.0.0.0.0.0.0.0.0.0.0.8.e.f.ip6.arpa. TTL=0 www.aaa.com."));
	}

	@Test
	public void MXレコードを読み込んだ時_MX_A及びPTRが保存される() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		OneDat oneDat = new OneDat(true, new String[] { "2", "smtp", "alias", "210.10.2.250", "15" }, isSecret);
		//exercise
		RrDbTest.addOneDat(sut, domainName, oneDat);

		//verify
		assertThat(RrDbTest.size(sut), is(3)); //MX,A,PTR
		assertThat(print(RrDbTest.get(sut, 0)), is("Mx aaa.com. TTL=0 15 smtp.aaa.com."));
		assertThat(print(RrDbTest.get(sut, 1)), is("A smtp.aaa.com. TTL=0 210.10.2.250"));
		assertThat(print(RrDbTest.get(sut, 2)), is("Ptr 250.2.10.210.in-addr.arpa. TTL=0 smtp.aaa.com."));
	}

	@Test
	public void NSレコードを読み込んだ時_NS_A及びPTRが保存される() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		OneDat oneDat = new OneDat(true, new String[] { "1", "ns", "alias", "111.3.255.0", "0" }, isSecret);
		//exercise
		RrDbTest.addOneDat(sut, domainName, oneDat);

		//verify count
		assertThat(RrDbTest.size(sut), is(3)); //NS,A,PTR
		assertThat(print(RrDbTest.get(sut, 0)), is("Ns aaa.com. TTL=0 ns.aaa.com."));
		assertThat(print(RrDbTest.get(sut, 1)), is("A ns.aaa.com. TTL=0 111.3.255.0"));
		assertThat(print(RrDbTest.get(sut, 2)), is("Ptr 0.255.3.111.in-addr.arpa. TTL=0 ns.aaa.com."));
	}

	@Test
	public void CNAMEレコードを読み込んだ時_CNAMEが保存される() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		OneDat oneDat = new OneDat(true, new String[] { "3", "cname", "alias", "255.254.253.252", "0" }, isSecret);
		//exercise
		RrDbTest.addOneDat(sut, domainName, oneDat);

		//verify
		assertThat(RrDbTest.size(sut), is(1)); //Cname
		assertThat(print(RrDbTest.get(sut, 0)), is("Cname alias.aaa.com. TTL=0 cname.aaa.com."));
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
