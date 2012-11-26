package bjd.net;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import bjd.ValidObjException;

public final class LocalAddressTest {

	@Test
	public void remoteStrで取得したテキストで改めてLocalAddressを生成して同じかどうかを確認() throws Exception {

		//setUp
		LocalAddress localAddress = LocalAddress.getInstance();
		String expected = localAddress.remoteStr();
		
		//exercise
		LocalAddress sut = new LocalAddress(expected);
		String actual = sut.remoteStr();

		//verify
		assertThat(actual, is(expected));
	}

	@Test(expected = ValidObjException.class)
	public void 無効な文字列で初期化すると例外_ValidObjException_が発生する() throws Exception {
		//exercise
		new LocalAddress("XXX");
	}
	
}
