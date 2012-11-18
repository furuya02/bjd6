package bjd.net;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import bjd.ValidObj;
import bjd.ValidObjException;
import bjd.util.Util;

/**
 * Ipアドレスを表現するクラス<br>
 * ValidObjを継承<br>
 * Ipv4,Ipv6に対応<br>
 * 初期化に失敗したオブジェクトを使用すると、実行時例外が発生する<br>
 * 
 * @author SIN
 *
 */
public final class Ip extends ValidObj {

	private InetKind inetKind;
	private boolean any;
	private byte[] ipV4;
	private byte[] ipV6;
	private int scopeId;

	/**
	 * スコープIDの取得
	 * @return スコープID
	 */
	public int getScopeId() {
		checkInitialise();
		return scopeId;
	}

	/**
	 * IpV4のバイト配列取得
	 * @return バイト配列  byte[4]
	 */
	public byte[] getIpV4() {
		checkInitialise();
		return ipV4;
	}

	/**
	 * IpV6のバイト配列取得
	 * @return バイト配列  byte[16]
	 */
	public byte[] getIpV6() {
		checkInitialise();
		return ipV6;
	}

	/**
	 * INADDR_ANY 若しくは、IN6ADDR_ANY_INITかどうかの取得
	 * @return true/false
	 */
	public boolean getAny() {
		checkInitialise();
		return any;
	}

	/**
	 * プロトコルの種類を取得
	 * @return V4 or V6
	 */
	public InetKind getInetKind() {
		checkInitialise();
		return inetKind;
	}

	/**
	 * 初期化
	 */
	@Override
	protected void init() {
		init(InetKind.V4);
	}

	/**
	 * デフォルト値の初期化
	 * @param inetKind TCP/UDP
	 */
	void init(InetKind inetKind) {
		this.inetKind = inetKind;
		ipV4 = new byte[] { 0, 0, 0, 0 };
		ipV6 = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		any = false;
		scopeId = 0;
	}

	/**
	 * デフォルトコンストラクタの隠蔽
	 */
	@SuppressWarnings("unused")
	private Ip() {
		init(InetKind.V4);
	}

	public Ip(InetKind inetKind) {
		init(inetKind);
	}

	// コンストラクタ
	/**
	 * コンストラクタ<br>
	 * 初期化文字列でIPアドレスを初期化する<br>
	 * 文字列が無効で初期化に失敗した場合は、例外(ValidObjException)がスローされる<br>
	 * 初期化に失敗したオブジェクトを使用すると「実行時例外」が発生するので、生成時に必ず例外処理しなければならない<br>
	 * 
	 * <初期化例><br>
	 * Ip(192.168.0.1)<br>
	 * Ip(INADDR_ANY)<br>
	 * Ip(IN6ADDR_ANY_INIT)<br>
	 * Ip(0.0.0.0)<br>
	 * Ip(::)<br>
	 * Ip(::1)<br>
	 * Ip([12::78:90ab])  [括弧付きで指定された場合]<br>
	 * 
	 * @param ipStr　初期化文字列
	 * @throws ValidObjException 初期化失敗
	 */
	public Ip(String ipStr) throws ValidObjException {
		init(ipStr);
	}

	/**
	 * InetAddrによるコンストラクタ
	 * @param inetAddress
	 */
	public Ip(InetAddress inetAddress) {
		String ipStr = inetAddress.toString();
		if (ipStr.charAt(0) == '/') {
			ipStr = ipStr.substring(1);
		}
		try {
			init(ipStr);
		} catch (ValidObjException e) {
			//InetAddressからの生成では、原則として例外は発生しないはず
			Util.runtimeException(this, e);
		}
	}

	/**
	 * IpKindによるコンストラクタ
	 * @param ipKind
	 */
	public Ip(IpKind ipKind) {
		String ipStr = "";
		switch (ipKind) {
		case V4_0:
			ipStr = "0.0.0.0";
			break;
		case V4_255:
			ipStr = "255.255.255.255";
			break;
		case V6_0:
			ipStr = "::";
			break;
		case V6_FF:
			ipStr = "FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF";
			break;
		case INADDR_ANY:
			ipStr = "INADDR_ANY";
			break;
		case IN6ADDR_ANY_INIT:
			ipStr = "IN6ADDR_ANY_INIT";
			break;
		case V4_LOCALHOST:
			ipStr = "127.0.0.1";
			break;
		case V6_LOCALHOST:
			ipStr = "::1";
			break;
		default:
			//定義が不足している場合
			Util.runtimeException(String.format("Ip(IpKind) ipKind=%s", ipKind));
			break;
		}
		try {
			init(ipStr);
		} catch (ValidObjException e) {
			//ここで例外が発生するのは、設計上の問題
			Util.runtimeException(this, e);
		}
	}

