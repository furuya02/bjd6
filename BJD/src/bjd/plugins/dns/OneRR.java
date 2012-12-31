package bjd.plugins.dns;

import java.util.Arrays;
import java.util.Calendar;

import bjd.util.Util;

/**
 * １つのリソースレコードを表現するクラス<br>
 * ONeRRの内部データは、ネットワークバイトオーダで保持されている<br>
 * 【タイプは、A NS CNAME MX PTR SOAの6種類のみに限定する】<br>
 * 
 * @author SIN
 *
 */
public abstract class OneRr {

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
	public OneRr(String name, DnsType dnsType, int ttl, byte[] data) {
		createTime = Calendar.getInstance().getTimeInMillis();
		this.name = name;
		this.dnsType = dnsType;
		this.ttl = ttl;
		this.data = Arrays.copyOf(data, data.length);
	}

	/**
	 * TTL値だけを変更したクローンを生成する
	 * @param t TTL値
	 * @return OnrRrオブジェクト
	 */
	public final OneRr clone(int t) {
		switch (dnsType) {
			case A:
				return new RrA(name, t, data);
			case Aaaa:
				return new RrAaaa(name, t, data);
			case Ns:
				return new RrNs(name, t, data);
			case Mx:
				return new RrMx(name, t, data);
			case Cname:
				return new RrCname(name, t, data);
			case Ptr:
				return new RrPtr(name, t, data);
			case Soa:
				return new RrSoa(name, t, data);
			default:
				Util.runtimeException(String.format("OneRr.close() not implement DnsType=%s", dnsType));
				break;
		}
		return null; //これは実行されない
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

	public final byte[] getData(int offset, int len) {
		byte[] dst = new byte[len];
		System.arraycopy(data, offset, dst, 0, len);
		return dst;
	}

	public final byte[] getData(int offset) {
		int len = data.length - offset;
		return getData(offset, len);
	}

	public final int getTtl() {
		return ttl;
	}

	/**
	 * equalsの実装は、テストで使用される<br>
	 * 内部変数の値が、全て等しいときにtrueとなる<br>
	 * TTLの値も検証するので、プロダクトコードには使用する場所が無い<br>
	 */
	@Override
	public final boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		OneRr r = ((OneRr) o);
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
		long t = ttl * 1000;
		//t *= 1000;
		if (createTime + t >= now) {
			return true;
		}
		return false;
	}
}
