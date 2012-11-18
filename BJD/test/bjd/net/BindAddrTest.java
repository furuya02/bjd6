package bjd.net;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import bjd.ValidObjException;
import bjd.test.TestUtil;

@RunWith(Enclosed.class)
public class BindAddrTest {
	
	@RunWith(Theories.class)
	public static final class newしたBindAddrオブジェクトをtotoStringで確認する {
		
		@DataPoints
		public static Fixture[] datas = {
			new Fixture("V4ONLY,INADDR_ANY,IN6ADDR_ANY_INIT"), 
		
		};
		static class Fixture {
			private String expected;
			public Fixture(String expected) {
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {
			
			TestUtil.prompt(String.format("new BindAddr() toString()=\"%s\"", fx.expected));

			BindAddr bindAddr = new BindAddr();
			assertThat(bindAddr.toString(), is(fx.expected));
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
				try {
					this.ipV4 = new Ip(ipV4);
					this.ipV6 = new Ip(ipV6);
				} catch (ValidObjException e) {
					Assert.fail(e.getMessage());
				}
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {
			
			TestUtil.prompt(String.format("new BindAddr(%s,%s,%s) toString()=\"%s\"", fx.bindStyle, fx.ipV4, fx.ipV6, fx.expected));

			BindAddr bindAddr = new BindAddr(fx.bindStyle, fx.ipV4, fx.ipV6);
			assertThat(bindAddr.toString(), is(fx.expected));
		}
	}

	@RunWith(Theories.class)
	public static final class 文字列でnewしたBindAddrオブジェクトをtoStringで確認する {
		

		@DataPoints
		public static Fixture[] datas = {
			new Fixture("V4ONLY,192.168.0.1,::1", "V4ONLY,192.168.0.1,::1"),
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
		public void test(Fixture fx) {
			
			TestUtil.prompt(String.format("new BindAddr(%s) toString()=\"%s\"", fx.actual, fx.expected));

			try {
				BindAddr bindAddr = new BindAddr(fx.actual);
				assertThat(bindAddr.toString(), is(fx.expected));
			} catch (ValidObjException e) {
				Assert.fail();
			}
		}
	}

	@RunWith(Theories.class)
	public static final class 無効文字列を指定してBindAddresをnewすると例がが発生する {
		
		@DataPoints
		public static Fixture[] datas = {
			new Fixture(null),
			new Fixture("XXX,INADDR_ANY,IN6ADDR_ANY_INIT"), // 無効な列挙名
			new Fixture("V4ONLY,INADDR_ANY,192.168.0.1"), // IpV6にV4のアドレスを指定
			new Fixture("V4ONLY,::1,IN6ADDR_ANY_INIT"), // IpV4にV6のアドレスを指定
		};
		static class Fixture {
			private String actual;

			public Fixture(String actual) {
				this.actual = actual;
			}
		}

		@Theory
		public void test(Fixture fx) {
			
			TestUtil.prompt(String.format("new BindAddr(%s) =>  throw ValidObjException", fx.actual));
			
			try {
				new BindAddr(fx.actual);
				Assert.fail("この行が実行されたらエラー");
			} catch (ValidObjException ex) {
				return;
			}
			Assert.fail("この行が実行されたらエラー");
		}
	}
	
	@RunWith(Theories.class)
	public static final class equalsの確認 {
		
		@DataPoints
		public static Fixture[] datas = {
				new Fixture("V4ONLY,INADDR_ANY,IN6ADDR_ANY_INIT", "V4ONLY,INADDR_ANY,IN6ADDR_ANY_INIT", true),
				new Fixture("V4ONLY,INADDR_ANY,::1", "V4ONLY,INADDR_ANY,::2", false),			
				new Fixture("V4ONLY,INADDR_ANY,::1", null, false),
				new Fixture("V4ONLY,INADDR_ANY,::1", "V4ONLY,0.0.0.1,::1", false),
				new Fixture("V6ONLY,0.0.0.1,::1", "V4ONLY,0.0.0.1,::1", false),
		};
		static class Fixture {
			private BindAddr b1;
			private BindAddr b2;
			private boolean expected;

			public Fixture(String b1, String b2, boolean expected) {
				try {
					this.b1 = (b1 == null) ? null : new BindAddr(b1);
					this.b2 = (b2 == null) ? null : new BindAddr(b2);
				} catch (ValidObjException e) {
					Assert.fail(e.getMessage());
				}
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {
			
			TestUtil.prompt(String.format("(\"%s\").equals(\"%s\")=%s", fx.b1, fx.b2, fx.expected));
			assertThat(fx.b1.equals(fx.b2), is(fx.expected));
		}
	}
	
	@RunWith(Theories.class)
	public static final class 競合があるかどうかの確認 {
		
		@DataPoints
		public static Fixture[] datas = {
				new Fixture("V4ONLY,INADDR_ANY,IN6ADDR_ANY_INIT", "V4ONLY,INADDR_ANY,IN6ADDR_ANY_INIT", true),
				new Fixture("V4ONLY,INADDR_ANY,::1", "V4ONLY,INADDR_ANY,::2", true),			
				new Fixture("V4ONLY,INADDR_ANY,::1", "V4ONLY,0.0.0.1,::1", true),
				new Fixture("V6ONLY,0.0.0.1,::1", "V4ONLY,0.0.0.1,::1", false),
				new Fixture("V46DUAL,0.0.0.1,::1", "V4ONLY,0.0.0.1,::1", true),
		};
		static class Fixture {
			private BindAddr b1;
			private BindAddr b2;
			private boolean expected;

			public Fixture(String b1, String b2, boolean expected) {
				try {
					this.b1 = new BindAddr(b1);
					this.b2 = new BindAddr(b2);
				} catch (ValidObjException ex) {
					Assert.fail(ex.getMessage());
				}
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {
			
			TestUtil.prompt(String.format("(\"%s\").checkCompetition(\"%s\")=%s", fx.b1, fx.b2, fx.expected));
			assertThat(fx.b1.checkCompetition(fx.b2), is(fx.expected));
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
			private BindAddr bindAddr;
			private ProtocolKind protocolKind;
			private int count;
			private String firstOneBind;

			public Fixture(String bindAddr, ProtocolKind protocolKind, int count, String firstOneBind) {
				try {
					this.bindAddr = new BindAddr(bindAddr);
				} catch (ValidObjException ex) {
					Assert.fail(ex.getMessage());
				}
				this.protocolKind = protocolKind;
				this.count = count;
				this.firstOneBind = firstOneBind;
			}
		}

		@Theory
		public void test(Fixture fx) {
			
			OneBind[] ar = fx.bindAddr.createOneBind(fx.protocolKind);
			
			TestUtil.prompt(String.format("OneBind[] ar = (\"%s\").createOneBind(%s)", fx.bindAddr.toString(), fx.protocolKind));
			assertThat(ar.length, is(fx.count));
			if (fx.count > 0) {
				assertThat(ar[0].toString(), is(fx.firstOneBind));
			}
		}
	}

}
