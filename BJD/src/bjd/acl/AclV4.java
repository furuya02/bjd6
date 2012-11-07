package bjd.acl;

import bjd.ValidObjException;
import bjd.net.InetKind;
import bjd.net.Ip;
import bjd.util.Util;

/**
 * 
 * IPv4のACL
 * 
 * コンストラクタの指定要領
 * AclV4(192.168.0.1)
 * AclV4(192.168.0.1-200)
 * AclV4(192.168.0.1-192.168.10.254)
 * AclV4(192.168.10.254-192.168.0.1) 開始と終了が逆転してもよい
 * AclV4(192.168.0.1/24)
 * AclV4(*.*.*,*)
 * AclV4(*)
 * 
 * @author SIN
 *
 */
final class AclV4 extends Acl {

	/**
	 * コンストラクタ<br>
	 * 初期化文字列によってstart、endが設定される
	 * 
	 * @param name 名前
	 * @param ipStr IPアドレス範囲を示す初期化文字列
	 * @throws ValidObjException パラメータが不正で初期化に失敗
	 * 
	 */
	AclV4(String name, String ipStr) throws ValidObjException {
		super(name);

		//「*」によるALL指定
		if (ipStr.equals("*") || ipStr.equals("*.*.*.*")) {
			
			setStart(new Ip("0.0.0.0"));
			setEnd(new Ip("255.255.255.255"));
			//setStatus(true);
			return; //初期化成功
		}

		//「*」表現を正規化する
		String[] tmp = ipStr.split("\\.");
		if (tmp.length == 4) {
			if (tmp[1].equals("*") && tmp[2].equals("*") && tmp[3].equals("*")) { //192.*.*.*
				ipStr = String.format("%s.0.0.0/8", tmp[0]);
			} else if (tmp[2].equals("*") && tmp[3].equals("*")) { //192.168.*.*
				ipStr = String.format("%s.%s.0.0/16", tmp[0], tmp[1]);
			} else if (tmp[3].equals("*")) { //192.168.0.*
				ipStr = String.format("%s.%s.%s.0/24", tmp[0], tmp[1], tmp[2]);
			}
		}

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
			String strTo = tmp[1];
			//to（終了アドレス）が192.168.2.254のように４オクテットで表現されているかどうかの確認
			tmp = strTo.split("\\.");
			if (tmp.length == 4) { //192.168.0.100
				try {
					Ip ip = new Ip(strTo);
					setEnd(ip);

				} catch (IllegalArgumentException e) {
					throwException(ipStr); //初期化失敗
				}
			} else if (tmp.length == 1) { //100
				//try {
				int n = Integer.valueOf(strTo);
				if (n < 0 || 255 < n) {
					throwException(ipStr); //初期化失敗
				}
				strTo = String.format("%d.%d.%d.%d", getStart().getIpV4()[0] & 0xFF, getStart().getIpV4()[1] & 0xFF, getStart().getIpV4()[2] & 0xFF, n);
				try {
					Ip ip = new Ip(strTo);
					setEnd(ip);
				} catch (IllegalArgumentException e) {
					throwException(ipStr); //初期化失敗
				}
			} else {
				throwException(ipStr); //初期化失敗
			}

			//開始アドレスが終了アドレスより大きい場合、入れ替える
			//if ((start.getAddrV4() & 0xFFFFFFFFL) > (end.getAddrV4() & 0xFFFFFFFFL)) {
			if (getStart().getAddrV4() > getEnd().getAddrV4()) {
				swap(); // startとendの入れ替え
			}
		} else if (ipStr.indexOf('/') != -1) {
			//************************************************************
			// 「/」によるマスク指定
			//************************************************************
			tmp = ipStr.split("/");
			if (tmp.length != 2) {
				throwException(ipStr); //初期化失敗
			}
			String strIp = tmp[0];
			String strMask = tmp[1];

			int mask = 0;
			int xor = 0;
			try {
				int m = Integer.valueOf(strMask);
				if (m < 0 || 32 < m) {
					//マスクは32ビットが最大
					throwException(ipStr); //初期化失敗
				}
				for (int i = 0; i < 32; i++) {
					if (i != 0) {
						mask = mask << 1;
					}
					if (i < m) {
						mask = (mask | 1);
					}
				}
				xor = (0xffffffff ^ mask);
			} catch (Exception ex) {
				throwException(ipStr); //初期化失敗
			}

			try {
				Ip ip = new Ip(strIp);
				Ip start = new Ip(ip.getAddrV4() & mask);
				Ip end = new Ip(ip.getAddrV4() | xor);
				setStart(start);
				setEnd(end);
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

		//最終チェック
		if (getStart().getInetKind() != InetKind.V4) {
			throwException(ipStr); //初期化失敗
		}
		if (getStart().getInetKind() != InetKind.V4) {
			throwException(ipStr); //初期化失敗
		}

		if (getStart().getAddrV4() != 0 || getEnd().getAddrV4() != 0) {
			if (getStart().getAddrV4() <= getEnd().getAddrV4()) {
				return; //setStatus(true); //初期化成功
			}
		}
	}

	@Override
	boolean isHit(Ip ip) {
		checkInitialise();

		long longIp = ip.getAddrV4() & 0xFFFFFFFFL;
		long longStart = getStart().getAddrV4() & 0xFFFFFFFFL;

		if (longIp < longStart) {
			return false;
		}
		long longEnd = getEnd().getAddrV4() & 0xFFFFFFFFL;
		if (longEnd < longIp) {
			return false;
		}
		return true;
	}

	@Override
	protected void init() {
		try {
			setStart(new Ip("0.0.0.0"));
			setEnd(new Ip("255.255.255.255"));
		} catch (ValidObjException e) {
			Util.runtimeException("AclV4 init()");
		}
	}
}