	/**
	 * コンストラクタ<br>
	 * IPv4のバイトオーダで初期化する<br>
	 * @param ip バイトオーダ 　4byte(32bit)
	 */
	public Ip(int ip) {
		init(InetKind.V4); // デフォルト値での初期化

		ipV4 = ByteBuffer.allocate(4).putInt(ip).array();
		if (isAllZero(ipV4)) {
			any = true;
		}
	}

	/**
	 * コンストラクタ<br>
	 * IPv6のバイトオーダで初期化する<br>
	 * 
	 * @param h 上位バイトオーダ　8byte(64bit)
	 * @param l　下位バイトオーダ　8byte(64bit)
	 */
	public Ip(long h, long l) {

		init(InetKind.V6); // デフォルト値での初期化
		byte[] b = ByteBuffer.allocate(8).putLong(h).array();
		for (int i = 0; i < 8; i++) {
			ipV6[i] = b[i];
		}
		b = ByteBuffer.allocate(8).putLong(l).array();
		for (int i = 0; i < 8; i++) {
			ipV6[i + 8] = b[i];
		}
	}

	private void init(String ipStr) throws ValidObjException {
		init(InetKind.V4);

		if (ipStr == null) {
			throwException(ipStr); //例外終了
		}

		if (ipStr.equals("INADDR_ANY")) { // IPV4
			any = true;
		} else if (ipStr.equals("IN6ADDR_ANY_INIT")) { // IPV6
			init(InetKind.V6);
			any = true;
		} else if (ipStr.indexOf('.') > 0) { // IPV4
			// 名前で指定された場合は、例外に頼らずここで処理する（高速化）
			for (int i = 0; i < ipStr.length(); i++) {
				char c = ipStr.charAt(i);
				if (c != '.' && (c < '0' || '9' < c)) {
					throwException(ipStr); //例外終了
				}
			}
			String[] tmp = ipStr.split("\\.");
			try {
				// length==3 ネットアドレスでnewされた場合
				if (tmp.length == 4 || tmp.length == 3) {
					for (int i = 0; i < tmp.length; i++) {
						int n = Integer.valueOf(tmp[i]);
						if (n < 0 || 255 < n) {
							init(inetKind); // デフォルト値での初期化
							throwException(ipStr); //例外終了
						}
						ipV4[i] = (byte) n;
					}
				} else {
					throwException(ipStr); //例外終了
				}
			} catch (Exception ex) {
				throwException(ipStr); //例外終了
			}
		} else if (ipStr.indexOf(":") >= 0) { // IPV6
			init(InetKind.V6);

			String[] tmp = ipStr.split("\\[|\\]");
			if (tmp.length == 2) {
				ipStr = tmp[1];
			}
			int index = ipStr.indexOf('%');
			if (index >= 0) {
				try {
					scopeId = Integer.valueOf(ipStr.substring(index + 1));
				} catch (Exception ex) {
					scopeId = 0;
				}
				ipStr = ipStr.substring(0, index);
			}
			tmp = ipStr.split(":");

			int n = ipStr.indexOf("::");
			if (0 <= n) {
				StringBuilder sb = new StringBuilder();
				sb.append(ipStr.substring(0, n));
				for (int i = tmp.length; i < 8; i++) {
					sb.append(":");
				}
				sb.append(ipStr.substring(n));
				tmp = sb.toString().split(":", -1);
				if (tmp.length != 8) {
					String[] m = new String[] { "", "", "", "", "", "", "", "" };
					for (int i = 0; i < 8; i++) {
						m[i] = tmp[i];
					}
					tmp = m;
				}
			}
			if (tmp.length != 8) {
				throwException(ipStr); //例外終了
			}
			for (int i = 0; i < 8; i++) {
				if (tmp[i].length() > 4) {
					throwException(ipStr); // 例外終了
				}

				if (tmp[i].equals("")) {
					ipV6[i * 2] = 0;
					ipV6[i * 2 + 1] = 0;
				} else {
					int u = Integer.valueOf(tmp[i], 16);
					byte[] b = ByteBuffer.allocate(2).putShort((short) u).array();
					ipV6[i * 2] = b[0];
					ipV6[i * 2 + 1] = b[1];
				}
			}
		} else {
			throwException(ipStr); //例外終了
		}
	}

