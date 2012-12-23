package bjd.plugins.dns;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Calendar;

import bjd.net.Ip;
import bjd.util.Util;

/**
 * １つのリソースレコードを表現するクラス<br>
 * ONeRRの内部データは、ネットワークバイトオーダで保持されている<br>
 * 【タイプは、A NS CNAME MX PTR SOAの6種類のみに限定する】<br>
 * 
 * @author SIN
 *
 */
public class OneRR {

	private DnsType dnsType;
	private long createTime; //データが作成された日時
	private int ttl; //内部のネットワークバイトオーダのまま取得される
	private String name;
	private byte[] data;

	/**
	 * コンストラクタ
	 * @param name
	 * @param dnsType
	 * @param ttl
	 * @param data
	 */
	public OneRR(String name, DnsType dnsType, int ttl, byte[] data) {
		createTime = Calendar.getInstance().getTimeInMillis();
		this.name = name;
		this.dnsType = dnsType;
		this.ttl = ttl;
		this.data = Arrays.copyOf(data, data.length);
	}

	public final DnsType getDnsType() {
		return dnsType;
	}

	public final String getName() {
		return name;
	}

	public final byte[] getData() {
		return data;
	}

	public final int getTtl() {
		return ttl;
	}

	@Override
	public final boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		OneRR r = ((OneRR) o);
		if (!name.equals(r.getName())) {
			return false;
		}
		if (dnsType != r.getDnsType()) {
			return false;
		}
		if (ttl != r.getTtl()) {
			return false;
		}
		byte[] tmp = r.getData();
		if (data.length != tmp.length) {
			return false;
		}
		for (int i = 0; i < data.length; i++) {
			if (data[i] != tmp[i]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public final int hashCode() {
		assert false : "Use is not assumed.";
		return 101;
	}

	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append(" ");
		sb.append(dnsType);
		sb.append(" ");
		sb.append(String.format("TTL=0x%x", ttl));
		sb.append(" ");

		if (data.length != 0) {
			if (dnsType == DnsType.A) {
				Ip ip = new Ip(ByteBuffer.wrap(data, 0, 4).getInt());

				sb.append(ip.toString());
				sb.append(" ");
			}
			if (dnsType == DnsType.Aaaa) {
				//var v6H = BitConverter.ToUInt64(Data, 0);
				long v6H = ByteBuffer.wrap(data, 0, 8).getLong();
				//var v6L = BitConverter.ToUInt64(Data, 8);
				long v6L = ByteBuffer.wrap(data, 8, 16).getLong();
				Ip ip = new Ip(Util.htonl(v6H), Util.htonl(v6L));

				sb.append(ip.toString());
				sb.append(" ");
			}
			if (dnsType == DnsType.Cname || dnsType == DnsType.Ptr || dnsType == DnsType.Ns) {
				byte[] dataName = Arrays.copyOfRange(data, 0, data.length);
				sb.append(DnsUtil.dnsName2Str(dataName));
				sb.append(" ");

			}
			if (dnsType == DnsType.Mx) {
				//ushort preference = BitConverter.ToUInt16(Data, 0);
				short preference = ByteBuffer.wrap(data, 0, 16).getShort();

				//byte[] dataName = new byte[Data.Length - 2];
				//Buffer.BlockCopy(Data, 2, dataName, 0, Data.Length - 2);
				byte[] dataName = Arrays.copyOfRange(data, 2, data.length - 1);

				sb.append(String.format("%s %d", DnsUtil.dnsName2Str(dataName), Util.htons(preference)));
				sb.append(" ");

			}
		}
		//		return dnsType == DnsType.Soa ? name : "ERROR";
		return sb.toString();
	}

	/**
	 * データの有効・無効判断
	 * @param now
	 * @return
	 */
	public final boolean isEffective(long now) {
		if (ttl == 0) {
			return true;
		}
		//Ver5.7.3 みずき氏から情報提供いただきました
		//    long ttl = Util.htonl(Ttl2);
		//    if (_createTime + ttl < now)
		// nowとcreateTimeはTicksから得ているので100ns単位
		long t = Util.htonl(ttl);
		t *= 10000000;
		if (createTime + t >= now) {
			return true;
		}
		return false;
	}
}
