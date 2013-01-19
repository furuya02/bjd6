package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import bjd.test.TestUtil;
import bjd.util.Bytes;

public final class CompressTest {
	private String str0 = "9c608180000100010004000407737570706f727406676f6f676c6503636f6d00001c0001c00c00050001000017fe00090477777733016cc014c01400020001000017dd0006036e7331c014c01400020001000017dd0006036e7332c014c01400020001000017dd0006036e7334c014c01400020001000017dd0006036e7333c014c04500010001000018830004d8ef200ac057000100010000192c0004d8ef220ac07b00010001000018830004d8ef240ac06900010001000018240004d8ef260a";

	@Test
	public void ホスト名を圧縮して格納する() {
		//setUp
		byte[] buf = TestUtil.hexStream2Bytes(str0);
		byte[] hostName = new byte[] { 0x06, 0x67, 0x6f, 0x6f, 0x67, 0x6c, 0x65, 0x03, 0x63, 0x6f, 0x6d, 0x00 }; //google.com
		Compress sut = new Compress(buf, hostName);
		byte[] expected = new byte[] { (byte) 0xC0, 0x14 };

		//exercise
		byte[] actual = sut.getData();
		//verify
		assertThat(actual, is(expected));

		//以下の、UnCompressでもう一度元に戻してみる
		//exercise
		UnCompress s = new UnCompress(Bytes.create(buf, actual), buf.length);
		//verify
		assertThat(s.getHostName(), is("google.com."));
	}

	@Test
	public void ホスト名を圧縮しないで格納する() {
		//setUp
		byte[] buf = TestUtil.hexStream2Bytes(str0);
		byte[] hostName = new byte[] { 0x03, 0x67, 0x6f, 0x6f, 0x03, 0x63, 0x6f, 0x6d, 0x00 }; //goo.com
		Compress sut = new Compress(buf, hostName);
		byte[] expected = new byte[] { (byte) 0x03, 0x67, 0x6f, 0x6f, (byte) 0xC0, 0x1b };

		//exercise
		byte[] actual = sut.getData();
		//verify
		assertThat(actual, is(expected));

		//以下の、UnCompressでもう一度元に戻してみる
		//exercise
		UnCompress s = new UnCompress(Bytes.create(buf, actual), buf.length);
		//verify
		assertThat(s.getHostName(), is("goo.com."));
	}

}
