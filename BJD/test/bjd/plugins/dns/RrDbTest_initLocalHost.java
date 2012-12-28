package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;

import bjd.net.Ip;

public class RrDbTest_initLocalHost {
	//リフレクションを使用してプライベートメソッドにアクセスする RrDb.initLocalHost()
	void initLocalHost(RrDb sut) throws Exception {
		Class<RrDb> c = RrDb.class;
		Method m = c.getDeclaredMethod("initLocalHost");
		m.setAccessible(true);
		m.invoke(sut);
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
	public void 件数は４件になる() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		int expected = 4;
		//exercise
		initLocalHost(sut);
		int actual = size(sut);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void リソース確認_1番目はAレコードとなる() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		initLocalHost(sut);
		RrA o = (RrA) get(sut, 0);
		//verify
		assertThat(o.getDnsType(), is(DnsType.A));
		assertThat(o.getName(), is("localhost."));
		assertThat(o.getIp().toString(), is("127.0.0.1"));
	}

	@Test
	public void リソース確認_2番目はPTRレコードとなる() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		initLocalHost(sut);
		RrPtr o = (RrPtr) get(sut, 1);
		//verify
		assertThat(o.getDnsType(), is(DnsType.Ptr));
		assertThat(o.getName(), is("1.0.0.127.in-addr.arpa."));
		assertThat(o.getPtr(), is("localhost."));
	}

	@Test
	public void リソース確認_3番目はAAAAレコードとなる() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		initLocalHost(sut);
		RrAaaa o = (RrAaaa) get(sut, 2);
		//verify
		//assertThat(o.getDnsType(), is(DnsType.Aaaa));
		//assertThat(o.getName(), is("localhost."));
		assertThat(o.getIp().toString(), is("::1"));
	}
	
	@Test
	public void リソース確認_4番目はPTRレコードとなる() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		initLocalHost(sut);
		RrPtr o = (RrPtr) get(sut, 3);
		//verify
		assertThat(o.getDnsType(), is(DnsType.Ptr));
		assertThat(o.getName(), is("1.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.IP6.ARPA."));
		assertThat(o.getPtr(), is("localhost."));
	}



}
