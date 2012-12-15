package bjd.packet;

import java.io.InvalidObjectException;
import java.nio.ByteBuffer;

public final class OneField implements IField {
	private String name;
	private ByteBuffer buf;
	private int size;

	public OneField(String name, int size) {
		this.name = name;
		this.size = size;
		buf = ByteBuffer.allocate(size);
	}

	public String getName() {
		return name;
	}

	public boolean set(byte[] val) {
		if (val.length == size) {
			buf.clear();
			for (byte b : val) {
				buf.put(b);
			}
			return true;
		}
		return false;
	}

	public byte[] get() {
		buf.flip();
		return buf.array();
	}

	public short getShort() throws InvalidObjectException {
		if (size != 2) {
			throw new InvalidObjectException(String.format("getShort() size=%s", size));
		}
		buf.flip();
		return buf.getShort();
	}

	public int getInt() throws InvalidObjectException  {
		if (size != 4) {
			throw new InvalidObjectException(String.format("getInt() size=%s", size));
		}
		buf.flip();
		return buf.getInt();
	}

	public byte getByte() throws InvalidObjectException  {
		if (size != 1) {
			throw new InvalidObjectException(String.format("getByte() size=%s", size));
		}
		buf.flip();
		return buf.get();
	}

	public long getLong() throws InvalidObjectException {
		if (size != 8) {
			throw new InvalidObjectException(String.format("getLong() size=%s", size));
		}
		buf.flip();
		return buf.getLong();
	}
}