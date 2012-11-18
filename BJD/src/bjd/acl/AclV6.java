package bjd.acl;

import java.math.BigInteger;

import bjd.ValidObjException;
import bjd.net.InetKind;
import bjd.net.Ip;
import bjd.net.IpKind;

/**
 * 
 * IPv6のACL
 * 
 * コンストラクタの指定要領
 * AclV6(1122:3344::)
 * AclV6(1122:3344::-1122:3355::)
 * AclV6(1122:3355::-1122:3344::) 開始と終了が逆転してもよい
 * AclV6(1122:3344::/32)
 * AclV6(*:*:*:*:*:*:*:*)
 * AclV6(*)
 * 
 * @author SIN
 *
 */
final class AclV6 extends Acl {

	/**
	 * コンストラクタ<br>
	 * 初期化文字列によってstart、endが設定される
	 * 
	 * @param name 名前
	 * @param ipStr IPアドレス範囲を示す初期化文字列
	 * @throws パラメータが不正で初期化に失敗した場合 IllegalArgumentExceptionががスローされる
	 * 
	 */
	AclV6(String name, String ipStr) throws ValidObjException {
		super(name);

		//「*」によるALL指定
		if (ipStr.equals("*") || ipStr.equals("*:*:*:*:*:*:*:*")) {
			setStart(new Ip(IpKind.V6_0));
			setEnd(new Ip(IpKind.V6_FF));
			//setStatus(true);
			return; //初期化成功
		}

		String[] tmp;
		if (ipStr.indexOf('-') != -1) {
			//************************************************************
			// 「-」による範囲指定
			//************************************************************
			tmp = ipStr.split("-");
			if (tmp.length != 2) {
				throwException(ipStr); //初期化失敗
			}

			try {
				Ip ip = new Ip(tmp[0]);
				setStart(ip);
			} catch (IllegalArgumentException e) {
				throwException(ipStr); //初期化失敗
			}
			try {
				Ip ip = new Ip(tmp[1]);
				setEnd(ip);
			} catch (IllegalArgumentException e) {
				throwException(ipStr); //初期化失敗
			}

			//開始アドレスが終了アドレスより大きい場合、入れ替える
			if (getStart().getAddrV6H() == getEnd().getAddrV6H()) {
				if (getStart().getAddrV6L() > getEnd().getAddrV6L()) {
					swap(); // startとendの入れ替え
				}
			} else {
				if (getStart().getAddrV6H() > getEnd().getAddrV6H()) {
					swap(); // startとendの入れ替え
				}
			}

		} else if (ipStr.indexOf("/") != -1) {
			//************************************************************
			// 「/」によるマスク指定
			//************************************************************
			tmp = ipStr.split("/");
			if (tmp.length != 2) {
				throwException(ipStr); //初期化失敗
			}

			String strIp = tmp[0];
			String strMask = tmp[1];

			long maskH = 0;
			long maskL = 0;
			long xorH = 0;
			long xorL = 0;
			try {
				long m = Long.valueOf(strMask);
				if (m < 0 || 128 < m) {
					//マスクは128ビットが最大
					throwException(ipStr); //初期化失敗
				}

				for (long i = 0; i < 64; i++) {
					if (i != 0) {
						maskH = maskH << 1;
					}
					if (i < m) {
						maskH = (maskH | 1);
					}
				}
				xorH = (0xffffffffffffffffL ^ maskH);

				for (long i = 64; i < 128; i++) {
					if (i != 0) {
						maskL = maskL << 1;
					}
					if (i < m) {
						maskL = (maskL | 1);
					}
				}
				xorL = (0xffffffffffffffffL ^ maskL);
			} catch (Exception ex) {
				throwException(ipStr); //初期化失敗
			}
			try {
				Ip ip = new Ip(strIp);
				setStart(new Ip(ip.getAddrV6H() & maskH, ip.getAddrV6L() & maskL));
				setEnd(new Ip(ip.getAddrV6H() | xorH, ip.getAddrV6L() | xorL));
			} catch (IllegalArgumentException e) {
				throwException(ipStr); //初期化失敗
			}
		} else {
			//************************************************************
			// 通常指定
			//************************************************************
			try {
				Ip ip = new Ip(ipStr);
				setStart(ip);
				setEnd(ip);
			} catch (IllegalArgumentException e) {
				throwException(ipStr); //初期化失敗
			}
		}

		if (getStart().getInetKind() != InetKind.V6) {
			throwException(ipStr); //初期化失敗
		}
		if (getStart().getInetKind() != InetKind.V6) {
			throwException(ipStr); //初期化失敗
		}

		//最終チェック
		//setStatus(true); //初期化成功
	}

	@Override
	boolean isHit(Ip ip) {
		checkInitialise();

		BigInteger bigIp = new BigInteger(ip.getIpV6());
		BigInteger bigStart = new BigInteger(getStart().getIpV6());
		BigInteger bigEnd = new BigInteger(getEnd().getIpV6());

		if (bigIp.compareTo(bigStart) < 0) {
			return false;
		}
		if (bigEnd.compareTo(bigIp) < 0) {
			return false;
		}
		return true;
	}

	@Override
	protected void init() {
		//try {
		setStart(new Ip(IpKind.V6_0));
		setEnd(new Ip(IpKind.V6_FF));
		//} catch (ValidObjException e) {
		//	Util.runtimeException("AclV6 init()");
		//}
	}
}
