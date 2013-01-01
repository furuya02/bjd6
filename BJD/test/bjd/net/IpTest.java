package bjd.net;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

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
			private String ipStr;
			private String expected;

			public Fixture(String ipStr, String expected) {
				this.ipStr = ipStr;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) throws Exception {
			//setUp
			Ip sut = new Ip(fx.ipStr);
			String expected = fx.expected;
			//exercise
			String actual = sut.toString();
			//verify
			assertThat(actual, is(expected));
		}
	}

	@RunWith(Theories.class)
	public static final class 文字列のコンストラクタで生成してIP_getInetAddress及びtoStringで確認する {

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
			private String ipStr;
			private String expected;

			public Fixture(String ipStr, String expected) {
				this.ipStr = ipStr;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) throws Exception {
			//setUp
			Ip sut = new Ip(fx.ipStr);
			String expected = fx.expected;
			//exercise
			String actual = sut.getInetAddress().toString();
			//verify
			assertThat(actual, is(expected));
		}

	}

	@RunWith(Theories.class)
	public static final class getIpV4の確認 {

		@DataPoints
		public static Fixture[] datas = {
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
		public void xxx(Fixture fx) throws Exception {
			//setUp
			String ipStr = String.format("%d.%d.%d.%d", fx.n1, fx.n2, fx.n3, fx.n4);
			Ip sut = new Ip(ipStr);
			//exercise
			byte[] p = sut.getIpV4();
			//verify
			assertThat(p[0], is((byte) fx.n1));
			assertThat(p[1], is((byte) fx.n2));
			assertThat(p[2], is((byte) fx.n3));
			assertThat(p[3], is((byte) fx.n4));
		}
	}

	@RunWith(Theories.class)
	public static final class getIpV6の確認 {

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
		public void test(Fixture fx) throws Exception {
			//setUp
			Ip sut = new Ip(fx.ipStr);
			//exercise
			byte[] p = sut.getIpV6();
			//verify
			assertThat(p[0], is((byte) fx.n1));
			assertThat(p[1], is((byte) fx.n2));
			assertThat(p[2], is((byte) fx.n3));
			assertThat(p[3], is((byte) fx.n4));
			assertThat(p[4], is((byte) fx.n5));
			assertThat(p[5], is((byte) fx.n6));
			assertThat(p[6], is((byte) fx.n7));
			assertThat(p[7], is((byte) fx.n8));
			assertThat(p[8], is((byte) fx.n9));
			assertThat(p[9], is((byte) fx.n10));
			assertThat(p[10], is((byte) fx.n11));
			assertThat(p[11], is((byte) fx.n12));
			assertThat(p[12], is((byte) fx.n13));
			assertThat(p[13], is((byte) fx.n14));
			assertThat(p[14], is((byte) fx.n15));
			assertThat(p[15], is((byte) fx.n16));
		}

	}

	@RunWith(Theories.class)
	public static final class 演算子イコールイコールの判定_null判定 {

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
			private String ipStr;
			private Ip ip = null;
			private boolean expected;

			public Fixture(String ipStr, String target, boolean expected) {
				this.ipStr = ipStr;
				if (target != null) {
					ip = TestUtil.createIp(target);
				}
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) throws Exception {
			//setUp
			Ip sut = new Ip(fx.ipStr);
			boolean expected = fx.expected;
			//exercise
			boolean actual = sut.equals(fx.ip);
			//verify
			assertThat(actual, is(expected));
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
		public void test(Fixture fx) throws Exception {
			//setUp
			Ip ip = new Ip(fx.ipStr);
			int n = ip.getAddrV4();
			Ip sut = new Ip(n);
			String expected = fx.ipStr;
			//exercise
			String actual = sut.toString();
			//verify
			assertThat(actual, is(expected));
		}

	}

	@RunWith(Theories.class)
	public static final class getAddrV6HとgetAddrV6Lで取得した値からIpオブジェクトを再構築する {

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
		public void test(Fixture fx) throws Exception {
			//setUp
			Ip ip = new Ip(fx.ipStr);
			long h = ip.getAddrV6H();
			long l = ip.getAddrV6L();
			Ip sut = new Ip(h, l);
			String expected = fx.ipStr;
			//exercise
			String actual = sut.toString();
			//verify
			assertThat(actual, is(expected));
		}
	}

	@RunWith(Theories.class)
	public static final class 文字列によるコンストラクタで例外_IllegalArgumentException_が発生することを確認する {

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
		public void test(Fixture fx) throws Exception {
			//setUp
			Ip sut = new Ip(fx.ipStr);
			int expected = fx.addr;
			//exercise
			int actual = sut.getAddrV4();
			//verify
			assertThat(actual, is(expected));
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
		public void test(Fixture fx) throws Exception {
			//setUp
			Ip sut = new Ip(fx.ipStr);
			//exercise
			long h = sut.getAddrV6H();
			long l = sut.getAddrV6L();
			//verify
			assertThat(h, is(fx.h));
			assertThat(l, is(fx.l));
		}
	}

}
