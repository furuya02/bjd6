package bjd.log;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import org.junit.AfterClass;
import org.junit.Test;

import bjd.test.TestUtil;
import bjd.util.Util;

public final class OneLogFileTest {

	//テンポラリディレクトリ名
	private static String tmpDir = "OneLogFileTest";

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
<<<<<<< HEAD
	public void 一度disposeしたファイルに正常に追加できるかどうか() {


		String currentDir = new File(".").getAbsoluteFile().getParent(); // カレントディレクトリ
		String fileName = String.format("%s\\OneLogFileTest.txt", currentDir);

		(new File(fileName)).delete();

		System.out.println(String.format("new OneLogFile() -> set(\"1\") -> set(\"2\") -> set(\"3\") -> dispose()"));
		try {
			OneLogFile oneLogFile = new OneLogFile(fileName);
			oneLogFile.set("1");
			oneLogFile.set("2");
			oneLogFile.set("3");
			oneLogFile.dispose();
		} catch (IOException e1) {
			Assert.fail(e1.getMessage());
		}

		ArrayList<String> lines = null;
		try {
			lines = Util.textFileRead(new File(fileName));
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		assertThat(lines.get(0), is("1"));
		assertThat(lines.get(1), is("2"));
		assertThat(lines.get(2), is("3"));
		assertEquals(3, lines.size());

		TestUtil.prompt(String.format("lines.length==3"));
		TestUtil.prompt(String.format("new OneLogFile() -> set(\"4\") -> set(\"5\") -> set(\"6\") -> dispose()"));

		try {
			OneLogFile oneLogFile = new OneLogFile(fileName);
			oneLogFile.set("4");
			oneLogFile.set("5");
			oneLogFile.set("6");
			oneLogFile.dispose();
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}

		try {
			lines = Util.textFileRead(new File(fileName));
		} catch (IOException e2) {
			Assert.fail(e2.getMessage());
		}
		assertThat(lines.get(0), is("1"));
		assertThat(lines.get(1), is("2"));
		assertThat(lines.get(2), is("3"));
		assertThat(lines.get(3), is("4"));
		assertThat(lines.get(4), is("5"));
		assertThat(lines.get(5), is("6"));

		assertEquals(6, lines.size());

		TestUtil.prompt(String.format("lines.length==6"));

		(new File(fileName)).delete();

=======
	public void 一度disposeしたファイルに正常に追加できるかどうか() throws Exception {
		
		//setUp
		
		String fileName = TestUtil.getTmpPath(tmpDir);
		OneLogFile sut = new OneLogFile(fileName);
		sut.set("1");
		sut.set("2");
		sut.set("3");
		//いったんクローズする
		sut.dispose();
		
		//同一のファイルを再度開いてさらに３行追加
		sut = new OneLogFile(fileName);
		sut.set("4");
		sut.set("5");
		sut.set("6");
		sut.dispose();
		
		int expected = 6; 

		//exercise
		int actual = Util.textFileRead(new File(fileName)).size();
		
		//verify
		assertThat(actual, is(expected));
>>>>>>> work
	}
}
