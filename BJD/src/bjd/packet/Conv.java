package bjd.packet;

import java.nio.ByteBuffer;

public final class Conv {

	//デフォルトコンストラクタの隠蔽
	private Conv() {

	}

	public static byte[] getBytes(short val) {
		byte[] buf = { (byte) (val >> 8), (byte) val };
		return buf;
	}

	public static byte[] getBytes(int val) {
		byte[] buf = { (byte) (val >> 24), (byte) (val >> 16), (byte) (val >> 8), (byte) val };
		return buf;
	}

	public static byte[] getBytes(long val) {
		byte[] buf = { (byte) (val >> 52), (byte) (val >> 48), (byte) (val >> 40), (byte) (val >> 32), (byte) (val >> 24), (byte) (val >> 16), (byte) (val >> 8), (byte) val };
		return buf;
	}

	public static short getShort(byte[] buf) {
		return ByteBuffer.wrap(buf, 0, 2).getShort();
	}

	public static short getShort(byte[] buf, int offset) {
		return ByteBuffer.wrap(buf, offset, 2).getShort();
	}

	public static int getInt(byte[] buf) {
		return ByteBuffer.wrap(buf, 0, 4).getInt();
	}

	public static int getInt(byte[] buf, int offset) {
		return ByteBuffer.wrap(buf, offset, 4).getInt();
	}

	public static long getLong(byte[] buf) {
		return ByteBuffer.wrap(buf, 0, 8).getLong();
	}

	public static long getLong(byte[] buf, int offset) {
		return ByteBuffer.wrap(buf, offset, 8).getLong();
	}

}
