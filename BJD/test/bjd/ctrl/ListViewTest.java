package bjd.ctrl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import bjd.test.TestUtil;

public final class ListViewTest {

	@Test
	public void addColumnでカラムを追加しgetColumnCountでその数を確認する() {

		ListView sut = new ListView("listView");
		sut.addColumn("1");
		sut.addColumn("2");
		sut.addColumn("3");
		TestUtil.prompt("listView.addColumn(\"1\") addColumn(\"2\") addColumn(\"3\")");

		int actual = sut.getColumnCount();
		int expected = 3;

		assertThat(actual, is(expected));
		TestUtil.prompt(String.format("listView.getColumnCount()=%d", actual));

		sut.dispose();
	}

	@Test
	public void itemAddで行を追加しgetRowCountでその数を確認する() {
		ListView listView = new ListView("listView");
		listView.addColumn("Col1");

		listView.itemAdd(new String[] { "1" });
		listView.itemAdd(new String[] { "2" });
		listView.itemAdd(new String[] { "3" });
		TestUtil.prompt("listView.itemAdd(new String[]{\"1\"} itemAdd(new String[]{\"2\"} itemAdd(new String[]{\"3\"}");

		int actual = listView.getRowCount();
		int expected = 3;

		assertThat(actual, is(expected));
		TestUtil.prompt(String.format("行数  listView.getRowCount()=%d", actual));

		listView.dispose();
	}

	@Test
	public void itemClear_全行削除_で行数は0になる() {

		ListView listView = new ListView("listView");
		listView.addColumn("Col1");

		listView.itemAdd(new String[] { "1" });
		listView.itemAdd(new String[] { "2" });
		listView.itemAdd(new String[] { "3" });
		TestUtil.prompt("listView.itemAdd(new String[]{\"1\"} itemAdd(new String[]{\"2\"} itemAdd(new String[]{\"3\"}");

		listView.itemClear();
		TestUtil.prompt("listView.itemClear()");

		int aclual = listView.getRowCount();
		int expected = 0;

		assertThat(aclual, is(expected));
		TestUtil.prompt(String.format("行数  listView.getRowCount()=%d", aclual));

		listView.dispose();
	}

	@Test
	public void setColWidthで設定した値をgetColWidthで取得する() {

		ListView listView = new ListView("listView");
		listView.addColumn("Col1");

		int width = 100;

		listView.setColWidth(0, width);
		TestUtil.prompt(String.format("listView.setColWidth(0, %d)", width));

		int actual = listView.getColWidth(0);
		assertThat(actual, is(width));

		TestUtil.prompt(String.format("listView.getColWidth(0)=%d", actual));

		listView.dispose();
	}

	@Test
	public void setTextで値を設定しgetTextで取得する() {

		ListView listView = new ListView("listView");
		listView.addColumn("Col1");

		String expected = "1";
		listView.itemAdd(new String[] { expected });
		TestUtil.prompt(String.format("listView.itemAdd(new String[]{\"%s\"}", expected));

		String actual = listView.getText(0, 0);
		assertThat(actual, is(expected));
		TestUtil.prompt(String.format("listView.getText(0,0)=\"%s\"", actual));

		expected = "2";
		listView.setText(0, 0, expected);
		TestUtil.prompt(String.format("listView.setText(0,0,\"%s\")", expected));

		actual = listView.getText(0, 0);
		assertThat(actual, is(expected));
		TestUtil.prompt(String.format("listView.getText(0,0)=\"%s\"", actual));

		listView.dispose();
	}

	@Test
	public void setColumnでカラムに値を設定しgetColumnで取得する() {

		ListView listView = new ListView("listView");
		String expected = "default";

		listView.addColumn(expected);

		String actual = listView.getColumnText(0);
		assertThat(actual, is(expected));
		TestUtil.prompt(String.format("listView.getColumnText(0)=\"%s\"", actual));

		expected = "1";
		listView.setColumnText(0, expected);
		TestUtil.prompt(String.format("listView.setColumnText(0,\"%s\")", expected));

		actual = listView.getColumnText(0);
		assertThat(actual, is(expected));
		TestUtil.prompt(String.format("listView.getColumnText(0)=\"%s\"", actual));

		listView.dispose();
	}
}
