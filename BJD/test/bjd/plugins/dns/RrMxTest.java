package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import bjd.test.TestUtil;

public final class RrMxTest {

	//MX class=1 ttl=0x00000289 pref=30 alt3.gmail-smtp-in.l.google.com
	private String str0 = "000f0001000002890023001e04616c74330d676d61696c2d736d74702d696e016c06676f6f676c6503636f6d00";

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
	public void バイナリ初期化との比較() throws Exception {
		//setUp
		RrMx sut = new RrMx("aaa.com", 64800, (short) 20, "1.");
		OneRr expected = new RrMx("aaa.com", 64800, new byte[] { 0, 20, 01, 49, 0 });
		//exercise
		OneRr actual = (OneRr) sut;
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 実パケット生成したオブジェクトとの比較() throws Exception {
		//setUp
		RrMx sut = new RrMx("aaa.com", 0x00000289, (short) 30, "alt3.gmail-smtp-in.l.google.com");
		PacketRr rr = new PacketRr(TestUtil.hexStream2Bytes(str0), 0);
		OneRr expected = new RrMx("aaa.com", rr.getTtl(), rr.getData());
		//exercise
		OneRr actual = (OneRr) sut;
		//verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void toStringの確認() throws Exception {
		//setUp
		String expected = "Mx aaa.com TTL=10 10 smtp.aaa.com.";
		RrMx sut = new RrMx("aaa.com", 10, (short) 10, "smtp.aaa.com.");
		//exercise
		String actual = sut.toString();
		//verify
		assertThat(actual, is(expected));
	}
}