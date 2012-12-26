package bjd.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;

import javax.mail.MessagingException;

import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import bjd.test.TestUtil;

@RunWith(Enclosed.class)
public final class Base64Test {

	@RunWith(Theories.class) 
	public static final class Base64のエンコード及びデコード {

		@DataPoints
		public static Fixture[] datas = {
			// 入力文字列,出力文字列
			new Fixture("本日は晴天なり", "本日は晴天なり"),
			new Fixture("123", "123"),
			new Fixture("", ""),
			new Fixture(null, ""),
			new Fixture("1\r\n2", "1\r\n2"),
		};

		static class Fixture {
			private String actual;
			private String expected;

			public Fixture(String actual, String expected) {
				this.actual = actual;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) throws MessagingException, IOException {
			
			String s = Base64.encode(fx.actual);
			String expected = Base64.decode(s);
			TestUtil.prompt(String.format("encode(%s)=%s  decode(%s)=%s\n", fx.actual, s, s, expected));
			assertThat(expected, is(fx.expected));
		}
	}
}
