package bjd;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

<<<<<<< HEAD
<<<<<<< HEAD
import bjd.util.TestUtil;
=======
import bjd.test.TestUtil;
>>>>>>> work
=======
import bjd.test.TestUtil;
>>>>>>> work
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
<<<<<<< HEAD
=======

		MyThread sut = new MyThread();
>>>>>>> work

		sut.start();
		TestUtil.prompt("start()");
		assertThat(sut.isRunnig(), is(true));
		TestUtil.prompt("isRunnig()=true start()から返った時点で、isRunnig()はTrueになっている");

<<<<<<< HEAD
		myThread.start();
		TestUtil.prompt("myThread.start()");
		assertThat(myThread.isRunnig(), is(true));
		TestUtil.prompt("isRunnig()=true start()から返った時点で、isRunnig()はTrueになっている");

		myThread.stop();
		TestUtil.prompt("myThread.stop()");
		assertThat(myThread.isRunnig(), is(false));
		TestUtil.prompt("isRunnig()=false stop()から返った時点で、isRunnig()はfalseになっている");

		myThread.stop();
		TestUtil.prompt("myThread.stop() stop()が重複しても問題ない");

		//start()から返った時点で、isRunnig()はTrueになっている
		myThread.start();
		TestUtil.prompt("myThread.start()");
		assertThat(myThread.isRunnig(), is(true));
		TestUtil.prompt("isRunnig()=true");

		myThread.start(); //start()が重複しても問題ない
		TestUtil.prompt("myThread.start() start()が重複しても問題ない");

		myThread.stop();
		TestUtil.prompt("myThread.stop()");
		assertThat(myThread.isRunnig(), is(false));
		TestUtil.prompt("isRunnig()=false");

		myThread.dispose();
=======
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
>>>>>>> work
		TestUtil.prompt("myThread.dispose()");
	}

	@Test
	public final void start及びstopしてisRunnigの状態を確認する_負荷テスト() {

		MyThread sut = new MyThread();

		for (int i = 0; i < 5; i++) {
<<<<<<< HEAD
			TestUtil.prompt(String.format("[i=%d]", i));
			myThread.start();
			assertThat(myThread.isRunnig(), is(true));
			myThread.stop();
			assertThat(myThread.isRunnig(), is(false));
=======
			TestUtil.prompt(String.format("start() i=%d", i));
			sut.start();
			TestUtil.prompt("isRunning() = true");
			assertThat(sut.isRunnig(), is(true));
			TestUtil.prompt("stop()");
			sut.stop();
			TestUtil.prompt("isRunning() = false");
			assertThat(sut.isRunnig(), is(false));
>>>>>>> work
		}

		sut.dispose();
	}

	@Test
	public final void new及びstart_stop_disposeしてisRunnigの状態を確認する_負荷テスト() {

		for (int i = 0; i < 3; i++) {
<<<<<<< HEAD
			TestUtil.prompt(String.format("[i=%d]", i));
			MyThread myThread = new MyThread();
			myThread.start();
			assertThat(myThread.isRunnig(), is(true));
			myThread.stop();
			assertThat(myThread.isRunnig(), is(false));
			myThread.dispose();
=======
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
>>>>>>> work
		}
	}

}
