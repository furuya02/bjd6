package bjd.log;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import bjd.ValidObjException;
import bjd.test.TestUtil;

public final class OneLogTest {

	@Test
	public void 無効な文字列で初期化すると例外が発生する() {
		TestUtil.prompt(String.format("new OneLog(\"xxx\") => IllegalArgumentException"));

		try {
			new OneLog("xxx");
			Assert.fail("この行が実行されたらエラー");
		} catch (ValidObjException ex) {
			return;
		}
		Assert.fail("この行が実行されたらエラー");
	}

	@Test
	public void 初期化とtoString() {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(0)); // 1970.1.1で初期化
		LogKind logKind = LogKind.DEBUG;
		String nameTag = "NAME";
		long threadId = 100;
		String remoteHostname = "127.0.0.1";
		int messageId = 200;
		String message = "MSG";
		String detailInfomation = "DETAIL";
		OneLog oneLog = new OneLog(calendar, logKind, nameTag, threadId, remoteHostname, messageId, message, detailInfomation);

		String expected = "1970/01/01 09:00:00\tDEBUG\t100\tNAME\t127.0.0.1\t0000200\tMSG\tDETAIL";

		TestUtil.prompt(String.format("new OneLog() => toString()=%s", expected));
		assertThat(oneLog.toString(), is(expected));

	}

	@Test
	public void セキュアログの確認() {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(0)); // 1970.1.1で初期化
		String nameTag = "NAME";
		long threadId = 100;
		String remoteHostname = "127.0.0.1";
		int messageId = 200;
		String message = "MSG";
		String detailInfomation = "DETAIL";

		LogKind logKind = LogKind.DEBUG;
		boolean expected = false;
		TestUtil.prompt(String.format("new OneLog(LogKind=%s) => isSecure()=%s", logKind, expected));
		OneLog oneLog = new OneLog(calendar, logKind, nameTag, threadId, remoteHostname, messageId, message, detailInfomation);
		assertThat(oneLog.isSecure(), is(expected));

		logKind = LogKind.SECURE;
		expected = true;
		TestUtil.prompt(String.format("new OneLog(LogKind=%s) => isSecure()=%s", logKind, expected));
		oneLog = new OneLog(calendar, logKind, nameTag, threadId, remoteHostname, messageId, message, detailInfomation);
		assertThat(oneLog.isSecure(), is(expected));

	}

}
