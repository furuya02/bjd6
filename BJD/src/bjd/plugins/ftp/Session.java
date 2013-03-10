package bjd.plugins.ftp;

import java.util.Random;

import bjd.sock.SockTcp;

/**
 * セッションごとの情報
 * 
 * @author SIN
 *
 */
public final class Session {
	private String userName = "";
	private OneUser oneUser = null;
	private CurrentDir currentDir = null;
	private FtpType ftpType = FtpType.ASCII;
	private SockTcp sockData = null;
	private SockTcp sockCtrl;
	private int port;
	private String rnfrName = "";

	public Session(SockTcp sockCtrl) {
		this.sockCtrl = sockCtrl;

		//PASV接続用ポート番号の初期化 (開始番号は2000～9900)
		Random rnd = new Random();
		port = (rnd.nextInt(79) + 20) * 100;

	}

	/**
	 * ユーザの設定
	 * @param userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * ユーザ名の取得<br>
	 * 単にUSERコマンドで送信された名前を記憶するだけで、ユーザ情報があるかどうかは関係ない
	 * @return
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * RCFRで指定されたパスの設定
	 * @param rnfrName
	 */
	public void setRnfrName(String rnfrName) {
		this.rnfrName = rnfrName;
	}

	/**
	 * RCFRで指定されたパスの取得
	 * @return
	 */
	public String getRnfrName() {
		return rnfrName;
	}

	/**
	 * PASV接続用ポート番号の設定
	 * @param port ポート番号
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * PASV接続用ポート番号の取得
	 * @return ポート番号
	 */
	public int getPort() {
		return port;
	}

	/**
	 * ユーザ情報設定<br>
	 * @param oneUser
	 */
	public void setOneUser(OneUser oneUser) {
		this.oneUser = oneUser;
	}

	/**
	 * ユーザ情報取得<br>
	 * USERコマンドで検索されるので、認証が完了しているかどうかは関係ない
	 * @return
	 */
	public OneUser getOneUser() {
		return oneUser;
	}

	/**
	 * 現在のカレントディレクトリの情報設定
	 * @param currentDirオブジェクト
	 */
	public void setCurrentDir(CurrentDir currentDir) {
		this.currentDir = currentDir;
	}

	/**
	 * 現在のカレントディレクトリの情報取得<br>
	 * ログイン後にログインユーザのホームディレクトリで初期化される<br>
	 * <font color=red>このオブジェクトがnullかどうかでログインが完了しているかどうかを表現している</fonr><br>
	 * @return CurrentiDirオブジェクト
	 */
	public CurrentDir getCurrentDir() {
		return currentDir;
	}

	/**
	 * 転送タイプの設定
	 * @param ftpType 
	 */
	public void setFtpType(FtpType ftpType) {
		this.ftpType = ftpType;
	}

	/**
	 * 転送タイプの取得 
	 * @return FtpType
	 */
	public FtpType getFtpType() {
		return ftpType;
	}

	/**
	 * データソケットの設定
	 * @param sockData
	 */
	public void setSockData(SockTcp sockData) {
		this.sockData = sockData;
	}

	/**
	 * データソケットの取得
	 * @return sockData
	 */
	public SockTcp getSockData() {
		return sockData;
	}

	/**
	 * １行送信
	 * @param str　送信文字列
	 */
	public void stringSend(String str) {
		sockCtrl.stringSend(str);
	}

	/**
	 * 制御ソケットの取得
	 * @return sockCtrl
	 */
	public SockTcp getSockCtrl() {
		return sockCtrl;
	}

}