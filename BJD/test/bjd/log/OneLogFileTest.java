package bjd.log;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Test;

import bjd.util.TestUtil;
import bjd.util.Util;

public final class OneLogFileTest {

	@Test
	public void a001() {

		TestUtil.dispHeader("a001 一度dispose()したファイルに正常に追加できるかどうか"); // TESTヘッダ

		String currentDir = new File(".").getAbsoluteFile().getParent(); // カレントディレクトリ
		String fileName = String.format("%s\\OneLogFileTest.txt", currentDir);

		(new File(fileName)).delete();

		TestUtil.dispPrompt(this); // TESTプロンプト
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

		TestUtil.dispPrompt(this); // TESTプロンプト
		System.out.println(String.format("lines.length==3"));

		TestUtil.dispPrompt(this); // TESTプロンプト
		System.out.println(String.format("new OneLogFile() -> set(\"4\") -> set(\"5\") -> set(\"6\") -> dispose()"));

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

		TestUtil.dispPrompt(this); // TESTプロンプト
		System.out.println(String.format("lines.length==6"));

		(new File(fileName)).delete();

	}
}
