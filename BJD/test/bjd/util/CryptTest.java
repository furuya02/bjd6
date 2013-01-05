package bjd.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import junit.framework.Assert;

import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class CryptTest {

	@RunWith(Theories.class)
	public static final class encrypt及びdecrypt {

		@DataPoints
		public static Fixture[] datas = {
				// 入力文字列
				new Fixture("本日は晴天なり"),
				new Fixture("123"),
				new Fixture("xxxx"),
				new Fixture("1\r\n2"), 
			};

		static class Fixture {
			private String str;

			public Fixture(String str) {
				this.str = str;
			}
		}

		@Theory
		public void test(Fixture fx) throws Exception {
			//setUp
			String expected = fx.str;
			//exercise
			String actual = Crypt.decrypt(Crypt.encrypt(fx.str));
			//verify
			assertThat(actual, is(expected));
		}
	}

	@RunWith(Theories.class)
	public static final class encryptの例外テスト {


		@DataPoints
		public static Fixture[] datas = {
			// 入力文字列
			new Fixture(null), 
		};

		static class Fixture {
			private String str;

			public Fixture(String str) {
				this.str = str;
			}
		}
		@Theory
		public void test(Fixture fx) {
			try {
				Crypt.encrypt(fx.str);
				Assert.fail("この行が実行されたらエラー");
			} catch (Exception ex) {
			}
		}
	}

	@RunWith(Theories.class)
	public static final class decryptの例外テスト {

		@DataPoints
		public static Fixture[] datas = {
				// 入力文字列
				new Fixture(null),
				new Fixture("123"), 
				new Fixture("本日は晴天なり"), 
		};

		static class Fixture {
			private String str;

			public Fixture(String str) {
				this.str = str;
			}
		}
		@Theory
		public void test(Fixture fx) {
			try {
				Crypt.decrypt(fx.str);
				Assert.fail("この行が実行されたらエラー");
			} catch (Exception ex) {
			}
		}
	}

}