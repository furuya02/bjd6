package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import bjd.test.TestUtil;

public final class PacketDnsHeaderTest {

	private String str0 = "000381800001000200030004";

	@Test
	public void getClsの確認() throws Exception {
		//setUp
		PacketDnsHeader sut = new PacketDnsHeader(TestUtil.hexStream2Bytes(str0), 0);
		short expected = 0x0003;
		//exercise
		short actual = sut.getId();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getFlagsの確認() throws Exception {
		//setUp
		PacketDnsHeader sut = new PacketDnsHeader(TestUtil.hexStream2Bytes(str0), 0);
		short expected = (short) 0x8180;
		//exercise
		short actual = sut.getFlags();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getQDの確認() throws Exception {
		//setUp
		PacketDnsHeader sut = new PacketDnsHeader(TestUtil.hexStream2Bytes(str0), 0);
		short expected = 1;
		//exercise
		short actual = sut.getCount(0); //QD=0
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getANの確認() throws Exception {
		//setUp
		PacketDnsHeader sut = new PacketDnsHeader(TestUtil.hexStream2Bytes(str0), 0);
		short expected = 2;
		//exercise
		short actual = sut.getCount(1); //AN=1
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getNSの確認() throws Exception {
		//setUp
		PacketDnsHeader sut = new PacketDnsHeader(TestUtil.hexStream2Bytes(str0), 0);
		short expected = 3;
		//exercise
		short actual = sut.getCount(2); //NS=2
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getARの確認() throws Exception {
		//setUp
		PacketDnsHeader sut = new PacketDnsHeader(TestUtil.hexStream2Bytes(str0), 0);
		short expected = 4;
		//exercise
		short actual = sut.getCount(3); //AR=3
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void setCountの確認() throws Exception {
		//setUp
		PacketDnsHeader sut = new PacketDnsHeader();
		short expected = (short) 0xf1f1;
		sut.setCount(3, expected);
		//exercise
		short actual = sut.getCount(3);
		//verify
		assertThat(actual, is(expected));
	}

}
