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
	public final void a001() {
		TestUtil.dispHeader("a001 3要素追加してsize()を確認する");
		TestUtil.dispPrompt(this);
		
		TestClass ts = new TestClass();
	
		ts.add(new OneClass("1"));
		ts.add(new OneClass("2"));
		ts.add(new OneClass("3"));

		Assert.assertEquals(ts.size(), 3);
	}

	@Test
	public final void a002() {
		TestUtil.dispHeader("a002 拡張forループで要素を取り出す");
		TestUtil.dispPrompt(this);

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
	public final void a003() {
		TestUtil.dispHeader("a003 3要素追加してremobve()の後、要素を確認する");
		TestUtil.dispPrompt(this);

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
	public final void a004() {
		TestUtil.dispHeader("a004 3要素追加してwhile()で回してみる");
		TestUtil.dispPrompt(this);

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