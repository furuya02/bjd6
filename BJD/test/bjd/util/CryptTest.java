package bjd.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.mail.MessagingException;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class CryptTest {

	@RunWith(Theories.class)
	public static final class A001 {

		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("encrypt及びdecrypt");
		}

		@DataPoints
		public static Fixture[] datas = {
				// 入力文字列
				new Fixture("本日は晴天なり"),
				new Fixture("123"),
				new Fixture("xxxx"),
				new Fixture("1\r\n2"), 
			};

		static class Fixture {
			private String actual;

			public Fixture(String actual) {
				this.actual = actual;
			}
		}

		@Theory
		public void test(Fixture fx) throws MessagingException, IOException {
			TestUtil.dispPrompt(this);

			String s = "";
			String expected = "";
			try {
				s = Crypt.encrypt(fx.actual);
				expected = Crypt.decrypt(s);
			} catch (Exception e) {
				Assert.fail();
			}
			System.out.printf("encrypt(%s)=%s  decrypt(%s)=%s\n", fx.actual, s, s, expected);
			assertThat(expected, is(not("ERROR"))); //ERRORが出力された場合は、テスト失敗
			assertThat(expected, is(expected));
		}
	}

	@RunWith(Theories.class)
	public static final class A002 {

		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("encryptの例外発生");
		}

		@DataPoints
		public static Fixture[] datas = {
			// 入力文字列
			new Fixture(null), 
		};

		static class Fixture {
			private String actual;

			public Fixture(String actual) {
				this.actual = actual;
			}
		}

		@Theory
		public void test(Fixture fx) throws MessagingException, IOException {
			TestUtil.dispPrompt(this);
			try {
				Crypt.encrypt(fx.actual);
				Assert.fail("この行が実行されたらエラー");
			} catch (Exception e) {
				//ここへ来ればテスト成功
				System.out.printf("encrypt(%s) => %s\n", fx.actual, e.getClass());
				return;
			}
			Assert.fail("この行が実行されたらエラー");
		}
	}

	@RunWith(Theories.class)
	public static final class A003 {

		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("decryptのエラー発生");
		}

		@DataPoints
		public static Fixture[] datas = {
				// 入力文字列
				new Fixture(null),
				new Fixture("123"), 
				new Fixture("本日は晴天なり"), 
		};

		static class Fixture {
			private String actual;

			public Fixture(String actual) {
				this.actual = actual;
			}
		}

		@Theory
		public void test(Fixture fx) throws MessagingException, IOException {
			TestUtil.dispPrompt(this);

			try {
				Crypt.decrypt(fx.actual);
				Assert.fail("この行が実行されたらエラー");
			} catch (Exception e) {
				System.out.printf("encrypt(%s) => %s\n", fx.actual, e.getClass());
				return;
			}
			Assert.fail("この行が実行されたらエラー");
		}
	}

}