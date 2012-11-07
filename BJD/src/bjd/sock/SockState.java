package bjd.sock;

/**
 * ソケットオブジェクト（SockObj）の状態
 * 
 * @author user1
 *
 */
public enum SockState {
	//TODO 移植完了後　リファクタリングで大文字に変更
	/**初期状態*/
	IDLE,
	/**接続完了*/
	CONNECT,
	/**bind完了*/
	Bind,
	/**エラー（切断）状態　使用できない*/
	Error, 
}
