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
public class AclV4Test {
	@RunWith(Theories.class)
	public static final class A001 {
		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("Acl()で生成して、StartとEndを検証"); //TESTヘッダ
		}

		@DataPoints
		public static Fixture[] datas = {
				//コントロールの種類,デフォルト値,toRegの出力
				new Fixture("192.168.0.1-192.168.10.254", "192.168.0.1", "192.168.10.254"),
				new Fixture("192.168.0.1-200", "192.168.0.1", "192.168.0.200"),
				new Fixture("*", "0.0.0.0", "255.255.255.255"),
				new Fixture("192.168.*.*", "192.168.0.0", "192.168.255.255"),
				new Fixture("192.168.0.*", "192.168.0.0", "192.168.0.255"),
				new Fixture("192.168.0.1/24", "192.168.0.0", "192.168.0.255"),
				new Fixture("192.168.10.254-192.168.0.1", "192.168.0.1", "192.168.10.254"),
				new Fixture("192.168.0.1", "192.168.0.1", "192.168.0.1"),
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
				AclV4 aclV4 = new AclV4("test", fx.aclStr);
				System.out.printf("new AclV4(%s) => start=%s end=%s\n", fx.aclStr, fx.startStr, fx.endStr);
				assertThat(aclV4.getStart().toString(), is(fx.startStr));
				assertThat(aclV4.getEnd().toString(), is(fx.endStr));
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
				//コントロールの種類,デフォルト値,toRegの出力
				new Fixture("192.168.1.0/24", "192.168.1.0", true),
				new Fixture("192.168.1.0/24", "192.168.1.255", true),
				new Fixture("192.168.1.0/24", "192.168.0.255", false),
				new Fixture("192.168.1.0/24", "192.168.2.0", false),
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
				AclV4 aclV4 = new AclV4("test", fx.aclStr);
				System.out.printf("new AclV4(%s) => isHit(%s)=%s\n", fx.aclStr, fx.ipStr, fx.expected);
				assertThat(aclV4.isHit(new Ip(fx.ipStr)), is(fx.expected));
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
				new Fixture("192.168.1.0.0"),
				new Fixture("::1"),
				new Fixture("x"),
				new Fixture("192.168.1.0-267"),
				new Fixture("192.168.1.0/200"),
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
				new AclV4("test", fx.aclStr);
				Assert.fail("この行が実行されたらエラー");
			} catch (ValidObjException ex) {
				System.out.printf("new AclV4(%s) => ValidObjException\n", fx.aclStr);
				return;
			}
			Assert.fail("この行が実行されたらエラー");
		}
	}
}
