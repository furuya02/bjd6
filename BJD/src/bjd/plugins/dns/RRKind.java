package bjd.plugins.dns;

public enum RrKind {
	QD(0),
	AN(1),
	NS(2),
	AR(3);

	private int intValue;

	private RrKind(final int intValue) {
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
	public static RrKind valueOf(final int intValue) {
		for (RrKind p : values()) {
			if (p.getIntValue() == intValue) {
				return p;
			}
		}
		return null;
	}
}
