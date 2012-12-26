package bjd.plugins.dns;

import java.io.IOException;

import bjd.packet.Packet;
import bjd.util.Util;

public final class PacketRr extends Packet {
	// Name 不定幅のため、このクラスでは取り扱わない
	// short type
	// short class
	// int ttl
	// byte DataLength
	// byte[] RData

	private final int pTYPE = 0;
	private final int pCLS = 2;
	private final int pTTL = 4;
	private final int pDLEN = 8;
	private final int pDATA = 10;

	// デフォルトコンストラクラの隠蔽
	private PacketRr() {
		super(new byte[0], 0);
	}

	private boolean isQD = false;

	/**
	 * パケットを生成する場合のコンストラクタ
	 * @param dlen
	 */
	public PacketRr(int dlen) {
		//dlenが0の時、QDを表す（dLen及びdataが存在しない）
		super((dlen == 0) ? (new byte[8]) : (new byte[10 + dlen]), 0);
		if (dlen == 0) {
			isQD = true;
		}
	}

	/**
	 * パケットを解析するためのコンストラクタ
	 * @param data
	 * @param offset
	 * @throws IOException 
	 */
	public PacketRr(byte[] data, int offset) throws IOException {
		super(data, offset);
		if (data.length - offset < 10) {
			throw new IOException("A lack of data");
		}
	}

	public DnsType getType() throws IOException {
		short d = getShort(pTYPE);
		return DnsUtil.short2DnsType(d);
	}

	public void setType(DnsType val) throws IOException {
		short n = DnsUtil.dnsType2Short(val);
		setShort(n, pTYPE);
	}

	public short getCls() throws IOException {
		return getShort(pCLS);
	}

	public void setCls(short val) throws IOException {
		setShort(val, pCLS);
	}

	public int getTtl() throws IOException {
		return getInt(pTTL);
	}

	public void setTtl(int val) throws IOException {
		setInt(val, pTTL);
	}

	public short getDLen() throws IOException {
		if (isQD) {
			return 0;
		}
		return getShort(pDLEN);
	}

	public byte[] getData() throws IOException {
		if (isQD) {
			return new byte[0];
		}
		return getBytes(pDATA, getDLen());
	}
	
	public void setData(byte[] val) throws IOException {
		setBytes(val, pDATA);
	}

	
	@Override
	public int length() {
		if (isQD) {
			return 8; //dlen及びdataが存在しない
		}
		try {
			int dataLen = getDLen();
			return 2 + 2 + 4 + dataLen + 1;
		} catch (IOException e) {
			Util.runtimeException(this, e);
		}
		return 0;
	}

	@Override
	public byte[] getBytes() {
		try {
			return this.getBytes(0, length());
		} catch (IOException e) {
			Util.runtimeException(this, e); //設計上の問題
		}
		return null; //これが返されることはない
	}

}
