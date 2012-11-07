package bjd.ctrl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import bjd.util.TestUtil;

public final class ListViewTest {

	@Test
	public void a001() {

		TestUtil.dispHeader("a001 カラムの生成とカラム数取得");
		ListView listView = new ListView("listView");
		listView.addColumn("Col1");
		listView.addColumn("Col2");
		listView.addColumn("Col3");
		TestUtil.dispPrompt(this, "listView.addColumn(\"1\") addColumn(\"2\") addColumn(\"3\")");

		int count = listView.getColumnCount();
		assertThat(count, is(3));
		TestUtil.dispPrompt(this, String.format("カラム数  listView.getColumnCount()=%d", count));

		listView.dispose();
	}

	@Test
	public void a002() {

		TestUtil.dispHeader("a002 行の追加と行数取得");
		ListView listView = new ListView("listView");
		listView.addColumn("Col1");

		listView.itemAdd(new String[] { "1" });
		listView.itemAdd(new String[] { "2" });
		listView.itemAdd(new String[] { "3" });
		TestUtil.dispPrompt(this, "listView.itemAdd(new String[]{\"1\"} itemAdd(new String[]{\"2\"} itemAdd(new String[]{\"3\"}");

		int count = listView.getRowCount();
		assertThat(count, is(3));
		TestUtil.dispPrompt(this, String.format("行数  listView.getRowCount()=%d", count));

		listView.dispose();
	}

	@Test
	public void a003() {

		TestUtil.dispHeader("a003 全行削除");
		ListView listView = new ListView("listView");
		listView.addColumn("Col1");

		listView.itemAdd(new String[] { "1" });
		listView.itemAdd(new String[] { "2" });
		listView.itemAdd(new String[] { "3" });
		TestUtil.dispPrompt(this, "listView.itemAdd(new String[]{\"1\"} itemAdd(new String[]{\"2\"} itemAdd(new String[]{\"3\"}");

		listView.itemClear();
		TestUtil.dispPrompt(this, "listView.itemClear()");

		int count = listView.getRowCount();
		assertThat(count, is(0));
		TestUtil.dispPrompt(this, String.format("行数  listView.getRowCount()=%d", count));

		listView.dispose();
	}

	@Test
	public void a004() {

		TestUtil.dispHeader("a004 カラム幅の設定と取得");
		ListView listView = new ListView("listView");
		listView.addColumn("Col1");

		listView.setColWidth(0, 100);
		TestUtil.dispPrompt(this, "listView.setColWidth(0, 100)");

		int width = listView.getColWidth(0);
		assertThat(width, is(100));
		TestUtil.dispPrompt(this, String.format("listView.getColWidth(0)=%d", width));

		listView.dispose();
	}

	@Test
	public void a005() {

		TestUtil.dispHeader("a005 値の設定と取得");
		ListView listView = new ListView("listView");
		listView.addColumn("Col1");

		String expected = "1";
		listView.itemAdd(new String[] { expected });
		TestUtil.dispPrompt(this, String.format("listView.itemAdd(new String[]{\"%s\"}", expected));

		String actual = listView.getText(0, 0);
		assertThat(actual, is(expected));
		TestUtil.dispPrompt(this, String.format("listView.getText(0,0)=\"%s\"", actual));

		expected = "2";
		listView.setText(0, 0, expected);
		TestUtil.dispPrompt(this, String.format("listView.setText(0,0,\"%s\")", expected));

		actual = listView.getText(0, 0);
		assertThat(actual, is("2"));
		TestUtil.dispPrompt(this, String.format("listView.getText(0,0)=\"%s\"", actual));

		listView.dispose();
	}

	@Test
	public void a006() {

		TestUtil.dispHeader("a006 カラムの値の設定と取得");
		ListView listView = new ListView("listView");
		String expected = "default";
				
		listView.addColumn(expected);

		String actual = listView.getColumnText(0);
		assertThat(actual, is(expected));
		TestUtil.dispPrompt(this, String.format("listView.getColumnText(0)=\"%s\"", actual));

		expected = "1";
		listView.setColumnText(0, expected);
		TestUtil.dispPrompt(this, String.format("listView.setColumnText(0,\"%s\")", expected));

		actual = listView.getColumnText(0);
		assertThat(actual, is(expected));
		TestUtil.dispPrompt(this, String.format("listView.getColumnText(0)=\"%s\"", actual));

		listView.dispose();
	}
}
