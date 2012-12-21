package bjd.packet;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public final class PacketTest {

	int max = 100;

	class MyPacket extends Packet {

		public MyPacket() {
			super(new byte[max], 0);
		}

		@Override
		public int length() {
			return max;
		}
	}

	@Test
	public void getShortで値を取得する() throws Exception {
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
	public void getIntで値を取得する() throws Exception {
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
	public void getByteで値を取得する() throws Exception {
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
	public void getLongで値を取得する() throws Exception {
		//setUp
		MyPacket sut = new MyPacket();
		long expected = (long) 3333;
		sut.setLong(expected, 20);
		//exercise
		long actual = sut.getLong(20);
		//verify
		assertThat(actual, is(expected));
	}
}
