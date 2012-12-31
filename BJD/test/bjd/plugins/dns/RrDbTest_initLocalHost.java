package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class RrDbTest_initLocalHost {
	
	@Test
	public void 件数は４件になる() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		int expected = 5;
		//exercise
		RrDbTest.initLocalHost(sut);
		int actual = RrDbTest.size(sut);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void リソース確認_1番目はAレコードとなる() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		RrDbTest.initLocalHost(sut);
		RrA o = (RrA) RrDbTest.get(sut, 0);
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
		RrDbTest.initLocalHost(sut);
		RrPtr o = (RrPtr) RrDbTest.get(sut, 1);
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
		RrDbTest.initLocalHost(sut);
		RrAaaa o = (RrAaaa) RrDbTest.get(sut, 2);
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
		RrDbTest.initLocalHost(sut);
		RrPtr o = (RrPtr) RrDbTest.get(sut, 3);
		//verify
		assertThat(o.getDnsType(), is(DnsType.Ptr));
		assertThat(o.getName(), is("1.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.IP6.ARPA."));
		assertThat(o.getPtr(), is("localhost."));
	}

	@Test
	public void リソース確認_5番目はNSレコードとなる() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		RrDbTest.initLocalHost(sut);
		RrNs o = (RrNs) RrDbTest.get(sut, 4);
		//verify
		assertThat(o.getDnsType(), is(DnsType.Ns));
		assertThat(o.getName(), is("localhost."));
		assertThat(o.getNsName(), is("localhost."));
	}


}
