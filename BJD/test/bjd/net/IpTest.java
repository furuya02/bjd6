package bjd.net;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import java.net.InetAddress;
import java.net.UnknownHostException;

import junit.framework.Assert;

import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import bjd.ValidObjException;
import bjd.test.TestUtil;

@RunWith(Enclosed.class)
public class IpTest {

	@RunWith(Theories.class)
	public static final class 文字列のコンストラクタで生成してtoStringで確認する {
<<<<<<< HEAD
<<<<<<< HEAD

=======
		
>>>>>>> work
=======
		
>>>>>>> work
		@DataPoints
		public static Fixture[] datas = {
				// コンストラクタ文字列,toString()出力
				new Fixture("192.168.0.1", "192.168.0.1"),
				new Fixture("255.255.0.254", "255.255.0.254"),
				new Fixture("INADDR_ANY", "INADDR_ANY"),
				new Fixture("0.0.0.0", "0.0.0.0"),
				new Fixture("IN6ADDR_ANY_INIT", "IN6ADDR_ANY_INIT"),
				new Fixture("::", "::0"),
				new Fixture("::1", "::1"),
				new Fixture("::809f", "::809f"),
				new Fixture("ff34::809f", "ff34::809f"),
				new Fixture("1234:56::1234:5678:90ab", "1234:56::1234:5678:90ab"),
				new Fixture("fe80::7090:40f5:96f7:17db%13", "fe80::7090:40f5:96f7:17db%13"),
				new Fixture("12::78:90ab", "12::78:90ab"),
				new Fixture("[12::78:90ab]", "12::78:90ab"), //[括弧付きで指定された場合]
				new Fixture("fff::", "fff::"),

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

			TestUtil.prompt(String.format("new Ip(\"%s\") toString()=\"%s\"", fx.actual, fx.expected));
			Ip ip = null;
			try {
				ip = new Ip(fx.actual);
			} catch (ValidObjException ex) {
				Assert.fail(ex.getMessage());
			}
			assertThat(ip.toString(), is(fx.expected));
		}
	}

	@RunWith(Theories.class)
	public static final class 文字列のコンストラクタで生成してIP_getInetAddress及びtoStringで確認する {
<<<<<<< HEAD
<<<<<<< HEAD

=======
>>>>>>> work
=======
>>>>>>> work

		@DataPoints
		public static Fixture[] datas = {
				// コンストラクタ文字列,toString()出力
				new Fixture("192.168.0.1", "/192.168.0.1"),
				new Fixture("255.255.0.254", "/255.255.0.254"),
				new Fixture("INADDR_ANY", "/0.0.0.0"),
				new Fixture("0.0.0.0", "/0.0.0.0"),
				new Fixture("IN6ADDR_ANY_INIT", "/0:0:0:0:0:0:0:0"),
				new Fixture("::", "/0:0:0:0:0:0:0:0%0"),
				new Fixture("::1", "/0:0:0:0:0:0:0:1%0"),
				new Fixture("::809f", "/0:0:0:0:0:0:0:809f%0"),
				new Fixture("ff34::809f", "/ff34:0:0:0:0:0:0:809f%0"),
				new Fixture("1234:56::1234:5678:90ab", "/1234:56:0:0:0:1234:5678:90ab%0"),
				new Fixture("fe80::7090:40f5:96f7:17db%13", "/fe80:0:0:0:7090:40f5:96f7:17db%13"),
				new Fixture("12::78:90ab", "/12:0:0:0:0:0:78:90ab%0"),
				new Fixture("[12::78:90ab]", "/12:0:0:0:0:0:78:90ab%0"), //[括弧付きで指定された場合]
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

			TestUtil.prompt(String.format("new Ip(\"%s\") IP.getInetAddress().toString()=\"%s\"", fx.actual, fx.expected));

			Ip ip = null;
			try {
				ip = new Ip(fx.actual);
			} catch (ValidObjException ex) {
				Assert.fail(ex.getMessage());

			}
			try {
				InetAddress inetAddress = ip.getInetAddress();
				assertThat(inetAddress.toString(), is(fx.expected));
			} catch (UnknownHostException ex) {
				Assert.fail("InetAddressの取得に失敗しました");
			}
		}
	}

