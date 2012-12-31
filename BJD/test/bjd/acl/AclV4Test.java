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
	public static final class getStart及びgetEndの確認 {

		@DataPoints
		public static Fixture[] datas = {
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
		public void getStartの検証(Fixture fx) throws Exception {
			//setUp
			AclV4 sut = new AclV4("TAG", fx.aclStr);
			String expected = fx.startStr;
			//exercise
			String actual = sut.getStart().toString();
			//verify
			assertThat(actual, is(expected));
		}

		@Theory
		public void getEndの検証(Fixture fx) throws Exception {
			//setUp
			AclV4 sut = new AclV4("TAG", fx.aclStr);
			String expected = fx.endStr;
			//exercise
			String actual = sut.getEnd().toString();
			//verify
			assertThat(actual, is(expected));
		}
	}

	@RunWith(Theories.class)
	public static final class isHitを使用して範囲に入っているかを確認する {

		@DataPoints
		public static Fixture[] datas = {
				new Fixture("192.168.1.0/24", "192.168.1.0", true),
				new Fixture("192.168.1.0/24", "192.168.1.255", true),
				new Fixture("192.168.1.0/24", "192.168.0.255", false),
				new Fixture("192.168.1.0/24", "192.168.2.0", false),
		};

		static class Fixture {
			private String aclStr;
			private Ip ip;
			private boolean expected;

			public Fixture(String aclStr, String ipStr, boolean expected) {
				this.aclStr = aclStr;
				this.expected = expected;
				this.ip = TestUtil.createIp(ipStr);
			}
		}

		@Theory
		public void isHitの検証(Fixture fx) throws Exception {
			//setUp
			AclV4 sut = new AclV4("TAG", fx.aclStr);
			boolean expected = fx.expected;
			//exercise
			boolean actual = sut.isHit(fx.ip);
			//verify
			assertThat(actual, is(expected));
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
		}
		
		@Theory
		public void 例外を発生させるテスト(Fixture fx){
			//exercise
			try {
				new AclV4("TAG", fx.aclStr);
				Assert.fail("この行が実行されたらエラー");
			} catch (ValidObjException ex) {
				return;
			}
			Assert.fail("この行が実行されたらエラー");
		}
	}
}
