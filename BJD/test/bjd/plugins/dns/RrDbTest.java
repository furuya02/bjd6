package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.junit.Test;

import bjd.net.Ip;
import bjd.test.TestUtil;
import bjd.util.Util;

public final class RrDbTest {
	
	//リフレクションを使用してプライベートメソッドにアクセスする RrDb.size()
	int size(RrDb sut) throws Exception {
		Class<RrDb> c = RrDb.class;
		Method m = c.getDeclaredMethod("size");
		m.setAccessible(true);
		return (int) m.invoke(sut);
	}

	@Test(expected = IOException.class)
	//例外テスト
	public void コンストラクタの例外処理_指定したファイルが存在しない() throws Exception {
		//exercise
		String namedCaPath = "dmy";
		new RrDb(namedCaPath);
	}

	@Test
	public void getDomainNameの確認_namedcaで初期化された場合ルートになる() throws Exception {
		//setUp
		String namedCaPath = File.createTempFile("xxx", ".ca").getPath();
		RrDb sut = new RrDb(namedCaPath);
		String expected = ".";
		//exercise
		String actual = sut.getDomainName();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getDomainNameの確認_Datで初期化された場合指定されたドメインになる() throws Exception {
		//setUp
		RrDb sut = new RrDb(null, null, null, "example.com");
		String expected = "example.com.";
		//exercise
		String actual = sut.getDomainName();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getListによる検索_ヒットするデータが存在する場合() throws Exception {
		//setUp
		RrDb sut = new RrDb(null, null, null, "example.com");
		sut.add(new RrA("www.example.com.", 100, new Ip("192.168.0.1")));
		int expected = 1;
		//exercise
		int actual = sut.getList("www.example.com.", DnsType.A).size();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getListによる検索_ヒットするデータが存在しない場合() throws Exception {
		//setUp
		RrDb sut = new RrDb(null, null, null, "example.com");
		sut.add(new RrA("www1.example.com.", 100, new Ip("192.168.0.1")));
		int expected = 0;
		//exercise
		int actual = sut.getList("www.example.com.", DnsType.A).size();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getListによる検索_名前が同じでタイプのデータが存在する場合() throws Exception {
		//setUp
		RrDb sut = new RrDb(null, null, null, "example.com");
		sut.add(new RrAaaa("www.example.com.", 100, new Ip("::1")));
		int expected = 0;
		//exercise
		int actual = sut.getList("www.example.com.", DnsType.A).size();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getListを使用すると期限の切れたリソースが削除される() throws Exception {
		//setUp
		int ttl=1; //TTL=1秒
		RrDb sut = new RrDb(null, null, null, "example.com");
		sut.add(new RrA("www.example.com.", ttl, new Ip("1.1.1.1")));
		sut.add(new RrA("www.example.com.", ttl, new Ip("2.2.2.2")));
		int expected = 0;
		
		TestUtil.waitDisp("RrDb.getList()で期限切れリソースの削除を確認するため、TTL指定時間が経過するまで待機");
		Util.sleep(1001); //1001ms経過
		//exercise
		sut.getList("www.example.com.", DnsType.A);
		int actual = size(sut); //DBのサイズは0になっている
		//verify
		assertThat(actual, is(expected));
		//TearDown
		TestUtil.waitDisp(null);
	}

	
	@Test
	public void findによる検索_ヒットするデータが存在しない場合() throws Exception {
		//setUp
		RrDb sut = new RrDb(null, null, null, "example.com");
		sut.add(new RrA("www1.example.com.", 100, new Ip("192.168.0.1")));
		boolean expected = false;
		//exercise
		boolean actual = sut.find("www.example.com.", DnsType.A);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void findによる検索_ヒットするデータが存在する場合() throws Exception {
		//setUp
		RrDb sut = new RrDb(null, null, null, "example.com");
		sut.add(new RrA("www1.example.com.", 100, new Ip("192.168.0.1")));
		sut.add(new RrA("www.example.com.", 100, new Ip("192.168.0.1")));
		boolean expected = true;
		//exercise
		boolean actual = sut.find("www.example.com.", DnsType.A);
		//verify
		assertThat(actual, is(expected));
	}

}
