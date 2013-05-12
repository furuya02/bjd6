package bjd.log;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

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
	}
}
