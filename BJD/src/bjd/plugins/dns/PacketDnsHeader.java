package bjd.plugins.dns;

import java.io.IOException;

import bjd.packet.Packet;
import bjd.util.Util;

public final class PacketDnsHeader extends Packet {

	private static int length = 12;

	public PacketDnsHeader() {
		super(new byte[length], 0, length);
	}

	public PacketDnsHeader(byte[] data, int offset) throws IOException {
		super(data, offset, length);
		if (data.length - offset < length) {
			throw new IOException("A lack of data");
		}

	}

	private final int pID = 0;
	private final int pFLAGS = 2;
	private final int pQD = 4;
	private final int pAN = 6;
	private final int pNS = 8;
	private final int pAR = 10;

	/**
	 * パケットサイズ
	 */
	@Override
	public int length() {
		return length;
	}

	/**
	 * バイトイメージの取得
	 * @return
	 */
	@Override
	public byte[] getBytes() {
		try {
			return this.getBytes(0, length);
		} catch (IOException e) {
			//設計上の問題
			Util.runtimeException(this, e);
		}
		return null; //これが返されることは無い
	}

	/**
	 * 識別子の設定
	 * @param val 識別子
	 * @throws IOException
	 */
	public void setId(short val) throws IOException {
		setShort(val, pID);
	}

	/**
	 * 識別子の取得
	 * @return
	 * @throws IOException
	 */
	public short getId() throws IOException {
		return getShort(pID);
	}

	/**
	 * フラグの設定
	 * @param val フラグ値
	 * @throws IOException
	 */
	public void setFlags(short val) throws IOException {
		setShort(val, pFLAGS);
	}

	/**
	 * フラグの取得
	 * @return
	 * @throws IOException
	 */
	public short getFlags() throws IOException {
		return getShort(pFLAGS);
	}

	/**
	 * RR数の設定
	 * @param rr 
	 * @param count
	 * @throws IOException 
	 */
	public void setCount(int rr, short count) throws IOException {
		setShort(count, getRRPos(rr));
	}

	/**
	 * RR数の取得
	 * @param rr
	 * @return
	 * @throws IOException
	 */
	public short getCount(int rr) throws IOException {
		return getShort(getRRPos(rr));
	}

	private int getRRPos(int rr) {
		if (rr == 0) {
			return pQD;
		} else if (rr == 1) {
			return pAN;
		} else if (rr == 2) {
			return pNS;
		} else if (rr == 3) {
			return pAR;
		}
		//設計上の問題
		Util.runtimeException(String.format("DnsHeader.getCountTag(%d)", rr));
		return 0;
	}

}
