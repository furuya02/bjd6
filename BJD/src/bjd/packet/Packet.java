package bjd.packet;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class Packet {

	/**
	 * 保持するパケット
	 */
	private ByteBuffer packets;
	/**
	 * 保持しているパケットサイズ<br>
	 * プロトコル上の有効無効には関係ない
	 */
	private int size;

	/**
	 * 最大サイズが不明な場合のコンストラクタ
	 * @param data
	 * @param offset
	 */
	public Packet(byte[] data, int offset) {
		size = data.length - offset;
		packets = ByteBuffer.allocate(size);
		packets.put(data, offset, size);
		packets.limit(size);
	}

	/**
	 * 最大サイズが判明している場合のコンストラクタ
	 * @param data
	 * @param offset
	 * @param length
	 */
	public Packet(byte[] data, int offset, int length) {
		size = length;
		packets = ByteBuffer.allocate(size);
		packets.put(data, offset, size);
		packets.limit(size);
	}

	/**
	 * 指定したオフセットへのbyte値の設定
	 * @param val
	 * @param offset
	 * @throws IOException
	 */
	protected final void setByte(byte val, int offset) throws IOException {
		int len = 1;
		comformeSize(offset, len);

		byte[] src = { (byte) val };
		packets.position(offset);
		packets.put(src, 0, len);
	}

	/**
	 * 指定したオフセットからのbyte値の取得
	 * @param offset
	 * @return
	 * @throws IOException
	 */
	protected final byte getByte(int offset) throws IOException {
		int len = 1;
		comformeSize(offset, len);

		packets.position(offset);
		return packets.get();

	}

	/**
	 * 指定したオフセットへのShot値の設定
	 * @param val
	 * @param offset
	 * @throws IOException
	 */
	protected final void setShort(short val, int offset) throws IOException {
		int len = 2;
		comformeSize(offset, len);

		byte[] src = { (byte) (val >> 8), (byte) val };
		packets.position(offset);
		packets.put(src, 0, len);
	}

	/**
	 * 指定したオフセットからのShot値の取得
	 * @param offset
	 * @throws IOException 
	 * @return Short値
	 */
	protected final Short getShort(int offset) throws IOException {
		int len = 2;
		comformeSize(offset, len);

		packets.position(offset);
		return packets.getShort();

	}

	/**
	 * 指定したオフセットへのint値の設定
	 * @param val
	 * @param offset
	 * @throws IOException
	 */
	protected final void setInt(int val, int offset) throws IOException {
		int len = 4;
		comformeSize(offset, len);

		byte[] src = { (byte) (val >> 24), (byte) (val >> 16), (byte) (val >> 8), (byte) val };
		packets.position(offset);
		packets.put(src, 0, len);
	}

	/**
	 * 指定したオフセットからのint値の取得
	 * @param offset
	 * @return
	 * @throws IOException
	 */
	protected final int getInt(int offset) throws IOException {
		int len = 4;
		comformeSize(offset, len);

		packets.position(offset);
		return packets.getInt();

	}

	protected final void setLong(long val, int offset) throws IOException {
		int len = 8;
		comformeSize(offset, len);

		byte[] src = { (byte) (val >> 52), (byte) (val >> 48), (byte) (val >> 40), (byte) (val >> 32), (byte) (val >> 24), (byte) (val >> 16), (byte) (val >> 8), (byte) val };
		packets.position(offset);
		packets.put(src, 0, len);
	}

	protected final long getLong(int offset) throws IOException {
		int len = 8;
		comformeSize(offset, len);

		packets.position(offset);
		return packets.getLong();

	}

	protected final byte[] getBytes(int offset, int len) throws IOException {

		comformeSize(offset, len);
		byte[] dst = new byte[len];

		packets.position(offset);
		packets.get(dst, 0, len);
		return dst;
	}

	/**
	 * パケットサイズのオーバーラン確認
	 * @param offset 開始位置 
	 * @param len 取得サイズ
	 * @throws IOException オーバしている場合、この例外が発生する
	 */
	private void comformeSize(int offset, int len) throws IOException {
		if (offset + len > size) {
			throw new IOException();
		}
	}

	public abstract int length();

}
