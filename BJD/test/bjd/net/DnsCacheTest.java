package bjd.net;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.net.InetAddress;

import org.junit.Test;

import bjd.log.Logger;
import bjd.test.TestUtil;

public final class DnsCacheTest {

	@Test
	public void アドレスからホスト名を取得する() throws Exception {

		//setUp
		DnsCache sut = new DnsCache();
		InetAddress inetAddress = InetAddress.getByName("59.106.27.208");
		String expected = "www1968.sakura.ne.jp";

		//exercise
		String actual = sut.getHostName(inetAddress, new Logger());

		//verify
		assertThat(actual, is(expected));

	}

	@Test
	public void ホスト名からアドレスを取得する() throws Exception {

		//setUp
		DnsCache sut = new DnsCache();
		String expected = "59.106.27.208";

		//exercise
		Ip[] ipList = sut.getAddress("www1968.sakura.ne.jp");
		String actual = ipList[0].toString();

		//verify
		assertThat(actual, is(expected));

	}

	@Test
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

		//exercise
		sut.getAddress("www.sapporoworks.ne.jp");
		sut.getAddress("www.google.com");
		int actual = sut.size();

		//verify
		assertThat(actual, is(expected));
	}

	public static void echo() {
		String threadNo = Long.toString(Thread.currentThread().getId());
		for (int i = 0; i < 5; i++) {
			System.out.println("ThreadId = " + threadNo + " : " + i);
		}
	}

	@Test
	public void 無効なホスト名を検索すると0件の配列が返される_タイムアウトに時間を要する() {

		//setUp
		DnsCache sut = new DnsCache();

		int expected = 0;

		//exercise
		TestUtil.waitDisp();
		System.out.print("無効ホスト名の検索　タイムアウトまで待機");
		Ip[] ipList = sut.getAddress("xxx");
		int actual = ipList.length;

		//verify
		assertThat(actual, is(expected));
		//TearDown
		System.out.println(""); //waitDisp()の最終改行
		
	}

	@Test
	public void 無効なアドレスを検索するとアドレス表記がそのまま返される_タイムアウトに時間を要する() throws Exception {

		//setUp
		DnsCache sut = new DnsCache();
		InetAddress inetAddress = InetAddress.getByName("1.1.1.1");

		String expected = "1.1.1.1";

		//exercise
		TestUtil.waitDisp();
		System.out.print("無効アドレスの検索　タイムアウトまで待機");
		String actual = sut.getHostName(inetAddress, new Logger());

		//verify
		assertThat(actual, is(expected));
		//TearDown
		System.out.println(""); //waitDisp()の最終改行
	}
}
