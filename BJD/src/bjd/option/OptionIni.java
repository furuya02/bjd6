package bjd.option;

import bjd.Kernel;
import bjd.util.IniDb;
import bjd.util.Util;

/**
 * Option.iniを表現するクラス<br>
 * プログラムで唯一のインスタンス<br>
 * @author user1
 *
 */
public final class OptionIni {
	private static IniDb iniDb = null;

	/**
	 * コンストラクタの隠蔽
	 */
	private OptionIni() {
	}

	public static void create(Kernel kernel) {
		if (iniDb != null) {
			//Util.runtimeException("既にcreate()されています");
		}
		iniDb = new IniDb(kernel.getProgDir(), "Option");
	}

	public static IniDb getInstance() {
		if (iniDb == null) {
			Util.runtimeException("create()されていません");
		}
		return iniDb;
	}

}
