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
public final class OneRR {

	private DnsType dnsType;
	private long createTime; //データが作成された日時
	private int ttl2; //内部のネットワークバイトオーダのまま取得される
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
		this.ttl2 = ttl;
		this.data = Arrays.copyOf(data, data.length);
	}

	public DnsType getDnsType() {
		return dnsType;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		if (dnsType == DnsType.A) {
			//int addr = BitConverter.ToUInt32(Data, 0);
			int addr = ByteBuffer.wrap(data, 0, 4).getInt();
			Ip ip = new Ip(Util.htonl(addr));
			return ip.toString();
		}
		if (dnsType == DnsType.Aaaa) {
			//var v6H = BitConverter.ToUInt64(Data, 0);
			long v6H = ByteBuffer.wrap(data, 0, 8).getLong();
			//var v6L = BitConverter.ToUInt64(Data, 8);
			long v6L = ByteBuffer.wrap(data, 8, 16).getLong();
			Ip ip = new Ip(Util.htonl(v6H), Util.htonl(v6L));
			return ip.toString();
		}
		if (dnsType == DnsType.Cname || dnsType == DnsType.Ptr || dnsType == DnsType.Ns) {
			return N1();
		}
		if (dnsType == DnsType.Mx) {
			//ushort preference = BitConverter.ToUInt16(Data, 0);
			short preference = ByteBuffer.wrap(data, 0, 16).getShort();

			//byte[] dataName = new byte[Data.Length - 2];
			//Buffer.BlockCopy(Data, 2, dataName, 0, Data.Length - 2);
			byte[] dataName = Arrays.copyOfRange(data, 2, data.length - 1);
			return String.format("%s %d", DnsUtil.dnsName2Str(dataName), Util.htons(preference));
		}
		return dnsType == DnsType.Soa ? N1() : "ERROR";
	}

	/**
	 * データの有効・無効判断
	 * @param now
	 * @return
	 */
	public boolean isEffective(long now) {
		if (ttl2 == 0) {
			return true;
		}

		//Ver5.7.3 みずき氏から情報提供いただきました
		//    long ttl = Util.htonl(Ttl2);
		//    if (_createTime + ttl < now)
		// nowとcreateTimeはTicksから得ているので100ns単位
		long ttl = Util.htonl(ttl2) * 10000000;
		if (createTime + ttl >= now)
			return true;
		return false;
	}

	public String N1() {
		if (dnsType == DnsType.Cname || dnsType == DnsType.Ptr || dnsType == DnsType.Ns || dnsType == DnsType.Soa) {
			return DnsUtil.dnsName2Str(data);
		}
		if (dnsType == DnsType.Mx) {
			byte[] dataName = Arrays.copyOfRange(data, 2, data.length - 1);
			return DnsUtil.dnsName2Str(dataName);
		}
		return "";
	}



}
/*
namespace DnsServer {

    internal class OneRR {


   }
}
*/