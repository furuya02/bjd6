package bjd.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * エンコードの種類(Charset)を処理するクラス
 * @author SIN
 *
 */
public final class MLang {

	private MLang() {
		//デフォルトコンストラクタの隠蔽
	}
	
	/**
	 * エンコードの種類を取得
	 * @param fileName
	 * @return
	 */
	public static Charset getEncoding(String fileName) {
		try {
			File f = new File(fileName);
			long len = f.length();
			if (len > 1500) {
				len = 1500;
			}
			byte[] buf = new byte[(int) len];
			FileInputStream fs = new FileInputStream(fileName);
            fs.read(buf);
            fs.close();
    		return buf.length == 0 ? Charset.forName("ASCII") : getEncoding(buf);
        } catch (IOException e) {
            Util.runtimeException(e.getMessage());
        }
		return null;
	}

	/**
	 * エンコードの種類を検出してStringへ変換する
	 * @param buf
	 * @return
	 */
	public static String getString(byte[] buf) {

		int len = buf.length;
		if (len == 0) {
			return "";
		}
		Charset charset = getEncoding(buf);
		return new String(buf, charset);

	}

	/**
	 * エンコード種類の取得
	 * 雅階凡の C# プログラミング<br>
	 * 文字コードの判定 を参考にさせて頂きました。<br>
	 * http://www.geocities.jp/gakaibon/tips/csharp2008/charset-check.html<br>
	 * @param bytes
	 * @return
	 */
	public static Charset getEncoding(byte[] bytes) {

		int len = bytes.length;
		if (len > 1500) {
			len = 1500;
		}

		//【ASCIIコードかどうかの判定】
		boolean isAscii = true;

		//【日本語JISかどうかの判定】
		boolean isJis = false;
		for (int i = 0; i < len; i++) {
			int b1 = bytes[i] & 0xFF; 
			if (b1 > 0x7F) {
				isAscii = false;
				break; //範囲外
			}
			//ESCパターンの出現を確認できたら、ISO-2022-JPと判断する
			if (bytes[i] == 0x1b) {
				if (i < len - 2) {
					byte b2 = bytes[i + 1];
					byte b3 = bytes[i + 2];
					if (b2 == 0x28) {
						// ESC(B / ESC(J / ESC(I
						if (b3 == 0x42 || b3 == 0x4A || b3 == 0x49) {
							isJis = true;
							break;
						}

					} else if (b2 == 0x24) {
						// ESC$@ /  ESC$B 
						if (b3 == 0x40 || b3 == 0x42) {
							isJis = true;
							break;
						}
					}
				} else if (i < len - 3) {
					byte b2 = bytes[i + 1];
					byte b3 = bytes[i + 2];
					byte b4 = bytes[i + 3];
					if (b2 == 0x24 && b3 == 0x28 && b4 == 0x44) { // ESC$(D 
						isJis = true;
						break;
					}
				} else if (i < len - 5) {
					byte b2 = bytes[i + 1];
					byte b3 = bytes[i + 2];
					byte b4 = bytes[i + 3];
					byte b5 = bytes[i + 4];
					byte b6 = bytes[i + 5];
					if (b2 == 0x26 && b3 == 0x40 && b4 == 0x1B && b5 == 0x24 && b6 == 0x42) { // ESC&@ESC$B 
						isJis = true;
						break;
					}
				}
			}
		}
		if (isJis) {
			return Charset.forName("ISO-2022-JP"); //return Encoding.GetEncoding(50220);
		}
		if (isAscii) {
			return Charset.forName("ASCII"); //return Encoding.ASCII;
		}

		//【Shjift-JISの可能性と2バイトコードの出現数カウント】
		boolean isSjis = true;
		int sjis = 0;
		for (int i = 0; i < len; i++) {
			int b1 = bytes[i] & 0xFF; //byte b1 = bytes[i];
			if (b1 <= 0x7F) { //ASCII
			
			} else if (0xA1 <= b1 && b1 <= 0xDF) { //半角カタカナ

			} else if (i < len - 1) {
				int b2 = bytes[i + 1] & 0xFF; //byte b2 = bytes[i + 1];
				//第1バイト: 0x81～0x9F、0xE0～0xFC 第2バイト: 0x40～0x7E、0x80～0xFC
				if (((0x81 <= b1 && b1 <= 0x9F) || (0xE0 <= b1 && b1 <= 0xFC)) && ((0x40 <= b2 && b2 <= 0x7E) || (0x80 <= b2 && b2 <= 0xFC))) {
					i++;
					sjis++;
				} else {
					isSjis = false;
					break;
				}
			}
		}
		if (!isSjis) { //Shift-JISの可能性なし
			sjis = -1;
		}

		//【EUCの可能性と2バイトコードの出現数カウント】
		boolean isEuc = true;
		int euc = 0;
		for (int i = 0; i < len; i++) {
			int b1 = bytes[i] & 0xFF; //byte b1 = bytes[i];
			if (b1 <= 0x7F) { //ASCII	
				;
			} else if (i < len - 1) {
				int b2 = bytes[i + 1] & 0xFF; //byte b2 = bytes[i + 1];
				if ((b1 >= 0xA1 && b1 <= 0xFE) && (b2 >= 0xA1 && b2 <= 0xFE)) { //漢字
					i++;
					euc++;
				} else if ((b1 == 0x8E) && (b2 >= 0xA1 && b2 <= 0xDF)) { //半角カタカナ
					i++;
					euc++;
				} else if (i < len - 2) {
					int b3 = bytes[i + 2] & 0xFF; //byte b3 = bytes[i + 2];
					if ((b1 == 0x8F) && (b2 >= 0xA1 && b2 <= 0xFE) && (b3 >= 0xA1 && b3 <= 0xFE)) { // 補助漢字
						i += 2;
						euc++;
					} else {
						isEuc = false;
						break;
					}
				} else {
					isEuc = false;
					break;
				}
			}
		}
		if (!isEuc) { //EUCの可能性なし
			euc = -1;
		}

		//【UTF8の可能性と2バイトコードの出現数カウント】
		boolean isUtf8 = true;
		int utf8 = 0;
		for (int i = 0; i < len; i++) {
			int b1 = bytes[i] & 0xFF; //byte b1 = bytes[i];
			if (b1 <= 0x7F) { //ASCII
				
			} else if (i < len - 1) {
				int b2 = bytes[i + 1] & 0xFF; //byte b2 = bytes[i + 1];
				if ((0xC0 <= b1 && b1 <= 0xDF) && (0x80 <= b2 && b2 <= 0xBF)) { // 2 バイト 文字
					i += 1;
					utf8++;
				} else if (i < len - 2) {
					int b3 = bytes[i + 2] & 0xFF; //byte b3 = bytes[i + 2];
					if (b1 == 0xEF && b2 == 0xBB && b3 == 0xBF) {
						i += 2;
						utf8 += 2;
					} else if ((0xE0 <= b1 && b1 <= 0xEF) && (0x80 <= b2 && b2 <= 0xBF) && (0x80 <= b3 && b3 <= 0xBF)) { // 3バイト文字
						i += 2;
						utf8 += 2;
					} else if (i < len - 3) {
						int b4 = bytes[i + 3] & 0xFF; //byte b4 = bytes[i + 3];
						if ((0xF0 <= b1 && b1 <= 0xF7) && (0x80 <= b2 && b2 <= 0xBF) && (0x80 <= b3 && b3 <= 0xBF) && (0x80 <= b4 && b4 <= 0xBF)) { // 4バイト文字
							i += 3;
							utf8 += 3;
						} else {
							isUtf8 = false;
							break;
						}
					} else {
						isUtf8 = false;
						break;
					}
				}
			}
		}
		if (!isUtf8) { //UTF8の可能性なし
			utf8 = -1;
		}
		if (isSjis) {
			if (sjis > euc && sjis > utf8) {
				return Charset.forName("SHIFT-JIS"); //return Encoding.GetEncoding(932);
			}
		}
		if (isEuc) {
			if (euc > sjis && euc > utf8) {
				return Charset.forName("EUC-JP"); //return Encoding.GetEncoding(51932);
			}
		}
		if (isUtf8) {
			if (utf8 > sjis && utf8 > euc) {
				return Charset.forName("UTF-8"); //return Encoding.GetEncoding(65001);
			}
		}
		return null;
	}
}
