package bjd.packet;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public final class ListFieldTest {

	@Test
	public void getShortで値を取得する() throws Exception {
		//setUp
		ListField sut = new ListField("TEST");
		sut.add(new OneField("ID", 2));
		sut.set("ID", new byte[] { 0x12, 0x34 });
		short expected = 0x1234;
		//exercise
		short actual = sut.getShort("ID");
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getIntで値を取得する() throws Exception {
		//setUp
		ListField sut = new ListField("TEST");
		sut.add(new OneField("ID", 4));
		sut.set("ID", new byte[] { 0x12, 0x34, 0x56, 0x78 });
		int expected = 0x12345678;
		//exercise
		int actual = sut.getInt("ID");
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getByteで値を取得する() throws Exception {
		//setUp
		ListField sut = new ListField("TEST");
		sut.add(new OneField("ID", 1));
		sut.set("ID", new byte[] { 0x12 });
		byte expected = 0x12;
		//exercise
		byte actual = sut.getByte("ID");
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void getLongで値を取得する() throws Exception {
		//setUp
		ListField sut = new ListField("TEST");
		sut.add(new OneField("ID", 8));
		sut.set("ID", new byte[] { 0x12, 0x34, 0x56, 0x78, (byte) 0x9a, (byte) 0xbc, (byte) 0xde, 0x00 });
		long expected = (long) 0x123456789abcde00L;
		//exercise
		long actual = sut.getLong("ID");
		//verify
		assertThat(actual, is(expected));
	}
}
