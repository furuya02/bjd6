package bjd.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import bjd.test.TestUtil;

@RunWith(Enclosed.class)
public final class FileSearchTest {
	@RunWith(Theories.class)
	public static final class 各種のパタｰﾝで列挙する {
		private static File tmpDir;

		@BeforeClass
		public static void before() {

			//テンポラリディレクトリの作成
			String currentDir = new File(".").getAbsoluteFile().getParent();
			String path = String.format("%s\\tmpDir", currentDir);
			tmpDir = new File(path);
			if (!tmpDir.exists()) {
				try {
					tmpDir.mkdir();

					//000.txt
					//001.txt
					//002.txt
					for (int i = 0; i < 3; i++) {
						String fileName = String.format("%s\\%03d.txt", tmpDir.getPath(), i);
						(new File(fileName)).createNewFile();
					}
					//000.tgz
					//001.tgz
					//002.tgz
					for (int i = 0; i < 3; i++) {
						String fileName = String.format("%s\\%03d.tgz", tmpDir.getPath(), i);
						(new File(fileName)).createNewFile();
					}
					//<a>
					//<b>
					//<n>
					for (int i = 0; i < 3; i++) {
						String fileName = String.format("%s\\%c", tmpDir.getPath(), i + 'a');
						(new File(fileName)).mkdir();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

		@AfterClass
		public static void doAfterClass() {
			//テンポラリディレクトリの削除
			Util.fileDelete(tmpDir);
		}

		@DataPoints
		public static Fixture[] datas = {
				new Fixture("*", new String[] { "000.txt", "001.txt", "002.txt", "000.tgz", "001.tgz", "002.tgz", "a", "b", "c" }),
				new Fixture("*.*", new String[] { "000.txt", "001.txt", "002.txt", "000.tgz", "001.tgz", "002.tgz" }),
				new Fixture("*.txt", new String[] { "000.txt", "001.txt", "002.txt" }),
				new Fixture("??1.*", new String[] { "001.txt", "001.tgz" }),
				new Fixture("?", new String[] { "a", "b", "c" }),
		};

		static class Fixture {
			private String pattern;
			private String[] expected;

			public Fixture(String pattern, String[] expected) {
				this.pattern = pattern;
				Arrays.sort(expected);
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {


			FileSearch fileSearch = new FileSearch(tmpDir.getPath());
			File[] files = fileSearch.listFiles(fx.pattern);
			Arrays.sort(files);

			TestUtil.prompt(String.format("[%s] length=%d ", fx.pattern, files.length));
			for (int i = 0; i < files.length; i++) {
				TestUtil.prompt(String.format("%s ", files[i].getName()));
			}

			assertThat(files.length, is(fx.expected.length));

			for (int i = 0; i < fx.expected.length; i++) {
				assertThat(files[i].getName(), is(fx.expected[i]));
			}
			TestUtil.prompt("");
		}
	}
}
