package bjd.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;

/**
 * 文字列をBase64文字列に変換するクラス
 * 
 * @author SIN
 *
 */
public final class Base64 {

	private static final String CHARSET = "UTF-8";
	private static final String ENCODING = "base64";

	private Base64() {
		// デフォルトコンストラクタの隠蔽
	}

	/**
	 * 文字列をBase64文字列にエンコードする
	 * @param src　変換前の文字列
	 * @return Base64文字列
	 * @throws IOException
	 * @throws MessagingException
	 */
	public static String encode(String src) throws IOException, MessagingException {
		if (src == null) {
			return "";
		}
		String res = "";
		OutputStream out = null;
		ByteArrayOutputStream outStream = null;
		
		//例外が発生しても、特にオブジェクト初期化の必要はない
		try {
			outStream = new ByteArrayOutputStream();
			out = MimeUtility.encode(outStream, ENCODING);
			out.write(src.getBytes(CHARSET));
		} finally {
			if (out != null) {
				out.close();
			}
		}
		if (outStream != null) {
			res = outStream.toString();
			if (res.length() >= 2) {
				res = res.substring(0, res.length() - 2); // \r\nの削除
			}
		}
		return res;
	}

	/**
	 * Base64文字列を文字列にデコードする
	 * @param src　Base64文字列
	 * @return 変換された文字列
	 * @throws IOException
	 * @throws MessagingException
	 */
	public static String decode(String src) throws IOException, MessagingException {
		ByteArrayInputStream inputStream = null;
		InputStream in = null;
		String res = "";

		//例外が発生しても、特にオブジェクト初期化の必要はない
		try {
			inputStream = new ByteArrayInputStream(src.getBytes(CHARSET));
			in = MimeUtility.decode(inputStream, ENCODING);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			int len;
			byte[] buf = new byte[2048];
			while ((len = in.read(buf)) >= 0) {
				outputStream.write(buf, 0, len);
			}
			res = new String(outputStream.toByteArray(), CHARSET);
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return res;
	}
}
