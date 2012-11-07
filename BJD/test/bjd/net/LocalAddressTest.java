package bjd.net;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import junit.framework.Assert;

import org.junit.Test;

import bjd.ValidObjException;
import bjd.util.TestUtil;

public final class LocalAddressTest {

	@Test
	public void a001() {

		TestUtil.dispHeader("a001 remoteStr()で取得したテキストで、改めてLocalAddressを生成して、同じかどうかを確認"); //TESTヘッダ

		LocalAddress localAddress = LocalAddress.getInstance();
		String remoteStr = localAddress.remoteStr();

		try {
			localAddress = new LocalAddress(remoteStr);
		} catch (ValidObjException ex) {
			Assert.fail(ex.getMessage());
		}

		TestUtil.dispPrompt(this); //TESTプロンプト
		System.out.println(String.format("%s", remoteStr));
		TestUtil.dispPrompt(this); //TESTプロンプト
		System.out.println(String.format("%s", localAddress.remoteStr()));

		assertThat(remoteStr, is(localAddress.remoteStr()));
	}

	@Test
	public void a002() {

		TestUtil.dispHeader("a002 無効な文字列で初期化すると例外が発生するか"); //TESTヘッダ

		String str = "XXX";
		TestUtil.dispPrompt(this, String.format("new LocalAddress(\"%s\") => ValidObjException", str));
		try {
			new LocalAddress(str);
			Assert.fail("この行が実行されたらエラー");
		} catch (ValidObjException ex) {
			return;
		}
		Assert.fail("この行が実行されたらエラー");

	}
}
