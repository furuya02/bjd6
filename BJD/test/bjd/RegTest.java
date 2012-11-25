package bjd;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import org.junit.AfterClass;
import org.junit.Test;

import bjd.test.TestUtil;
<<<<<<< HEAD

public final class RegTest {

	//共通処理
	private Reg create(File file) {
		Reg reg = null;
		try {
			reg = new Reg(file.getPath());
		} catch (IOException e1) {
			Assert.fail();
		}
		return reg;
	}

	// 前処理
	private File before(String fileName) {
		String currentDir = new File(".").getAbsoluteFile().getParent(); // カレントディレクトリ
		File file = new File(String.format("%s\\%s.regtest", currentDir, fileName));
		if (file.exists()) {
			file.delete();
		}

		Reg reg = create(file);

		if (reg != null) {
			try {
				//テスト用のデフォルト値を設定する
				reg.setInt("key1", 1);
				reg.setString("key2", "2");
			} catch (RegException e) {
				//値設定に失敗した場合は、テスト失敗
				Assert.fail(e.getKind().toString());
			} finally {
				reg.dispose();
			}
		}
		TestUtil.prompt(String.format("reg=new Reg(%s) => setInt(key1,1) => setString(key2,\"2\") => dispose()", fileName));
		return file;
	}

	// 後処理
	private void after(File file) {
		file.delete();
	}

	@Test
	public void 設定されている値をgetIntで読み出す() {
=======
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
>>>>>>> work

		//setUp
		Reg sut = new Reg(TestUtil.getTmpPath(tmpDir));
		sut.setInt("key1", 1);
		int expected = 1;

<<<<<<< HEAD
		String key = "key1";
		int actual = 0;
		try {
			actual = reg.getInt(key);
		} catch (RegException e) {
			Assert.fail(e.getKind().toString());
		}
		TestUtil.prompt(String.format("reg.getInt(\"%s\")= %d", key, actual));
		assertThat(actual, is(1));
		reg.dispose();
=======
		//exercise
		int actual = sut.getInt("key1");
>>>>>>> work

		//verify
		assertThat(actual, is(expected));
	}

