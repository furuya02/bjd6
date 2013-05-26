package bjd.option;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class OneDatTest {

	private static String[] strList = new String[] { "user1", "pass" };
	private static boolean[] isSecretlList = new boolean[] { true, false };

	@RunWith(Theories.class)
	public static final class isSecretの違いによるtoRegの確認_enableがtrueの場合 {

		@DataPoints
		public static Fixture[] datas = {
				new Fixture(false, "\tuser1\tpass"),
				new Fixture(true, "\t***\tpass"),
		};

		static class Fixture {
			private boolean isSecret;
			private String expected;

			public Fixture(boolean isSecret, String expected) {
				this.isSecret = isSecret;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) throws Exception {
			//setUp
			boolean enable = true; //Enable=TRUE
			OneDat sut = new OneDat(enable, strList, isSecretlList);
			String expected = fx.expected;
			//exercise
			String actual = sut.toReg(fx.isSecret);
			//verify
			assertThat(actual, is(expected));
		}
	}

	@RunWith(Theories.class)
	public static final class isSecretの違いによるtoRegの確認_enableがfalseの場合 {

		@DataPoints
		public static Fixture[] datas = {
				new Fixture(false, "#\tuser1\tpass"),
				new Fixture(true, "#\t***\tpass"),
		};

		static class Fixture {
			private boolean isSecret;
			private String expected;

			public Fixture(boolean isSecret, String expected) {
				this.isSecret = isSecret;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) throws Exception {
			//setUp
			boolean enable = false; //Enable=FALSE
			OneDat sut = new OneDat(enable, strList, isSecretlList);
			String expected = fx.expected;
			//exercise
			String actual = sut.toReg(fx.isSecret);
			//verify
			assertThat(actual, is(expected));
		}
	}

	@RunWith(Theories.class)
	public static final class fromRegで初期化してtoRegで出力する {

		@DataPoints
		public static Fixture[] datas = {
				new Fixture(2, "\tuser1\tpass"),
				new Fixture(2, "#\tuser1\tpass"),
				new Fixture(3, "\tn1\tn2\tn3"),
		};

		static class Fixture {
			private int max; //カラム数 (コンストラクタ初期化用)
			private String str;

			public Fixture(int max, String str) {
				this.max = max;
				this.str = str;
			}
		}

		@Theory
		public void test(Fixture fx) throws Exception {
			//setUp
			OneDat sut = new OneDat(true, new String[fx.max], new boolean[fx.max]);
			sut.fromReg(fx.str);
			String expected = fx.str;
			//exercise
			String actual = sut.toReg(false);
			//verify
			assertThat(actual, is(expected));
		}
	}

	@RunWith(Theories.class)
	public static final class fromRegに無効な入力があった時falseが帰る {

		@DataPoints
		public static Fixture[] datas = {
				new Fixture(3, "\tuser1\tpass"), //カラム数宇一致
				new Fixture(2, null),
				new Fixture(3, "_\tn1\tn2\tn3"), //無効文字列
				new Fixture(3, ""), //無効文字列
				new Fixture(3, "\t"), //無効文字列
		};

		static class Fixture {
			private int max; //カラム数 (コンストラクタ初期化用)
			private String str;

			public Fixture(int max, String str) {
				this.max = max;
				this.str = str;
			}
		}

		@Theory
		public void test(Fixture fx) throws Exception {
			//setUp
			OneDat sut = new OneDat(true, new String[fx.max], new boolean[fx.max]);
			boolean expected = false;
			//exercise
			boolean actual = sut.fromReg(fx.str);
			//verify
			assertThat(actual, is(expected));
		}
	}

}
