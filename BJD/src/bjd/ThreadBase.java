package bjd;

import bjd.log.ILogger;
import bjd.log.LogKind;
import bjd.log.Logger;
import bjd.util.IDisposable;
import bjd.util.Util;

/**
 * スレッドの起動停止機能を持った基本クラス
 * 
 * @author SIN
 *
 */
public abstract class ThreadBase implements IDisposable, ILogger, ILife {
	private MyThread myThread = null;

	//private boolean isRunning = false;
	private ThreadBaseKind threadBaseKind = ThreadBaseKind.Before;
	private boolean life;
	private Logger logger;

	/**
	 * 
	 * @param logger　スレッド実行中に例外がスローされたとき表示するためのLogger(nullを設定可能)
	 */
	protected ThreadBase(Logger logger) {
		this.logger = logger;
	}

	/**
	 * スレッドの状態
	 * @return
	 */
	public final ThreadBaseKind getThreadBaseKind() {
		return threadBaseKind;
	}

	/**
	 * 時間を要するループがある場合、ループ条件で値がtrueであることを確認する<br>
	 * falseになったら直ちにループを中断する
	 * @return life
	 */
	public final boolean isLife() {
		return life;
	}

	/**
	 * 終了処理<br>
	 * Override可能<br>
	 */
	public void dispose() {
		stop();
	}

	/**
	 * 【スレッド開始前処理】
	 * @return falseでスレッド起動をやめる
	 */
	protected abstract boolean onStartThread();

	/**
	 * 	開始処理<br>
	 * Override可能<br>
	 */
	public void start() {
		if (threadBaseKind == ThreadBaseKind.Running) {
			return;
		}
		if (!onStartThread()) {
			return;
		}
		//try {
		threadBaseKind = ThreadBaseKind.Before;
		
		life = true;
		myThread = new MyThread();
		myThread.start();
		while (threadBaseKind == ThreadBaseKind.Before) {
			Util.sleep(10);
		}

		//} catch (Exception ex) {
		//	ex.printStackTrace();
		//}
	}

	/**
	 * 【スレッド終了処理】
	 */
	protected abstract void onStopThread();

	/**
	 * 	停止処理<br>
	 * Override可能<br>
	 */
	public void stop() {
		//		life = false; //スイッチを切るとLoop内の無限ループからbreakする
		//		while (threadBaseKind == ThreadBaseKind.Running) { //stop()を抜けた時点でisRunnigがfalseになるように、処理が終了するまで待つ
		//			//Util.sleep(100);
		//			while (threadBaseKind!=ThreadBaseKind.After) {
		//				Util.sleep(100);//breakした時点でAfterになるので、ループを抜けるまでここで待つ
		//            }
		//		}

		if (myThread != null && threadBaseKind == ThreadBaseKind.Running) {
			life = false; //スイッチを切るとLoop内の無限ループからbreakする
			while (threadBaseKind != ThreadBaseKind.After) {
				Util.sleep(100); //breakした時点でAfterになるので、ループを抜けるまでここで待つ
			}
		}
		onStopThread();
		myThread = null;
	}

	/**
	 * 【スレッドループ】
	 */
	protected abstract void onRunThread();

	private class MyThread extends Thread {
		@Override
		public void run() {
			//isRunning = true;
			threadBaseKind = ThreadBaseKind.Running;
			try {
				onRunThread();
			} catch (Exception ex) {
				if (logger != null) {
					logger.set(LogKind.ERROR, null, 9000021, ex.getMessage());
				}
			}
			//	kernel.getView().setColor();
			//isRunning = false;
			threadBaseKind = ThreadBaseKind.After;
		}
	}
}
