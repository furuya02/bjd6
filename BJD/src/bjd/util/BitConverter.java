package bjd.util;

import java.nio.ByteBuffer;

/**
 * 移植時に一時的にコンパイルを通すための関数群
 * @author SIN
 *
 */
public final class BitConverter {
	//コンストラクタの隠蔽
	private BitConverter(){
		;
	}
	public static short ToUInt16(byte[] data, int offSet) {
		ByteBuffer buf = ByteBuffer.allocate(2);
		buf.put(data, offSet, 2);
		buf.flip();
		return buf.getShort();
	}
	public static short ToInt16(byte[] data, int offSet) {
		ByteBuffer buf = ByteBuffer.allocate(2);
		buf.put(data, offSet, 2);
		buf.flip();
		return buf.getShort();
	}
	public static int ToUInt32(byte[] data, int offSet) {
		ByteBuffer buf = ByteBuffer.allocate(4);
		buf.put(data, offSet, 4);
		buf.flip();
		return buf.getInt();
	}
	public static long ToInt64(byte[] data, int offSet) {
		ByteBuffer buf = ByteBuffer.allocate(8);
		buf.put(data, offSet, 8);
		buf.flip();
		return buf.getLong();
	}
	public static long ToUInt64(byte[] data, int offSet) {
		ByteBuffer buf = ByteBuffer.allocate(8);
		buf.put(data, offSet, 8);
		buf.flip();
		return buf.getLong();
	}

	public static byte[] GetBytes(int i) {
		byte[] buf = new byte[4];
        buf[3] = (byte) (0x000000ff & (i));
        buf[2] = (byte) (0x000000ff & (i >>> 8));
        buf[1] = (byte) (0x000000ff & (i >>> 16));
        buf[0] = (byte) (0x000000ff & (i >>> 24));
		
//        buf[0] = (byte) (0x000000ff & (i));
//        buf[1] = (byte) (0x000000ff & (i >>> 8));
//        buf[2] = (byte) (0x000000ff & (i >>> 16));
//        buf[3] = (byte) (0x000000ff & (i >>> 24));
        return buf;
	}
}
