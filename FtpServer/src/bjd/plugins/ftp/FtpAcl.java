package bjd.plugins.ftp;

/**
 * FTPユーザの権限の種類
 * @author SIN
 *
 */
enum FtpAcl {
	/*アップ・ダウン*/
	Full(0),
	/*ダウンロードのみ*/
	Down(1),
	/*アップロードのみ*/
	Up(2);

	private int intValue;

	private FtpAcl(final int intValue) {
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
	public static FtpAcl valueOf(final int intValue) {
		for (FtpAcl p : values()) {
			if (p.getIntValue() == intValue) {
				return p;
			}
		}
		return null;
	}
}

