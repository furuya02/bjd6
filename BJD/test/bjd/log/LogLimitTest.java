package bjd.log;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import bjd.ctrl.CtrlType;
import bjd.option.Dat;

public final class LogLimitTest {

	//TODO パラメータ化テストにした方がいい?

	/**
	 * 初期化
	 * @return
	 */
	private LogLimit create(boolean isDisplay) {
		Dat dat = new Dat(new CtrlType[]{CtrlType.TEXTBOX});
		dat.add(true, "AAA");
		dat.add(true, "表示");
		dat.add(true, "123");
		dat.add(true, "アイウ");
		return new LogLimit(dat, isDisplay);
	}
	
	@Test
	public void 指定文字列を表示する_で初期化された場合_AAA_は表示する() throws Exception {

		//setUp
		boolean isDisplay = true; //表示する
		LogLimit sut = create(isDisplay);

		boolean expected = true; 

		//exercise
		boolean actual = sut.isDisplay("AAA");
		
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 指定文字列を表示する_で初期化された場合_表示A_は表示する() throws Exception {

		//setUp
		boolean isDisplay = true; //表示する
		LogLimit sut = create(isDisplay);

		boolean expected = true; 

		//exercise
		boolean actual = sut.isDisplay("表示A");
		
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 指定文字列を表示する_で初期化された場合_表A123_は表示する() throws Exception {

		//setUp
		boolean isDisplay = true; //表示する
		LogLimit sut = create(isDisplay);

		boolean expected = true; 

		//exercise
		boolean actual = sut.isDisplay("表A123");
		
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 指定文字列を表示する_で初期化された場合_123_は表示する() throws Exception {

		//setUp
		boolean isDisplay = true; //表示する
		LogLimit sut = create(isDisplay);

		boolean expected = true; 

		//exercise
		boolean actual = sut.isDisplay("123");
		
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 指定文字列を表示する_で初期化された場合_12アイウ_は表示する() throws Exception {

		//setUp
		boolean isDisplay = true; //表示する
		LogLimit sut = create(isDisplay);

		boolean expected = true; 

		//exercise
		boolean actual = sut.isDisplay("12アイウ");
		
		//verify
		assertThat(actual, is(expected));
	}
	
	
	@Test
	public void 指定文字列を表示しない_で初期化された場合_AAA_は表示しない() throws Exception {

		//setUp
		boolean isDisplay = false; //表示しない
		LogLimit sut = create(isDisplay);
		
		boolean expected = false; 

		//exercise
		boolean actual = sut.isDisplay("AAA");
		
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 指定文字列を表示しない_で初期化された場合_表示A_は表示しない() throws Exception {

		//setUp
		boolean isDisplay = false; //表示しない
		LogLimit sut = create(isDisplay);
		
		boolean expected = false; 

		//exercise
		boolean actual = sut.isDisplay("表示A");
		
		//verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void 指定文字列を表示しない_で初期化された場合_表A123_は表示しない() throws Exception {

		//setUp
		boolean isDisplay = false; //表示しない
		LogLimit sut = create(isDisplay);
		
		boolean expected = false; 

		//exercise
		boolean actual = sut.isDisplay("表A123");
		
		//verify
		assertThat(actual, is(expected));
	}

	
	
	@Test
	public void 指定文字列を表示しない_で初期化された場合_123_は表示しない() throws Exception {

		//setUp
		boolean isDisplay = false; //表示しない
		LogLimit sut = create(isDisplay);
		
		boolean expected = false; 

		//exercise
		boolean actual = sut.isDisplay("123");
		
		//verify
		assertThat(actual, is(expected));
	}
	@Test
	public void 指定文字列を表示しない_で初期化された場合_12アイウ_は表示しない() throws Exception {

		//setUp
		boolean isDisplay = false; //表示しない
		LogLimit sut = create(isDisplay);
		
		boolean expected = false; 

		//exercise
		boolean actual = sut.isDisplay("12アイウ");
		
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 指定した文字列が表示対象か否かの判断() {
		
		Dat dat = new Dat(new CtrlType[]{CtrlType.TEXTBOX});
		dat.add(true, "AAA");
		dat.add(true, "表示");
		dat.add(true, "123");
		dat.add(true, "アイウ");
		boolean isDisplay = true;
		LogLimit logLimit = new LogLimit(dat, isDisplay);
		
		//表示する
		boolean expected = true;
		check(logLimit, "AAA", expected);
		check(logLimit, "表示A", expected);
		check(logLimit, "表A123", expected);
		check(logLimit, "123", expected);
		check(logLimit, "12アイウ", expected);

		//表示しない
		expected = false;
		check(logLimit, "AA", expected);
		check(logLimit, "表a示A", expected);
		check(logLimit, "表A23", expected);
		check(logLimit, "", expected);
		check(logLimit, "12アイ", expected);
		check(logLimit, null, expected);
		
	}
	
	private void check(LogLimit logLimit, String str, boolean expected) {
		boolean actual = logLimit.isDisplay(str);
		assertThat(actual, is(expected));
	}

}
