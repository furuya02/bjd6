package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public final class PacketRRTest {

	//type= 0x0002 class=0x0001 ttl=0x00011e86 dlen=0x0006 data=036e7332c00c
	private byte[] data0 = new byte[] { 0x00, 0x02, 0x00, 0x01, 0x00, 0x01, 0x1e, (byte) 0x86, 0x00, 0x06, 0x03, 0x6e, 0x73, 0x32, (byte) 0xc0, 0x0c };

	@Test
	public void getClsの確認() throws Exception {
		//setUp
		PacketRR sut = new PacketRR(data0, 0);
		short expected = 0x0001;
		//exercise
		short actual = sut.getCls();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getTypeの確認() throws Exception {
		//setUp
		PacketRR sut = new PacketRR(data0, 0);
		DnsType expected = DnsType.Ns;
		//exercise
		DnsType actual = sut.getType();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getTtlの確認() throws Exception {
		//setUp
		PacketRR sut = new PacketRR(data0, 0);
		int expected = 0x11E86; //733350
		//exercise
		int actual = sut.getTtl();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getDLenの確認() throws Exception {
		//setUp
		PacketRR sut = new PacketRR(data0, 0);
		short expected = 6;
		//exercise
		short actual = sut.getDLen();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getData確認() throws Exception {
		//setUp
		PacketRR sut = new PacketRR(data0, 0);
		byte[] expected = new byte[6];
		System.arraycopy(data0, 10, expected, 0, 6);
		//exercise
		byte[] actual = sut.getData();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void setClsの確認() throws Exception {
		//setUp
		PacketRR sut = new PacketRR(0);

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
		PacketRR sut = new PacketRR(0);
		DnsType expected = DnsType.Mx;
		sut.setType(expected);

		//exercise
		DnsType actual = sut.getType();
		//verify
		assertThat(actual, is(expected));
	}
}
