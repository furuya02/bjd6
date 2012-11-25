package bjd.log;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.AfterClass;
import org.junit.Test;

<<<<<<< HEAD
import bjd.ValidObjException;
<<<<<<< HEAD
=======
import bjd.test.TestUtil;
>>>>>>> work
=======
import bjd.test.TestUtil;
>>>>>>> work
import bjd.util.FileSearch;
import bjd.util.Util;

public final class LogFileTest {
<<<<<<< HEAD

	//多重スレッドでテストが走ると、同一ファイルにアクセスしてしまうので、テストごとにテンポラリディレクトリを用意するようにする
	private File before(String tmpDir) {
		File dir; // 作業ディレクトリ
		String path = String.format("%s\\%s", new File(".").getAbsoluteFile().getParent(), tmpDir);
		dir = new File(path);
		// 作業ディレクトリの生成
		if (dir.exists()) {
			Util.fileDelete(dir);
		}
=======
	
	//テンポラリディレクトリ名
	private static String tmpDir = "LogFileTest";

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
	
	@Test
	public void ログの種類_日別_で予想されたパターンのファイルが２つ生成される() throws Exception {

		int logKind = 0; //通常ログの種類
		String pattern = "*.????.??.??.log";
		
		//setUp
		File dir = new File(TestUtil.getTmpPath(tmpDir));
>>>>>>> work
		dir.mkdir();
		LogFile sut = new LogFile(dir.getPath(), logKind, logKind, 0);
		
		int expected = 2;

		//exercise
		int actual = ((new FileSearch(dir.getPath())).listFiles(pattern)).length;
		
		//verify
		assertThat(actual, is(expected));
		
		//tearDown
		sut.dispose();
		
	}

	@Test
	public void ログの種類_月別_で予想されたパターンのファイルが２つ生成される() throws Exception {
		
		int logKind = 1; //通常ログの種類
		String pattern = "*.????.??.log";

		//setUp
		File dir = new File(TestUtil.getTmpPath(tmpDir));
		dir.mkdir();
		LogFile sut = new LogFile(dir.getPath(), logKind, logKind, 0);
		
		int expected = 2;

		//exercise
		int actual = ((new FileSearch(dir.getPath())).listFiles(pattern)).length;
		
		//verify
		assertThat(actual, is(expected));
		
		//tearDown
		sut.dispose();
		
	}

	@Test
<<<<<<< HEAD
	public void LogFileの生成時にオプションで指定したログファイルが生成されているか() {

		//TestUtil.dispHeader("a001 LogFileの生成時に、オプションで指定したログファイルが生成されているか");

		File dir = before("a001");
		String saveDirectory = dir.getPath();
		int saveDays = 0; //自動削除しない

		for (int n = 0; n <= 2; n++) {

			LogFile logFile = null;
			try {
				logFile = new LogFile(saveDirectory, n, n, saveDays);
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}

			// ログの種類に応じた２つのファイルが生成されていることを確認する
			FileSearch fs = new FileSearch(dir.getPath());
			File[] files = fs.listFiles("*.Log"); // n==2
			switch (n) {
				case 0:
					files = fs.listFiles("*.????.??.??.log");
					break;
				case 1:
					files = fs.listFiles("*.????.??.log");
					break;
				default:
					break;
			}
			assertThat(files.length, is(2));
			TestUtil.prompt(String.format("ログの種類 = %d files.length = %d", n, files.length));
			logFile.dispose(); // ログクローズ
			for (File f : files) {
				f.delete();
				System.out.print(" " + f.getName());
			}
			System.out.println();
		}

		after(dir);
	}

	@Test
	public void append_OneLog_してそれぞれのファイルに当該行数が追加されているかどうか() {

		//TestUtil.dispHeader("a002 ");

		File dir = before("a002");
		String saveDirectory = dir.getPath();
		int saveDays = 0;
		LogFile logFile = null;
		try {
			logFile = new LogFile(saveDirectory, 2, 2, saveDays);
		} catch (IOException e1) {
			Assert.fail(e1.getMessage());
		}

		String s1 = "2012/06/01 00:00:00\tDETAIL\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater";
		String s2 = "2012/06/02 00:00:00\tERROR\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater";
		String s3 = "2012/06/03 00:00:00\tSECURE\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater";
		TestUtil.prompt(String.format("append(%s)", s1));
		TestUtil.prompt(String.format("append(%s)", s2));
		TestUtil.prompt(String.format("append(%s)", s3));
		try {
			logFile.append(new OneLog(s1));
			logFile.append(new OneLog(s2));
			logFile.append(new OneLog(s3));
		} catch (ValidObjException ex) {
			Assert.fail(ex.getMessage());
		}
		logFile.dispose();

		try {
			String fileName = "BlackJumboDog.Log";
			ArrayList<String> lines = Util.textFileRead(new File(String.format("%s\\%s", dir.getPath(), fileName)));
			assertThat(lines.size(), is(3));
			TestUtil.prompt(String.format("%s には　%d行　追加されている", fileName, lines.size()));

			fileName = "secure.Log";
			lines = Util.textFileRead(new File(String.format("%s\\%s", dir.getPath(), fileName)));
			assertThat(lines.size(), is(1));
			TestUtil.prompt(String.format("%s には　%d行　追加されている", fileName, lines.size()));
		} catch (IOException e) {
			//textFileRead()の例外をキャッチ
			Assert.fail(e.getMessage());
		}
		after(dir);
	}

	@Test
	public void 過去7日分のログを準備して本日からsaveDaysでtailする() {

		//TestUtil.dispHeader("a003 tail() 2012/09/01~7日分のログを準備して9/7(本日)からsaveDays=2でtailする");

		File dir = before("a003");
		String saveDirectory = dir.getPath();
		int saveDays = 2;
		LogFile logFile = null;
		try {
			logFile = new LogFile(saveDirectory, 2, 2, saveDays);
		} catch (IOException e1) {
			Assert.fail(e1.getMessage());
		}
=======
	public void ログの種類_固定_で予想されたパターンのファイルが２つ生成される() throws Exception {
		
		int logKind = 2; //固定ログの種類
		String pattern = "*.Log";

		//setUp
		File dir = new File(TestUtil.getTmpPath(tmpDir));
		dir.mkdir();
		LogFile sut = new LogFile(dir.getPath(), logKind, logKind, 0);
		
		int expected = 2;

		//exercise
		int actual = ((new FileSearch(dir.getPath())).listFiles(pattern)).length;
		
		//verify
		assertThat(actual, is(expected));
		
		//tearDown
		sut.dispose();
		
	}

	@Test
	public void appendで３行ログを追加すると通常ログが3行になる() throws Exception {
		
		int logKind = 2; //固定ログの種類
		String fileName = "BlackJumboDog.Log";

		//setUp
		File dir = new File(TestUtil.getTmpPath(tmpDir));
		dir.mkdir();
		LogFile sut = new LogFile(dir.getPath(), logKind, logKind, 0);
		sut.append(new OneLog("2012/06/01 00:00:00\tDETAIL\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater"));
		sut.append(new OneLog("2012/06/02 00:00:00\tERROR\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater"));
		sut.append(new OneLog("2012/06/03 00:00:00\tSECURE\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater"));
		sut.dispose();
		
		int expected = 3;

		//exercise
		ArrayList<String> lines = Util.textFileRead(new File(String.format("%s\\%s", dir.getPath(), fileName)));
		int actual = lines.size();
		
		//verify
		assertThat(actual, is(expected));
		
	}

	@Test
	public void appendで３行ログを追加するとセキュアログが1行になる() throws Exception {
		
		int logKind = 2; //固定ログの種類
		String fileName = "secure.Log";

		//setUp
		File dir = new File(TestUtil.getTmpPath(tmpDir));
		dir.mkdir();
		LogFile sut = new LogFile(dir.getPath(), logKind, logKind, 0);
		sut.append(new OneLog("2012/06/01 00:00:00\tDETAIL\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater"));
		sut.append(new OneLog("2012/06/02 00:00:00\tERROR\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater"));
		sut.append(new OneLog("2012/06/03 00:00:00\tSECURE\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater"));
		sut.dispose();
		
		int expected = 1;

		//exercise
		ArrayList<String> lines = Util.textFileRead(new File(String.format("%s\\%s", dir.getPath(), fileName)));
		int actual = lines.size();
		
		//verify
		assertThat(actual, is(expected));
		
	}

	
	@Test
	public void 過去7日分のログを準備して本日からsaveDaysでtailする() throws Exception {
>>>>>>> work

		//setUp
		File dir = new File(TestUtil.getTmpPath(tmpDir));
		dir.mkdir();
		
		//2012/09/01~7日分のログを準備
<<<<<<< HEAD
		String s1 = "2012/09/01 00:00:00\tDETAIL\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater";
		String s2 = "2012/09/02 00:00:00\tERROR\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater";
		String s3 = "2012/09/03 00:00:00\tSECURE\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater";
		String s4 = "2012/09/04 00:00:00\tSECURE\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater";
		String s5 = "2012/09/05 00:00:00\tSECURE\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater";
		String s6 = "2012/09/06 00:00:00\tSECURE\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater";
		String s7 = "2012/09/07 00:00:00\tSECURE\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater";
		TestUtil.prompt(String.format("append(%s)", s1));
		TestUtil.prompt(String.format("append(%s)", s2));
		TestUtil.prompt(String.format("append(%s)", s3));
		TestUtil.prompt(String.format("append(%s)", s4));
		TestUtil.prompt(String.format("append(%s)", s5));
		TestUtil.prompt(String.format("append(%s)", s6));
		TestUtil.prompt(String.format("append(%s)", s7));
		try {
			logFile.append(new OneLog(s1));
			logFile.append(new OneLog(s2));
			logFile.append(new OneLog(s3));
			logFile.append(new OneLog(s4));
			logFile.append(new OneLog(s5));
			logFile.append(new OneLog(s6));
			logFile.append(new OneLog(s7));
		} catch (ValidObjException ex) {
			Assert.fail(ex.getMessage());
		}

		String fileName = "BlackJumboDog.Log";
		String path = String.format("%s\\%s", dir.getPath(), fileName);
		File file = new File(path);

		ArrayList<String> lines = null;
		try {
			lines = Util.textFileRead(file);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		assertThat(lines.size(), is(7));
		TestUtil.prompt(String.format("%s には　%d行　追加されている", fileName, lines.size()));

		try {
			//リフレクションを使用してprivateメソッドにアクセスする
			Method tail = LogFile.class.getDeclaredMethod("tail",
					new Class[] { String.class, int.class, Calendar.class });
			tail.setAccessible(true);
			Calendar c = Calendar.getInstance();
			c.set(2012, 8, 7);
			System.out.println(String.format("本日を、%d.%d.%dにセットする", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
					c.get(Calendar.DAY_OF_MONTH)));
			tail.invoke(logFile, path, saveDays, c);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		TestUtil.prompt(String.format("tail(%s,%d) 保存日数=%d", fileName, saveDays, saveDays));

		try {
			lines = Util.textFileRead(file);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		assertThat(lines.size(), is(2));

		for (String s : lines) {
			TestUtil.prompt(s);
		}
		TestUtil.prompt(String.format("%s には　%d行　追加されている", fileName, lines.size()));

		logFile.dispose();

		after(dir);
=======
		LogFile logFile = new LogFile(dir.getPath(), 2, 2, 0); //最初は、保存期間指定なしで起動する
		logFile.append(new OneLog("2012/09/01 00:00:00\tDETAIL\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater"));
		logFile.append(new OneLog("2012/09/02 00:00:00\tERROR\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater"));
		logFile.append(new OneLog("2012/09/03 00:00:00\tSECURE\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater"));
		logFile.append(new OneLog("2012/09/04 00:00:00\tSECURE\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater"));
		logFile.append(new OneLog("2012/09/05 00:00:00\tSECURE\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater"));
		logFile.append(new OneLog("2012/09/06 00:00:00\tSECURE\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater"));
		logFile.append(new OneLog("2012/09/07 00:00:00\tSECURE\t3208\tWeb-localhost:88\t127.0.0.1\t0000018\texecute\tramapater"));
		logFile.dispose(); 
		
		int expected = 2;

		//exercise
		//リフレクションを使用してprivateメソッドにアクセスする
		Method tail = LogFile.class.getDeclaredMethod("tail", new Class[] { String.class, int.class, Calendar.class });
		tail.setAccessible(true);
		Calendar c = Calendar.getInstance();
		c.set(2012, 8, 7); //2012.9.7に設定する
		String path = String.format("%s\\BlackJumboDog.Log", dir.getPath());
		int saveDays = 2; //保存期間２日
		tail.invoke(logFile, path, saveDays, c);
		
		int actual  = (Util.textFileRead(new File(path))).size();

		//verify
		assertThat(actual, is(expected));
>>>>>>> work
	}


}
