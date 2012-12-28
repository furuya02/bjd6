package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import bjd.test.TestUtil;

public final class RrSoaTest {

	//MX class=1 ttl=0x00000289 pref=30 alt3.gmail-smtp-in.l.google.com
	private String str0 = "000f0001000002890023001e04616c74330d676d61696c2d736d74702d696e016c06676f6f676c6503636f6d00";

	@Test
	public void getNameServerの確認() throws Exception {
		//setUp
		String expected = "ns.aaa.com.";
		RrSoa sut = new RrSoa("aaa.com", 0, expected, "post.master.", 1, 2, 3, 4, 5);
		//exercise
		String actual = sut.getNameServer();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getPostMasterの確認() throws Exception {
		//setUp
		String expected = "root.aaa.com.";
		RrSoa sut = new RrSoa("aaa.com.", 0, "ns.aaa.com.", expected, 1, 2, 3, 4, 5);
		//exercise
		String actual = sut.getPostMaster();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getSerialの確認() throws Exception {
		//setUp
		int expected = 100;
		RrSoa sut = new RrSoa("aaa.com.", 0, "ns.aaa.com.", "postmaster.", expected, 2, 3, 4, 5);
		//exercise
		int actual = sut.getSerial();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getRefreshの確認() throws Exception {
		//setUp
		int expected = 300;
		RrSoa sut = new RrSoa("aaa.com.", 0, "ns.aaa.com.", "postmaster.", 1, expected, 3, 4, 5);
		//exercise
		int actual = sut.getRefresh();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getRetryの確認() throws Exception {
		//setUp
		int expected = 400;
		RrSoa sut = new RrSoa("aaa.com.", 0, "ns.aaa.com.", "postmaster.", 1, 2, expected, 4, 5);
		//exercise
		int actual = sut.getRetry();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getExpireの確認() throws Exception {
		//setUp
		int expected = 500;
		RrSoa sut = new RrSoa("aaa.com", 0, "ns.aaa.com.", "postmaster.", 1, 2, 3, expected, 5);
		//exercise
		int actual = sut.getExpire();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getMinimumの確認() throws Exception {
		//setUp
		int expected = 300;
		RrSoa sut = new RrSoa("aaa.com", 0, "ns.aaa.com.", "postmaster.", 1, 2, 3, 4, expected);
		//exercise
		int actual = sut.getMinimum();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void バイナリ初期化との比較() throws Exception {
		//setUp
		RrSoa sut = new RrSoa("aaa.com", 10, "1", "2", 1, 2, 3, 4, 5);
		OneRr expected = new RrSoa("aaa.com", 10, new byte[] { 1, 49, 0, 1, 50, 0, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 4, 0, 0, 0, 5 });
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
		String expected = "Soa aaa.com. TTL=0 ns.aaa.com. postmaster. 00000001 00000002 00000003 00000004 00000005";
		RrSoa sut = new RrSoa("aaa.com.", 0, "ns.aaa.com.", "postmaster.", 1, 2, 3, 4, 5);
		//exercise
		String actual = sut.toString();
		//verify
		assertThat(actual, is(expected));
	}
}