package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.util.BitSet;

import org.junit.Test;

public class DnsHeaderTest {

	@Test
	public void setIdで設定した識別子をgetIdで読み出す() throws Exception {
		//setUp
		DnsHeader sut = new DnsHeader();
		short id = (short)0xff34;
		//sut.setId(new byte[] { (byte) 0xfe, (byte) 0xdc });
		sut.setId(id);
		String expected = "0xff34";
		//exercise
		String actual = String.format("0x%x", sut.getId());
		//verify
		assertThat(actual, is(expected));


	}

}
