package bjd;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import javax.swing.JFrame;

import org.junit.AfterClass;
import org.junit.Test;

import bjd.ctrl.ListView;
import bjd.option.Conf;
import bjd.test.TestUtil;
<<<<<<< HEAD
<<<<<<< HEAD

=======
=======
>>>>>>> work
import bjd.util.Util;

/**
 * WindowsSizeのdisposeは、レジストリの保存だけなので、必要が無い場合は省略できる
 * @author SIN
 *
 */
<<<<<<< HEAD
>>>>>>> work
=======
>>>>>>> work
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

<<<<<<< HEAD
<<<<<<< HEAD
	// 後処理
	private void after(File file) {
		file.delete();
	}

	@Test
	public void listViewのカラムの復元() {
=======
	@Test
=======
	@Test
>>>>>>> work
	public void listViewのカラムサイズを保存して復元する() throws Exception {
		//setUp
		WindowSize sut = new WindowSize(createConf(), TestUtil.getTmpPath(tmpDir));

		ListView listView = new ListView("name");
		listView.addColumn("col1");
		listView.setColWidth(0, 111);
		sut.save(listView); // カラムサイズ保存
		listView.dispose();
<<<<<<< HEAD
>>>>>>> work
=======
>>>>>>> work

		int expected = 111;

		//exercise
		listView = new ListView("name"); //初期化
		listView.addColumn("col1");
		sut.read(listView); // カラムサイズ読込
		int actual = listView.getColWidth(0);
<<<<<<< HEAD

		//verify
		assertThat(actual, is(expected));
	}

<<<<<<< HEAD
		TestUtil.prompt("windowSize.save(listView) col0=111 col1=222");
=======
	@Test
	public void listViewのカラムサイズが保存されていない場合最低値100を読み出される() throws Exception {

		//setUp
		WindowSize sut = new WindowSize(createConf(), TestUtil.getTmpPath(tmpDir));
>>>>>>> work

		int expected = 100;

=======

		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void listViewのカラムサイズが保存されていない場合最低値100を読み出される() throws Exception {

		//setUp
		WindowSize sut = new WindowSize(createConf(), TestUtil.getTmpPath(tmpDir));

		int expected = 100;

>>>>>>> work
		//exercise
		ListView listView = new ListView("name"); //初期化
		listView.addColumn("col1");
		sut.read(listView); // カラムサイズ読込
		int actual = listView.getColWidth(0);

		//verify
		assertThat(actual, is(expected));
	}

<<<<<<< HEAD
<<<<<<< HEAD
		//１カラム目のサイズ
		int actual = listView.getColWidth(0);
		assertThat(actual, is(111));
		TestUtil.prompt(String.format("windowSize.read(listView) col0=%d", actual));

		//2カラム目のサイズ
		actual = listView.getColWidth(1);
		assertThat(actual, is(222));
		TestUtil.prompt(String.format("windowSize.read(listView) col1=%d", actual));
=======
	@Test
	public void JFrameのサイズが保存された状態でgetWidthで値を読み出す() throws Exception {

		//setUp
		WindowSize sut = new WindowSize(createConf(), TestUtil.getTmpPath(tmpDir));
>>>>>>> work

		//JFrameの値を保存する 200,100,30,40
		initJFrame(sut);
		int expected = 200;

		//exercise
		JFrame frame = new JFrame("name");
		sut.read(frame); // サイズ読込
		int actual = frame.getWidth();

=======
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

>>>>>>> work
		//verify
		assertThat(actual, is(expected));
	}

	@Test
<<<<<<< HEAD
<<<<<<< HEAD
	public void listViewのカラムの復元_保存データが無いとき最低値100を読み出す() {
=======
=======
>>>>>>> work
	public void JFrameのサイズが保存された状態でgetHeightで値を読み出す() throws Exception {

		//setUp
		WindowSize sut = new WindowSize(createConf(), TestUtil.getTmpPath(tmpDir));
<<<<<<< HEAD
>>>>>>> work
=======
>>>>>>> work

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

<<<<<<< HEAD
<<<<<<< HEAD
		//１カラム目のサイズ
		int actual = listView.getColWidth(0);
		assertThat(actual, is(100));
		TestUtil.prompt(String.format("windowSize.read(listView) col0=%d", actual));

		//2カラム目のサイズ
		actual = listView.getColWidth(1);
		assertThat(actual, is(100));
		TestUtil.prompt(String.format("windowSize.read(listView) col1=%d", actual));
=======
	@Test
	public void JFrameのサイズが保存された状態でgetXで値を読み出す() throws Exception {

		//setUp
		WindowSize sut = new WindowSize(createConf(), TestUtil.getTmpPath(tmpDir));
>>>>>>> work

=======
	@Test
	public void JFrameのサイズが保存された状態でgetXで値を読み出す() throws Exception {

		//setUp
		WindowSize sut = new WindowSize(createConf(), TestUtil.getTmpPath(tmpDir));

>>>>>>> work
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
<<<<<<< HEAD
<<<<<<< HEAD

	@Test
	public void JFrameのサイズの復元() {
=======

	@Test
	public void JFrameのサイズが保存された状態でgetYで値を読み出す() throws Exception {

		//setUp
		WindowSize sut = new WindowSize(createConf(), TestUtil.getTmpPath(tmpDir));
>>>>>>> work

		//JFrameの値を保存する 200,100,30,40
		initJFrame(sut);
		int expected = 40;

<<<<<<< HEAD
		JFrame frame = new JFrame(tag);
		frame.setSize(200, 100);
		frame.setLocation(30, 40);
		windowSize.save(frame); // サイズ保存
		TestUtil.prompt("windowSize.save(frame) width=200 height=100 x=30 y=40");
		windowSize.dispose(); // 破棄
		frame.dispose(); // 破棄

		frame = new JFrame(tag);
		windowSize.read(frame); // サイズ読込
		TestUtil.prompt("windowSize.read(frame)");
=======
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
>>>>>>> work

=======

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

>>>>>>> work
		//exercise
		JFrame frame = new JFrame("name");
		sut.read(frame); // サイズ読込
		int actual = frame.getWidth();
<<<<<<< HEAD
<<<<<<< HEAD
		int expected = 200;
		assertThat(actual, is(expected));
		TestUtil.prompt(String.format("width=%d", actual));
=======
>>>>>>> work

		//verify
		assertThat(actual, is(expected));
<<<<<<< HEAD
		TestUtil.prompt(String.format("heighth=%d", actual));

		actual = frame.getX();
		expected = 30;
		assertThat(actual, is(expected));
		TestUtil.prompt(String.format("x=%d", actual));

		actual = frame.getY();
		expected = 40;
		assertThat(actual, is(expected));
		TestUtil.prompt(String.format("y=%d", actual));
=======
	}

	@Test
	public void JFrameのサイズが保存されていない状態でgetHeightでデフォルト値を読み出す() throws Exception {

		//setUp
		WindowSize sut = new WindowSize(createConf(), TestUtil.getTmpPath(tmpDir));
>>>>>>> work

		int expected = 400;

		//exercise
		JFrame frame = new JFrame("name");
		sut.read(frame); // サイズ読込
		int actual = frame.getHeight();

=======

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

>>>>>>> work
		//verify
		assertThat(actual, is(expected));
	}

	@Test
<<<<<<< HEAD
<<<<<<< HEAD
	public void JFrameのサイズの復元_保存データが無いときデフォルト値を読み出す() {
=======
	public void JFrameのサイズが保存されていない状態でgetXでデフォルト値を読み出す() throws Exception {
>>>>>>> work
=======
	public void JFrameのサイズが保存されていない状態でgetXでデフォルト値を読み出す() throws Exception {
>>>>>>> work

		//setUp
		WindowSize sut = new WindowSize(createConf(), TestUtil.getTmpPath(tmpDir));

<<<<<<< HEAD
<<<<<<< HEAD
		//		JFrame frame = new JFrame(tag);
		//		windowSize.save(frame); // サイズ保存
		//		TestUtil.prompt( "windowSize.save(frame) XXXX=XXX");
		//		windowSize.dispose(); // 破棄
		//		frame.dispose(); // 破棄

		JFrame frame = new JFrame(tag);
		windowSize.read(frame); // サイズ読込
		TestUtil.prompt("windowSize.read(frame)");
=======
		int expected = 0;

		//exercise
		JFrame frame = new JFrame("name");
		sut.read(frame); // サイズ読込
		int actual = frame.getX();
>>>>>>> work

		//verify
		assertThat(actual, is(expected));
<<<<<<< HEAD
		TestUtil.prompt(String.format("width=%d", actual));

		actual = frame.getHeight();
		expected = 400;
		assertThat(actual, is(expected));
		TestUtil.prompt(String.format("heighth=%d", actual));

		actual = frame.getX();
		expected = 0;
		assertThat(actual, is(expected));
		TestUtil.prompt(String.format("x=%d", actual));

		actual = frame.getY();
		expected = 0;
		assertThat(actual, is(expected));
		TestUtil.prompt(String.format("y=%d", actual));
=======

	}

	@Test
	public void JFrameのサイズが保存されていない状態でgetYでデフォルト値を読み出す() throws Exception {

		//setUp
		WindowSize sut = new WindowSize(createConf(), TestUtil.getTmpPath(tmpDir));
>>>>>>> work

=======
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

>>>>>>> work
		int expected = 0;

		//exercise
		JFrame frame = new JFrame("name");
		sut.read(frame); // サイズ読込
		int actual = frame.getY();

		//verify
		assertThat(actual, is(expected));

	}

}
