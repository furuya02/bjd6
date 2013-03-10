package bjd.plugins.ftp;

/**
 * FTPコマンド
 * @author SIN
 *
 */
enum FtpCmd {
    Quit,
    Noop,
    User,
    Pass,
    Cwd,
    Port,
    Eprt,
    Pasv,
    Epsv,
    Retr,
    Stor,
    Rnfr,
    Rnto,
    Abor,
    Dele,
    Rmd,
    Mkd,
    Pwd,
    List,
    Nlst,
    Type,
    Cdup,
    Syst,
    Unknown;

    
    /**
	 * 文字列からEnum値を取得する<br>
	 * <font color=red>該当がない場合、Unknownに設定される</font><br>
	 * 
	 * @param 文字列
	 * @return Enum値
	 */
	public static FtpCmd parse(String cmdStr) {
		for (FtpCmd p : values()) {
			//大文字・小文字の区別なく検査する
			if (p.toString().toUpperCase().equals(cmdStr.toUpperCase())) {
				return p;
			}
		}
		return FtpCmd.Unknown;
	}
}
