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
		String expected = "name.server.";
		RrSoa sut = new RrSoa("aaa.com", 0, expected, "post.master.", 1, 2, 3, 4, 5);
		//exercise
		String actual = sut.getNameServer();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getPostMasterの確認() throws Exception {
		//setUp
		String expected = "post.master.";
		RrSoa sut = new RrSoa("aaa.com", 0, "name.server.", expected, 1, 2, 3, 4, 5);
		//exercise
		String actual = sut.getPostMaster();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getSerialの確認() throws Exception {
		//setUp
		int expected = 1;
		RrSoa sut = new RrSoa("aaa.com", 0, "name.server.", "post.master.", expected, 2, 3, 4, 5);
		//exercise
		int actual = sut.getSerial();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getRefreshの確認() throws Exception {
		//setUp
		int expected = 300;
		RrSoa sut = new RrSoa("aaa.com", 0, "name.server.", "post.master.", 1, expected, 3, 4, 5);
		//exercise
		int actual = sut.getRefresh();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getRetryの確認() throws Exception {
		//setUp
		int expected = 300;
		RrSoa sut = new RrSoa("aaa.com", 0, "name.server.", "post.master.", 1, 2, expected, 4, 5);
		//exercise
		int actual = sut.getRetry();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getExpireの確認() throws Exception {
		//setUp
		int expected = 300;
		RrSoa sut = new RrSoa("aaa.com", 0, "name.server.", "post.master.", 1, 2, 3, expected, 5);
		//exercise
		int actual = sut.getExpire();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getMinimumの確認() throws Exception {
		//setUp
		int expected = 300;
		RrSoa sut = new RrSoa("aaa.com", 0, "name.server.", "post.master.", 1, 2, 3, 4, expected);
		//exercise
		int actual = sut.getMinimum();
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
}