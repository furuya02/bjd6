package bjd.ctrl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

<<<<<<< HEAD
import bjd.test.TestUtil;

public final class ListViewTest {

	@Test
	public void addColumnでカラムを追加しgetColumnCountでその数を確認する() {

=======
/**
 * ListViewのdisposeは、コントロール等の資源の解放だが、テストの場合は継続してプロセスが起動するわけではないので省略できる
 * @author SIN
 *
 */
public final class ListViewTest {

	@Test
	public void addColumnでカラムを追加しgetColumnCountでその数を確認する() throws Exception {

		//setUp
>>>>>>> work
		ListView sut = new ListView("listView");
		sut.addColumn("1");
		sut.addColumn("2");
		sut.addColumn("3");
<<<<<<< HEAD
		TestUtil.prompt("listView.addColumn(\"1\") addColumn(\"2\") addColumn(\"3\")");

		int actual = sut.getColumnCount();
		int expected = 3;

		assertThat(actual, is(expected));
		TestUtil.prompt(String.format("listView.getColumnCount()=%d", actual));

		sut.dispose();
=======
		int expected = 3;
		
		//exercise
		int actual = sut.getColumnCount();

		//verify
		assertThat(actual, is(expected));

>>>>>>> work
	}

	@Test
	public void itemAddで行を追加しgetRowCountでその数を確認する() {
<<<<<<< HEAD
=======

		//setUp
>>>>>>> work
		ListView listView = new ListView("listView");
		listView.addColumn("Col1");
		listView.itemAdd(new String[] { "1" });
		listView.itemAdd(new String[] { "2" });
		listView.itemAdd(new String[] { "3" });
<<<<<<< HEAD
		TestUtil.prompt("listView.itemAdd(new String[]{\"1\"} itemAdd(new String[]{\"2\"} itemAdd(new String[]{\"3\"}");

		int actual = listView.getRowCount();
		int expected = 3;

		assertThat(actual, is(expected));
		TestUtil.prompt(String.format("行数  listView.getRowCount()=%d", actual));
=======
		int expected = 3;

		//exercise
		int actual = listView.getRowCount();
>>>>>>> work

		//verify
		assertThat(actual, is(expected));
	}

	@Test
<<<<<<< HEAD
	public void itemClear_全行削除_で行数は0になる() {

=======
	public void itemClearで全行削除すると行数は0になる() {

		//setUp
>>>>>>> work
		ListView listView = new ListView("listView");
		listView.addColumn("Col1");
		listView.itemAdd(new String[] { "1" });
		listView.itemAdd(new String[] { "2" });
		listView.itemAdd(new String[] { "3" });
<<<<<<< HEAD
		TestUtil.prompt("listView.itemAdd(new String[]{\"1\"} itemAdd(new String[]{\"2\"} itemAdd(new String[]{\"3\"}");

		listView.itemClear();
		TestUtil.prompt("listView.itemClear()");

		int aclual = listView.getRowCount();
		int expected = 0;

		assertThat(aclual, is(expected));
		TestUtil.prompt(String.format("行数  listView.getRowCount()=%d", aclual));
=======
		listView.itemClear();
		int expected = 0;

		//exercise
		int actual = listView.getRowCount();
>>>>>>> work

		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void setColWidthで設定した値をgetColWidthで取得する() {

<<<<<<< HEAD
		ListView listView = new ListView("listView");
		listView.addColumn("Col1");

		int width = 100;

		listView.setColWidth(0, width);
		TestUtil.prompt(String.format("listView.setColWidth(0, %d)", width));

		int actual = listView.getColWidth(0);
		assertThat(actual, is(width));

		TestUtil.prompt(String.format("listView.getColWidth(0)=%d", actual));
=======
		//setUp
		ListView listView = new ListView("listView");
		listView.addColumn("Col1");
		listView.setColWidth(0, 100);
		int expected = 100;

		//exercise
		int actual = listView.getColWidth(0);
>>>>>>> work

		//verify
		assertThat(actual, is(expected));
	}

	@Test
<<<<<<< HEAD
	public void setTextで値を設定しgetTextで取得する() {

=======
	public void setTextでitemに設定した値をgetTextで取得する() {

		//setUp
>>>>>>> work
		ListView listView = new ListView("listView");
		listView.addColumn("Col1");
		listView.itemAdd(new String[] { "1" });

<<<<<<< HEAD
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
=======
		listView.setText(0, 0, "2");
		String expected = "2";
		
		//exercise
		String actual = listView.getText(0, 0);

		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void setColumnTextでカラムに設定した値をgetColumnTextで取得する() {

		//setUp
		ListView listView = new ListView("listView");
		listView.addColumn("default");
		
		listView.setColumnText(0, "1");
		String expected = "1";
>>>>>>> work

		//exercise
		String actual = listView.getColumnText(0);
<<<<<<< HEAD
		assertThat(actual, is(expected));
		TestUtil.prompt(String.format("listView.getColumnText(0)=\"%s\"", actual));

		expected = "1";
		listView.setColumnText(0, expected);
		TestUtil.prompt(String.format("listView.setColumnText(0,\"%s\")", expected));
=======
>>>>>>> work

		//verify
		assertThat(actual, is(expected));
<<<<<<< HEAD
		TestUtil.prompt(String.format("listView.getColumnText(0)=\"%s\"", actual));
=======
>>>>>>> work

	}
}
