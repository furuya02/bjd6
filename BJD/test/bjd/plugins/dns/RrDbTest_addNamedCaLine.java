package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Method;

import org.junit.Test;

import bjd.option.OneDat;

public final class RrDbTest_addNamedCaLine {

	//リフレクションを使用してプライベートメソッドにアクセスする RrDb.addNamedCaLine(String tmpName, String str)
	String addNamedCaLine(RrDb sut, String tmpName, String str) throws Exception {
		Class<RrDb> c = RrDb.class;
		Method m = c.getDeclaredMethod("addNamedCaLine", new Class[] { String.class, String.class });
		m.setAccessible(true);
		return (String) m.invoke(sut, tmpName, str);
	}

	//リフレクションを使用してプライベートメソッドにアクセスする RrDb.get(int)
	OneRr get(RrDb sut, int index) throws Exception {
		Class<RrDb> c = RrDb.class;
		Method m = c.getDeclaredMethod("get", new Class[] { int.class });
		m.setAccessible(true);
		return (OneRr) m.invoke(sut, index);
	}

	//リフレクションを使用してプライベートメソッドにアクセスする RrDb.size()
	int size(RrDb sut) throws Exception {
		Class<RrDb> c = RrDb.class;
		Method m = c.getDeclaredMethod("size");
		m.setAccessible(true);
		return (int) m.invoke(sut);
	}

	@Test
	public void コメント行は処理されない() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		String tmpName = "";
		//exercise
		addNamedCaLine(sut, tmpName, "; formerly NS.INTERNIC.NET");
		//verify
		assertThat(size(sut), is(0));
	}

	@Test
	public void 空白行は処理されない() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		String tmpName = "";
		//exercise
		addNamedCaLine(sut, tmpName, "");
		//verify
		assertThat(size(sut), is(0));
	}

	@Test
	public void Aレコードの処理() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		String tmpName = "";
		//exercise

		String retName = addNamedCaLine(sut, tmpName, "A.ROOT-SERVERS.NET.      3600000      A     198.41.0.4");
		//verify
		RrA o = (RrA) get(sut, 0);
		assertThat(retName, is("A.ROOT-SERVERS.NET."));
		assertThat(size(sut), is(1));
		assertThat(o.getDnsType(), is(DnsType.A));
		assertThat(o.getIp().toString(), is("198.41.0.4"));
		assertThat(o.getTtl(), is(3600000));
		assertThat(o.getName(), is("A.ROOT-SERVERS.NET."));
	}

	@Test
	public void AAAAレコードの処理() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		String tmpName = "";
		//exercise

		String retName = addNamedCaLine(sut, tmpName, "A.ROOT-SERVERS.NET.      3600000      AAAA  2001:503:BA3E::2:30");
		//verify
		RrAaaa o = (RrAaaa) get(sut, 0);
		assertThat(retName, is("A.ROOT-SERVERS.NET."));
		assertThat(size(sut), is(1));
		assertThat(o.getDnsType(), is(DnsType.Aaaa));
		assertThat(o.getIp().toString(), is("2001:503:ba3e::2:30"));
		assertThat(o.getTtl(), is(3600000));
		assertThat(o.getName(), is("A.ROOT-SERVERS.NET."));
	}

	@Test
	public void NSレコードの処理() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		String tmpName = "";
		//exercise

		String retName = addNamedCaLine(sut, tmpName, ".                        3600000  IN  NS    A.ROOT-SERVERS.NET.");
		//verify
		RrNs o = (RrNs) get(sut, 0);
		assertThat(retName, is("."));
		assertThat(size(sut), is(1));
		assertThat(o.getDnsType(), is(DnsType.Ns));
		assertThat(o.getNsName(), is("A.ROOT-SERVERS.NET."));
		assertThat(o.getTtl(), is(3600000));
		assertThat(o.getName(), is("."));
	}

	@Test(expected = Exception.class)
	public void DnsTypeが無い場合例外が発生する() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		addNamedCaLine(sut, "", ".                        3600000  IN      A.ROOT-SERVERS.NET.");
	}

	@Test(expected = Exception.class)
	public void DnsTypeの次のカラムのDataが無い場合例外が発生する() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		addNamedCaLine(sut, "", ".                        3600000  IN  NS");
	}

	@Test(expected = Exception.class)
	public void Aタイプでアドレスに矛盾があると例外が発生する() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		addNamedCaLine(sut, "", "A.ROOT-SERVERS.NET.      3600000      A     ::1");
	}

	@Test(expected = Exception.class)
	public void AAAAタイプでアドレスに矛盾があると例外が発生する() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		addNamedCaLine(sut, "", "A.ROOT-SERVERS.NET.      3600000      AAAA     192.168.0.1");
	}

	@Test(expected = Exception.class)
	public void A_AAAA_NS以外タイプは例外が発生する() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		addNamedCaLine(sut, "", ".                        3600000  IN  MX    A.ROOT-SERVERS.NET.");
	}

	@Test(expected = Exception.class)
	public void Aタイプで不正なアドレスを指定すると例外が発生する() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		addNamedCaLine(sut, "", "A.ROOT-SERVERS.NET.      3600000      A     1.1.1.1.1");
	}

	@Test(expected = Exception.class)
	public void AAAAタイプで不正なアドレスを指定すると例外が発生する() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		addNamedCaLine(sut, "", "A.ROOT-SERVERS.NET.      3600000      AAAA     xxx");
	}
	
	@Test
	public void 名前補完_アットマークの場合ドメイン名になる() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise

		String retName = addNamedCaLine(sut, "", "@      3600000      A     198.41.0.4");
		//verify
		RrA o = (RrA) get(sut, 0);
		assertThat(retName, is("example.com."));
	}

	@Test
	public void 名前補完_最後にドットが無い場合_ドメイン名が補完される() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise

		String retName = addNamedCaLine(sut, "", "www      3600000      A     198.41.0.4");
		//verify
		RrA o = (RrA) get(sut, 0);
		assertThat(retName, is("www.example.com."));
	}

	@Test
	public void 名前補完_指定されない場合_前行と同じになる() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise

		String retName = addNamedCaLine(sut, "before.aaa.com.", "     3600000      A     198.41.0.4");
		//verify
		RrA o = (RrA) get(sut, 0);
		assertThat(retName, is("before.aaa.com."));
	}
}