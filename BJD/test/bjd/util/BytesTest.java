package bjd.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

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
	@Ignore("テストから除外される　-各テストクラスで共通に使用される元データの作成クラス")  //テストから除外
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
	public static final class Bytes_create_offset番目のデータの確認 {
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
		public void test(Fixture fx) throws Exception {
			//setUp
			byte [] data = Data.generate();
			byte expected = fx.expected;
			//exercise
			byte actual = data[fx.offset];
			//verify
			assertThat(actual, is(expected));
		}
	}

	@RunWith(Theories.class)
	public static final class Bytes_search指定したoffset以降で123が出現する位置を検索する {

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
		public void test(Fixture fx) throws Exception {
			//setUp
			byte [] data = Data.generate();
			byte [] src = ("123").getBytes();
			int expected = fx.expected;
			//exercise
			int actual = Bytes.indexOf(data, fx.offset, src);
			//verify
			assertThat(actual, is(expected));
		}
	}
}

