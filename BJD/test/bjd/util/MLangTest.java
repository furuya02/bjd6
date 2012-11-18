package bjd.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.junit.Test;

import bjd.test.TestUtil;

public final class MLangTest {

	@Test
	public void getEncoding及びgetStringの確認() {


		try {
			String str = "あいうえお";

			for (int i = 0; i < 4; i++) {
				String charset = "";
				switch (i) {
					case 0:
						charset = "UTF-8";
						break;
					case 1:
						charset = "EUC-JP";
						break;
					case 2:
						charset = "ISO-2022-JP";
						break;
					case 3:
						charset = "Shift_JIS";
						break;
					default:
						break;
				}
				byte[] bytes = str.getBytes(charset);
				TestUtil.prompt(String.format("byte[] buf=str.getBytes(\"%s\") => MLang.getEncoding(buf)=%s => MLang.getString(buf)=%s", charset, charset, str));
				assertThat(MLang.getEncoding(bytes).toString(), is(charset));
				assertThat(MLang.getString(bytes), is(str));
			}
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	@Test
	public void getEncoding_fileName_の確認() {

		String currentDir = new File(".").getAbsoluteFile().getParent(); // カレントディレクトリ
		String path = String.format("%s\\MLangTest.txt", currentDir);
		File file = new File(path);

		ArrayList<String> lines = new ArrayList<>();
		lines.add("あいうえお");
		Util.textFileSave(file, lines);

		Charset charset = MLang.getEncoding(file.getPath());
		//assertThat(charset.name(), is("Shift_JIS"));
		assertThat(charset.name(), is("UTF-8"));

		TestUtil.prompt(String.format("MLang.getEncoding(fileName)=%s", charset.name()));

		file.delete();
		
	}

}
