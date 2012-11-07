package bjd.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class BytesTest {

	//*****************************************************
	//各テストクラスで共通に使用される元データの作成クラス
	//*****************************************************
	@Ignore("テストから場外される　-各テストクラスで共通に使用される元データの作成クラス")  //テストから除外
	public static final class Data {
		public static byte [] generate() {
			int max = 100;
			byte [] dmy = new byte[max];
			for (byte i = 0; i < max; i++) {
				dmy[i] = i;
			}
			byte b = 1;
			short a1 = 2;
			int a2 = 3;
			long a3 = 4;
			String s = "123";
			return Bytes.create(dmy, b, a1, a2, a3, s, dmy);
		}
	}
	
	
	@RunWith(Theories.class)
	public static final class A001 {
		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("Bytes.create(....) offset番目のデータの確認"); //TESTヘッダ
		}
		@DataPoints
		public static Fixture[] datas = {
			new Fixture(100, (byte) 1),		
			new Fixture(100 + 1, (byte) 2), 
			new Fixture(100 + 1 + 2, (byte) 3), 
			new Fixture(100 + 1 + 2 + 4, (byte) 4), 
			new Fixture(100 + 1 + 2 + 4 + 8 + 0, (byte) '1'),
			new Fixture(100 + 1 + 2 + 4 + 8 + 1, (byte) '2'), 
			new Fixture(100 + 1 + 2 + 4 + 8 + 2, (byte) '3'), 
			new Fixture(100 + 1 + 2 + 4 + 8 + 3, (byte) 0),
		};
		static class Fixture {
			private int offset;
			private byte expected;

			public Fixture(int offset, byte expected) {
				this.offset = offset;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {

			TestUtil.dispPrompt(this); //TESTプロンプト
			
			System.out.printf("data[%d] = %d\n", fx.offset, fx.expected);
			byte [] data = Data.generate();
			assertThat(data[fx.offset], is(fx.expected));
		}
	}

	@RunWith(Theories.class)
	public static final class A002 {
		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("Bytes.search() 指定したoffset以降で、「\"123\"」が出現する位置を検索する"); //TESTヘッダ
		}

		@DataPoints
		public static Fixture[] datas = {
			new Fixture(0, 49),	//１つ目dmyの中に存在する
			new Fixture(50, 100 + 1 + 2 + 4 + 8), 
			new Fixture(100, 100 + 1 + 2 + 4 + 8), 
			new Fixture(150, 167), //2つ目dmyの中に存在する
			new Fixture(200, -1), //存在しない
		};
		static class Fixture {
			private int offset;
			private int expected;

			public Fixture(int offset, int expected) {
				this.offset = offset;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {

			TestUtil.dispPrompt(this); //TESTプロンプト
			
			System.out.printf("data.indexOf(%d,\"123\") = %d\n", fx.offset, fx.expected);
			byte [] data = Data.generate();
			byte [] src = ("123").getBytes();

			int actual = Bytes.indexOf(data, fx.offset, src);
			assertThat(actual, is(fx.expected));
		}
	}
}

