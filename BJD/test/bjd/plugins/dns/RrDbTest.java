package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public final class RrDbTest {

	@Test
	public void getDomainNameの確認_namedcaで初期化された場合ルートになる() throws Exception {
		//setUp
		RrDb sut = new RrDb("name.ca");
		String expected = ".";
		//exercise
		String actual = sut.getDomainName();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getDomainNameの確認_Datで初期化された場合指定されたドメインになる() throws Exception {
		//setUp
		RrDb sut = new RrDb(null, null, null, "example.com");
		String expected = "example.com";
		//exercise
		String actual = sut.getDomainName();
		//verify
		assertThat(actual, is(expected));
	}

}
