package bjd.packet;

import java.nio.ByteBuffer;

public abstract class Packet {

	private ByteBuffer buf;

	public Packet(byte[] data, int offset) {
		int length = data.length - offset;
		buf = ByteBuffer.allocate(length);
		buf.put(data, offset, length);
	}
	
	public boolean setShort(short val) {
		byte[] buf = { (byte) (val >> 8), (byte) val };
		return set(name, buf);
	}	

	public abstract int length();

}
