package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import bjd.test.TestUtil;

public final class PacketRRTest {

	//type= 0x0002 class=0x0001 ttl=0x00011e86 dlen=0x0006 data=036e7332c00c
	private String str0 = "0002000100011e860006036e7332c00c";

	
	@Test
	public void getClsの確認() throws Exception {
		//setUp
		PacketRr sut = new PacketRr(TestUtil.hexStream2Bytes(str0), 0);
		short expected = 0x0001;
		//exercise
		short actual = sut.getCls();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getTypeの確認() throws Exception {
		//setUp
		PacketRr sut = new PacketRr(TestUtil.hexStream2Bytes(str0), 0);
		DnsType expected = DnsType.Ns;
		//exercise
		DnsType actual = sut.getType();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getTtlの確認() throws Exception {
		//setUp
		PacketRr sut = new PacketRr(TestUtil.hexStream2Bytes(str0), 0);
		int expected = 0x11E86; //733350
		//exercise
		int actual = sut.getTtl();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getDLenの確認() throws Exception {
		//setUp
		PacketRr sut = new PacketRr(TestUtil.hexStream2Bytes(str0), 0);
		short expected = 6;
		//exercise
		short actual = sut.getDLen();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getData確認() throws Exception {
		//setUp
		PacketRr sut = new PacketRr(TestUtil.hexStream2Bytes(str0), 0);
		byte[] expected = new byte[6];
		System.arraycopy(TestUtil.hexStream2Bytes(str0), 10, expected, 0, 6);
		//exercise
		byte[] actual = sut.getData();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void setClsの確認() throws Exception {
		//setUp
		PacketRr sut = new PacketRr(0);

		short expected = 0x0002;
		sut.setCls(expected);

		//exercise
		short actual = sut.getCls();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void setTypeの確認() throws Exception {
		//setUp
		PacketRr sut = new PacketRr(0);
		DnsType expected = DnsType.Mx;
		sut.setType(expected);

		//exercise
		DnsType actual = sut.getType();
		//verify
		assertThat(actual, is(expected));
	}
}
