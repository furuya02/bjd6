package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import bjd.test.TestUtil;

public final class UnCompressTest {

	private String str0 = "b61d81800001000c00040004026934057974696d6703636f6d0000010001c00c0005000100000af70011057974696d67016c06676f6f676c65c015c02a000100010000006a00044a7deb66c02a000100010000006a00044a7deb67c02a000100010000006a00044a7deb68c02a000100010000006a00044a7deb69c02a000100010000006a00044a7deb6ec02a000100010000006a00044a7deb60c02a000100010000006a00044a7deb61c02a000100010000006a00044a7deb62c02a000100010000006a00044a7deb63c02a000100010000006a00044a7deb64c02a000100010000006a00044a7deb65c03200020001000027020006036e7331c032c03200020001000027020006036e7334c032c03200020001000027020006036e7332c032c03200020001000027020006036e7333c032c0f700010001000027560004d8ef200ac11b00010001000027c60004d8ef220ac12d00010001000028c60004d8ef240ac109000100010000277f0004d8ef260a";

	
	@Test
	public void 圧縮なしのホスト名取得() {
		//setUp
		UnCompress sut = new UnCompress(TestUtil.hexStream2Bytes(str0),0x36-0x2a);
		String expected = "i4.ytimg.com."; 
		
		//exercise
		String actual = sut.getHostName(); 
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 圧縮ありのホスト名取得() {
		//setUp
		UnCompress sut = new UnCompress(TestUtil.hexStream2Bytes(str0),0x48-0x2a);
		String expected = "i4.ytimg.com."; 
		
		//exercise
		String actual = sut.getHostName(); 
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 圧縮ありのホスト名取得2() {
		//setUp
		UnCompress sut = new UnCompress(TestUtil.hexStream2Bytes(str0),0x65-0x2a);
		String expected = "ytimg.l.google.com."; 
		
		//exercise
		String actual = sut.getHostName(); 
		//verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void 圧縮ありのホスト名取得3() {
		//setUp
		UnCompress sut = new UnCompress(TestUtil.hexStream2Bytes(str0),0x75-0x2a);
		String expected = "ytimg.l.google.com."; 
		
		//exercise
		String actual = sut.getHostName(); 
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 圧縮ありのホスト名取得4() {
		//setUp
		UnCompress sut = new UnCompress(TestUtil.hexStream2Bytes(str0),0x15d-0x2a);
		String expected = "ns1.google.com."; 
		
		//exercise
		String actual = sut.getHostName(); 
		//verify
		assertThat(actual, is(expected));
	}
}
