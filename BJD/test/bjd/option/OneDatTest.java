package bjd.option;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertSame;
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
public class OneDatTest {

	@RunWith(Theories.class)
	public static final class A001 {

		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("コンストラクタで初期化してtoReg()の出力を確認する [*]はisSecretList toReg()のパラメータは isSecret");
		}

		@DataPoints
		public static Fixture[] datas = {
				new Fixture(true, new String[] { "user1", "pass" }, new boolean[] { false, false }, true, "\tuser1\tpass"),
				new Fixture(true, new String[] { "user1", "pass" }, new boolean[] { true, false }, true, "\t***\tpass"), 
				new Fixture(true, new String[] { "user1", "pass" }, new boolean[] { true, false }, false, "\tuser1\tpass"), 
		};

		static class Fixture {
			private boolean enable;
			private String[] list;
			private boolean[] isSecretList;
			private boolean isSecret;
			private String expected;

			public Fixture(boolean enable, String[] list, boolean[] isSecretList, boolean isSecret, String expected) {

				this.enable = enable;
				this.list = list;
				this.isSecretList = isSecretList;
				this.isSecret = isSecret;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {
			TestUtil.dispPrompt(this);

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < fx.list.length; i++) {
				if (sb.length() != 0) {
					sb.append(",");
				}
				sb.append(fx.list[i]);
				sb.append(String.format("[%s]", fx.isSecretList[i] ? "*" : ""));
			}
			System.out.printf("new OneDat(%s,{%s}) toReg(%s)=\"%s\"\n", fx.enable, sb.toString(), fx.isSecret, fx.expected);

			OneDat oneDat = null;
			try {
				oneDat = new OneDat(fx.enable, fx.list, fx.isSecretList);
			} catch (ValidObjException e) {
				Assert.fail(e.getMessage());
			}
			assertThat(oneDat.toReg(fx.isSecret), is(fx.expected));
		}
	}
	
	@RunWith(Theories.class) 
	public static final class A002 {
		@BeforeClass public static void before() {
			TestUtil.dispHeader("fromRegで初期化してtoReg()で出力する"); 
		}
		
		@DataPoints public static Fixture[] datas = { 
				new Fixture(2, "\tuser1\tpass", "\tuser1\tpass"),
				new Fixture(2, "#\tuser1\tpass", "#\tuser1\tpass"),
				new Fixture(3, "\tn1\tn2\tn3", "\tn1\tn2\tn3"),
		};
		
		static class Fixture {
			private int max; //カラム数 (コンストラクタ初期化用)
			private String actual;
			private String expected;

			public Fixture(int max, String actual, String expected) {
				this.max = max;
				this.actual = actual;
				this.expected = expected;
			}
		}
		@Theory
		public void test(Fixture fx) {
			TestUtil.dispPrompt(this);

			System.out.printf("new OneDat(max=%d) frmoReg(\"%s\") => toReg()=\"%s\"\n", fx.max, fx.actual, fx.expected);

			String [] list = new String[fx.max];
			boolean [] isSecretList = new boolean[fx.max];
			for (int i = 0; i < fx.max; i++) {
				list[i] = "";
				isSecretList[i] = false;
			}
			OneDat oneDat = null;
			try {
				oneDat = new OneDat(true, list, isSecretList);
			} catch (ValidObjException e) {
				Assert.fail(e.getMessage());
			}
			oneDat.fromReg(fx.actual);
			assertThat(oneDat.toReg(false), is(fx.expected));
		}
	}

	@RunWith(Theories.class)
	public static final class A003 {
		@BeforeClass public static void before() {
			TestUtil.dispHeader("// fromRegに無効な入力があった時、falseが帰る"); 
		}
		
		@DataPoints public static Fixture[] datas = { 
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
		public void test(Fixture fx) {
			TestUtil.dispPrompt(this);

			System.out.printf("new OneDat(max=%d) frmoReg(\"%s\") = false\n", fx.max, fx.str);

			String [] list = new String[fx.max];
			boolean [] isSecretList = new boolean[fx.max];
			for (int i = 0; i < fx.max; i++) {
				list[i] = "";
				isSecretList[i] = false;
			}
			OneDat oneDat = null;
			try {
				oneDat = new OneDat(true, list, isSecretList);
			} catch (ValidObjException e) {
				Assert.fail(e.getMessage());
			}
			assertSame(oneDat.fromReg(fx.str), false);
		}
	}

	// コンストラクタで定義したカラム型との宇一致や、無効文字列fromRegに送ったとき、falseが帰る
	
}

