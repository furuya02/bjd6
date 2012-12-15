package bjd.plugins.dns;

import java.io.InvalidObjectException;

import bjd.packet.ListField;
import bjd.packet.OneField;

public final class DnsHeader {
	private ListField listField = new ListField("DNS_HEADER");

	public DnsHeader() {
		listField.add(new OneField("ID", 2)); // 識別
		listField.add(new OneField("FLAGS", 2)); // 各種フラグ

		ListField count = new ListField("COUNT"); // 各種フラグ
		count.add(new OneField("QD", 2)); // 質問数(QD)
		count.add(new OneField("AN", 2)); // 回答数(AN)
		count.add(new OneField("NS", 2)); // オーソリティ数(NS)
		count.add(new OneField("AR", 2)); // 追加情報数(AR)

		listField.add(count); // 各種フラグ
	}

	public boolean setId(short id) {
		byte[] buf = {(byte)(id >> 8), (byte)id}; 
		return listField.set("ID", buf);
	}
	
	public short getId() throws InvalidObjectException {
		return listField.getShort("ID");
	}

}