	/**
	 * アドレスが同じかどうかの検査
	 * @param o Ipオブジェクト
	 */
	@Override
	public boolean equals(Object o) {
		checkInitialise();
		// 非NULL及び型の確認
		if (o == null || !(o instanceof Ip)) {
			return false;
		}
		Ip ip = (Ip) o;
		if (ip.getInetKind() == inetKind) {
			if (inetKind == InetKind.V4) {
				byte[] b = ip.getIpV4();
				for (int i = 0; i < 4; i++) {
					if (ipV4[i] != b[i]) {
						return false;
					}
				}
			} else {
				if (ip.getScopeId() != scopeId) {
					return false;
				}
				byte[] b = ip.getIpV6();
				for (int i = 0; i < 16; i++) {
					if (ipV6[i] != b[i]) {
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		assert false : "Use is not assumed.";
		return 100;
	}

	/**
	 * 1回だけ省略表記を使用する
	 * @author SIN
	 *
	 */
	private enum State {
		UNUSED, //未使用
		USING, //使用中
		FINISH, //使用済
	}

	/**
	 * 文字列化
	 */
	@Override
	public String toString() {
		checkInitialise();
		if (inetKind == InetKind.V4) {
			if (any) {
				return "INADDR_ANY";
			}
			return String.format("%d.%d.%d.%d", (ipV4[0] & 0xff), (ipV4[1] & 0xff), (ipV4[2] & 0xff), (ipV4[3] & 0xff));
		}
		if (any) {
			return "IN6ADDR_ANY_INIT";
		}

		if (isAllZero(ipV6)) {
			return "::0";
		}

		StringBuilder sb = new StringBuilder();
		State state = State.UNUSED; //未使用
		for (int i = 0; i < 8; i++) {
			ByteBuffer b = ByteBuffer.allocate(2).put(ipV6, i * 2, 2);
			b.position(0);
			short u = b.getShort();
			if (u == 0) {
				if (state == State.UNUSED) { // 未使用の場合
					state = State.USING; // 使用中に設定する
					sb.append(":");
				} else if (state == State.FINISH) { // 使用済の場合、0を表記する
					sb.append(String.format(":%x", u));
					// sb.AppendFormat(":{0:x}", u);
				}
			} else {
				if (state == State.USING) { // 使用中の場合は
					state = State.FINISH; // 使用済に設定する
				}
				if (i == 0) {
					sb.append(String.format("%x", u));
				} else {
					sb.append(String.format(":%x", u));
				}
			}
		}
		if (state == State.USING) { // 使用中で終了した場合は:を足す
			sb.append(":");
		}
		if (scopeId != 0) {
			sb.append(String.format("%%%d", scopeId));
		}
		return sb.toString();
	}

	/**
	 * IPv4  バイトオーダ<br>
	 * この値を比較に使用する場合は、longへのキャストが必要 (getAddrV4()&0xFFFFFFFFL)
	 * @return int 4byte(32bit) 
	 */
	public int getAddrV4() {
		checkInitialise();
		if (inetKind == InetKind.V4) {
			ByteBuffer byteBuffer = ByteBuffer.allocate(4);
			byteBuffer.put(ipV4[0]);
			byteBuffer.put(ipV4[1]);
			byteBuffer.put(ipV4[2]);
			byteBuffer.put(ipV4[3]);
			byteBuffer.rewind();
			return byteBuffer.getInt();
		}
		return 0;
	}

	/**
	 * IPv6 上位バイトオーダ
	 * @return byte[] 8byte(64bit) 
	 */
	public long getAddrV6H() {
		checkInitialise();
		if (inetKind == InetKind.V6) {
			ByteBuffer byteBuffer = ByteBuffer.allocate(8);
			for (int i = 0; i < 8; i++) {
				byteBuffer.put(ipV6[i]);
			}
			byteBuffer.rewind();
			return byteBuffer.getLong();
		}
		return 0;
	}

	/**
	 * IPv6 下位バイトオーダ
	 * @return byte[] 8byte(64bit) 
	 */
	public long getAddrV6L() {
		checkInitialise();
		if (inetKind == InetKind.V6) {
			ByteBuffer byteBuffer = ByteBuffer.allocate(8);
			for (int i = 0; i < 8; i++) {
				byteBuffer.put(ipV6[i + 8]);
			}
			byteBuffer.rewind();
			return byteBuffer.getLong();
		}
		return 0;
	}

	/**
	 * InetAddress(Inet4Address/Inet6Address)形式での取得
	 * 変換に失敗した場合、例外がスローされる
	 * 
	 * @return　InetAddress
	 * @throws UnknownHostException 
	 */
	public InetAddress getInetAddress() throws UnknownHostException {
		checkInitialise();
		if (any) {
			if (inetKind == InetKind.V4) {
				return Inet4Address.getByName("0.0.0.0");
			} else {
				return Inet6Address.getByName("0::0");
			}
		}
		if (inetKind == InetKind.V4) {
			return Inet4Address.getByAddress(netBytes());
		}
		return Inet6Address.getByAddress("", netBytes(), scopeId);
	}

	/**
	 * ネットワークバイトオーダ<br>
	 * getInetAddress()内で使用される
	 * @return
	 */
	private byte[] netBytes() {
		checkInitialise();
		if (inetKind == InetKind.V4) {
			return ipV4;
		}
		return ipV6;
	}

	/**
	 * バイト値がすべて0かどうかの検査
	 * @param buf 検査対象バッファ
	 * @return 全部0の場合true
	 */
	private boolean isAllZero(byte[] buf) {
		for (byte b : buf) {
			if (b != 0) {
				return false;
			}
		}
		return true;
	}

}
