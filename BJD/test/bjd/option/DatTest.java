package bjd.option;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import bjd.ctrl.CtrlType;
import bjd.test.TestUtil;

@RunWith(Enclosed.class)
public class DatTest {
	@RunWith(Theories.class)
	public static final class fromRegで初期化してtoReg_false_で取り出す {

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

			CtrlType[]  ctrlTypeList = new CtrlType[fx.colMax];
			
			for (int i = 0; i < fx.colMax; i++) {
				ctrlTypeList[i] = CtrlType.INT;
			}

			TestUtil.prompt(String.format("fromReg(\"%s\") => toReg(\"%s\")", fx.colMax, fx.str));

			Dat dat = new Dat(ctrlTypeList);
			if (!dat.fromReg(fx.str)) {
				Assert.fail();
			}
			assertThat(dat.toReg(false), is(fx.str));
		}
	}
	
	@RunWith(Theories.class)
	public static final class fromRegに無効な文字列を与えると例外_DatExceptionが発生する {


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

			CtrlType[] ctrlTypeList = new CtrlType[fx.colMax];
			for (int i = 0; i < fx.colMax; i++) {
				ctrlTypeList[i] = CtrlType.INT;
			}

			TestUtil.prompt(String.format("colMax=%d fromReg(\"%s\") => return false", fx.colMax, fx.str));

			Dat dat = new Dat(ctrlTypeList);
			if (!dat.fromReg(fx.str)) {
				return; //テスト成功f;
			}
			Assert.fail("この行が実行されたらエラー");
		}
	}

}
