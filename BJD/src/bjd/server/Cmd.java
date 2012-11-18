package bjd.server;

/**
 * 受信したコマンドを表現するクラス<br>
 * 内部データは、nullの場合、""で初期化される
 * 
 * @author SIN
 *
 */
public final class Cmd {
	private String str;
	private String cmdStr;
	private String paramStr;

	public String getStr() {
		return str;
	}

	public String getCmdStr() {
		return cmdStr;
	}

	public String getParamStr() {
		return paramStr;
	}

	public Cmd(String str, String cmdStr, String paramStr) {
		this.str = (str == null) ? "" : str;
		this.cmdStr = (cmdStr == null) ? "" : cmdStr;
		this.paramStr = (paramStr == null) ? "" : paramStr;
	}
}
