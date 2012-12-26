package bjd;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import bjd.test.TestUtil;
import bjd.util.Util;

public class ThreadBaseTest {

	class MyThread extends ThreadBase {

		protected MyThread() {
			super(null);
		}

		@Override
		protected boolean onStartThread() {
			return true;
		}

		@Override
		protected void onRunThread() {
			while (isLife()) {
				Util.sleep(100);
			}
		}

		@Override
		protected void onStopThread() {
		}

		@Override
		public String getMsg(int no) {
			return "";
		}

	}

	@Test
	public final void start及びstopでisRunnigの状態を確認する() {

		MyThread sut = new MyThread();

		sut.start();
		TestUtil.prompt("start()");
		assertThat(sut.isRunnig(), is(true));
		TestUtil.prompt("isRunnig()=true start()から返った時点で、isRunnig()はTrueになっている");

		sut.stop();
		TestUtil.prompt("stop()");
		assertThat(sut.isRunnig(), is(false));
		TestUtil.prompt("isRunnig()=false stop()から返った時点で、isRunnig()はfalseになっている");

		sut.stop();
		TestUtil.prompt("stop() stop()が重複しても問題ない");

		//start()から返った時点で、isRunnig()はTrueになっている
		sut.start();
		TestUtil.prompt("start()");
		assertThat(sut.isRunnig(), is(true));
		TestUtil.prompt("isRunnig()=true");

		sut.start(); //start()が重複しても問題ない
		TestUtil.prompt("start() start()が重複しても問題ない");

		sut.stop();
		TestUtil.prompt("stop()");
		assertThat(sut.isRunnig(), is(false));
		TestUtil.prompt("isRunnig()=false");

		sut.dispose();
		TestUtil.prompt("myThread.dispose()");
	}

	@Test
	public final void start及びstopしてisRunnigの状態を確認する_負荷テスト() {

		MyThread sut = new MyThread();

		for (int i = 0; i < 5; i++) {
			TestUtil.prompt(String.format("start() i=%d", i));
			sut.start();
			TestUtil.prompt("isRunning() = true");
			assertThat(sut.isRunnig(), is(true));
			TestUtil.prompt("stop()");
			sut.stop();
			TestUtil.prompt("isRunning() = false");
			assertThat(sut.isRunnig(), is(false));
		}

		sut.dispose();
	}

	@Test
	public final void new及びstart_stop_disposeしてisRunnigの状態を確認する_負荷テスト() {

		for (int i = 0; i < 3; i++) {
			TestUtil.prompt(String.format("new i=%d", i));
			MyThread sut = new MyThread();
			TestUtil.prompt("start()");
			sut.start();
			TestUtil.prompt("isRunning() = true");
			assertThat(sut.isRunnig(), is(true));
			TestUtil.prompt("stopt()");
			sut.stop();
			TestUtil.prompt("isRunning() = false");
			assertThat(sut.isRunnig(), is(false));
			TestUtil.prompt("dispose()");
			sut.dispose();
		}
	}

}
