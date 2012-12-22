package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import bjd.net.Ip;

public final class PacketDnsTest {

	//www.google.com のAレコードをリクエストした時のレスポンス
	private byte[] data0 = new byte[] { 0x00, 0x03, (byte) 0x81, (byte) 0x80, 0x00, 0x01, 0x00, 0x00, 0x00, 0x04, 0x00, 0x04,
			0x03, 0x77, 0x77, 0x77, 0x06, 0x67, 0x6f, 0x6f, 0x67, 0x6c, 0x65, 0x03, 0x63, 0x6f, 0x6d, 0x00, 0x00, 0x1c,
			0x00, 0x01, (byte) 0xc0, 0x10, 0x00, 0x02, 0x00, 0x01, 0x00, 0x01, 0x45, (byte) 0xc8, 0x00, 0x06, 0x03, 0x6e,
			0x73, 0x34, (byte) 0xc0, 0x10, (byte) 0xc0, 0x10, 0x00, 0x02, 0x00, 0x01, 0x00, 0x01, 0x45, (byte) 0xc8, 0x00,
			0x06, 0x03, 0x6e, 0x73, 0x32, (byte) 0xc0, 0x10, (byte) 0xc0, 0x10, 0x00, 0x02, 0x00, 0x01, 0x00, 0x01, 0x45,
			(byte) 0xc8, 0x00, 0x06, 0x03, 0x6e, 0x73, 0x33, (byte) 0xc0, 0x10, (byte) 0xc0, 0x10, 0x00, 0x02, 0x00, 0x01,
			0x00, 0x01, 0x45, (byte) 0xc8, 0x00, 0x06, 0x03, 0x6e, 0x73, 0x31, (byte) 0xc0, 0x10, (byte) 0xc0, 0x62, 0x00,
			0x01, 0x00, 0x01, 0x00, 0x01, 0x46, 0x09, 0x00, 0x04, (byte) 0xd8, (byte) 0xef, 0x20, 0x0a, (byte) 0xc0, 0x3e,
			0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x46, 0x6b, 0x00, 0x04, (byte) 0xd8, (byte) 0xef, 0x22, 0x0a, (byte) 0xc0,
			0x50, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x46, 0x09, 0x00, 0x04, (byte) 0xd8, (byte) 0xef, 0x24, 0x0a,
			(byte) 0xc0, 0x2c, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x46, 0x09, 0x00, 0x04, (byte) 0xd8, (byte) 0xef, 0x26, 0x0a };

	@Test
	public void getIdの確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(data0);
		short expected = 0x0003;
		//exercise
		short actual = sut.getId();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getCount_QDの確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(data0);
		int expected = 1;
		//exercise
		int actual = sut.getCount(RRKind.QD);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getCount_ANの確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(data0);
		int expected = 0;
		//exercise
		int actual = sut.getCount(RRKind.AN);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getCount_NSの確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(data0);
		int expected = 4;
		//exercise
		int actual = sut.getCount(RRKind.NS);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getCount_ARの確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(data0);
		int expected = 4;
		//exercise
		int actual = sut.getCount(RRKind.AR);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getRCodeの確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(data0);
		short expected = 0;
		//exercise
		short actual = sut.getRcode();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getAAの確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(data0);
		boolean expected = false;
		//exercise
		boolean actual = sut.getAA();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getRdの確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(data0);
		boolean expected = true;
		//exercise
		boolean actual = sut.getRd();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getDnsTypeの確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(data0);
		DnsType expected = DnsType.Aaaa;
		//exercise
		DnsType actual = sut.getDnsType();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getRequestNameの確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(data0);
		String expected = "www.google.com.";
		//exercise
		String actual = sut.getRequestName();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getRR_QDの確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(data0);
		//QDレコードなので、data.length=0となる
		OneRR expected = (new OneRR("www.google.com.", DnsType.Aaaa, 0, new byte[0]));
		//exercise
		OneRR actual = sut.getRR(RRKind.QD, 0);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getRR_NS0の確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(data0);
		OneRR expected = (new RrNs("google.com.", 0, "ns4.google.com."));
		//exercise
		OneRR actual = sut.getRR(RRKind.NS, 0);
		//verify
		assertThat(actual, is(expected));
	}
	

	@Test
	public void getRR_NS1の確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(data0);
		OneRR expected = (new RrNs("google.com.", 0, "ns2.google.com."));
		//exercise
		OneRR actual = sut.getRR(RRKind.NS, 1);
		//verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void getRR_NS2の確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(data0);
		OneRR expected = (new RrNs("google.com.", 0, "ns3.google.com."));
		//exercise
		OneRR actual = sut.getRR(RRKind.NS, 2);
		//verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void getRR_NS3の確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(data0);
		OneRR expected = (new RrNs("google.com.", 0, "ns1.google.com."));
		//exercise
		OneRR actual = sut.getRR(RRKind.NS, 3);
		//verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void getRR_AR0の確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(data0);
		OneRR expected = (new RrA("ns1.google.com.", 0,new Ip("216.239.32.10")));
		//exercise
		OneRR actual = sut.getRR(RRKind.AR, 0); 
		//verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void getRR_AR1の確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(data0);
		OneRR expected = (new RrA("ns2.google.com.", 0,new Ip("216.239.34.10")));
		//exercise
		OneRR actual = sut.getRR(RRKind.AR, 1); 
		//verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void getRR_AR2の確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(data0);
		OneRR expected = (new RrA("ns3.google.com.", 0,new Ip("216.239.36.10")));
		//exercise
		OneRR actual = sut.getRR(RRKind.AR, 2); 
		//verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void getRR_AR3の確認() throws Exception {
		//setUp
		PacketDns sut = new PacketDns(data0);
		OneRR expected = (new RrA("ns4.google.com.", 0,new Ip("216.239.38.10")));
		//exercise
		OneRR actual = sut.getRR(RRKind.AR, 3); 
		//verify
		assertThat(actual, is(expected));
	}
	
	

}
