package bjd;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import org.junit.AfterClass;
import org.junit.Test;

import bjd.test.TestUtil;
import bjd.util.Util;

public final class RegTest {

	//テンポラリディレクトリ名
	private static String tmpDir = "RegTest";

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
	public void setIntで保存した値をgetIntで読み出す() throws Exception {

		//setUp
		Reg sut = new Reg(TestUtil.getTmpPath(tmpDir));
		sut.setInt("key1", 1);
		int expected = 1;

		//exercise
		int actual = sut.getInt("key1");

		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void setStringで保存した値をgetStringで読み出す() throws Exception {

		//setUp
		Reg sut = new Reg(TestUtil.getTmpPath(tmpDir));
		sut.setString("key2", "2");
		String expected = "2";

		//exercise
		String actual = sut.getString("key2");

		//verify
		assertThat(actual, is(expected));
	}

	@Test(expected = RegException.class)
	public void getIntで無効なkeyを指定すると例外が発生する() throws Exception {

		//setUp
		Reg sut = new Reg(TestUtil.getTmpPath(tmpDir));

		//exercise
		sut.getInt("key1");
	}

	@Test(expected = RegException.class)
	public void getStringで無効なkeyを指定すると例外が発生する() throws Exception {
		//setUp
		Reg sut = new Reg(TestUtil.getTmpPath(tmpDir));

		//exercise
		sut.getString("key2");
	}

	@Test(expected = RegException.class)
	public void getIntでKeyにnullを指定すると例外が発生する() throws Exception {
		//setUp
		Reg sut = new Reg(TestUtil.getTmpPath(tmpDir));

		//exercise
		sut.getInt(null);
	}

	@Test(expected = RegException.class)
	public void getStringでKeyにnullを指定すると例外が発生する() throws Exception {
		//setUp
		Reg sut = new Reg(TestUtil.getTmpPath(tmpDir));

		//exercise
		sut.getString(null);
	}

	@Test(expected = RegException.class)
	public void setIntでKeyにnullを指定すると例外が発生する() throws Exception {
		//setUp
		Reg sut = new Reg(TestUtil.getTmpPath(tmpDir));

		//exercise
		sut.setInt(null, 1);
	}

	@Test
	public void setIntでKeyにnullを指定して例外が発生しても元の値は破壊されない() throws Exception {
		//setUp
		Reg sut = new Reg(TestUtil.getTmpPath(tmpDir));
		sut.setInt("key1", 1); //元の値
		int expected = 1;

		try {
			sut.setInt(null, 1);
		} catch (RegException ex) {
			; //nullを指定してsetIntすることで例外が発生する
		}

		//exercise
		int actual = sut.getInt("key1");

		//verify
		assertThat(actual, is(expected));
	}

	@Test(expected = RegException.class)
	public void setStringでKeyにnullを指定すると例外が発生する() throws Exception {
		//setUp
		Reg sut = new Reg(TestUtil.getTmpPath(tmpDir));

		//exercise
		sut.setString(null, "2");
	}

	@Test
	public void setStringでKeyにnullを指定して例外が発生しても元の値は破壊されない() throws Exception {
		//setUp
		Reg sut = new Reg(TestUtil.getTmpPath(tmpDir));
		sut.setString("key2", "2"); //元の値
		String expected = "2";

		try {
			sut.setString(null, "3");
		} catch (RegException ex) {
			; //nullを指定してsetIntすることで例外が発生する
		}

		//exercise
		String actual = sut.getString("key2");

		//verify
		assertThat(actual, is(expected));

	}

	@Test
	public void setStringでvalにnullを指定すると空白が保存される() throws Exception {
		//setUp
		Reg sut = new Reg(TestUtil.getTmpPath(tmpDir));
		sut.setString("key1", null);
		String expected = "";

		//exercise
		String actual = sut.getString("key1");

		//verify
		assertThat(actual, is(expected));

	}

}
