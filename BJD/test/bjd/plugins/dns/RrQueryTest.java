package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import bjd.net.Ip;
import bjd.test.TestUtil;

public final class RrQueryTest {

	@Test
	public void getDnsTypeの確認() throws Exception {
		//setUp
		DnsType expected = DnsType.A;
		RrQuery sut = new RrQuery("aaa.com", expected);
		//exercise
		DnsType actual = sut.getDnsType();
		//verify
		assertThat(actual, is(expected));
	}

}
