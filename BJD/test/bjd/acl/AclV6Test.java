package bjd.acl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import bjd.ValidObjException;
import bjd.net.Ip;
import bjd.util.TestUtil;


@RunWith(Enclosed.class)
public class AclV6Test {
	@RunWith(Theories.class)
	public static final class A001 {
		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("Acl()で生成して、StartとEndを検証"); //TESTヘッダ
		}

		@DataPoints
		public static Fixture[] datas = {
				new Fixture("1122:3344::", "1122:3344::", "1122:3344::"),
				new Fixture("1122:3344::/32", "1122:3344::", "1122:3344:ffff:ffff:ffff:ffff:ffff:ffff"),
				new Fixture("1122:3344::/64", "1122:3344::", "1122:3344::ffff:ffff:ffff:ffff"),
				new Fixture("1122:3344::-1122:3355::", "1122:3344::", "1122:3355::"),
				new Fixture("1122:3355::-1122:3344::", "1122:3344::", "1122:3355::"),
				new Fixture("1122:3344::2", "1122:3344::2", "1122:3344::2"),
				new Fixture("*", "::0", "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"),
				new Fixture("*:*:*:*:*:*:*:*", "::0", "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"),
		};
		static class Fixture {
			private String aclStr;
			private String startStr;
			private String endStr;

			public Fixture(String aclStr, String startStr, String endStr) {
				this.aclStr = aclStr;
				this.startStr = startStr;
				this.endStr = endStr;
			}
		}

		@Theory
		public void test(Fixture fx) {

			TestUtil.dispPrompt(this); //TESTプロンプト
			try {
				AclV6 aclV6 = new AclV6("test", fx.aclStr);
				System.out.printf("new AclV6(%s) => start=%s end=%s\n", fx.aclStr, fx.startStr, fx.endStr);
				assertThat(aclV6.getStart().toString(), is(fx.startStr));
				assertThat(aclV6.getEnd().toString(), is(fx.endStr));
			} catch (ValidObjException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	
	@RunWith(Theories.class)
	public static final class A002 {
		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("範囲に入っているかどうかの検証 　isHit()"); //TESTヘッダ
		}

		@DataPoints
		public static Fixture[] datas = {
			    new Fixture("1122:3344::/64", "1111:3343::", false),
				new Fixture("1122:3344::/64", "1122:3343::", false),
				new Fixture("1122:3344::/64", "1122:3344::1", true),
				new Fixture("1122:3344::/64", "1122:3345::", false),
		};
		static class Fixture {
			private String aclStr;
			private String ipStr;
			private boolean expected;

			public Fixture(String aclStr, String ipStr, boolean expected) {
				this.aclStr = aclStr;
				this.ipStr = ipStr;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {
			TestUtil.dispPrompt(this); //TESTプロンプト

			try {
				AclV6 aclV6 = new AclV6("test", fx.aclStr);
				System.out.printf("new AclV6(%s) => isHit(%s)=%s\n", fx.aclStr, fx.ipStr, fx.expected);
				//Ip a = aclV6.getStart();
				//Ip b = aclV6.getEnd();
				//Ip c = new Ip(fx.ipStr);
				aclV6.isHit(new Ip(fx.ipStr));
				assertThat(aclV6.isHit(new Ip(fx.ipStr)), is(fx.expected));

			} catch (ValidObjException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	
	@RunWith(Theories.class)
	public static final class A003 {
		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("無効な文字列で初期化した場合に例外が発生するかの検証"); //TESTヘッダ
		}

		@DataPoints
		public static Fixture[] datas = {
				//コントロールの種類,デフォルト値,toRegの出力
				new Fixture("192.168.0.1"),
				new Fixture("x"),
				new Fixture("::1-234"),
				new Fixture("::1/200"),
		};
		static class Fixture {
			private String aclStr;

			public Fixture(String aclStr) {
				this.aclStr = aclStr;
			}
		}

		@Theory
		public void test(Fixture fx) {
			TestUtil.dispPrompt(this); //TESTプロンプト
			try {
				new AclV6("test", fx.aclStr);
				Assert.fail("この行が実行されたらエラー");
			} catch (ValidObjException ex) {
				System.out.printf("new AclV6(%s) => ValidObjException\n", fx.aclStr);
				return;
			}
			Assert.fail("この行が実行されたらエラー");
		}
	}


}

/*
        [TestCase("1122:3344::/32", "1122:3344::", "1122:3344:ffff:ffff:ffff:ffff:ffff:FFFF")]
        [TestCase("1122:3344::/64", "1122:3344::", "1122:3344::ffff:ffff:ffff:FFFF")]
        [TestCase("1122:3344::-1122:3355::", "1122:3344::", "1122:3355::")]
        [TestCase("1122:3355::-1122:3344::", "1122:3344::", "1122:3355::")]
        [TestCase("1122:3344::2", "1122:3344::2", "1122:3344::2")]
        [TestCase("*", "::0", "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff")]
        [TestCase("*:*:*:*:*:*:*:*", "::0", "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff")]
        public void StartEndTest(string strAcl, string start, string end) {
            var o = new AclV6("test", strAcl);
            Assert.AreEqual(o.Start, new Ip(start));
            Assert.AreEqual(o.End, new Ip(end));
        }

        [TestCase("1122:3344::/64", "1122:3343::", false)]
        [TestCase("1122:3344::/64", "1122:3344::1", true)]
        [TestCase("1122:3344::/64", "1122:3345::", false)]
        public void IsHitTest(string strAcl, string ipStr, bool status) {
            var o = new AclV6("test", strAcl);
            Assert.AreEqual(o.IsHit(new Ip(ipStr)), status);
        }
    }

}
*/