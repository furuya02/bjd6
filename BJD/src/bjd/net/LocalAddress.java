package bjd.net;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import bjd.ValidObj;
import bjd.ValidObjException;
import bjd.util.Util;

/**
 * 自端末インターフェースのアドレスを列挙するクラス
 * 
 * @author SIN
 *
 */
public final class LocalAddress extends ValidObj {

	private ArrayList<Ip> v4 = new ArrayList<Ip>();
	private ArrayList<Ip> v6 = new ArrayList<Ip>();

	/**
	 * プログラムで唯一のインスタンスを返す
	 */
	private static LocalAddress localAddress = null;
	public static LocalAddress getInstance() {
		if (localAddress == null) {
			localAddress = new LocalAddress();
		}
		return localAddress;
	}

	/**
	 * (隠蔽)コンストラクタ
	 * 
	 * インターフェース状態を読み込んでリストを初期化する
	 */
	private LocalAddress() {
		init(); //初期化
		try {
			v4.add(new Ip("INADDR_ANY"));
			v6.add(new Ip("IN6ADDR_ANY_INIT"));
		} catch (ValidObjException ex) {
			//状息の初期化で例外となった場合は、実行時例外とする
			Util.runtimeException("Ip(INADDR_ANY) Ip(IN6ADDR_ANY_INIT)"); //実行時例外
		}

		Enumeration<NetworkInterface> interfaceList;
		try {
			interfaceList = NetworkInterface.getNetworkInterfaces();
			if (interfaceList != null) {
				while (interfaceList.hasMoreElements()) {
					NetworkInterface iface = interfaceList.nextElement();
					Enumeration<InetAddress> addrList = iface.getInetAddresses();
					if (!addrList.hasMoreElements()) {
						continue;
					}
					while (addrList.hasMoreElements()) {
						InetAddress inetAddress = addrList.nextElement();

						if (inetAddress instanceof Inet4Address) {
							String s = inetAddress.getHostAddress();
							try {
								v4.add(new Ip(s));
							} catch (ValidObjException e) {
								//システムから返された文字列でIpを初期化して例外が出るという事は、実行時例外とするしかない
								Util.runtimeException(String.format("inetAddress=%s", s)); //実行時例外
							}
						} else if (inetAddress instanceof Inet6Address) {
							boolean linkLocal = inetAddress.isLinkLocalAddress();
							boolean multicast = inetAddress.isMulticastAddress();
							boolean siteLocal = inetAddress.isSiteLocalAddress();
							if (!linkLocal && !multicast && !siteLocal) {
								String s = inetAddress.getHostAddress();
								try {
									v6.add(new Ip(s));
								} catch (ValidObjException e) {
									//システムから返された文字列でIpを初期化して例外が出るという事は、実行時例外とするしかない
									Util.runtimeException(String.format("inetAddress=%s", s)); //実行時例外
								}
							}
						}
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	/**
	 * コンストラクタ(リモートから受信した文字列による初期化)<br>
	 * remoteStr()で作成された文字列以外が挿入された場合、リスク回避のため、初期化失敗としてオブジェクトを利用を禁止する<br>
	 * 
	 * @param str　初期化文字列
	 * @throws ValidObjException 初期化失敗
	 */
	public LocalAddress(String str) throws ValidObjException  {
		init(); //初期化

		String[] tmp = str.split("\t");
		if (tmp.length != 2) {
			throwException(str); //例外終了
		}

		for (String s : tmp[0].split("\b", 0)) {
			try {
				Ip ip = new Ip(s);
				v4.add(ip);
			} catch (IllegalArgumentException e) {
				throwException(str); //例外終了
			}
		}
		for (String s : tmp[1].split("\b", 0)) {
			try {
				v6.add(new Ip(s));
			} catch (IllegalArgumentException e) {
				throwException(str); //例外終了
			}
		}
	}

	/*
	 * Remoteへの送信用文字列を生成する
	 * 
	 */
	public String remoteStr() {
		StringBuilder sb = new StringBuilder();
		for (Ip ip : v4) {
			sb.append(ip + "\b");
		}
		sb.append("\t");
		for (Ip ip : v6) {
			sb.append(ip + "\b");
		}
		return sb.toString();
	}

	/**
	 * 初期化
	 */
	@Override
	protected void init() {
		v4 = new ArrayList<Ip>();
		v6 = new ArrayList<Ip>();
	}

	/**
	 * IPv4アドレスの一覧
	 * 
	 * @return IPv4リスト
	 */
	public Ip[] getV4() {
		checkInitialise();
		return v4.toArray(new Ip[]{});
	}

	/**
	 * IPv6アドレスの一覧
	 * 
	 * @return IPv6リスト
	 */
	public Ip[] getV6() {
		checkInitialise();
		return v6.toArray(new Ip[]{});
	}
}
