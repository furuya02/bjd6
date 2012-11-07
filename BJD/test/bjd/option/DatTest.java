package bjd.option;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import bjd.ctrl.CtrlType;
import bjd.util.TestUtil;

@RunWith(Enclosed.class)
public class DatTest {
	@RunWith(Theories.class)
	public static final class A001 {

		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("fromReg()で初期化してtoReg(false)で取り出す");
		}

		@DataPoints
		public static Fixture[] datas = {
			new Fixture(2, "#\tn1\tn2\b\tn1\tn2"),		
			new Fixture(1, "\tn1\b\tn1\b#\tn1\b#\tn1"),		
		};

		static class Fixture {
			private int colMax;
			private String str;

			public Fixture(int colMax, String str) {
				this.colMax = colMax;
				this.str = str;
			}
		}

		@Theory
		public void test(Fixture fx) {
			TestUtil.dispPrompt(this);

			CtrlType[]  ctrlTypeList = new CtrlType[fx.colMax];
			
			for (int i = 0; i < fx.colMax; i++) {
				ctrlTypeList[i] = CtrlType.INT;
			}

			System.out.printf("fromReg(\"%s\") => toReg(\"%s\")\n", fx.colMax, fx.str);

			Dat dat = new Dat(ctrlTypeList);
			if (!dat.fromReg(fx.str)) {
				Assert.fail();
			}
			assertThat(dat.toReg(false), is(fx.str));
		}
	}
	
	@RunWith(Theories.class)
	public static final class A002 {

		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("fromReg()に無効な文字列を与えると例外(DatException)が発生する");
		}

		@DataPoints
		public static Fixture[] datas = {
			new Fixture(3, "#\tn1\tn2\b\tn1\tn2"), //カラム数不一致		
			new Fixture(1, "#\tn1\b\tn1\tn2"), //カラム数不一致		
			new Fixture(1, "_\tn1"), //矛盾データ		
			new Fixture(1, "\b"), //矛盾データ		
			new Fixture(1, ""),		
			new Fixture(1, null),		
		};

		static class Fixture {
			private int colMax;
			private String str;

			public Fixture(int colMax, String str) {
				this.colMax = colMax;
				this.str = str;
			}
		}

		@Theory
		public void test(Fixture fx) {
			TestUtil.dispPrompt(this);

			CtrlType[] ctrlTypeList = new CtrlType[fx.colMax];
			for (int i = 0; i < fx.colMax; i++) {
				ctrlTypeList[i] = CtrlType.INT;
			}

			System.out.printf("colMax=%d fromReg(\"%s\") => return false\n", fx.colMax, fx.str);

			Dat dat = new Dat(ctrlTypeList);
			if (!dat.fromReg(fx.str)) {
				return; //テスト成功f;
			}
			Assert.fail("この行が実行されたらエラー");
		}
	}

	
//	@Test
//	public void testAdd() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testToReg() {
//		fail("まだ実装されていません");
//	}
//
//	@Test
//	public void testFromReg() {
//		fail("まだ実装されていません");
//	}

}