	@Test
<<<<<<< HEAD
	public void 設定されている値をgetStringで読み出す() {
=======
	public void setStringで保存した値をgetStringで読み出す() throws Exception {

		//setUp
		Reg sut = new Reg(TestUtil.getTmpPath(tmpDir));
		sut.setString("key2", "2");
		String expected = "2";
>>>>>>> work

		//exercise
		String actual = sut.getString("key2");

<<<<<<< HEAD
		String key = "key2";
		String actual = "";
		try {
			actual = reg.getString(key);
		} catch (RegException e) {
			Assert.fail(e.getKind().toString());
		}
		TestUtil.prompt(String.format("reg.getString(\"%s\")= \"%s\"", key, actual));
		assertThat(actual, is("2"));
		reg.dispose();

		after(file); //後始末
	}

	@Test
	public void 保存されていないKeyを指定してgetIntで読み出すと例外が発生する() {
=======
		//verify
		assertThat(actual, is(expected));
	}

	@Test(expected = RegException.class)
	public void getIntで無効なkeyを指定すると例外が発生する() throws Exception {

		//setUp
		Reg sut = new Reg(TestUtil.getTmpPath(tmpDir));
>>>>>>> work

		//exercise
		sut.getInt("key1");
	}

	@Test(expected = RegException.class)
	public void getStringで無効なkeyを指定すると例外が発生する() throws Exception {
		//setUp
		Reg sut = new Reg(TestUtil.getTmpPath(tmpDir));

<<<<<<< HEAD
		TestUtil.prompt(String.format("reg.getInt(\"%s\") => RegException", key));
		try {
			@SuppressWarnings("unused")
			int actual = reg.getInt(key);
			Assert.fail("この行が実行されたらエラー");
		} catch (RegException ex) {
			reg.dispose();
			after(file); //後始末
			return;
		}
		Assert.fail("この行が実行されたらエラー");
	}

	@Test
	public void 保存されていないKeyを指定してgetStringで読み出すと例外が発生する() {
=======
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
>>>>>>> work

	@Test(expected = RegException.class)
	public void getStringでKeyにnullを指定すると例外が発生する() throws Exception {
		//setUp
		Reg sut = new Reg(TestUtil.getTmpPath(tmpDir));

		//exercise
		sut.getString(null);
	}

<<<<<<< HEAD
		TestUtil.prompt(String.format("reg.getString(\"%s\")  => RegException", key));
		try {
			@SuppressWarnings("unused")
			String actual = reg.getString(key);
			Assert.fail("この行が実行されたらエラー");
		} catch (RegException ex) {
			reg.dispose();
			after(file); //後始末
			return;
		}
		Assert.fail("この行が実行されたらエラー");
=======
	@Test(expected = RegException.class)
	public void setIntでKeyにnullを指定すると例外が発生する() throws Exception {
		//setUp
		Reg sut = new Reg(TestUtil.getTmpPath(tmpDir));
>>>>>>> work

		//exercise
		sut.setInt(null, 1);
	}

	@Test
<<<<<<< HEAD
	public void Keyにnullを指定してgetIntで読み出すと例外がが発生する() {

		File file = before("a005"); //前処理
		Reg reg = create(file);

		String key = null;

		TestUtil.prompt(String.format("reg.getString(\"%s\")  => RegException", key));
=======
	public void setIntでKeyにnullを指定して例外が発生しても元の値は破壊されない() throws Exception {
		//setUp
		Reg sut = new Reg(TestUtil.getTmpPath(tmpDir));
		sut.setInt("key1", 1); //元の値
		int expected = 1;

>>>>>>> work
		try {
			sut.setInt(null, 1);
		} catch (RegException ex) {
			; //nullを指定してsetIntすることで例外が発生する
		}
<<<<<<< HEAD
		Assert.fail("この行が実行されたらエラー");
	}

	@Test
	public void Keyにnullを指定してgetStringで読みだすと例外が発生する() {
=======
>>>>>>> work

		//exercise
		int actual = sut.getInt("key1");

		//verify
		assertThat(actual, is(expected));
	}

<<<<<<< HEAD
		TestUtil.prompt(String.format("reg.getString(%s)  => Regxception", key));
		try {
			@SuppressWarnings("unused")
			String actual = reg.getString(key);
			Assert.fail("この行が実行されたらエラー");
		} catch (RegException ex) {
			reg.dispose();
			after(file); //後始末
			return;
		}
		Assert.fail("この行が実行されたらエラー");
=======
	@Test(expected = RegException.class)
	public void setStringでKeyにnullを指定すると例外が発生する() throws Exception {
		//setUp
		Reg sut = new Reg(TestUtil.getTmpPath(tmpDir));
>>>>>>> work

		//exercise
		sut.setString(null, "2");
	}

	@Test
<<<<<<< HEAD
	public void Keyにnullを指定してsetIntで値を設定すると例外が発生する() {

		File file = before("a007"); //前処理
		Reg reg = create(file);

		String key = "TEST";
		int val = 123;
		try {
			reg.setInt(key, val);
		} catch (RegException e) {
			Assert.fail();
		}
		TestUtil.prompt(String.format("reg.setInt(%s,%d)", key, val));

		key = null;
		TestUtil.prompt(String.format("reg.getString(%s)  => RegException", key));
=======
	public void setStringでKeyにnullを指定して例外が発生しても元の値は破壊されない() throws Exception {
		//setUp
		Reg sut = new Reg(TestUtil.getTmpPath(tmpDir));
		sut.setString("key2", "2"); //元の値
		String expected = "2";

>>>>>>> work
		try {
			sut.setString(null, "3");
		} catch (RegException ex) {
<<<<<<< HEAD

			key = "TEST";
			int actual = 0;
			try {
				actual = reg.getInt(key);
			} catch (RegException e) {
				Assert.fail();
			}
			assertThat(actual, is(val));
			TestUtil.prompt(String.format("reg.getInt(%s)=%d", key, actual));

			reg.dispose();
			after(file); //後始末
			return;
=======
			; //nullを指定してsetIntすることで例外が発生する
>>>>>>> work
		}

		//exercise
		String actual = sut.getString("key2");

<<<<<<< HEAD
	@Test
	public void Keyにnullを指定してsetStringで値を設定すると例外が発生する() {

		File file = before("a008"); //前処理
		Reg reg = create(file);

		String key = null;
		String val = "123";

		TestUtil.prompt(String.format("reg.setString(%s,\"%s\") => RegException", key, val));
		try {
			reg.setString(key, val);
			Assert.fail("この行が実行されたらエラー");
		} catch (RegException ex) {
			reg.dispose();
			after(file); //後始末
			return;
		}
		Assert.fail("この行が実行されたらエラー");
	}

	@Test
	public void valにnullを指定してsetStringで値を設定すると空白が保存される() {

		File file = before("a009"); //前処理
		Reg reg = create(file);

		String key = "key2";
		String val = null;

		try {

			reg.setString(key, val);
			TestUtil.prompt(String.format("reg.setString(\"%s\",%s)", key, val));

			String actual = reg.getString(key);
			TestUtil.prompt(String.format("reg.getString(\"%s\")= \"%s\"", key, actual));
			assertThat(actual, is(""));

		} catch (RegException e) {
			Assert.fail();
		}
=======
		//verify
		assertThat(actual, is(expected));

	}

	@Test
	public void setStringでvalにnullを指定すると空白が保存される() throws Exception {
		//setUp
		Reg sut = new Reg(TestUtil.getTmpPath(tmpDir));
		sut.setString("key1", null);
		String expected = "";
>>>>>>> work

		//exercise
		String actual = sut.getString("key1");

		//verify
		assertThat(actual, is(expected));

	}

}
