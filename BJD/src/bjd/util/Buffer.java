package bjd.util;

/**
 * 移植時に一時的にコンパイルを通すための関数群
 * @author SIN
 *
 */
public final class Buffer {
	//コンストラクタの隠蔽
	private Buffer() {
		;
	}
	
	/**
	 * Buffer.BlockCopy(C#)の移植用<br>
	 * 第３パラメータの byte[]dst は、戻り値に変更する必要がある
	 * 第４パラメータの int dstOffsetは0に固定される
	 * @param src
	 * @param srcOffset
	 * @param count
	 * @return
	 */
	public static byte [] BlockCopy(byte[] src, int srcOffset, int count) {
		byte [] dst = new byte[count];
		System.arraycopy(src, srcOffset, dst, 0, count);  
		return dst;
	}
	
}
