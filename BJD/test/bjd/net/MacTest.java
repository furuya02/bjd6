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
public class MacTest {
	@RunWith(Theories.class)
	public static final class Mac_macStr_で初期化してtoStringで確かめる {

		@DataPoints
		public static Fixture[] datas = {
				new Fixture("00-00-00-00-00-00"),
				new Fixture("00-26-2D-3F-3F-67"),
				new Fixture("00-ff-ff-ff-3F-67"),
				new Fixture("FF-FF-FF-FF-FF-FF"),
		};

		static class Fixture {
			private String macStr;

			public Fixture(String macStr) {
				this.macStr = macStr;
			}
		}

		@Theory
		public void test(Fixture fx) throws Exception {
			//setUp
			Mac sut = new Mac(fx.macStr);
			String expected = fx.macStr.toLowerCase();
			//exercise
			String actual = sut.toString().toLowerCase();
			//verify
			assertThat(actual, is(expected));
		}
	}

	@RunWith(Theories.class)
	public static final class equalのテスト12_34_56_78_9A_BCと比較する {

		@DataPoints
		public static Fixture[] datas = {
				new Fixture("12-34-56-78-9a-bc", true),
				new Fixture("12-34-56-78-9A-BC", true),
				new Fixture("00-26-2D-3F-3F-67", false),
				new Fixture("00-00-00-00-00-00", false),
				new Fixture("ff-ff-ff-ff-ff-ff", false),
				new Fixture(null, false),
		};

		static class Fixture {
			private String macStr;
			private boolean expected;

			public Fixture(String macStr, boolean expected) {
				this.macStr = macStr;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) throws Exception {
			//setUp
			Mac sut = new Mac("12-34-56-78-9A-BC");
			Mac target = null;
			if (fx.macStr != null) {
				target = new Mac(fx.macStr);
			}
			boolean expected = fx.expected;
			//exercise
			boolean actual = sut.equals(target);
			//verify
			assertThat(actual, is(expected));
		}

//		@Theory
//		public void test2(Fixture fx) {
//
//			try {
//				Mac mac = new Mac("12-34-56-78-9A-BC");
//				Mac dmy = (fx.macStr != null) ? new Mac(fx.macStr) : null;
//				boolean actual = mac.equals(dmy);
//
//				TestUtil.prompt(String.format("mac=new Mac(\"%s\") => mac.equals(%s)==%s", mac.toString(), fx.macStr, actual));
//				assertThat(actual, is(fx.expected));
//			} catch (ValidObjException ex) {
//				Assert.fail(ex.getMessage());
//			}
//		}
	}
}
