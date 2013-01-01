package bjd.log;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import bjd.ValidObjException;

public final class OneLogTest {

	private String nameTag = "NAME";
	private long threadId = 100;
	private String remoteHostname = "127.0.0.1";
	private int messageId = 200;
	private String message = "MSG";
	private String detailInfomation = "DETAIL";

	private Calendar getCalendar() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(0));
		return calendar;
	}

	@Test(expected = ValidObjException.class)
	public void 無効な文字列で初期化すると例外_ValidObjException_が発生する() throws Exception {
		//exercise
		new OneLog("xxx");
	}

	@Test
	public void toStringによる出力の確認() throws Exception {
		//setUp
		OneLog sut = new OneLog(getCalendar(), LogKind.DEBUG, nameTag, threadId, remoteHostname, messageId, message, detailInfomation);
		String expected = "1970/01/01 09:00:00\tDEBUG\t100\tNAME\t127.0.0.1\t0000200\tMSG\tDETAIL";
		//exercise
		String actual = sut.toString();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void isSecureによる確認_LogKind_SECUREでtrueが返る() {

		//setUp
		LogKind logKind = LogKind.SECURE;
		boolean expected = true;
		OneLog sut = new OneLog(getCalendar(), logKind, nameTag, threadId, remoteHostname, messageId, message, detailInfomation);
		//exercise
		boolean actual = sut.isSecure();
		//verify
		assertThat(actual, is(expected));

	}

	@Test
	public void isSecureによる確認_LogKind_DEBUGでfalseが返る() {

		//setUp
		LogKind logKind = LogKind.DEBUG;
		boolean expected = false;
		OneLog sut = new OneLog(getCalendar(), logKind, nameTag, threadId, remoteHostname, messageId, message, detailInfomation);
		//exercise
		boolean actual = sut.isSecure();
		//verify
		assertThat(actual, is(expected));

	}

}
