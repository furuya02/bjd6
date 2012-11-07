package bjd.sock;

/**
 * SockTcpで使用されるデータキュー
 * @author SIN
 *
 */
public final class SockQueue {
	private byte[] db = new byte[0]; //現在のバッファの内容
	private static int max = 1048560; //保持可能な最大数<=この辺りが適切な値かもしれない
	//TODO modifyの動作に不安あり（これ必要なのか？） 
	private boolean modify; //バッファに追加があった場合にtrueに変更される

	private Object lock = new Object(); // 排他制御

	public int getMax() {
		return max;
	}

	/**
	 * 空いているスペース
	 * @return
	 */
	public int getSpace() {
		return max - db.length;
	}

	/**
	 * 現在のキューに溜まっているデータ量
	 * @return
	 */
	public int length() {
		return db.length;
	}

	/**
	 * キューへの追加
	 * @param buf
	 * @param len
	 * @return 追加したバイト数
	 */
	public int enqueue(byte[] buf, int len) {
		if (getSpace() == 0) {
			return 0;
		}
		//空きスペースを越える場合は失敗する 0が返される
		if (getSpace() < len) {
			return 0;
		}

		synchronized (lock) {
			byte[] tmpBuf = new byte[db.length + len]; //テンポラリバッファ
			System.arraycopy(db, 0, tmpBuf, 0, db.length); //現有DBのデータをテンポラリ前部へコピー
			System.arraycopy(buf, 0, tmpBuf, db.length, len); //追加のデータをテンポラリ後部へコピー
			db = tmpBuf; //テンポラリを現用DBへ変更
			modify = true; //データベースの内容が変化した
			return len;
		}
	}

	/**
	 * キューからのデータ取得
	 * @param len
	 * @return
	 */
	public byte[] dequeue(int len) {
		if (db.length == 0 || len == 0 || !modify) {
			return new byte[0];
		}

		synchronized (lock) {
			//要求サイズが現有数を超える場合はカットする
			if (db.length < len) {
				len = db.length;
			}
			byte[] retBuf = new byte[len]; //出力用バッファ
			byte[] tmpBuf = new byte[db.length - len]; //テンポラリバッファ
			System.arraycopy(db, 0, retBuf, 0, len); //現有DBから出力用バッファへコピー
			System.arraycopy(db, len, tmpBuf, 0, db.length - len); //残りのデータをテンポラリへ
			db = tmpBuf; //テンポラリを現用DBへ変更

			if (db.length == 0) {
				modify = false; //次に何か受信するまで処理の必要はない
			}

			return retBuf;
		}
	}

	/**
	 * キューからの１行取り出し(\r\nを削除しない)
	 * @return
	 */
	public byte[] dequeueLine() {
		if (!modify) {
			return new byte[0];
		}
		synchronized (lock) {
			for (int i = 0; i < db.length; i++) {
				if (db[i] != '\n') {
					continue;
				}
				byte[] retBuf = new byte[i + 1]; //\r\nを削除しない
				System.arraycopy(db, 0, retBuf, 0, i + 1); //\r\nを削除しない
				byte[] tmpBuf = new byte[db.length - (i + 1)]; //テンポラリバッファ
				System.arraycopy(db, (i + 1), tmpBuf, 0, db.length - (i + 1)); //残りのデータをテンポラリへ
				db = tmpBuf; //テンポラリを現用DBへ変更

				return retBuf;
			}
			modify = false; //次に何か受信するまで処理の必要はない
			return new byte[0];
		}
	}	
}
