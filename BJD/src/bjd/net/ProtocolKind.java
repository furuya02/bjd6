package bjd.net;

/**
 * プロトコルの種類<br>
 * @author SIN
 * 
 */
public enum ProtocolKind {
	Tcp(0),
	Udp(1);

	private int intValue;

	private ProtocolKind(final int intValue) {
		this.intValue = intValue;
	}

	/**
	 * 定義されている定数を取得する
	 * @return 定数
	 */
	public int getIntValue() {
		return intValue;
	}

	/**
	 * 数値からEnum値を取得する
	 * @param intValue 定数
	 * @return Enum値
	 */
	public static ProtocolKind valueOf(final int intValue) {
		for (ProtocolKind p : values()) {
			if (p.getIntValue() == intValue) {
				return p;
			}
		}
		return null;
	}
}
