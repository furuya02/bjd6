package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Test;

import bjd.net.Ip;
import bjd.test.TestUtil;

public final class RrMxTest {

	//type= 0x0001(A) class=0x0001 ttl=0x00000e10 dlen=0x0004 data=3b6a1bd0
	private String str0 = "0001000100000e1000043b6a1bd0";

	@Test
	public void getPreferenceの確認() throws Exception {
		//setUp
		short expected = 10;
		RrMx sut = new RrMx("aaa.com", 0, expected, "exchange.host.");
		//exercise
		short actual = sut.getPreference();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getMailExchangeHostの確認() throws Exception {
		//setUp
		String expected = "exchange.host.";
		RrMx sut = new RrMx("aaa.com", 0, (short) 10, expected);
		//exercise
		String actual = sut.getMailExchangeHost();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void OneRRとの比較() throws Exception {
		//setUp
		RrMx sut = new RrMx("aaa.com", 64800, (short) 20, "1.");
		OneRr expected = new RrMx("aaa.com", 64800, new byte[] { 0, 20, 01, 49, 0 });
		//exercise
		OneRr actual = (OneRr) sut;
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 実パケット生成したOneRRとの比較() throws Exception {
//		//setUp
//		RrA sut = new RrA("aaa.com", 0x00000e10, new Ip("59.106.27.208"));
//		PacketRr rr = new PacketRr(TestUtil.hexStream2Bytes(str0), 0);
//		OneRr expected = new OneRr("aaa.com", rr.getType(), rr.getTtl(), rr.getData());
//		//exercise
//		OneRr actual = (OneRr) sut;
//		//verify
//		assertThat(actual, is(expected));
		Assert.fail("未実装");
	}
}