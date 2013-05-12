package bjd.packet;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public final class PacketTest {

	private int max = 100;

	class MyPacket extends Packet {

		public MyPacket() {
			super(new byte[max], 0);
		}

		@Override
		public int length() {
			return max;
		}

		@Override
		public byte[] getBytes() throws IOException {
			return super.getBytes(0, max);
		}
	}

	@Test
	public void setShortで値を設定してgetShortで取得する() throws Exception {
		//setUp
		MyPacket sut = new MyPacket();
		short expected = (short) 0xff01;
		sut.setShort(expected, 20);
		//exercise
		short actual = sut.getShort(20);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void setIntで値を設定してgetIntで取得する() throws Exception {
		//setUp
		MyPacket sut = new MyPacket();
		int expected = (int) 12345678;
		sut.setInt(expected, 20);
		//exercise
		int actual = sut.getInt(20);
		//verify
		assertThat(actual, is(expected));

	}

	@Test
	public void setByteで値を設定してgetByteで取得する() throws Exception {
		//setUp
		MyPacket sut = new MyPacket();
		byte expected = (byte) 0xfd;
		sut.setByte(expected, 20);
		//exercise
		byte actual = sut.getByte(20);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void setLongで値を設定してgetLongで取得する() throws Exception {
		//setUp
		MyPacket sut = new MyPacket();
		long expected = (long) 3333;
		sut.setLong(expected, 20);
		//exercise
		long actual = sut.getLong(20);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void setBytesで値を設定してgetBytesで取得する() throws Exception {
		//setUp
		MyPacket sut = new MyPacket();

		byte[] expected = new byte[max - 20];
		for (int i = 0; i < max - 20; i++) {
			expected[i] = (byte) i;
		}
		sut.setBytes(expected, 20);
		//exercise
		byte[] actual = sut.getBytes(20, max - 20);
		//verify
		assertThat(actual, is(expected));
	}

}
