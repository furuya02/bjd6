package bjd.net;

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
import bjd.util.TestUtil;

@RunWith(Enclosed.class)
public class MacTest {
	@RunWith(Theories.class)
	public static final class A001 {
		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("Mac(macStr)で初期化して、ｔｏＳｔｒｉｎｇ（）で確かめる"); //TESTヘッダ
		}

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
		public void test(Fixture fx) {
			TestUtil.dispPrompt(this); //TESTプロンプト
			try {
				String actual = (new Mac(fx.macStr)).toString().toUpperCase();

				System.out.printf("mac=new Mac(%s) => mac.toString()==%s\n", fx.macStr, actual);
				assertThat(actual, is(fx.macStr.toUpperCase()));
			} catch (ValidObjException ex) {
				Assert.fail(ex.getMessage());
			}
		}
	}
	
	@RunWith(Theories.class)
	public static final class A002 {
		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("equal()のテスト \"12-34-56-78-9A-BC\"と比較する"); //TESTヘッダ
		}

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
		public void test(Fixture fx) {
			TestUtil.dispPrompt(this); //TESTプロンプト
            
			try {
				Mac mac = new Mac("12-34-56-78-9A-BC");
				Mac dmy = (fx.macStr != null) ? new Mac(fx.macStr) : null;
				boolean actual = mac.equals(dmy);

				System.out.printf("mac=new Mac(\"%s\") => mac.equals(%s)==%s\n", mac.toString(), fx.macStr, actual);
				assertThat(actual, is(fx.expected));
			} catch (ValidObjException ex) {
				Assert.fail(ex.getMessage());
			}
		}
	}
}
