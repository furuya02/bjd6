package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import bjd.net.Ip;
import bjd.test.TestUtil;

public final class PacketDnsTest {

	//www.google.com のAレコードをリクエストした時のレスポンス
	private String str0 = "0003818000010000000400040377777706676f6f676c6503636f6d00001c0001c01000020001000145c80006036e7334c010c01000020001000145c80006036e7332c010c01000020001000145c80006036e7333c010c01000020001000145c80006036e7331c010c06200010001000146090004d8ef200ac03e000100010001466b0004d8ef220ac05000010001000146090004d8ef240ac02c00010001000146090004d8ef260a";

	
	@Test
	public void getIdの確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(TestUtil.hexStream2Bytes(str0));
		short expected = 0x0003;
		//exercise
		short actual = sut.getId();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getCount_QDの確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(TestUtil.hexStream2Bytes(str0));
		int expected = 1;
		//exercise
		int actual = sut.getCount(RrKind.QD);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getCount_ANの確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(TestUtil.hexStream2Bytes(str0));
		int expected = 0;
		//exercise
		int actual = sut.getCount(RrKind.AN);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getCount_NSの確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(TestUtil.hexStream2Bytes(str0));
		int expected = 4;
		//exercise
		int actual = sut.getCount(RrKind.NS);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getCount_ARの確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(TestUtil.hexStream2Bytes(str0));
		int expected = 4;
		//exercise
		int actual = sut.getCount(RrKind.AR);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getRCodeの確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(TestUtil.hexStream2Bytes(str0));
		short expected = 0;
		//exercise
		short actual = sut.getRcode();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getAAの確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(TestUtil.hexStream2Bytes(str0));
		boolean expected = false;
		//exercise
		boolean actual = sut.getAA();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getRdの確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(TestUtil.hexStream2Bytes(str0));
		boolean expected = true;
		//exercise
		boolean actual = sut.getRd();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getDnsTypeの確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(TestUtil.hexStream2Bytes(str0));
		DnsType expected = DnsType.Aaaa;
		//exercise
		DnsType actual = sut.getDnsType();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getRequestNameの確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(TestUtil.hexStream2Bytes(str0));
		String expected = "www.google.com.";
		//exercise
		String actual = sut.getRequestName();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getRR_QDの確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(TestUtil.hexStream2Bytes(str0));
		//QDレコードなので、data.length=0となる
		OneRr expected = (new RrQuery("www.google.com.", DnsType.Aaaa));
		//exercise
		OneRr actual = sut.getRR(RrKind.QD, 0);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getRR_NS0の確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(TestUtil.hexStream2Bytes(str0));
		OneRr expected = (new RrNs("google.com.", 0, "ns4.google.com."));
		//exercise
		OneRr actual = sut.getRR(RrKind.NS, 0);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getRR_NS1の確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(TestUtil.hexStream2Bytes(str0));
		OneRr expected = (new RrNs("google.com.", 0, "ns2.google.com."));
		//exercise
		OneRr actual = sut.getRR(RrKind.NS, 1);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getRR_NS2の確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(TestUtil.hexStream2Bytes(str0));
		OneRr expected = (new RrNs("google.com.", 0, "ns3.google.com."));
		//exercise
		OneRr actual = sut.getRR(RrKind.NS, 2);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getRR_NS3の確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(TestUtil.hexStream2Bytes(str0));
		OneRr expected = (new RrNs("google.com.", 0, "ns1.google.com."));
		//exercise
		OneRr actual = sut.getRR(RrKind.NS, 3);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getRR_AR0の確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(TestUtil.hexStream2Bytes(str0));
		OneRr expected = (new RrA("ns1.google.com.", 0, new Ip("216.239.32.10")));
		//exercise
		OneRr actual = sut.getRR(RrKind.AR, 0);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getRR_AR1の確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(TestUtil.hexStream2Bytes(str0));
		OneRr expected = (new RrA("ns2.google.com.", 0, new Ip("216.239.34.10")));
		//exercise
		OneRr actual = sut.getRR(RrKind.AR, 1);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getRR_AR2の確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(TestUtil.hexStream2Bytes(str0));
		OneRr expected = (new RrA("ns3.google.com.", 0, new Ip("216.239.36.10")));
		//exercise
		OneRr actual = sut.getRR(RrKind.AR, 2);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getRR_AR3の確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(TestUtil.hexStream2Bytes(str0));
		OneRr expected = (new RrA("ns4.google.com.", 0, new Ip("216.239.38.10")));
		//exercise
		OneRr actual = sut.getRR(RrKind.AR, 3);
		//verify
		assertThat(actual, is(expected));
	}

}
