package bjd.log;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import bjd.ctrl.CtrlType;
import bjd.option.Dat;
import bjd.util.TestUtil;

public final class LogLimitTest {

	@Test
	public void a001() {
		
		TestUtil.dispHeader("a001 指定した文字列が表示対象か否かの判断");
		
		TestUtil.dispPrompt(this, "logLimit = new LogLimit(dat={\"AAA\",\"表示\",\"123\",\"アイウ\"},isDisplay=true)");
		Dat dat = new Dat(new CtrlType[]{CtrlType.TEXTBOX});
		dat.add(true, "AAA");
		dat.add(true, "表示");
		dat.add(true, "123");
		dat.add(true, "アイウ");
		boolean isDisplay = true;
		
		LogLimit logLimit = new LogLimit(dat, isDisplay);
		
		//表示する
		TestUtil.dispPrompt(this, "[表示する]");
		boolean expected = true;
		check(logLimit, "AAA", expected);
		check(logLimit, "表示A", expected);
		check(logLimit, "表A123", expected);
		check(logLimit, "123", expected);
		check(logLimit, "12アイウ", expected);

		//表示しない
		TestUtil.dispPrompt(this, "[表示しない]");
		expected = false;
		check(logLimit, "AA", expected);
		check(logLimit, "表a示A", expected);
		check(logLimit, "表A23", expected);
		check(logLimit, "", expected);
		check(logLimit, "12アイ", expected);
		check(logLimit, null, expected);
		
	}
	
	private void check(LogLimit logLimit, String str, boolean expected) {
		TestUtil.dispPrompt(this, String.format("logLimit(\"%s\")=%s", str, expected));
		assertThat(logLimit.isDisplay(str), is(expected));
	}

}
