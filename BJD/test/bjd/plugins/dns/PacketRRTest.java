package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public final class PacketRRTest {

	//type= 0x0002 class=0x0001 ttl=0x00011e86 dlen=0x0006 data=036e7332c00c
	private byte[] data0;
	private String str0 = "0002000100011e860006036e7332c00c";

	@Before
	public void before() {
		//str0 -> data0
		data0 = new byte[str0.length() / 2];
		for (int i = 0; i < data0.length; i++) {
			data0[i] = (byte) Integer.parseInt(str0.substring(i * 2, (i + 1) * 2), 16);
		}
	}

	
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
