package bjd;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import bjd.util.TestUtil;

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
		TestUtil.dispPrompt(this, String.format("reg=new Reg(%s) => setInt(key1,1) => setString(key2,\"2\") => dispose()", fileName));
		return file;
	}


	// 後処理
	private void after(File file) {
		file.delete();
	}
	

	@Test
	public void a001() {

		TestUtil.dispHeader("a001 設定されている値を読み出す　getInt()");

		File file = before("a001"); //前処理
		Reg reg = create(file);

		String key = "key1";
		int actual = 0;
		try {
			actual = reg.getInt(key);
		} catch (RegException e) {
			Assert.fail(e.getKind().toString());
		}
		TestUtil.dispPrompt(this, String.format("reg.getInt(\"%s\")= %d", key, actual));
		assertThat(actual, is(1));
		reg.dispose();

		after(file); //後始末
	}

	@Test
	public void a002() {

		TestUtil.dispHeader("a002 設定されている値を読み出す　getString()");

		File file = before("a002"); //前処理
		Reg reg = create(file);

		String key = "key2";
		String actual = "";
		try {
			actual = reg.getString(key);
		} catch (RegException e) {
			Assert.fail(e.getKind().toString());
		}
		TestUtil.dispPrompt(this, String.format("reg.getString(\"%s\")= \"%s\"", key, actual));
		assertThat(actual, is("2"));
		reg.dispose();

		after(file); //後始末
	}

	@Test
	public void a003() {

		TestUtil.dispHeader("a003 保存されていないKeyで読み出すと例外が発生する　getInt()");

		File file = before("a003"); //前処理
		Reg reg = create(file);

		String key = "xxx";

		TestUtil.dispPrompt(this, String.format("reg.getInt(\"%s\") => RegException", key));
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
	public void a004() {

		TestUtil.dispHeader("a004 保存されていないKeyで読み出すと 例外が発生する　getString()");

		File file = before("a004"); //前処理
		Reg reg = create(file);

		String key = "xxx";

		TestUtil.dispPrompt(this, String.format("reg.getString(\"%s\")  => RegException", key));
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
	public void a005() {

		TestUtil.dispHeader("a005 Key=null で読み出すと例外がが発生する　getInt()");

		File file = before("a005"); //前処理
		Reg reg = create(file);

		String key = null;

		TestUtil.dispPrompt(this, String.format("reg.getString(\"%s\")  => RegException", key));
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
	public void a006() {

		TestUtil.dispHeader("a006 Key=null で読みだすと例外が発生する　getString()");

		File file = before("a006"); //前処理
		Reg reg = create(file);

		String key = null;

		TestUtil.dispPrompt(this, String.format("reg.getString(%s)  => Regxception", key));
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
	public void a007() {

		TestUtil.dispHeader("a007 Key=null で値を設定すると例外が発生する　setInt()");

		File file = before("a007"); //前処理
		Reg reg = create(file);

		String key = "TEST";
		int val = 123;
		try {
			reg.setInt(key, val);
		} catch (RegException e) {
			Assert.fail();
		}
		TestUtil.dispPrompt(this, String.format("reg.setInt(%s,%d)", key, val));

		key = null;
		TestUtil.dispPrompt(this, String.format("reg.getString(%s)  => RegException", key));
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
			TestUtil.dispPrompt(this, String.format("reg.getInt(%s)=%d", key, actual));

			reg.dispose();
			after(file); //後始末
			return;
		}
		Assert.fail("この行が実行されたらエラー");

	}

	@Test
	public void a008() {

		TestUtil.dispHeader("a008 Key=null で値を設定すると例外が発生する　setString()");

		File file = before("a008"); //前処理
		Reg reg = create(file);

		String key = null;
		String val = "123";

		TestUtil.dispPrompt(this, String.format("reg.setString(%s,\"%s\") => RegException", key, val));
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
	public void a009() {

		TestUtil.dispHeader("a009 val=null で値を設定すると\"\"が保存される　setString()");

		File file = before("a009"); //前処理
		Reg reg = create(file);

		String key = "key2";
		String val = null;

		try {

			reg.setString(key, val);
			TestUtil.dispPrompt(this, String.format("reg.setString(\"%s\",%s)", key, val));

			String actual = reg.getString(key);
			TestUtil.dispPrompt(this, String.format("reg.getString(\"%s\")= \"%s\"", key, actual));
			assertThat(actual, is(""));

		} catch (RegException e) {
			Assert.fail();
		}

		reg.dispose();

		after(file); //後始末

	}

}
