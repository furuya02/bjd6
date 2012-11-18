package bjd;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import bjd.test.TestUtil;

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

		File file = before("a001"); //前処理
		Reg reg = create(file);

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

		after(file); //後始末
	}

	@Test
	public void 設定されている値をgetStringで読み出す() {

		File file = before("a002"); //前処理
		Reg reg = create(file);

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

		File file = before("a003"); //前処理
		Reg reg = create(file);

		String key = "xxx";

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

		File file = before("a004"); //前処理
		Reg reg = create(file);

		String key = "xxx";

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

	}

	@Test
	public void Keyにnullを指定してgetIntで読み出すと例外がが発生する() {

		File file = before("a005"); //前処理
		Reg reg = create(file);

		String key = null;

		TestUtil.prompt(String.format("reg.getString(\"%s\")  => RegException", key));
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
	public void Keyにnullを指定してgetStringで読みだすと例外が発生する() {

		File file = before("a006"); //前処理
		Reg reg = create(file);

		String key = null;

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

	}

	@Test
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
		try {
			@SuppressWarnings("unused")
			int actual = reg.getInt(key);
			Assert.fail("この行が実行されたらエラー");
		} catch (RegException ex) {

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
		}
		Assert.fail("この行が実行されたらエラー");

	}

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

		reg.dispose();

		after(file); //後始末

	}

}
