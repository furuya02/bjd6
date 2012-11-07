package bjd.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.codec.binary.Hex;

/**
 * 文字列を暗号化するクラス
 * 
 * @author SIN
 *
 */
public final class Crypt {

	private Crypt() {
		// デフォルトコンストラクタの隠蔽
	}

	private static String key = "ABCDEFGHIJKLMNOPQRSTUVWX"; //キー(24バイト)

	/**
	 * 暗号化
	 * @param str　平文
	 * @return　暗号化された文字列
	 * @throws Exception 
	 */
	public static String encrypt(String str) throws Exception {
		//if (str == null || str.equals("")) {
		if (str == null) {
			throw new Exception();
		}
		SecretKeyFactory keyFac = SecretKeyFactory.getInstance("DESede");
		DESedeKeySpec keySpec = new DESedeKeySpec(key.getBytes());
		SecretKey secKey = keyFac.generateSecret(keySpec);

		Cipher encoder = Cipher.getInstance("DESede");
		encoder.init(Cipher.ENCRYPT_MODE, secKey);
		byte[] b = encoder.doFinal(str.getBytes());
		return Hex.encodeHexString(b);
	}

	/**
	 * 複合化
	 * @param str　暗号化された文字列
	 * @return　平文
	 * @throws Exception 
	 */
	public static String decrypt(String str) throws Exception { 
		//if (str == null || str.equals("")) {
		if (str == null) {
			throw new Exception("");
		}
		SecretKeyFactory keyFac = SecretKeyFactory.getInstance("DESede");
		DESedeKeySpec keySpec = new DESedeKeySpec(key.getBytes());
		SecretKey secKey = keyFac.generateSecret(keySpec);

		Cipher decoder = Cipher.getInstance("DESede");
		decoder.init(Cipher.DECRYPT_MODE, secKey);

		byte[] b = Hex.decodeHex(str.toCharArray());
		return new String(decoder.doFinal(b));
	}
}
