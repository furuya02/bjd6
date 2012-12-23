package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import bjd.net.Ip;
import bjd.test.TestUtil;

public final class RrATest {

	//type= 0x0001(A) class=0x0001 ttl=0x00000e10 dlen=0x0004 data=3b6a1bd0
	private String str0 = "0001000100000e1000043b6a1bd0";

	@Test
	public void getIpの確認() throws Exception {
		//setUp
		Ip expected = new Ip("127.0.0.1");
		RrA sut = new RrA("www.cmo", 0, expected);
		//exercise
		Ip actual = sut.getIp();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void OneRRとの比較() throws Exception {
		//setUp
		RrA sut = new RrA("aaa.com", 64800, new Ip("1.2.3.4"));
		OneRR expected = new OneRR("aaa.com", DnsType.A, 64800, new byte[] { 1, 2, 3, 4 });
		//exercise
		OneRR actual = (OneRR) sut;
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 実パケット生成したOneRRとの比較() throws Exception {
		//setUp
		RrA sut = new RrA("aaa.com", 0x00000e10, new Ip("59.106.27.208"));
		PacketRR rr = new PacketRR(TestUtil.hexStream2Bytes(str0), 0);
		OneRR expected = new OneRR("aaa.com", rr.getType(), rr.getTtl(), rr.getData());
		//exercise
		OneRR actual = (OneRR) sut;
		//verify
		assertThat(actual, is(expected));
	}

}
