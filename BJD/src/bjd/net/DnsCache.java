package bjd.net;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import bjd.ValidObjException;
import bjd.log.LogKind;
import bjd.log.Logger;
import bjd.util.Util;

/**
 * DNSのキャッシュ
 * @author SIN
 *
 */
public final class DnsCache {
	private ArrayList<OneDnsCache> ar = new ArrayList<>();
	private Object lock = new Object();


	/**
	 * IPアドレスからホスト名を検索する（逆引き）
	 * @param hostName ホスト名
	 * @return 取得したIPアドレスの配列 検索に失敗した場合、検索した文字列がそのまま返される
	 */
	public String getHostName(InetAddress inetAddress, Logger logger) {
		synchronized (lock) {

			String ipStr = inetAddress.toString();
			if (ipStr.charAt(0) == '/') {
				ipStr = ipStr.substring(1);
			}
			
			for (OneDnsCache oneDnsCache : ar) {
				for (Ip ip : oneDnsCache.getIpList()) {
					if (ip.toString().equals(ipStr)) {
						return oneDnsCache.getName();
					}
				}
			}
			
			removeOldCache(); //古いものを整理する

			//DNSに問い合わせる
			String hostName = inetAddress.getHostName();
			if (hostName.equals(ipStr)) {
				logger.set(LogKind.NORMAL, null, 9000052, String.format("IP=%s", ipStr));
			}
			
			//データベースへの追加
			Ip [] ipList = new Ip[1];
			try {
				ipList[0] = new Ip(ipStr);
			} catch (ValidObjException e) {
				//ここで失敗するのはおかしい
				Util.runtimeException(String.format("new Ip(%s) => ValidObjException", ipStr));
			}
			ar.add(new OneDnsCache(hostName, ipList));
			return hostName;
		}
	}
	
	/*
	 * キャッシュの件数取得(デバッグ用)
	 */
	public int size() {
		return ar.size();
	}

	//キャッシュの容量制限
	private void removeOldCache() {
		if (ar.size() > 200) {
			for (int i = 0; i < 50; i++) { //古いものから50件削除
				ar.remove(0);
			}
		}
	}

	/**
	 * ホスト名からIPアドレスを検索する(正引き)
	 * @param hostName ホスト名
	 * @return 取得したIPアドレスの配列 検索に失敗した場合、0件の配列が返される
	 */
	public Ip[] getAddress(String hostName) {
		synchronized (lock) {
			//データベースから検索して取得する
			for (OneDnsCache oneDnsCache : ar) {
				if (oneDnsCache.getName().toUpperCase().equals(hostName.toUpperCase())) {
					return oneDnsCache.getIpList();
				}
			}
			removeOldCache(); //古いものを整理する

			//DNSに問い合わせる
			InetAddress[] list;
			try {
				list = InetAddress.getAllByName(hostName);
			} catch (UnknownHostException e) {
				//名前が見つからない場合
				return new Ip[0];
			}

			ArrayList<Ip> tmp = new ArrayList<>();
			for (InetAddress addr : list) {
				//IPv4及びIPv6以外は処理しない
				if (!(addr instanceof Inet6Address) && !(addr instanceof Inet4Address)) {
					continue;
				}
				//Ipv6の場合　リンクローカル・マルチキャスト・サイトローカルは対象外とする
				if (addr instanceof Inet6Address) {
					boolean linkLocal = addr.isLinkLocalAddress();
					boolean multicast = addr.isMulticastAddress();
					boolean siteLocal = addr.isSiteLocalAddress();
					if (linkLocal || multicast || siteLocal) {
						continue;
					}
				}
				String ipStr = addr.getHostAddress();
				try {
					tmp.add(new Ip(ipStr));
				} catch (ValidObjException e) {
					//ここで失敗するのはおかしい
					Util.runtimeException(String.format("new Ip(%s) => ValidObjException", ipStr));
				}
			}
			Ip[] ipList = tmp.toArray(new Ip[]{});
//			for (Ip ip : ipList) {
//				System.out.println(String.format("%s", ip.toString()));
//			}
			//データベースへの追加
			ar.add(new OneDnsCache(hostName, ipList));
			return ipList;
		}

	}
}
