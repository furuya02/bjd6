package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import bjd.util.Util;

public final class RrDbTest_addNamedCaLine {

	int expire = 2400;
	
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
	public void コメント行は処理されない() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		int expected = 0;
		RrDbTest.addNamedCaLine(sut, "", "; formerly NS.INTERNIC.NET");
		int actual = RrDbTest.size(sut);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 空白行は処理されない() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		int expected = 0;
		RrDbTest.addNamedCaLine(sut, "", "");
		int actual = RrDbTest.size(sut);
		//verify
		assertThat(actual, is(expected));
	}

	
	@Test
	public void Aレコードの処理() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		String retName = RrDbTest.addNamedCaLine(sut, "", "A.ROOT-SERVERS.NET.      3600000      A     198.41.0.4");
		//verify
		assertThat(retName, is("A.ROOT-SERVERS.NET."));
		assertThat(RrDbTest.size(sut), is(1)); //A
		assertThat(print(RrDbTest.get(sut, 0)), is("A A.ROOT-SERVERS.NET. TTL=0 198.41.0.4")); //TTLは強制的に0になる
	}

	@Test
	public void AAAAレコードの処理() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		String retName = RrDbTest.addNamedCaLine(sut, "", "A.ROOT-SERVERS.NET.      3600000      AAAA  2001:503:BA3E::2:30");
		//verify
		assertThat(retName, is("A.ROOT-SERVERS.NET."));
		assertThat(RrDbTest.size(sut), is(1)); //Aaaa
		assertThat(print(RrDbTest.get(sut, 0)), is("Aaaa A.ROOT-SERVERS.NET. TTL=0 2001:503:ba3e::2:30")); //TTLは強制的に0になる
	}

	@Test
	public void NSレコードの処理() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		String retName = RrDbTest.addNamedCaLine(sut, "", ".                        3600000  IN  NS    A.ROOT-SERVERS.NET.");
		//verify
		assertThat(retName, is("."));
		assertThat(RrDbTest.size(sut), is(1)); //Ns
		assertThat(print(RrDbTest.get(sut, 0)), is("Ns . TTL=0 A.ROOT-SERVERS.NET.")); //TTLは強制的に0になる
	}

	@Test(expected = Exception.class)
	public void DnsTypeが無い場合例外が発生する() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		RrDbTest.addNamedCaLine(sut, "", ".                        3600000  IN      A.ROOT-SERVERS.NET.");
	}

	@Test(expected = Exception.class)
	public void DnsTypeの次のカラムのDataが無い場合例外が発生する() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		RrDbTest.addNamedCaLine(sut, "", ".                        3600000  IN  NS");
	}

	@Test(expected = Exception.class)
	public void Aタイプでアドレスに矛盾があると例外が発生する() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		RrDbTest.addNamedCaLine(sut, "", "A.ROOT-SERVERS.NET.      3600000      A     ::1");
	}

	@Test(expected = Exception.class)
	public void AAAAタイプでアドレスに矛盾があると例外が発生する() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		RrDbTest.addNamedCaLine(sut, "", "A.ROOT-SERVERS.NET.      3600000      AAAA     192.168.0.1");
	}

	@Test(expected = Exception.class)
	public void A_AAAA_NS以外タイプは例外が発生する() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		RrDbTest.addNamedCaLine(sut, "", ".                        3600000  IN  MX    A.ROOT-SERVERS.NET.");
	}

	@Test(expected = Exception.class)
	public void Aタイプで不正なアドレスを指定すると例外が発生する() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		RrDbTest.addNamedCaLine(sut, "", "A.ROOT-SERVERS.NET.      3600000      A     1.1.1.1.1");
	}

	@Test(expected = Exception.class)
	public void AAAAタイプで不正なアドレスを指定すると例外が発生する() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		RrDbTest.addNamedCaLine(sut, "", "A.ROOT-SERVERS.NET.      3600000      AAAA     xxx");
	}
	
	@Test
	public void 名前補完_アットマークの場合ドメイン名になる() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		String expected = "example.com.";
		String actual = RrDbTest.addNamedCaLine(sut, "", "@      3600000      A     198.41.0.4");
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 名前補完_最後にドットが無い場合_ドメイン名が補完される() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		String expected = "www.example.com.";
		String actual = RrDbTest.addNamedCaLine(sut, "", "www      3600000      A     198.41.0.4");
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 名前補完_指定されない場合_前行と同じになる() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		String expected = "before.aaa.com.";
		String actual = RrDbTest.addNamedCaLine(sut, "before.aaa.com.", "     3600000      A     198.41.0.4");
		//verify
		assertThat(actual, is(expected));
	}
}
