package bjd.net;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import bjd.ValidObjException;
import bjd.test.TestUtil;

@RunWith(Enclosed.class)
public final class BindAddrTest {

	@RunWith(Theories.class)
	public static final class totoStringによる確認 {

		@Test
		public void newしたBindAddrオブジェクトをtotoStringで確認する() throws Exception {
			//setUp
			String expected = "V4ONLY,INADDR_ANY,IN6ADDR_ANY_INIT";
			BindAddr sut = new BindAddr();
			//exercise
			String actual = sut.toString();
			//verify
			assertThat(actual, is(expected));
		}
	}

	@RunWith(Theories.class)
	public static final class パラメータBindStyle_ipv4_ipv6でnewしたBindAddrオブジェクトをtoStringで確認する {

		@DataPoints
		public static Fixture[] datas = {
				new Fixture(BindStyle.V4ONLY, "192.168.0.1", "::1", "V4ONLY,192.168.0.1,::1"),
				new Fixture(BindStyle.V46DUAL, "0.0.0.0", "ffe0::1", "V46DUAL,0.0.0.0,ffe0::1"),
		};

		static class Fixture {
			private BindStyle bindStyle;
			private Ip ipV4;
			private Ip ipV6;
			private String expected;

			public Fixture(BindStyle bindStyle, String ipV4, String ipV6, String expected) {
				this.bindStyle = bindStyle;
				this.ipV4 = TestUtil.createIp(ipV4);
				this.ipV6 = TestUtil.createIp(ipV6);
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {
			//setUp
			String expected = fx.expected;
			BindAddr sut = new BindAddr(fx.bindStyle, fx.ipV4, fx.ipV6);
			//exercise
			String actual = sut.toString();
			//verify
			assertThat(actual, is(expected));
		}
	}

	@RunWith(Theories.class)
	public static final class 文字列でnewしたBindAddrオブジェクトをtoStringで確認する {

		@DataPoints
		public static Fixture[] datas = {
				new Fixture("V4ONLY,192.168.0.1,::1", "V4ONLY,192.168.0.1,::1"),
		};

		static class Fixture {
			private String bindStr;
			private String expected;

			public Fixture(String bindStr, String expected) {
				this.bindStr = bindStr;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) throws Exception {
			//setUp
			String expected = fx.expected;
			BindAddr sut = new BindAddr(fx.bindStr);
			//exercise
			String actual = sut.toString();
			//verify
			assertThat(actual, is(expected));
		}
	}

	@RunWith(Theories.class)
	public static final class 無効文字列を指定してBindAddresをnewすると例外が発生する_例外テスト {

		@DataPoints
		public static Fixture[] datas = {
				new Fixture(null),
				new Fixture("XXX,INADDR_ANY,IN6ADDR_ANY_INIT"), // 無効な列挙名
				new Fixture("V4ONLY,INADDR_ANY,192.168.0.1"), // IpV6にV4のアドレスを指定
				new Fixture("V4ONLY,::1,IN6ADDR_ANY_INIT"), // IpV4にV6のアドレスを指定
		};

		static class Fixture {
			private String bindStr;

			public Fixture(String bindStr) {
				this.bindStr = bindStr;
			}
		}

		@Theory
		public void test(Fixture fx) {

			try {
				new BindAddr(fx.bindStr);
				Assert.fail("この行が実行されたらエラー");
			} catch (ValidObjException ex) {
			}
		}
	}

	@RunWith(Theories.class)
	public static final class equalsによる同一確認 {

		@DataPoints
		public static Fixture[] datas = {
				new Fixture("V4ONLY,INADDR_ANY,IN6ADDR_ANY_INIT", "V4ONLY,INADDR_ANY,IN6ADDR_ANY_INIT", true),
				new Fixture("V4ONLY,INADDR_ANY,::1", "V4ONLY,INADDR_ANY,::2", false),
				new Fixture("V4ONLY,INADDR_ANY,::1", null, false),
				new Fixture("V4ONLY,INADDR_ANY,::1", "V4ONLY,0.0.0.1,::1", false),
				new Fixture("V6ONLY,0.0.0.1,::1", "V4ONLY,0.0.0.1,::1", false),
		};

