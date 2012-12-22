package bjd.packet;

import java.nio.ByteBuffer;

public final class Conv {

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
		return ByteBuffer.wrap(buf).getShort();
	}

	public static int getInt(byte[] buf) {
		return ByteBuffer.wrap(buf).getInt();
	}

	public static long getLong(byte[] buf) {
		return getLong(buf,0);
	}

	public static long getLong(byte[] buf, int offset) {
		return ByteBuffer.wrap(buf).getLong(offset);
	}

}