	@RunWith(Theories.class)
	public static final class getIpV4の確認 {

		@DataPoints
		public static Fixture[] datas = {
				// コンストラクタ文字列,toString()出力
				new Fixture(192, 168, 0, 1),
				new Fixture(127, 0, 0, 1),
				new Fixture(0, 0, 0, 0),
				new Fixture(255, 255, 255, 255),
				new Fixture(255, 255, 0, 254),

		};

		static class Fixture {
			private int n1;
			private int n2;
			private int n3;
			private int n4;

			public Fixture(int n1, int n2, int n3, int n4) {
				this.n1 = n1;
				this.n2 = n2;
				this.n3 = n3;
				this.n4 = n4;
			}
		}

		@Theory
		public void test(Fixture fx) {

			String ipStr = String.format("%d.%d.%d.%d", fx.n1, fx.n2, fx.n3, fx.n4);

			Ip ip = null;
			try {
				ip = new Ip(ipStr);
			} catch (ValidObjException ex) {
				Assert.fail(ex.getMessage());
			}

			byte[] ipV4 = ip.getIpV4();

			TestUtil.prompt(String.format("new Ip(\"%s\") ipV4[0]=%d ipV4[1]=%d ipV4[2]=%d ipV4[3]=%d", ipStr, ipV4[0], ipV4[1], ipV4[2], ipV4[3]));

			assertSame(((byte) fx.n1) == ipV4[0], true);
			assertSame(((byte) fx.n2) == ipV4[1], true);
			assertSame(((byte) fx.n3) == ipV4[2], true);
			assertSame(((byte) fx.n4) == ipV4[3], true);

		}
	}

