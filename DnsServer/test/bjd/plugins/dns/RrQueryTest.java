package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

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

	@Test
	public void toStringの確認() throws Exception {
		//setUp
		String expected = "Query A aaa.com";
		RrQuery sut = new RrQuery("aaa.com", DnsType.A);
		//exercise
		String actual = sut.toString();
		//verify
		assertThat(actual, is(expected));
	}

}
