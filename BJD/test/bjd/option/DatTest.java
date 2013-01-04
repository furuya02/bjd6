package bjd.option;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import bjd.ctrl.CtrlType;

@RunWith(Enclosed.class)
public final class DatTest {
	@RunWith(Theories.class)
	public static final class fromRegで初期化してtoRegで取り出す {

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
		public void test(Fixture fx) throws Exception {
			//setUp
			CtrlType[] ctrlTypeList = new CtrlType[fx.colMax];
			for (int i = 0; i < fx.colMax; i++) {
				ctrlTypeList[i] = CtrlType.INT;
			}
			Dat sut = new Dat(new CtrlType[fx.colMax]);
			String expected = fx.str;
			sut.fromReg(fx.str);

			//exercise
			String actual = sut.toReg(false);
			//verify
			assertThat(actual, is(expected));
		}

	}

	@RunWith(Theories.class)
	public static final class fromRegに無効な文字列を与えるとfalseが返る {

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
		public void test(Fixture fx) throws Exception {
			//setUp
			Dat sut = new Dat(new CtrlType[fx.colMax]);
			boolean expected = false;
			//exercise
			boolean actual = sut.fromReg(fx.str);
			//verify
			assertThat(actual, is(expected));
		}
	}

}
