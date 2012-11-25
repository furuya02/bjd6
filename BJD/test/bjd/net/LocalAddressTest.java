package bjd.net;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import bjd.ValidObjException;
<<<<<<< HEAD
import bjd.test.TestUtil;
=======
>>>>>>> work

public final class LocalAddressTest {

	@Test
<<<<<<< HEAD
	public void remoteStrで取得したテキストで改めてLocalAddressを生成して同じかどうかを確認() {
=======
	public void remoteStrで取得したテキストで改めてLocalAddressを生成して同じかどうかを確認() throws Exception {
>>>>>>> work

		//setUp
		LocalAddress localAddress = LocalAddress.getInstance();
<<<<<<< HEAD
		String remoteStr = localAddress.remoteStr();

		try {
			localAddress = new LocalAddress(remoteStr);
		} catch (ValidObjException ex) {
			Assert.fail(ex.getMessage());
		}

		TestUtil.prompt(String.format("%s", remoteStr));
		TestUtil.prompt(String.format("%s", localAddress.remoteStr()));

		assertThat(remoteStr, is(localAddress.remoteStr()));
	}

	@Test
	public void 無効な文字列で初期化すると例外が発生するか() {

		String str = "XXX";
		TestUtil.prompt(String.format("new LocalAddress(\"%s\") => ValidObjException", str));
		try {
			new LocalAddress(str);
			Assert.fail("この行が実行されたらエラー");
		} catch (ValidObjException ex) {
			return;
		}
		Assert.fail("この行が実行されたらエラー");

=======
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
>>>>>>> work
	}
	
}
