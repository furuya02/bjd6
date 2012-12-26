package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import bjd.test.TestUtil;

public final class RrNsTest {

	//NS class=1 ttl=0x000002b25 ns2.google.com
	private String str0 = "0002000100002b250010036e733206676f6f676c6503636f6d00";

	@Test
	public void getNsNameの確認() throws Exception {
		//setUp
		String expected = "ns.google.com.";
		RrNs sut = new RrNs("aaa.com", 0, expected);
		//exercise
		String actual = sut.getNsName();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void バイナリ初期化との比較() throws Exception {
		//setUp
		RrNs sut = new RrNs("aaa.com", 64800, "1.");
		OneRr expected = new RrNs("aaa.com", 64800, new byte[] { 01, 49, 0 });
		//exercise
		OneRr actual = (OneRr) sut;
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 実パケット生成したオブジェクトとの比較() throws Exception {
		//setUp
		RrNs sut = new RrNs("aaa.com", 0x00002b25, "ns2.google.com");
		PacketRr rr = new PacketRr(TestUtil.hexStream2Bytes(str0), 0);
		OneRr expected = new RrNs("aaa.com", rr.getTtl(), rr.getData());
		//exercise
		OneRr actual = (OneRr) sut;
		//verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void toStringの確認() throws Exception {
		//setUp
		String expected = "Ns aaa.com TTL=0 ns.google.com.";
		RrNs sut = new RrNs("aaa.com", 0, "ns.google.com.");
		//exercise
		String actual = sut.toString();
		//verify
		assertThat(actual, is(expected));
	}
}