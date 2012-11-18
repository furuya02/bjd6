package bjd.util;

import junit.framework.Assert;

import org.junit.Test;


public class ListBaseTest {

	//ListBaseを継承すｔるクラスを定義
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
	public final void 要素を３つ追加してsizeを確認する() {
		
		TestClass ts = new TestClass();
	
		ts.add(new OneClass("1"));
		ts.add(new OneClass("2"));
		ts.add(new OneClass("3"));

		Assert.assertEquals(ts.size(), 3);
	}

	@Test
	public final void 拡張forループで要素を取り出す() {

		TestClass ts = new TestClass();

		ts.add(new OneClass("1"));
		ts.add(new OneClass("2"));
		ts.add(new OneClass("3"));

		StringBuilder sb = new StringBuilder();
		for (OneClass o : ts) {
			sb.append(o.getS());
		}
		Assert.assertEquals(sb.toString(), "123");
	}
	
	@Test
	public final void 要素を３つ追加してremobveの後要素を確認する() {

		TestClass ts = new TestClass();

		ts.add(new OneClass("1"));
		ts.add(new OneClass("2"));
		ts.add(new OneClass("3"));
		ts.remove(0);
		StringBuilder sb = new StringBuilder();
		for (OneClass o : ts) {
			sb.append(o.getS());
		}
		Assert.assertEquals(sb.toString(), "23");
	}
	
	@Test
	public final void 要素を３つ追加してwhileで回す() {

		TestClass ts = new TestClass();

		ts.add(new OneClass("1"));
		ts.add(new OneClass("2"));
		ts.add(new OneClass("3"));

		StringBuilder sb = new StringBuilder();
		while (ts.hasNext()) {
			sb.append(ts.next().getS());
		}
		Assert.assertEquals(sb.toString(), "123");
	}
}