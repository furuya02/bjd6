package bjd.acl;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import bjd.ValidObjException;
import bjd.net.Ip;
import bjd.test.TestUtil;


@RunWith(Enclosed.class)
public class AclV4Test {
	@RunWith(Theories.class)
	public static final class startとendの確認 {

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

			public String toString() {
				return String.format("new AclV4(%s) => getStartr()=%s getEnd()=%s", aclStr, startStr, endStr);
			}

		}

		@Theory
		public void test(Fixture fx) {

			TestUtil.prompt(fx.toString()); 

			try {
				AclV4 sut = new AclV4("test", fx.aclStr);
				assertThat(sut.getStart().toString(), is(fx.startStr));
				assertThat(sut.getEnd().toString(), is(fx.endStr));
			} catch (ValidObjException e) {
				Assert.fail(e.getMessage());
			}
			
		}
	}
	
	@RunWith(Theories.class)
	public static final class isHitを使用して範囲に入っているかを確認する {

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

			public String toString() {
				return String.format("new AclV4(%s) => isHit(%s)=%s", aclStr, ipStr, expected);
			}

		}

		@Theory
		public void test(Fixture fx) {
			TestUtil.prompt(fx.toString()); 

			try {
				AclV4 aclV4 = new AclV4("test", fx.aclStr);
				assertThat(aclV4.isHit(new Ip(fx.ipStr)), is(fx.expected));
			} catch (ValidObjException e) {
				Assert.fail(e.getMessage());
			}
		}
	}

	@RunWith(Theories.class)
	public static final class 無効な文字列で初期化した場合に例外が発生する {

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
			public String toString() {
				return String.format("new AclV6(%s) => ValidObjException", aclStr);
			}
		}

		@Theory
		public void test(Fixture fx) {
			TestUtil.prompt(fx.toString()); 
			try {
				new AclV4("TEST", fx.aclStr);
				Assert.fail("この行が実行されたらエラー");
			} catch (ValidObjException ex) {
				return;
			}
			Assert.fail("この行が実行されたらエラー");
		}
	}
}
