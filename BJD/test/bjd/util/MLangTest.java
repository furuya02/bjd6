package bjd.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.junit.Test;

public final class MLangTest {
	
	@Test
	public void getEncoding及びgetStringの確認() throws Exception {
		//setUp
		String str = "あいうえお";
		String[] charsetList = new String[] { "UTF-8", "EUC-JP", "ISO-2022-JP", "Shift_JIS" };

		//verify
		for (String charset : charsetList) {
			byte[] bytes = str.getBytes(charset);
			assertThat(MLang.getEncoding(bytes).toString(), is(charset));
			assertThat(MLang.getString(bytes), is(str));
		}
	}

	@Test
	public void getEncoding_fileName_の確認() throws Exception {

		//setUp
		File tempFile = File.createTempFile("tmp", ".txt");
		ArrayList<String> lines = new ArrayList<>();
		lines.add("あいうえお");
		Util.textFileSave(tempFile, lines);

		Charset sut = MLang.getEncoding(tempFile.getPath());
		String expected = "UTF-8";
		//exercise
		String actual = sut.name();
		//verify
		assertThat(actual, is(expected));
		//TearDown
		tempFile.delete();
	}

}
