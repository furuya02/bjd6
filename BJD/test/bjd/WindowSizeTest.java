package bjd;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;

import javax.swing.JFrame;

import org.junit.AfterClass;
import org.junit.Test;

import bjd.ctrl.ListView;
import bjd.option.Conf;
import bjd.test.TestUtil;
import bjd.util.Util;

/**
 * WindowsSizeのdisposeは、レジストリの保存だけなので、必要が無い場合は省略できる
 * @author SIN
 *
 */
public final class WindowSizeTest {

	//テンポラリディレクトリ名
	private static String tmpDir = "WindowSizeTest";

	/**
	 * テンポラリのフォルダの削除<br>
	 * このクラスの最後に１度だけ実行される<br>
	 * 個々のテストでは、例外終了等で完全に削除出来ないので、ここで最後にディレクトリごと削除する
	 */
	@AfterClass
	public static void afterClass() {
		File file = new File(TestUtil.getTmpDir(tmpDir));
		Util.fileDelete(file);
	}

	/**
	 * OptionBasicを読み込んだConfの生成<br>
	 * すべてのテストがこのcreateConf()を使用している
	 * @return
	 */
	private Conf createConf() {
		Conf conf = TestUtil.createConf("OptionBasic");
//		Kernel kernel = new Kernel();
//		OneOption op = kernel.getListOption().get("Basic");
//		Conf conf = new Conf(op);
		conf.set("useLastSize", true);
		return conf;
	}

	/**
	 * JFrameの値を保存する 200,100,30,40
	 * @param sut
	 */
	private void initJFrame(WindowSize sut) {
		JFrame frame = new JFrame("name");
		frame.setSize(200, 100);
		frame.setLocation(30, 40);
		sut.save(frame); // サイズ保存
		sut.dispose(); // 破棄
		frame.dispose(); // 破棄
	}

	@Test
	public void listViewのカラムサイズを保存して復元する() throws Exception {
		//setUp
		WindowSize sut = new WindowSize(createConf(), TestUtil.getTmpPath(tmpDir));

		ListView listView = new ListView("name");
		listView.addColumn("col1");
		listView.setColWidth(0, 111);
		sut.save(listView); // カラムサイズ保存
		listView.dispose();

		int expected = 111;

		//exercise
		listView = new ListView("name"); //初期化
		listView.addColumn("col1");
		sut.read(listView); // カラムサイズ読込
		int actual = listView.getColWidth(0);

		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void listViewのカラムサイズが保存されていない場合最低値100を読み出される() throws Exception {

		//setUp
		WindowSize sut = new WindowSize(createConf(), TestUtil.getTmpPath(tmpDir));

		int expected = 100;

		//exercise
		ListView listView = new ListView("name"); //初期化
		listView.addColumn("col1");
		sut.read(listView); // カラムサイズ読込
		int actual = listView.getColWidth(0);

		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void JFrameのサイズが保存された状態でgetWidthで値を読み出す() throws Exception {

		//setUp
		WindowSize sut = new WindowSize(createConf(), TestUtil.getTmpPath(tmpDir));

		//JFrameの値を保存する 200,100,30,40
		initJFrame(sut);
		int expected = 200;

		//exercise
		JFrame frame = new JFrame("name");
		sut.read(frame); // サイズ読込
		int actual = frame.getWidth();

		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void JFrameのサイズが保存された状態でgetHeightで値を読み出す() throws Exception {

		//setUp
		WindowSize sut = new WindowSize(createConf(), TestUtil.getTmpPath(tmpDir));

		//JFrameの値を保存する 200,100,30,40
		initJFrame(sut);
		int expected = 100;

		//exercise
		JFrame frame = new JFrame("name");
		sut.read(frame); // サイズ読込
		int actual = frame.getHeight();

		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void JFrameのサイズが保存された状態でgetXで値を読み出す() throws Exception {

		//setUp
		WindowSize sut = new WindowSize(createConf(), TestUtil.getTmpPath(tmpDir));

		//JFrameの値を保存する 200,100,30,40
		initJFrame(sut);
		int expected = 30;

		//exercise
		JFrame frame = new JFrame("name");
		sut.read(frame); // サイズ読込
		int actual = frame.getX();

		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void JFrameのサイズが保存された状態でgetYで値を読み出す() throws Exception {

		//setUp
		WindowSize sut = new WindowSize(createConf(), TestUtil.getTmpPath(tmpDir));

		//JFrameの値を保存する 200,100,30,40
		initJFrame(sut);
		int expected = 40;

		//exercise
		JFrame frame = new JFrame("name");
		sut.read(frame); // サイズ読込
		int actual = frame.getY();

		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void JFrameのサイズが保存されていない状態でgetWidthでデフォルト値を読み出す() throws Exception {

		//setUp
		WindowSize sut = new WindowSize(createConf(), TestUtil.getTmpPath(tmpDir));

		int expected = 800;

		//exercise
		JFrame frame = new JFrame("name");
		sut.read(frame); // サイズ読込
		int actual = frame.getWidth();

		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void JFrameのサイズが保存されていない状態でgetHeightでデフォルト値を読み出す() throws Exception {

		//setUp
		WindowSize sut = new WindowSize(createConf(), TestUtil.getTmpPath(tmpDir));

		int expected = 400;

		//exercise
		JFrame frame = new JFrame("name");
		sut.read(frame); // サイズ読込
		int actual = frame.getHeight();

		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void JFrameのサイズが保存されていない状態でgetXでデフォルト値を読み出す() throws Exception {

		//setUp
		WindowSize sut = new WindowSize(createConf(), TestUtil.getTmpPath(tmpDir));

		int expected = 0;

		//exercise
		JFrame frame = new JFrame("name");
		sut.read(frame); // サイズ読込
		int actual = frame.getX();

		//verify
		assertThat(actual, is(expected));

	}

	@Test
	public void JFrameのサイズが保存されていない状態でgetYでデフォルト値を読み出す() throws Exception {

		//setUp
		WindowSize sut = new WindowSize(createConf(), TestUtil.getTmpPath(tmpDir));

		int expected = 0;

		//exercise
		JFrame frame = new JFrame("name");
		sut.read(frame); // サイズ読込
		int actual = frame.getY();

		//verify
		assertThat(actual, is(expected));

	}

}
