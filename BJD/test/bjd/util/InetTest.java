package bjd.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import bjd.test.TestUtil;

@RunWith(Enclosed.class)
public class InetTest {
	@RunWith(Theories.class)
	public static final class toBytes {

		@DataPoints
		public static Fixture[] datas = { new Fixture("本日は晴天なり", "feff672c65e5306f66745929306a308a"), new Fixture("12345", "feff00310032003300340035"),
				new Fixture("", ""), new Fixture(null, ""), };

		static class Fixture {
			private String str;
			private String byteStr;

			public Fixture(String str, String byteStr) {
				this.str = str;
				this.byteStr = byteStr;
			}
		}

		@Theory
		public void test(Fixture fx) {

			TestUtil.prompt(String.format("Inet.getBytes(\"%s\")=%s", fx.str, fx.byteStr));
			byte[] bytes = Inet.toBytes(fx.str);
			StringBuilder sb = new StringBuilder(bytes.length * 2);
			for (byte b : bytes) {
				sb.append(String.format("%02x", b & 0xFF));
			}
			assertThat(sb.toString(), is(fx.byteStr));
		}
	}

	@RunWith(Theories.class)
	public static final class fromBytes {

		@DataPoints
		public static Fixture[] datas = { new Fixture("本日は晴天なり", "feff672c65e5306f66745929306a308a"), new Fixture("12345", "feff00310032003300340035"),
				new Fixture("", ""), };

		static class Fixture {
			private String str;
			private String byteStr;

			public Fixture(String str, String byteStr) {
				this.str = str;
				this.byteStr = byteStr;
			}
		}

		@Theory
		public void test(Fixture fx) {

			TestUtil.prompt(String.format("Inet.fromBytes(\"%s\")=%s", fx.byteStr, fx.str));
			byte[] bytes = new byte[fx.byteStr.length() / 2];
			for (int index = 0; index < bytes.length; index++) {
				bytes[index] = (byte) Integer.parseInt(fx.byteStr.substring(index * 2, (index + 1) * 2), 16);
			}
			String str = Inet.fromBytes(bytes);
			assertThat(str, is(fx.str));
		}
	}

	@RunWith(Theories.class)
	public static final class fromBytes_String {

		@DataPoints
		public static Fixture[] datas = { new Fixture("1\r\n2\r\n3", 3), new Fixture("1\r\n2\r\n3\r\n", 3), new Fixture("1\n2\n3", 1), new Fixture("", 0),
				new Fixture("\r\n", 1), };

		static class Fixture {
			private String str;
			private int count;

			public Fixture(String str, int count) {
				this.str = str;
				this.count = count;
			}
		}

		@Theory
		public void test(Fixture fx) {

			TestUtil.prompt(String.format("Inet.getLines(\"%s\") count=%d", TestUtil.toString(fx.str), fx.count));
			ArrayList<String> lines = Inet.getLines(fx.str);
			assertThat(lines.size(), is(fx.count));
		}
	}

	@RunWith(Theories.class)
	public static final class fromBytes_byte__ {

		@DataPoints
		public static Fixture[] datas = { new Fixture(new byte[] { 0x62, 0x0d, 0x0a, 0x62, 0x0d, 0x0a, 0x62 }, 3),
				new Fixture(new byte[] { 0x62, 0x0d, 0x0a, 0x62, 0x0d, 0x0a, 0x62, 0x0d, 0x0a }, 3), new Fixture(new byte[] { 0x62, 0x0d, 0x0a }, 1),
				new Fixture(new byte[] { 0x0d, 0x0a }, 1), new Fixture(new byte[] {}, 0), new Fixture(null, 0), };

		static class Fixture {
			private byte[] buf;
			private int count;

			public Fixture(byte[] buf, int count) {
				this.buf = buf;
				this.count = count;
			}
		}

		@Theory
		public void test(Fixture fx) {

			TestUtil.prompt(String.format("Inet.getLines(\"%s\") count=%d", TestUtil.toString(fx.buf), fx.count));
			ArrayList<byte[]> lines = Inet.getLines(fx.buf);
			assertThat(lines.size(), is(fx.count));
		}
	}

	@RunWith(Theories.class)
	public static final class trimCrlf_String {
		
		@DataPoints
		public static Fixture[] datas = { new Fixture("1", "1"), new Fixture("1\r\n", "1"), new Fixture("1\r", "1\r"), new Fixture("1\n", "1"),
				new Fixture("1\n2\n", "1\n2"), };

		static class Fixture {
			private String str;
			private String expected;

			public Fixture(String str, String expected) {
				this.str = str;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {

			TestUtil.prompt(String.format("Inet.trimCrlf(\"%s\") =%s", TestUtil.toString(fx.str), fx.expected));
			assertThat(Inet.trimCrlf(fx.str), is(fx.expected));
		}
	}

	@RunWith(Theories.class)
	public static final class trimCrlf_byte配列 {

		@DataPoints
		public static Fixture[] datas = { new Fixture(new byte[] { 0x64 }, new byte[] { 0x64 }),
				new Fixture(new byte[] { 0x64, 0x0d, 0x0a }, new byte[] { 0x64 }), new Fixture(new byte[] { 0x64, 0x0d }, new byte[] { 0x64, 0x0d }),
				new Fixture(new byte[] { 0x64, 0x0a }, new byte[] { 0x64 }),
				new Fixture(new byte[] { 0x64, 0x0a, 0x65, 0x0a }, new byte[] { 0x64, 0x0a, 0x65 }), };

		static class Fixture {
			private byte[] buf;
			private byte[] expected;

			public Fixture(byte[] buf, byte[] expected) {
				this.buf = buf;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {

			TestUtil.prompt(String.format("Inet.trimCrlf(%s) = %s", TestUtil.toString(fx.buf), TestUtil.toString(fx.expected)));
			assertThat(Inet.trimCrlf(fx.buf), is(fx.expected));
		}
	}

	@RunWith(Theories.class)
	public static final class サニタイズ処理 {

		@DataPoints
		public static Fixture[] datas = { new Fixture("<HTML>", "&lt;HTML&gt;"), new Fixture("R&B", "R&amp;B"), new Fixture("123~", "123%7E"), };

		static class Fixture {
			private String str;
			private String expected;

			public Fixture(String str, String expected) {
				this.str = str;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {

			TestUtil.prompt(String.format("Inet.Sanitize(\"%s\") = \"%s\"", fx.str, fx.expected));
			assertThat(Inet.sanitize(fx.str), is(fx.expected));
		}

	}

	@RunWith(Theories.class)
	public static final class MD5ハッシュ文字列 {

		@DataPoints
		public static Fixture[] datas = { new Fixture("<HTML>", "BE-90-72-8C-11-BF-70-8F-52-50-28-A6-78-0F-8E-17"),
				new Fixture("abc", "90-01-50-98-3C-D2-4F-B0-D6-96-3F-7D-28-E1-7F-72"), new Fixture("", "D4-1D-8C-D9-8F-00-B2-04-E9-80-09-98-EC-F8-42-7E"),
				new Fixture(null, ""), };

		static class Fixture {
			private String str;
			private String expected;

			public Fixture(String str, String expected) {
				this.str = str;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {
			TestUtil.prompt(String.format("Inet.Md5Str(\"%s\") = \"%s\"\n", fx.str, fx.expected));
			assertThat(Inet.md5Str(fx.str), is(fx.expected));
		}

	}
}
