package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class RRPacketTest {

	@Test
	public void getClsの確認() throws Exception {
		//setUp
		byte[] data = new byte[] { 0x00, 0x02, 0x00, 0x01, 0x00, 0x01, 0x1e, (byte) 0x86, 0x00, 0x06, 0x03, 0x6e, 0x73, 0x32, (byte) 0xc0, 0x0c };
		RRPacket sut = new RRPacket(data, 0);
		short expected = 0x0001;
		//exercise
		short actual = sut.getCls();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getTypeの確認() throws Exception {
		//setUp
		byte[] data = new byte[] { 0x00, 0x02, 0x00, 0x01, 0x00, 0x01, 0x1e, (byte) 0x86, 0x00, 0x06, 0x03, 0x6e, 0x73, 0x32, (byte) 0xc0, 0x0c };
		RRPacket sut = new RRPacket(data, 0);
		DnsType expected = DnsType.Ns;
		//exercise
		DnsType actual = sut.getType();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getTtlの確認() throws Exception {
		//setUp
		byte[] data = new byte[] { 0x00, 0x02, 0x00, 0x01, 0x00, 0x01, 0x1e, (byte) 0x86, 0x00, 0x06, 0x03, 0x6e, 0x73, 0x32, (byte) 0xc0, 0x0c };
		RRPacket sut = new RRPacket(data, 0);
		int expected = 0x11E86;
		//exercise
		int actual = sut.getTtl();
		//System.out.println(String.format("0x%x", actual));
		//verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void getDLenの確認() throws Exception {
		//setUp
		byte[] data = new byte[] { 0x00, 0x02, 0x00, 0x01, 0x00, 0x01, 0x1e, (byte) 0x86, 0x00, 0x06, 0x03, 0x6e, 0x73, 0x32, (byte) 0xc0, 0x0c };
		RRPacket sut = new RRPacket(data, 0);
		short expected = 6;
		//exercise
		short actual = sut.getDLen();
		//verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void getData確認() throws Exception {
		//setUp
		byte[] data = new byte[] { 0x00, 0x02, 0x00, 0x01, 0x00, 0x01, 0x1e, (byte) 0x86, 0x00, 0x06, 0x03, 0x6e, 0x73, 0x32, (byte) 0xc0, 0x0c };
		RRPacket sut = new RRPacket(data, 0);
		byte [] expected = ArrayCopy. 
				new byte[6]{};
		//exercise
		byte [] actual = sut.getData();
		//verify
		assertThat(actual, is(expected));
	}

}
