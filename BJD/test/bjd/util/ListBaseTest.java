package bjd.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * テストの性格上、リソース解放のdisposeは省略する
 * @author SIN
 *
 */
public class ListBaseTest {

	//テストのためにListBaseを継承するクラスを定義する
	class OneClass implements IDispose {
		private String s;

		public OneClass(String s) {
			this.s = s;
		}

		public String getS() {
			return s;
		}

		@Override
		public void dispose() {
		}
	}

	class TestClass extends ListBase<OneClass> {
		public void add(OneClass o) {
			getAr().add(o);
		}
	}

	@Test
<<<<<<< HEAD
	public final void 要素を３つ追加してsizeを確認する() {
		
		TestClass ts = new TestClass();
	
		ts.add(new OneClass("1"));
		ts.add(new OneClass("2"));
		ts.add(new OneClass("3"));

		Assert.assertEquals(ts.size(), 3);
	}

	@Test
	public final void 拡張forループで要素を取り出す() {
=======
	public void 要素を３つ追加してsizeは3になる() throws Exception {
		//setUp
		TestClass sut = new TestClass();
		sut.add(new OneClass("1"));
		sut.add(new OneClass("2"));
		sut.add(new OneClass("3"));

		int expected = 3;

		//exercise
		int actual = sut.size();

		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 要素を３つ追加してforループを回す() throws Exception {
		//setUp
		TestClass sut = new TestClass();
		sut.add(new OneClass("1"));
		sut.add(new OneClass("2"));
		sut.add(new OneClass("3"));
>>>>>>> work

		int expected = 3;

		//exercise
		int actual = 0;
		for (OneClass o : sut) {
			actual++;
		}

		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 要素を３つ追加してwhileで回す() throws Exception {
		//setUp
		TestClass sut = new TestClass();
		sut.add(new OneClass("1"));
		sut.add(new OneClass("2"));
		sut.add(new OneClass("3"));

		int expected = 3;

		//exercise
		int actual = 0;
		while (sut.hasNext()) {
			sut.next();
			actual++;
		}

		//verify
		assertThat(actual, is(expected));
	}

	@Test
<<<<<<< HEAD
	public final void 要素を３つ追加してremobveの後要素を確認する() {
=======
	public void 要素を３つ追加してgetSで取得する() throws Exception {
		//setUp
		TestClass sut = new TestClass();
		sut.add(new OneClass("1"));
		sut.add(new OneClass("2"));
		sut.add(new OneClass("3"));
>>>>>>> work

		String expected = "123";

		//exercise
		StringBuilder sb = new StringBuilder();
		for (OneClass o : sut) {
			sb.append(o.getS());
		}
		String actual = sb.toString();

		//verify
		assertThat(actual, is(expected));
	}
<<<<<<< HEAD
	
	@Test
	public final void 要素を３つ追加してwhileで回す() {
=======
>>>>>>> work

	@Test
	public void 要素を３つ追加してremobveで一部の要素を削除する() throws Exception {
		//setUp
		TestClass sut = new TestClass();
		sut.add(new OneClass("1"));
		sut.add(new OneClass("2"));
		sut.add(new OneClass("3"));
		sut.remove(0);

		String expected = "23";

		//exercise
		StringBuilder sb = new StringBuilder();
		for (OneClass o : sut) {
			sb.append(o.getS());
		}
		String actual = sb.toString();

		//verify
		assertThat(actual, is(expected));
	}
}