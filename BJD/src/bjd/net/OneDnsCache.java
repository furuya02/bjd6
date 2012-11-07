package bjd.net;

/**
 * DNSキャッシュのための１データを表現するクラス
 * @author SIN
 *
 */
public final class OneDnsCache {
	private String name;
	private Ip[] ipList;

	public OneDnsCache(String name, Ip[] ipList) {
		this.ipList = ipList;
		this.name = name;
	}

	/**
	 * 名前の取得
	 * @return 名前
	 */
	protected String getName() {
		return name;
	}

	/**
	 * IPアドレスの取得
	 * @return　IPアドレスの配列
	 */
	protected Ip[] getIpList() {
		return ipList;
	}
}