	@RunWith(Theories.class)
	public static final class getIpV6の確認 {

<<<<<<< HEAD
<<<<<<< HEAD

=======
>>>>>>> work
=======
>>>>>>> work
		@DataPoints
		public static Fixture[] datas = {
				// コンストラクタ文字列,toString()出力
				new Fixture("1234:56::1234:5678:90ab", 0x12, 0x34, 0x00, 0x56, 0, 0, 0, 0, 0, 0, 0x12, 0x34, 0x56, 0x78, 0x90, 0xab),
				new Fixture("1::1", 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
				new Fixture("ff04::f234", 0xff, 0xff04, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xf2, 0x34),
				new Fixture("1::1%16", 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
				new Fixture("[1::1]", 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),

		};

		static class Fixture {
			private String ipStr;
			private int n1;
			private int n2;
			private int n3;
			private int n4;
			private int n5;
			private int n6;
			private int n7;
			private int n8;
			private int n9;
			private int n10;
			private int n11;
			private int n12;
			private int n13;
			private int n14;
			private int n15;
			private int n16;

			public Fixture(String ipStr, int n1, int n2, int n3, int n4, int n5, int n6, int n7, int n8,
					int n9, int n10, int n11, int n12, int n13, int n14, int n15, int n16) {
				this.ipStr = ipStr;
				this.n1 = n1;
				this.n2 = n2;
				this.n3 = n3;
				this.n4 = n4;
				this.n5 = n5;
				this.n6 = n6;
				this.n7 = n7;
				this.n8 = n8;
				this.n9 = n9;
				this.n10 = n10;
				this.n11 = n11;
				this.n12 = n12;
				this.n13 = n13;
				this.n14 = n14;
				this.n15 = n15;
				this.n16 = n16;
			}
		}

		@Theory
		public void test(Fixture fx) {
<<<<<<< HEAD
<<<<<<< HEAD
			Ip ip = null;
			try {
				ip = new Ip(fx.ipStr);
			} catch (ValidObjException ex) {
				Assert.fail(ex.getMessage());
			}
=======

			Ip ip = TestUtil.createIp(fx.ipStr);
>>>>>>> work
=======

			Ip ip = TestUtil.createIp(fx.ipStr);
>>>>>>> work

			byte[] ipV6 = ip.getIpV6();

			TestUtil.prompt(String.format("new Ip(\"%s\")", fx.ipStr));
			for (int i = 0; i < 16; i++) {
				System.out.printf("%d:", ipV6[i]);
			}
			System.out.printf("");

			assertSame(((byte) fx.n1) == ipV6[0], true);
			assertSame(((byte) fx.n2) == ipV6[1], true);
			assertSame(((byte) fx.n3) == ipV6[2], true);
			assertSame(((byte) fx.n4) == ipV6[3], true);
			assertSame(((byte) fx.n5) == ipV6[4], true);
			assertSame(((byte) fx.n6) == ipV6[5], true);
			assertSame(((byte) fx.n7) == ipV6[6], true);
			assertSame(((byte) fx.n8) == ipV6[7], true);
			assertSame(((byte) fx.n9) == ipV6[8], true);
			assertSame(((byte) fx.n10) == ipV6[9], true);
			assertSame(((byte) fx.n11) == ipV6[10], true);
			assertSame(((byte) fx.n12) == ipV6[11], true);
			assertSame(((byte) fx.n13) == ipV6[12], true);
			assertSame(((byte) fx.n14) == ipV6[13], true);
			assertSame(((byte) fx.n15) == ipV6[14], true);
			assertSame(((byte) fx.n16) == ipV6[15], true);
		}
	}

	@RunWith(Theories.class)
	public static final class 演算子イコールイコールの判定_null判定 {

<<<<<<< HEAD
<<<<<<< HEAD
=======
=======
>>>>>>> work
		
>>>>>>> work
		@DataPoints
		public static Fixture[] datas = {
				// IP1.IP2,==の判定
				new Fixture("192.168.0.1", "192.168.0.1", true),
				new Fixture("192.168.0.1", "192.168.0.2", false),
				new Fixture("192.168.0.1", null, false),
				new Fixture("::1", "::1", true),
				new Fixture("::1%1", "::1%1", true),
				new Fixture("::1%1", "::1", false),
				new Fixture("ff01::1", "::1", false),
				new Fixture("::1", null, false),
		};

		static class Fixture {
			private Ip ip0;
			private Ip ip1;
			private boolean expected;

			public Fixture(String ip0, String ip1, boolean expected) {
				try {
					this.ip0 = (ip0 == null) ? null : new Ip(ip0);
					this.ip1 = (ip1 == null) ? null : new Ip(ip1);
				} catch (ValidObjException ex) {
					Assert.fail(ex.getMessage());
				}
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {
<<<<<<< HEAD
<<<<<<< HEAD

=======
>>>>>>> work
=======
>>>>>>> work
			
			String ipStr0 = null;
			if (fx.ip0 != null) {
				ipStr0 = fx.ip0.toString();
			}
			String ipStr1 = null;
			if (fx.ip1 != null) {
				ipStr0 = fx.ip1.toString();
			}

			TestUtil.prompt(String.format("Ip(%s) ip.equals(%s) => %s", ipStr0, ipStr1, fx.expected));

			if (fx.ip0 == null) {
				assert false : "fx.ip0 is null";
			}
			assertSame(fx.ip0.equals(fx.ip1), fx.expected);

		}
	}

	@RunWith(Theories.class)
	public static final class getAddrV4で取得した値からIpオブジェクトを再構築する {


		@DataPoints
		public static Fixture[] datas = {
				// IP1.IP2,==の判定
				new Fixture("1.2.3.4"),
				new Fixture("192.168.0.1"),
				new Fixture("255.255.255.255"),
				new Fixture("INADDR_ANY"),
		};

		static class Fixture {
			private String ipStr;

			public Fixture(String ipStr) {
				this.ipStr = ipStr;
			}
		}

		@Theory
		public void test(Fixture fx) {

			try {
				Ip p1 = new Ip(fx.ipStr);
				int i = p1.getAddrV4();
				Ip p2 = new Ip(i);
				TestUtil.prompt(String.format("Ip(%s) => ip.getAddrV4()=0x%x(%d) => new Ip(0x%x) => %s ", fx.ipStr, i, i, i, p2.toString()));

				assertThat(p2.toString(), is(fx.ipStr));
			} catch (ValidObjException ex) {
				Assert.fail(ex.getMessage());
			}

		}
	}

	@RunWith(Theories.class)
	public static final class getAddrV6HとgetAddrV6Lで取得した値からIpオブジェクトを再構築する {

<<<<<<< HEAD
<<<<<<< HEAD

=======
>>>>>>> work
=======
>>>>>>> work
		@DataPoints
		public static Fixture[] datas = {
				// IP1.IP2,==の判定
				new Fixture("102:304:506:708:90a:b0c:d0e:f01"),
				new Fixture("ff83::e:f01"),
				new Fixture("::1"),
				new Fixture("fff::"),
		};

		static class Fixture {
			private String ipStr;

			public Fixture(String ipStr) {
				this.ipStr = ipStr;
			}
		}

		@Theory
		public void test(Fixture fx) {

			try {
				Ip p1 = new Ip(fx.ipStr);
				long h = p1.getAddrV6H();
				long l = p1.getAddrV6L();
				Ip p2 = new Ip(h, l);
				TestUtil.prompt(String.format("Ip(%s) => ip.getAddrV6H()=0x%x  ip.getAddrV6L()=0x%x => new Ip(0x%x,0x%x) => %s ", fx.ipStr, h, l, h, l, p2.toString()));

				assertThat(p2.toString(), is(fx.ipStr));
			} catch (ValidObjException ex) {
				Assert.fail(ex.getMessage());
			}

		}
	}

	@RunWith(Theories.class)
	public static final class 文字列によるコンストラクタで例外_IllegalArgumentException_が発生することを確認する {

<<<<<<< HEAD
<<<<<<< HEAD
=======

>>>>>>> work
=======

>>>>>>> work
		@DataPoints
		public static Fixture[] datas = {
				//コンストラクタに与える文字列
				new Fixture(""),
				new Fixture("IN_ADDR_ANY"),
				new Fixture("xxx"),
				new Fixture("192.168.0.1.2"),
				new Fixture(null),
				new Fixture("11111::"),
		};

		static class Fixture {
			private String ipStr;

			public Fixture(String ipStr) {
				this.ipStr = ipStr;
			}
		}

		@Theory
		public void test(Fixture fx) {

			TestUtil.prompt(String.format("new Ip(%s) => throw ValidObjException", fx.ipStr));

			try {
				new Ip(fx.ipStr);
				Assert.fail("この行が実行されたらエラー");
			} catch (ValidObjException ex) {
				return;
			}
			Assert.fail("この行が実行されたらエラー");
		}
	}

	@RunWith(Theories.class)
	public static final class getAddrV4の検証 {


		@DataPoints
		public static Fixture[] datas = {
				//コンストラクタに与える文字列
				new Fixture("192.168.0.1", 0xc0a80001),
		};

		static class Fixture {
			private String ipStr;
			private int addr;

			public Fixture(String ipStr, int addr) {
				this.ipStr = ipStr;
				this.addr = addr;
			}
		}

		@Theory
		public void test(Fixture fx) {

			TestUtil.prompt(String.format("ip = new Ip(%s) => ip.getAddrV4()=%x", fx.ipStr, fx.addr));

			try {
				Ip ip = new Ip(fx.ipStr);
				int x1 = ip.getAddrV4();
				Assert.assertEquals(x1, fx.addr);
			} catch (ValidObjException ex) {
				Assert.fail(ex.getMessage());
			}

		}
	}

	@RunWith(Theories.class)
	public static final class getAddrV6H及びgetAddrV6Lの検証 {

		@DataPoints
		public static Fixture[] datas = {
				//コンストラクタに与える文字列
				new Fixture("1234:56::1234:5678:90ab", 0x1234005600000000L, 0x00001234567890abL),
		};

		static class Fixture {
			private String ipStr;
			private long h;
			private long l;

			public Fixture(String ipStr, long h, long l) {
				this.ipStr = ipStr;
				this.h = h;
				this.l = l;
			}
		}

		@Theory
		public void test(Fixture fx) {

			TestUtil.prompt(String.format("ip = new Ip(%s) => ip.getAddrV6H()=%x ip.getAddrV6L()=%x", fx.ipStr, fx.h, fx.l));

			try {
				Ip ip = new Ip(fx.ipStr);
				long h = ip.getAddrV6H();
				Assert.assertEquals(h, fx.h);
				long l = ip.getAddrV6L();
				Assert.assertEquals(l, fx.l);
			} catch (ValidObjException ex) {
				Assert.fail(ex.getMessage());
			}
		}
	}

}
