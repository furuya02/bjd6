package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import bjd.net.Ip;
import bjd.test.TestUtil;

public final class RrAaaaTest {

	//type= 0x0001(A) class=0x0001 ttl=0x00000e10 dlen=0x0004 data=3b6a1bd0
	private String str0 = "001c0001000151800010200102000dfffff102163efffeb144d7";

	@Test
	public void getIpの確認() throws Exception {
		//setUp
		Ip expected = new Ip("2001:200:dff:fff1:216:3eff:feb1:44d7");
		RrAaaa sut = new RrAaaa("www.com", 0, expected);
		//exercise
		Ip actual = sut.getIp();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void バイナリ初期化との比較() throws Exception {
		//setUp
		RrAaaa sut = new RrAaaa("aaa.com", 64800, new Ip("::1"));
		OneRr expected = new RrAaaa("aaa.com", 64800, new byte[] { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1 });
		//exercise
		OneRr actual = (OneRr) sut;
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 実パケット生成したオブジェクトとの比較() throws Exception {
		//setUp
		RrAaaa sut = new RrAaaa("orange.kame.net", 0x00015180, new Ip("2001:200:dff:fff1:216:3eff:feb1:44d7"));
		PacketRr rr = new PacketRr(TestUtil.hexStream2Bytes(str0), 0);
		OneRr expected = new RrAaaa("orange.kame.net", rr.getTtl(), rr.getData());
		//exercise
		OneRr actual = (OneRr) sut;
		//verify
		assertThat(actual, is(expected));
	}


}
