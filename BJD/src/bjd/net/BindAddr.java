package bjd.net;

import java.util.ArrayList;

import bjd.ValidObj;
import bjd.ValidObjException;

/**
 * バインドアドレスを表現するクラス<br>
 * ValidObjを継承<br>
 * 
 * @author SIN
 *
 */
public final class BindAddr extends ValidObj  {
	private Ip ipV4;
	private Ip ipV6;
	private BindStyle bindStyle;

	/**
	 * IPv4アドレスの取得
	 * @return IpV4アドレス
	 */
	public Ip getIpV4() {
		checkInitialise();
		return ipV4;
	}

	/**
	 * IPv6アドレスの取得
	 * @return IpV6アドレス
	 */
	public Ip getIpV6() {
		checkInitialise();
		return ipV6;
	}

	/**
	 * バインド方法の取得
	 * @return バインドスタイル
	 */
	public BindStyle getBindStyle() {
		checkInitialise();
		return bindStyle;
	}

	/**
	 * 初期化<br>
	 * IPv4=INADDR_ANY<br>
	 * IPv6=IN6ADDR_ANY_INIT<br>
	 * ディアルバインド<br>
	 */
	@Override
	protected void init() {
		bindStyle = BindStyle.V4ONLY;
		ipV4 = new Ip(IpKind.INADDR_ANY);
		ipV6 = new Ip(IpKind.IN6ADDR_ANY_INIT);
	}

	/**
	 * コンストラクタ<br>
	 * すべてデフォルト値で初期化される
	 */
	public BindAddr() {
		init(); // デフォルト値での初期化
	}

	/**
	 * コンストラクタ
	 * @param bindStyle バインド方法
	 * @param ipV4 V4アドレス
	 * @param ipV6 V6アドレス
	 */
	public BindAddr(BindStyle bindStyle, Ip ipV4, Ip ipV6) {
		this.bindStyle = bindStyle;
		this.ipV4 = ipV4;
		this.ipV6 = ipV6;
	}

	/**
	 * コンストラクタ(文字列指定)
	 * 文字列が無効で初期化に失敗した場合は、例外(IllegalArgumentException)がスローされる<br>
	 * 初期化に失敗したオブジェクトを使用すると「実行時例外」が発生するので、生成時に必ず例外処理しなければならない<br>
	 * 
	 * @param str　指定文字列
	 * @throws ValidObjException 初期化失敗
	 */
	public BindAddr(String str) throws ValidObjException {
		if (str == null) {
			throwException(str); //初期化失敗
		}

		String[] tmp = str.split(",");
		if (tmp.length != 3) {
			throwException(str); //初期化失敗
		}

		if (tmp[0].equals("V4_ONLY") || tmp[0].equals("V4Only") || tmp[0].equals("V4ONLY")) {
			tmp[0] = "V4ONLY";
		} else if (tmp[0].equals("V6_ONLY") || tmp[0].equals("V6Only") || tmp[0].equals("V6ONLY")) {
			tmp[0] = "V6ONLY";
		} else if (tmp[0].equals("V46_DUAL") || tmp[0].equals("V46Dual") || tmp[0].equals("V46DUAL")) {
			tmp[0] = "V46DUAL";
		} else {
			throwException(str); //初期化失敗
		}

		try {
			bindStyle = BindStyle.valueOf(tmp[0]);
			ipV4 = new Ip(tmp[1]);
			ipV6 = new Ip(tmp[2]);
		} catch (Exception ex) {
			throwException(str); //初期化失敗
		}
		if (ipV4.getInetKind() != InetKind.V4) {
			throwException(str); //初期化失敗
		}
		if (ipV6.getInetKind() != InetKind.V6) {
			throwException(str); //初期化失敗
		}

	}

	/**
	 * 文字列化
	 */
	@Override
	public String toString() {
		checkInitialise();
		return String.format("%s,%s,%s", bindStyle, ipV4, ipV6);
	}

	/**
	 * 比較
	 */
	@Override
	public boolean equals(Object o) {
		checkInitialise();
		// 非NULL及び型の確認
		if (o == null || !(o instanceof BindAddr)) {
			return false;
		}
		BindAddr b = (BindAddr) o;

		if (bindStyle == b.getBindStyle()) {
			if (ipV4.equals(b.getIpV4())) {
				if (ipV6.equals(b.getIpV6())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		assert false : "Use is not assumed.";
		return 101;
	}

	/**
	 * プロトコルを指定してOneBindの配列を取得<br>
	 * 取得した配列分だけインターフェースへのbindが必要となる
	 * 
	 * @param protocolKind TCP/UDP
	 * @return　OneBind配列
	 */
	public OneBind[] createOneBind(ProtocolKind protocolKind) {
		checkInitialise();
		ArrayList<OneBind> ar = new ArrayList<>();
		if (bindStyle != BindStyle.V4ONLY) {
			ar.add(new OneBind(ipV6, protocolKind));
		}
		if (bindStyle != BindStyle.V6ONLY) {
			ar.add(new OneBind(ipV4, protocolKind));
		}
		return ar.toArray(new OneBind[0]);
	}

	/**
	 * 競合があるかどうかの確認
	 * @param b 比較するBindAddr
	 * @return 競合ありの場合、ｔrue
	 */
	public boolean checkCompetition(BindAddr b) {
		checkInitialise();
		boolean v4Competition = false; // V4競合の可能性
		boolean v6Competition = false; // V6競合の可能性
		switch (bindStyle) {
			case V46DUAL:
				if (b.getBindStyle().equals(BindStyle.V46DUAL)) {
					v4Competition = true;
					v6Competition = true;
				} else if (b.getBindStyle().equals(BindStyle.V4ONLY)) {
					v4Competition = true;
				} else {
					v6Competition = true;
				}
				break;
			case V4ONLY:
				if (!b.getBindStyle().equals(BindStyle.V6ONLY)) {
					v4Competition = true;
				}
				break;
			case V6ONLY:
				if (!b.getBindStyle().equals(BindStyle.V4ONLY)) {
					v6Competition = true;
				}
				break;
			default:
				break;
		}

		//V4競合の可能性がある場合
		if (v4Competition) {
			// どちらかがANYの場合は、競合している
			if (ipV4.getAny() || b.getIpV4().getAny()) {
				return true;
			}
			if (ipV4.equals(b.getIpV4())) {
				return true;
			}
		}
		// V6競合の可能性がある場合
		if (v6Competition) {
			// どちらかがANYの場合は、競合している
			if (ipV6.getAny() || b.ipV6.getAny()) {
				return true;
			}
			if (ipV6.equals(b.getIpV6())) {
				return true;
			}
		}
		return false;
	}
}
