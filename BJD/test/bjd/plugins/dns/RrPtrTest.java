package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import bjd.test.TestUtil;

public final class RrPtrTest {

	//PTR class=1 ttl=0x000000e10 localhost
	private String str0 = "000c000100000e10000b096c6f63616c686f737400";

	@Test
	public void getPtrの確認() throws Exception {
		//setUp
		String expected = "ns.google.com.";
		RrPtr sut = new RrPtr("aaa.com", 0, expected);
		//exercise
		String actual = sut.getPtr();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void バイナリ初期化との比較() throws Exception {
		//setUp
		RrPtr sut = new RrPtr("aaa.com", 64800, "1.");
		OneRr expected = new RrPtr("aaa.com", 64800, new byte[] { 01, 49, 0 });
		//exercise
		OneRr actual = (OneRr) sut;
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 実パケット生成したオブジェクトとの比較() throws Exception {
		//setUp
		RrPtr sut = new RrPtr("aaa.com", 0x00000e10,"localhost");
		PacketRr rr = new PacketRr(TestUtil.hexStream2Bytes(str0), 0);
		OneRr expected = new RrPtr("aaa.com", rr.getTtl(), rr.getData());
		//exercise
		OneRr actual = (OneRr) sut;
		//verify
		assertThat(actual, is(expected));
	}
}
