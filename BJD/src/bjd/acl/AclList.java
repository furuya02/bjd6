package bjd.acl;

import java.util.ArrayList;
import java.util.Calendar;

import bjd.ValidObjException;
import bjd.log.LogKind;
import bjd.log.Logger;
import bjd.net.InetKind;
import bjd.net.Ip;
import bjd.option.Dat;
import bjd.option.OneDat;
import bjd.sock.SockObj;

/**
 * 複数のACLを保持して、範囲に該当するかどうかをチェックする<br>
 * ACLの初期化に失敗した場合は、Loggerにエラーを表示し、その行は無効になる
 * 
 * @author SIN
 *
 */
public final class AclList {
	private ArrayList<Acl> arV4 = new ArrayList<>();
	private ArrayList<Acl> arV6 = new ArrayList<>();
	private boolean enable; //許可:0 不許可:1
	private Logger logger;

	/**
	 * Dat dat==null　で初期化された場合、全てenableNumで指定したものになる<br>
	 * dat=null enableNum=0(不許可) => All Deny<br>
	 * dat=null enableNum=1(許可) => All Allow<br>
	 * 
	 * @param dat
	 * @param enableNum 
	 * @param logger
	 */
	public AclList(Dat dat, int enableNum, Logger logger) {
		this.enable = (enableNum == 1);
		this.logger = logger;
		if (dat == null) {
			return;
		}
		for (OneDat o : dat) {
			if (o.isEnable()) { //有効なデータだけを対象にする
				String name = o.getStrList().get(0);
				String ipStr = o.getStrList().get(1);

				if (ipStr.equals("*")) { //全部
					try {
						Acl acl = new AclV4(name, ipStr);
						arV4.add(acl);
					} catch (ValidObjException e) {
						logger.set(LogKind.ERROR, (SockObj) null, 9000034, String.format("Name:%s Address %s", name, ipStr));
					}
					try {
						Acl acl = new AclV6(name, ipStr);
						arV6.add(acl);
					} catch (ValidObjException e) {
						logger.set(LogKind.ERROR, (SockObj) null, 9000034, String.format("Name:%s Address %s", name, ipStr));
					}
				} else if (ipStr.indexOf('.') != -1) { //IPv4ルール
					try {
						Acl acl = new AclV4(name, ipStr);
						arV4.add(acl);
					} catch (ValidObjException e) {
						logger.set(LogKind.ERROR, (SockObj) null, 9000034, String.format("Name:%s Address %s", name, ipStr));
					}
				} else { //IPv6ルール
					try {
						Acl acl = new AclV6(name, ipStr);
						arV6.add(acl);
					} catch (ValidObjException e) {
						logger.set(LogKind.ERROR, (SockObj) null, 9000034, String.format("Name:%s Address %s", name, ipStr));
					}
				}
			}
		}
	}

	/**
	 * ACLリストへのIP追加 ダイナミックにアドレスをDenyリストに加えるためのメソッド<br>
	 * 追加に失敗した場合、Loggerにはエラー表示されるが、追加は無効（追加されない） 
	 * @param ip 追加するIp 
	 * @return 成否
	 */
	public boolean append(Ip ip) {
		if (!enable) {
			return false;
		}

		if (ip.getInetKind() == InetKind.V4) {
			for (Acl a : arV4) {
				if (a.isHit(ip)) {
					return false;
				}
			}
		} else {
			for (Acl a : arV6) {
				if (a.isHit(ip)) {
					return false;
				}
			}
		}

		Calendar c = Calendar.getInstance();
		String name = String.format("AutoDeny-%s", c.toString());

		if (ip.getInetKind() == InetKind.V4) {
			try {
				Acl acl = new AclV4(name, ip.toString());
				arV4.add(acl);
			} catch (ValidObjException e) {
				logger.set(LogKind.ERROR, (SockObj) null, 9000034, String.format("Name:%s Address %s", name, ip.toString()));
			}
		} else {
			try {
				Acl acl = new AclV6(String.format("AutoDeny-%s", c.toString()), ip.toString());
				arV6.add(acl);
			} catch (ValidObjException e) {
				logger.set(LogKind.ERROR, (SockObj) null, 9000034, String.format("Name:%s Address %s", name, ip.toString()));
			}
		}
		return true;
	}

	/**
	 * ACLリストにヒットするかどうかのチェック<br>
	 * 範囲にヒットしたものが有効/無効のフラグ（enable）も内部で評価されている
	 * 
	 * @param ip 検査対象IP
	 * @return AclKind  Allow or Deny　
	 */
	public AclKind check(Ip ip) {

		//ユーザリストの照合
		Acl acl = null;
		if (ip.getInetKind() == InetKind.V4) {
			for (Acl p : arV4) {
				if (p.isHit(ip)) {
					acl = p;
					break;
				}
			}
		} else {
			for (Acl p : arV6) {
				if (p.isHit(ip)) {
					acl = p;
					break;
				}
			}
		}

		if (!enable && acl == null) {
			logger.set(LogKind.SECURE, (SockObj) null, 9000017, String.format("address:%s", ip.toString())); //このアドレスからのリクエストは許可されていません
			return AclKind.Deny;
		}
		if (enable && acl != null) {
			logger.set(LogKind.SECURE, (SockObj) null, 9000018, String.format("user:%s address:%s", acl.getName(), ip.toString())); //この利用者のアクセスは許可されていません
			return AclKind.Deny;
		}
		return AclKind.Allow;
	}
}
