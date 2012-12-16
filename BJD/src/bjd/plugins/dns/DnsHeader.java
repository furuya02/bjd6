package bjd.plugins.dns;

import java.io.InvalidObjectException;
import java.nio.ByteBuffer;

import bjd.packet.ListField;
import bjd.packet.OneField;
import bjd.util.Util;

public final class DnsHeader {
	private ListField listField = new ListField("DNS_HEADER");

	//	[StructLayout(LayoutKind.Sequential, Pack = 1)]
	//	class HeaderDns {
	//		public ushort Id;             // 識別
	//        public ushort Flags;          // 各種フラグ
	//        [MarshalAs(UnmanagedType.ByValArray, SizeConst = 4)]
	//        readonly public ushort[] Count;       // 質問数(QD) 回答数(AN) オーソリティ数(NS) 追加情報数(AR)
	//        public HeaderDns() {
	//            Count = new ushort[4];
	//        }
	//	}
	public DnsHeader() {
		listField.add(new OneField("ID", 2)); // 識別
		listField.add(new OneField("FLAGS", 2)); // 各種フラグ

		// 各RRのフィールド数
		ListField count = new ListField("COUNT");
		count.add(new OneField("QD", 2)); // 質問数(QD)
		count.add(new OneField("AN", 2)); // 回答数(AN)
		count.add(new OneField("NS", 2)); // オーソリティ数(NS)
		count.add(new OneField("AR", 2)); // 追加情報数(AR)
		listField.add(count);
	}

	public void init(byte[] packet) {
		ByteBuffer buf = ByteBuffer.allocate(12);
		buf.put(packet, 0, 12);
		buf.flip();

		listField.setShort("ID", buf.getShort());
		listField.setShort("FLAGS", buf.getShort());
		listField.setShort("QA", buf.getShort());
		listField.setShort("AN", buf.getShort());
		listField.setShort("NS", buf.getShort());
		listField.setShort("AR", buf.getShort());
	}

	public int length() {
		return listField.length();
	}

	public boolean setId(short id) {
		return listField.setShort("ID", id);
	}

	public short getId() {
		try {
			return listField.getShort("ID");
		} catch (InvalidObjectException e) {
			Util.runtimeException(this, e); //設計上の問題
		}
		return 0; //これが返される事はない
	}

	public boolean setFlags(short flags) {
		return listField.setShort("FLAGS", flags);
	}

	public short getFlags(){
		try {
			return listField.getShort("FLAGS");
		} catch (InvalidObjectException e) {
			Util.runtimeException(this, e); //設計上の問題
		}
		return 0; //これが返される事はない
	}

	public void setCount(int rr, short count) {
		listField.setShort(getCountTag(rr), count);
	}

	public short getCount(int rr) {
		try {
			return listField.getShort(getCountTag(rr));
		} catch (InvalidObjectException e) {
			Util.runtimeException(this, e); //設計上の問題
		}
		return 0; //これが返される事はない
	}

	private String getCountTag(int rr) {
		if (rr == 0) {
			return "QD";
		} else if (rr == 1) {
			return "AN";
		} else if (rr == 2) {
			return "NS";
		} else if (rr == 3) {
			return "AR";
		}
		//設計上の問題
		Util.runtimeException(String.format("DnsHeader.getCountTag(%d)", rr));
		return null;
	}

}
