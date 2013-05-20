package bjd;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import bjd.util.Util;

public final class ThreadBaseTest {

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
	public void startする前はThreadBaeKindはBeforeとなる() throws Exception {
		//setUp
		MyThread sut = new MyThread();
		ThreadBaseKind expected = ThreadBaseKind.Before;
		//exercise
		ThreadBaseKind actual = sut.getThreadBaseKind();
		//verify
		assertThat(actual, is(expected));
		//tearDown
		sut.dispose();
	}

	@Test
	public void startするとisRunningはtrueとなる() throws Exception {
		//setUp
		MyThread sut = new MyThread();
		ThreadBaseKind expected = ThreadBaseKind.Running;
		//exercise
		sut.start();
		ThreadBaseKind actual = sut.getThreadBaseKind();
		//verify
		assertThat(actual, is(expected));
		//tearDown
		sut.dispose();
	}

	@Test
	public void startは重複しても問題ない() throws Exception {
		//setUp
		MyThread sut = new MyThread();
		ThreadBaseKind expected = ThreadBaseKind.Running;
		//exercise
		sut.start();
		sut.start(); //重複
		ThreadBaseKind actual = sut.getThreadBaseKind();
		//verify
		assertThat(actual, is(expected));
		//tearDown
		sut.dispose();
	}

	@Test
	public void stopは重複しても問題ない() throws Exception {
		//setUp
		MyThread sut = new MyThread();
		ThreadBaseKind expected = ThreadBaseKind.After;
		//exercise
		sut.stop(); //重複
		sut.start();
		sut.stop();
		sut.stop(); //重複
		ThreadBaseKind actual = sut.getThreadBaseKind();
		//verify
		assertThat(actual, is(expected));
		//tearDown
		sut.dispose();
	}

	@Test
	public final void start及びstopしてisRunnigの状態を確認する_負荷テスト() throws Exception {

		//setUp
		MyThread sut = new MyThread();
		//exercise verify 
		for (int i = 0; i < 5; i++) {
			sut.start();
			assertThat(sut.getThreadBaseKind(), is(ThreadBaseKind.Running));
			sut.stop();
			assertThat(sut.getThreadBaseKind(), is(ThreadBaseKind.After));
		}
		//tearDown
		sut.dispose();
	}

	@Test
	public final void new及びstart_stop_disposeしてisRunnigの状態を確認する_負荷テスト() {

		//exercise verify 
		for (int i = 0; i < 3; i++) {
			MyThread sut = new MyThread();
			sut.start();
			assertThat(sut.getThreadBaseKind(), is(ThreadBaseKind.Running));
			sut.stop();
			assertThat(sut.getThreadBaseKind(), is(ThreadBaseKind.After));
			sut.dispose();
		}
	}

}
