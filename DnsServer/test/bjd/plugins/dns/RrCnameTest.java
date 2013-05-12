package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import bjd.test.TestUtil;

public final class RrCnameTest {

	//CNAME class=1 ttl=0x000000067 ytimg.l.google.com
	private String str0 = "00050001000000670014057974696d67016c06676f6f676c6503636f6d00";

	@Test
	public void getCnameの確認() throws Exception {
		//setUp
		String expected = "ns.google.com.";
		RrCname sut = new RrCname("aaa.com", 0, expected);
		//exercise
		String actual = sut.getCName();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void バイナリ初期化との比較() throws Exception {
		//setUp
		RrCname sut = new RrCname("aaa.com", 64800, "1.");
		OneRr expected = new RrCname("aaa.com", 64800, new byte[] { 01, 49, 0 });
		//exercise
		OneRr actual = (OneRr) sut;
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 実パケット生成したオブジェクトとの比較() throws Exception {
		//setUp
		RrCname sut = new RrCname("aaa.com", 0x00000067, "ytimg.l.google.com");
		PacketRr rr = new PacketRr(TestUtil.hexStream2Bytes(str0), 0);
		OneRr expected = new RrCname("aaa.com", rr.getTtl(), rr.getData());
		//exercise
		OneRr actual = (OneRr) sut;
		//verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void toStringの確認() throws Exception {
		//setUp
		String expected = "Cname ns.aaa.com. TTL=222 www.aaa.com.";
		RrCname sut = new RrCname("ns.aaa.com.", 222, "www.aaa.com.");
		//exercise
		String actual = sut.toString();
		//verify
		assertThat(actual, is(expected));
	}
}