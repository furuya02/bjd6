package bjd.net;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import bjd.log.Logger;
import bjd.util.TestUtil;

public final class DnsCacheTest {

	@Test
	public void a001() {

		TestUtil.dispHeader("a001 アドレスからホスト名を取得する");

		DnsCache dnsCache = new DnsCache();
		String addr = "59.106.27.208";
		String expected = "www1968.sakura.ne.jp";

		InetAddress inetAddress = null;
		try {
			inetAddress = InetAddress.getByName(addr);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String hostName = dnsCache.getHostName(inetAddress, new Logger());
		TestUtil.dispPrompt(this, String.format("getHostName(%s) = %s", addr, expected));
		assertThat(hostName, is(expected));
	}

	@Test
	public void a002() {

		TestUtil.dispHeader("a002 ホスト名からアドレスを取得する");

		DnsCache dnsCache = new DnsCache();
		String hostName = "www.sapporoworks.ne.jp";
		String expected = "59.106.27.208";

		Ip[] ipList = dnsCache.getAddress(hostName);
		assertThat(ipList.length, is(1));
		TestUtil.dispPrompt(this, String.format("getAddress(%s) = %s", hostName, expected));
		assertThat(ipList[0].toString(), is(expected));
	}

	@Test
	public void a003() {

		TestUtil.dispHeader("a003 キャッシュの件数確認");

		DnsCache dnsCache = new DnsCache();

		dnsCache.getAddress("www.sapporoworks.ne.jp");
		TestUtil.dispPrompt(this, "1件検索すると　データ数が、１になる　 dnsCache.size()=1");
		assertThat(dnsCache.size(), is(1));

		dnsCache.getAddress("www.sapporoworks.ne.jp");
		TestUtil.dispPrompt(this, "もういちど同じホストを検索しても　データ数は、１のまま変わらない　 dnsCache.size()=1");
		assertThat(dnsCache.size(), is(1));

		dnsCache.getAddress("www.google.com");
		TestUtil.dispPrompt(this, "新たなホストを1件検索すると　データ数が、２になる　 dnsCache.size()=2");
		assertThat(dnsCache.size(), is(2));

	}

	@Test
	public void a004() {

		TestUtil.dispHeader("a004 無効なホスト名を検索すると0件の配列が返される");
		DnsCache dnsCache = new DnsCache();
		String hostName = "xxx";
		int length = 0;
		Ip[] ipList = dnsCache.getAddress(hostName);
		TestUtil.dispPrompt(this, String.format("ipList=dnsCache.getAddress(%s) ipList.length=%d", hostName, length));
		assertThat(ipList.length, is(0));

	}

	@Test
	public void a005() {

		TestUtil.dispHeader("a005 無効なアドレスを検索すると 、アドレス表記がそのまま返される");
		DnsCache dnsCache = new DnsCache();
		String addr = "1.1.1.1";

		InetAddress inetAddress = null;
		try {
			inetAddress = InetAddress.getByName(addr);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		String hostName = dnsCache.getHostName(inetAddress, new Logger());
		TestUtil.dispPrompt(this, String.format("hostName=dnsCache.getHostName(%s) hostName=%s", addr, hostName));
		assertThat(hostName, is(addr));

	}
}
