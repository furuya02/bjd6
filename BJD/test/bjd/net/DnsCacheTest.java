package bjd.net;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.net.InetAddress;
<<<<<<< HEAD
import java.net.UnknownHostException;

import junit.framework.Assert;

import org.junit.Test;

import bjd.log.Logger;
import bjd.test.TestUtil;
=======
import org.junit.Test;

import bjd.log.Logger;
>>>>>>> work

public final class DnsCacheTest {
	
	@Test
<<<<<<< HEAD
	public void アドレスからホスト名を取得する() {
=======
	public void アドレスからホスト名を取得する() throws Exception {
>>>>>>> work

		//setUp
		DnsCache sut = new DnsCache();
		InetAddress inetAddress = InetAddress.getByName("59.106.27.208");
		String expected = "www1968.sakura.ne.jp";

<<<<<<< HEAD
		InetAddress inetAddress = null;
		try {
			inetAddress = InetAddress.getByName(addr);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String hostName = dnsCache.getHostName(inetAddress, new Logger());
		TestUtil.prompt(String.format("getHostName(%s) = %s", addr, expected));
		assertThat(hostName, is(expected));
	}

	@Test
	public void ホスト名からアドレスを取得する() {
=======
		//exercise
		String actual = sut.getHostName(inetAddress, new Logger());
>>>>>>> work

		//verify
		assertThat(actual, is(expected));

<<<<<<< HEAD
		Ip[] ipList = dnsCache.getAddress(hostName);
		assertThat(ipList.length, is(1));
		TestUtil.prompt(String.format("getAddress(%s) = %s", hostName, expected));
		assertThat(ipList[0].toString(), is(expected));
=======
>>>>>>> work
	}
	
	@Test
<<<<<<< HEAD
	public void キャッシュの件数確認() {

		DnsCache dnsCache = new DnsCache();

		dnsCache.getAddress("www.sapporoworks.ne.jp");
		TestUtil.prompt("1件検索すると　データ数が、１になる　 dnsCache.size()=1");
		assertThat(dnsCache.size(), is(1));

		dnsCache.getAddress("www.sapporoworks.ne.jp");
		TestUtil.prompt("もういちど同じホストを検索しても　データ数は、１のまま変わらない　 dnsCache.size()=1");
		assertThat(dnsCache.size(), is(1));

		dnsCache.getAddress("www.google.com");
		TestUtil.prompt("新たなホストを1件検索すると　データ数が、２になる　 dnsCache.size()=2");
		assertThat(dnsCache.size(), is(2));
=======
	public void ホスト名からアドレスを取得する() throws Exception {

		//setUp
		DnsCache sut = new DnsCache();
		String expected = "59.106.27.208";

		//exercise
		Ip[] ipList = sut.getAddress("www1968.sakura.ne.jp");
		String actual = ipList[0].toString();

		//verify
		assertThat(actual, is(expected));
>>>>>>> work

	}

	@Test
<<<<<<< HEAD
	public void 無効なホスト名を検索すると0件の配列が返される() {

		DnsCache dnsCache = new DnsCache();
		String hostName = "xxx";
		int length = 0;
		Ip[] ipList = dnsCache.getAddress(hostName);
		TestUtil.prompt(String.format("ipList=dnsCache.getAddress(%s) ipList.length=%d", hostName, length));
		assertThat(ipList.length, is(0));
=======
	public void 一度検索するとキャッシュ件数は１となる() throws Exception {
		//setUp
		DnsCache sut = new DnsCache();

		int expected = 1;

		//exercise
		sut.getAddress("www.sapporoworks.ne.jp");
		int actual = sut.size();
		
		//verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void 同じ内容を複数回検索してもキャッシュ件数は１となる() throws Exception {
		//setUp
		DnsCache sut = new DnsCache();

		int expected = 1;

		//exercise
		sut.getAddress("www.sapporoworks.ne.jp");
		sut.getAddress("www.sapporoworks.ne.jp");
		sut.getAddress("www.sapporoworks.ne.jp");
		int actual = sut.size();
		
		//verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void 違う内容を検索するとキャッシュ件数は２となる() throws Exception {

		//setUp
		DnsCache sut = new DnsCache();

		int expected = 2;
>>>>>>> work

		//exercise
		sut.getAddress("www.sapporoworks.ne.jp");
		sut.getAddress("www.google.com");
		int actual = sut.size();
		
		//verify
		assertThat(actual, is(expected));
	}

	@Test
<<<<<<< HEAD
	public void 無効なアドレスを検索するとアドレス表記がそのまま返される() {

		DnsCache dnsCache = new DnsCache();
		String addr = "1.1.1.1";

		InetAddress inetAddress = null;
		try {
			inetAddress = InetAddress.getByName(addr);
		} catch (UnknownHostException e1) {
			Assert.fail(e1.getMessage());
		}
		String hostName = dnsCache.getHostName(inetAddress, new Logger());
		TestUtil.prompt(String.format("hostName=dnsCache.getHostName(%s) hostName=%s", addr, hostName));
		assertThat(hostName, is(addr));
=======
	public void 無効なホスト名を検索すると0件の配列が返される_タイムアウトに時間を要する() {

		//setUp
		DnsCache sut = new DnsCache();

		int expected = 0;
		
		//exercise
		System.out.println("無効ホスト名の検索　タイムアウトまで待機");
		Ip[] ipList = sut.getAddress("xxx");
		int actual = ipList.length;
		
		//verify
		assertThat(actual, is(expected));
	}
>>>>>>> work

	@Test
	public void 無効なアドレスを検索するとアドレス表記がそのまま返される_タイムアウトに時間を要する() throws Exception {

		//setUp
		DnsCache sut = new DnsCache();
		InetAddress inetAddress = InetAddress.getByName("1.1.1.1");

		String expected = "1.1.1.1";
		
		//exercise
		System.out.println("無効アドレスの検索　タイムアウトまで待機");
		String actual = sut.getHostName(inetAddress, new Logger());
		
		//verify
		assertThat(actual, is(expected));
	}
}
