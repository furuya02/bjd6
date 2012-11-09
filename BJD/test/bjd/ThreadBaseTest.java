package bjd;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import bjd.util.TestUtil;
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
	public final void test() {

		TestUtil.dispHeader("start() stop()　してisRunnig()の状態を確認する"); //TESTヘッダ

		MyThread myThread = new MyThread();

		myThread.start();
		TestUtil.dispPrompt(this, "myThread.start()");
		assertThat(myThread.isRunnig(), is(true));
		TestUtil.dispPrompt(this, "isRunnig()=true start()から返った時点で、isRunnig()はTrueになっている");

		myThread.stop();
		TestUtil.dispPrompt(this, "myThread.stop()");
		assertThat(myThread.isRunnig(), is(false));
		TestUtil.dispPrompt(this, "isRunnig()=false stop()から返った時点で、isRunnig()はfalseになっている");

		myThread.stop();
		TestUtil.dispPrompt(this, "myThread.stop() stop()が重複しても問題ない");

		//start()から返った時点で、isRunnig()はTrueになっている
		myThread.start();
		TestUtil.dispPrompt(this, "myThread.start()");
		assertThat(myThread.isRunnig(), is(true));
		TestUtil.dispPrompt(this, "isRunnig()=true");

		myThread.start(); //start()が重複しても問題ない
		TestUtil.dispPrompt(this, "myThread.start() start()が重複しても問題ない");

		myThread.stop();
		TestUtil.dispPrompt(this, "myThread.stop()");
		assertThat(myThread.isRunnig(), is(false));
		TestUtil.dispPrompt(this, "isRunnig()=false");

		myThread.dispose();
		TestUtil.dispPrompt(this, "myThread.dispose()");
	}

	@Test
	public final void test2() {

		TestUtil.dispHeader("start() stop()　してisRunnig()の状態を確認する(負荷テスト)"); //TESTヘッダ

		MyThread myThread = new MyThread();

		for (int i = 0; i < 5; i++) {
			TestUtil.dispPrompt(this, String.format("[i=%d]", i));
			myThread.start();
			assertThat(myThread.isRunnig(), is(true));
			myThread.stop();
			assertThat(myThread.isRunnig(), is(false));
		}

		myThread.dispose();
	}

	@Test
	public final void test3() {

		TestUtil.dispHeader("new start() stop()　dispose してisRunnig()の状態を確認する(負荷テスト)"); //TESTヘッダ

		for (int i = 0; i < 3; i++) {
			TestUtil.dispPrompt(this, String.format("[i=%d]", i));
			MyThread myThread = new MyThread();
			myThread.start();
			assertThat(myThread.isRunnig(), is(true));
			myThread.stop();
			assertThat(myThread.isRunnig(), is(false));
			myThread.dispose();
		}
	}

}
