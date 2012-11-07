package bjd.net;

import bjd.ValidObj;
import bjd.ValidObjException;

/**
 * MACアドレスを表現するクラス<br>
 * ValidObjを継承<br>
 * 
 * @author SIN
 *
 */
public final class Mac extends ValidObj  {

	private byte[] m = new byte[6];
	
	/**
	 * コンストラクタ(文字列)
	 * 初期化文字列でMACアドレスを初期化する<br>
	 * 文字列が無効で初期化に失敗した場合は、例外(IllegalArgumentException)がスローされる<br>
	 * 初期化に失敗したオブジェクトを使用すると「実行時例外」が発生するので、生成時に必ず例外処理しなければならない<br>
	 * 
	 * @param macStr
	 * @throws ValidObjException 初期化失敗
	 */
	public Mac(String macStr) throws ValidObjException {
		if (macStr.length() != 17) {
			throwException("buf.length!=6"); //初期化失敗
		}

		for (int i = 0; i < 6; i++) {
			String str = macStr.substring(i * 3, i * 3 + 2);
			try {
				m[i] = (byte) Integer.parseInt(str, 0x10);
			} catch (NumberFormatException ex) {
				throwException("buf.length!=6"); //初期化失敗
			}
		}
	}

	/**
	 * コンストラクタ (バイトオーダ)
	 * @param buf 6バイトで表現されたMACアドレス
	 * @throws ValidObjException 初期化失敗
	 */
	public Mac(byte[] buf) throws ValidObjException {
		if (buf.length != 6) {
			throwException("buf.length!=6"); //初期化失敗
		}
		for (int i = 0; i < 6; i++) {
			m[i] = buf[i];
		}
	}

	/**
	 * 初期化
	 */
	@Override
	protected void init() {
		for (int i = 0; i < 6; i++) {
			m[i] = 0;
		}
	}
	
	/**
	 * バイトオーダの取得
	 * @return byte[6]
	 */
	public byte[] getBytes() {
		checkInitialise();
		return m;
	}

	/**
	 * 比較
	 */
	@Override
	public boolean equals(Object obj) {
		checkInitialise();
		if (obj == null) {
			return false;
		}
		if (obj instanceof Mac) {
			byte[] o = ((Mac) obj).getBytes();
			for (int i = 0; i < 6; i++) {
				if (o[i] != m[i]) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return -1; //super.hashCode();
	}

	/**
	 * 文字列化
	 */
	@Override
	public String toString() {
		checkInitialise();
		return String.format("%02X-%02X-%02X-%02X-%02X-%02X", m[0], m[1], m[2], m[3], m[4], m[5]);
	}
	
}
