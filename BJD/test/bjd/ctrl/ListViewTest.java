package bjd.ctrl;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * ListViewのdisposeは、コントロール等の資源の解放だが、テストの場合は継続してプロセスが起動するわけではないので省略できる
 * @author SIN
 *
 */
public final class ListViewTest {

	@Test
	public void addColumnでカラムを追加しgetColumnCountでその数を確認する() throws Exception {

		//setUp
		ListView sut = new ListView("listView");
		sut.addColumn("1");
		sut.addColumn("2");
		sut.addColumn("3");
		int expected = 3;
		
		//exercise
		int actual = sut.getColumnCount();

		//verify
		assertThat(actual, is(expected));

	}

	@Test
	public void itemAddで行を追加しgetRowCountでその数を確認する() {

		//setUp
		ListView listView = new ListView("listView");
		listView.addColumn("Col1");
		listView.itemAdd(new String[] { "1" });
		listView.itemAdd(new String[] { "2" });
		listView.itemAdd(new String[] { "3" });
		int expected = 3;

		//exercise
		int actual = listView.getRowCount();

		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void itemClearで全行削除すると行数は0になる() {

		//setUp
		ListView listView = new ListView("listView");
		listView.addColumn("Col1");
		listView.itemAdd(new String[] { "1" });
		listView.itemAdd(new String[] { "2" });
		listView.itemAdd(new String[] { "3" });
		listView.itemClear();
		int expected = 0;

		//exercise
		int actual = listView.getRowCount();

		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void setColWidthで設定した値をgetColWidthで取得する() {

		//setUp
		ListView listView = new ListView("listView");
		listView.addColumn("Col1");
		listView.setColWidth(0, 100);
		int expected = 100;

		//exercise
		int actual = listView.getColWidth(0);

		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void setTextでitemに設定した値をgetTextで取得する() {

		//setUp
		ListView listView = new ListView("listView");
		listView.addColumn("Col1");
		listView.itemAdd(new String[] { "1" });

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

		//exercise
		String actual = listView.getColumnText(0);

		//verify
		assertThat(actual, is(expected));

	}
}
