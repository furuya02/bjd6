package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import bjd.net.Ip;

public final class RrDbTest {

	@Test
	public void getDomainNameの確認_namedcaで初期化された場合ルートになる() throws Exception {
		//setUp
		RrDb sut = new RrDb("name.ca");
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
		String expected = "example.com";
		//exercise
		String actual = sut.getDomainName();
		//verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void getListによる検索_ヒットするデータが存在する場合() throws Exception {
		//setUp
		RrDb sut = new RrDb(null, null, null, "example.com");
		sut.add(new RrA("www.example.com.",100,new Ip("192.168.0.1")));
		int expected = 1;
		//exercise
		int actual = sut.getList("www.example.com.",DnsType.A).size();
		//verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void getListによる検索_ヒットするデータが存在しない場合() throws Exception {
		//setUp
		RrDb sut = new RrDb(null, null, null, "example.com");
		sut.add(new RrA("www1.example.com.",100,new Ip("192.168.0.1")));
		int expected = 0;
		//exercise
		int actual = sut.getList("www.example.com.",DnsType.A).size();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getListによる検索_名前が同じでタイプのデータが存在する場合() throws Exception {
		//setUp
		RrDb sut = new RrDb(null, null, null, "example.com");
		sut.add(new RrAaaa("www.example.com.",100,new Ip("::1")));
		int expected = 0;
		//exercise
		int actual = sut.getList("www.example.com.",DnsType.A).size();
		//verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void findによる検索_ヒットするデータが存在しない場合() throws Exception {
		//setUp
		RrDb sut = new RrDb(null, null, null, "example.com");
		sut.add(new RrA("www1.example.com.",100,new Ip("192.168.0.1")));
		boolean expected = false;
		//exercise
		boolean actual = sut.find("www.example.com.",DnsType.A);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void findによる検索_ヒットするデータが存在する場合() throws Exception {
		//setUp
		RrDb sut = new RrDb(null, null, null, "example.com");
		sut.add(new RrA("www1.example.com.",100,new Ip("192.168.0.1")));
		sut.add(new RrA("www.example.com.",100,new Ip("192.168.0.1")));
		boolean expected = true;
		//exercise
		boolean actual = sut.find("www.example.com.",DnsType.A);
		//verify
		assertThat(actual, is(expected));
	}
	
	
	
}
