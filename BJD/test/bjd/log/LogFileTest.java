package bjd.log;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.AfterClass;
import org.junit.Test;

import bjd.test.TestUtil;
import bjd.util.FileSearch;
import bjd.util.Util;

public final class LogFileTest {
	
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

		//setUp
		File dir = new File(TestUtil.getTmpPath(tmpDir));
		dir.mkdir();
		
		//2012/09/01~7日分のログを準備
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
	}


}
