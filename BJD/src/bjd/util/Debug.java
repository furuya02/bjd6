package bjd.util;

public final class Debug {
	
	/**
	 * 処理時間計測のデバッグ用クラス
	 */
	private Debug() {
		//コンストラクタ隠蔽
	}

	/**
	 * 計測
	 * @param o
	 * @param str
	 */
	public static void print(Object o, String str) {
		System.out.println(String.format("[%3d] %s %s", Thread.currentThread().getId(), o.getClass().getSimpleName(), str));
	}
}
