package bjd.plugins.ftp;

import bjd.util.IDispose;

/**
 * Datオブジェクトの各プロパティをObject形式ではない本来の型で強制するため、これを表現するクラスを定義する
 * 
 * @author SIN
 *
 */
class OneUser implements IDispose {

	private FtpAcl ftpAcl;
	private String userName;
	private String password;
	private String homeDir;
	
	/**
	 * アクセス権種類リの取得
	 * @return FtoAcl
	 */
	public FtpAcl getFtpAcl() {
		return ftpAcl;
	}
	/**
	 * ユーザ名リの取得
	 * @return userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * パスワードリの取得
	 * @return password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * ホームディレクトリの取得
	 * @return homeDir
	 */
	public String getHomeDir() {
		return homeDir;
	}

	public OneUser(FtpAcl ftpAcl, String userName, String password, String homeDir) {
        this.ftpAcl = ftpAcl;
        this.userName = userName;
        this.password = password;
        //ホームディレクトリの指定は、必ず最後が\\になるようにする
        if (homeDir.charAt(homeDir.length() - 1) != '\\') {
            homeDir = homeDir + "\\";
        }
        this.homeDir = homeDir;
    }
	@Override
	public void dispose() {
	
	}

}