		static class Fixture {
			private String bindStr1;
			private String bindStr2;
			private boolean expected;

			public Fixture(String bindStr1, String bindStr2, boolean expected) {
				this.bindStr1 = bindStr1;
				this.bindStr2 = bindStr2;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) throws Exception {

			//setUp
			BindAddr sut = new BindAddr(fx.bindStr1);
			BindAddr target = (fx.bindStr2 == null) ? null : new BindAddr(fx.bindStr2);
			boolean expected = fx.expected;

			//exercise
			boolean actual = sut.equals(target);
			//verify
			assertThat(actual, is(expected));
		}
	}

	@RunWith(Theories.class)
	public static final class checkCompetitionによる競合があるかどうかの確認 {

		@DataPoints
		public static Fixture[] datas = {
				new Fixture("V4ONLY,INADDR_ANY,IN6ADDR_ANY_INIT", "V4ONLY,INADDR_ANY,IN6ADDR_ANY_INIT", true),
				new Fixture("V4ONLY,INADDR_ANY,::1", "V4ONLY,INADDR_ANY,::2", true),
				new Fixture("V4ONLY,INADDR_ANY,::1", "V4ONLY,0.0.0.1,::1", true),
				new Fixture("V6ONLY,0.0.0.1,::1", "V4ONLY,0.0.0.1,::1", false),
				new Fixture("V46DUAL,0.0.0.1,::1", "V4ONLY,0.0.0.1,::1", true),
		};

		static class Fixture {
			private String bindStr1;
			private String bindStr2;
			private boolean expected;

			public Fixture(String bindStr1, String bindStr2, boolean expected) {
				this.bindStr1 = bindStr1;
				this.bindStr2 = bindStr2;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) throws Exception {

			//setUp
			BindAddr sut = new BindAddr(fx.bindStr1);
			BindAddr target = (fx.bindStr2 == null) ? null : new BindAddr(fx.bindStr2);
			boolean expected = fx.expected;

			//exercise
			boolean actual = sut.checkCompetition(target);
			//verify
			assertThat(actual, is(expected));
		}
	}

	@RunWith(Theories.class)
	public static final class createOneBindの確認 {

		@DataPoints
		public static Fixture[] datas = {
				new Fixture("V4ONLY,INADDR_ANY,::1", ProtocolKind.Tcp, 1, "INADDR_ANY-Tcp"),
				new Fixture("V4ONLY,INADDR_ANY,::1", ProtocolKind.Udp, 1, "INADDR_ANY-Udp"),
				new Fixture("V4ONLY,0.0.0.1,::1", ProtocolKind.Tcp, 1, "0.0.0.1-Tcp"),
				new Fixture("V6ONLY,0.0.0.1,::1", ProtocolKind.Tcp, 1, "::1-Tcp"),
				new Fixture("V46DUAL,0.0.0.1,::1", ProtocolKind.Tcp, 2, "::1-Tcp"),
		};

		static class Fixture {
			private String bindStr;
			private ProtocolKind protocolKind;
			private int count;
			private String firstOneBind;

			public Fixture(String bindStr, ProtocolKind protocolKind, int count, String firstOneBind) {
				this.bindStr = bindStr;
				this.protocolKind = protocolKind;
				this.count = count;
				this.firstOneBind = firstOneBind;
			}
		}

		@Theory
		public void 生成されるOneBindの数の確認(Fixture fx) throws Exception {
			//stUp
			BindAddr sut = new BindAddr(fx.bindStr);
			int expected = fx.count;

			//exercise
			OneBind[] ar = sut.createOneBind(fx.protocolKind);
			int actual = ar.length;
			//verify
			assertThat(actual, is(expected));

		}

		@Theory
		public void 生成される最初のOneBindの確認(Fixture fx) throws Exception {
			//stUp
			BindAddr sut = new BindAddr(fx.bindStr);
			String expected = fx.firstOneBind;

			//exercise
			OneBind[] ar = sut.createOneBind(fx.protocolKind);
			String actual = ar[0].toString();

			//verify
			assertThat(actual, is(expected));

		}

	}

}
