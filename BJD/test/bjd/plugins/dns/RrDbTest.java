package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import org.junit.Test;

import bjd.ctrl.CtrlType;
import bjd.net.Ip;
import bjd.option.Dat;
import bjd.option.OneDat;
import bjd.test.TestUtil;
import bjd.util.Util;

public final class RrDbTest {

	//リフレクションを使用してプライベートメソッドにアクセスする RrDb.size()
	public static int size(RrDb sut) throws Exception {
		Class<RrDb> c = RrDb.class;
		Method m = c.getDeclaredMethod("size");
		m.setAccessible(true);
		return (int) m.invoke(sut);
	}

	//リフレクションを使用してプライベートメソッドにアクセスする RrDb.addNamedCaLine(String tmpName, String str)
	public static String addNamedCaLine(RrDb sut, String tmpName, String str) throws Exception {
		Class<RrDb> c = RrDb.class;
		Method m = c.getDeclaredMethod("addNamedCaLine", new Class[] { String.class, String.class });
		m.setAccessible(true);
		return (String) m.invoke(sut, tmpName, str);
	}

	//リフレクションを使用してプライベートメソッドにアクセスする RrDb.get(int)
	public static OneRr get(RrDb sut, int index) throws Exception {
		Class<RrDb> c = RrDb.class;
		Method m = c.getDeclaredMethod("get", new Class[] { int.class });
		m.setAccessible(true);
		return (OneRr) m.invoke(sut, index);
	}

	//リフレクションを使用してプライベートメソッドにアクセスする RrDb.addOneDat(String,OneDat)
	public static void addOneDat(RrDb sut, String domainName, OneDat oneDat) throws Exception {
		Class<RrDb> c = RrDb.class;
		Method m = c.getDeclaredMethod("addOneDat", new Class[] { String.class, OneDat.class });
		m.setAccessible(true);
		m.invoke(sut, domainName, oneDat);
	}

	//リフレクションを使用してプライベートメソッドにアクセスする RrDb.initLocalHost()
	public static void initLocalHost(RrDb sut) throws Exception {
		Class<RrDb> c = RrDb.class;
		Method m = c.getDeclaredMethod("initLocalHost");
		m.setAccessible(true);
		m.invoke(sut);
	}

	//リフレクションを使用してプライベートメソッドにアクセスする RrDb.addOneDat(String,OneDat)
	public static boolean initSoa(RrDb sut, String domainName, String mail, int serial, int refresh, int retry, int expire, int minimum) throws Exception {
		Class<RrDb> c = RrDb.class;
		Method m = c.getDeclaredMethod("initSoa", new Class[] { String.class, String.class, int.class, int.class, int.class, int.class, int.class });
		m.setAccessible(true);
		return (boolean) m.invoke(sut, domainName, mail, serial, refresh, retry, expire, minimum);
	}

	@Test(expected = IOException.class)
	//例外テスト
	public void コンストラクタの例外処理_指定したファイルが存在しない() throws Exception {
		//exercise
		String namedCaPath = "dmy";
		new RrDb(namedCaPath, 2400);
	}

	@Test
	public void getDomainNameの確認_namedcaで初期化された場合ルートになる() throws Exception {
		//setUp
		String namedCaPath = File.createTempFile("xxx", ".ca").getPath();
		RrDb sut = new RrDb(namedCaPath, 2400);
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
		int ttl = 1; //TTL=1秒
		RrDb sut = new RrDb(null, null, null, "example.com");
		sut.add(new RrA("www.example.com.", ttl, new Ip("1.1.1.1")));
		sut.add(new RrA("www.example.com.", ttl, new Ip("2.2.2.2")));
		int expected = 0;

		TestUtil.waitDisp("RrDb.getList()で期限切れリソースの削除を確認するため、TTL指定時間が経過するまで待機");
		Util.sleep(1001); //1001ms経過
		//exercise
		sut.getList("www.example.com.", DnsType.A);
		int actual = RrDbTest.size(sut); //DBのサイズは0になっている
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

	@Test
	public void 重複しない２つのリソースの追加() throws Exception {
		//setUp
		Dat dat = new Dat(new CtrlType[5]);
		dat.add(true, "0\twww\talias\t192.168.0.1\t10\t");
		dat.add(true, "1\tns\talias\t192.168.0.1\t10\t");
		RrDb sut = new RrDb(null, null, dat, "example.com");
		//(1)a   www.example.com. 192.168.0.1
		//(2)ptr 1.0.168.192.in.addr.ptr  www.example.com.
		//(3)ns  example.com. ns.example.com. 
		//(4)a   ns.example.com. 192.168.0.1
		//(5)ptr 1.0.168.192.in.addr.ptr  ns.example.com.
		int expected = 5;
		//exercise
		int actual = RrDbTest.size(sut);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 重複する２つのリソースの追加() throws Exception {
		//setUp
		Dat dat = new Dat(new CtrlType[5]);
		dat.add(true, "0\twww\talias\t192.168.0.1\t10\t");
		dat.add(true, "0\twww\talias\t192.168.0.1\t10\t");
		RrDb sut = new RrDb(null, null, dat, "example.com");
		//(1)a   www.example.com. 192.168.0.1
		//(2)ptr 1.0.168.192.in.addr.ptr  www.example.com.
		int expected = 2;
		//exercise
		int actual = RrDbTest.size(sut);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 一部が重複する２つのリソースの追加() throws Exception {
		//setUp
		Dat dat = new Dat(new CtrlType[5]);
		dat.add(true, "0\tns\talias\t192.168.0.1\t10\t");
		dat.add(true, "1\tns\talias\t192.168.0.1\t10\t");
		RrDb sut = new RrDb(null, null, dat, "example.com");
		//(1)a   ns.example.com. 192.168.0.1
		//(2)ptr 1.0.168.192.in.addr.ptr  ns.example.com.
		//(3)ns  example.com. ns.example.com. 
		int expected = 3;
		//exercise
		int actual = RrDbTest.size(sut);
		//verify
		assertThat(actual, is(expected));
	}
}
