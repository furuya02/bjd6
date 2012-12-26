package bjd.log;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import bjd.ValidObjException;

public final class OneLogTest {
	
	@Test(expected = ValidObjException.class)
	public void 無効な文字列で初期化すると例外_ValidObjException_が発生する() throws Exception {
		//exercise
		new OneLog("xxx");
	}
	
	@Test
	public void toStringによる出力の確認() throws Exception {
		//setUp
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(0)); // 1970.1.1で初期化
		LogKind logKind = LogKind.DEBUG;
		String nameTag = "NAME";
		long threadId = 100;
		String remoteHostname = "127.0.0.1";
		int messageId = 200;
		String message = "MSG";
		String detailInfomation = "DETAIL";
		OneLog sut = new OneLog(calendar, logKind, nameTag, threadId, remoteHostname, messageId, message, detailInfomation);

		String expected = "1970/01/01 09:00:00\tDEBUG\t100\tNAME\t127.0.0.1\t0000200\tMSG\tDETAIL";
		//exercise
		String actual = sut.toString(); 
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void セキュアログの確認() {

		//setUp
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(0)); // 1970.1.1で初期化
		String nameTag = "NAME";
		long threadId = 100;
		String remoteHostname = "127.0.0.1";
		int messageId = 200;
		String message = "MSG";
		String detailInfomation = "DETAIL";
		
		//exercise
		LogKind logKind = LogKind.SECURE; //セキュアログの場合
		boolean expected = true;
		OneLog sut = new OneLog(calendar, logKind, nameTag, threadId, remoteHostname, messageId, message, detailInfomation);
		boolean actual = sut.isSecure(); 

		//verify
		assertThat(actual, is(expected));
		
		//exercise
		logKind = LogKind.DEBUG; //セキュアログ以外の場合
		expected = false;
		sut = new OneLog(calendar, logKind, nameTag, threadId, remoteHostname, messageId, message, detailInfomation);
		actual = sut.isSecure(); 
		
		//verify
		assertThat(actual, is(expected));

	}
}
