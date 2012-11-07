package bjd.util;

import bjd.Kernel;
import bjd.option.Conf;
import bjd.option.OptionBasic;
import bjd.option.OptionLog;
import bjd.option.OptionSample;

public final class TestUtil {
	private TestUtil() {
		//デフォルトコンストラクタの隠蔽
	}
	
	/**
	 * テスト用のダミーのConf生成
	 * @return Conf
	 */
	public static Conf createConf(String optionName) {
		Kernel kernel = new Kernel();
		if (optionName.equals("OptionSample")) {
			return new Conf(new OptionSample(kernel, ""));
		} else if (optionName.equals("OptionLog")) {
			return new Conf(new OptionLog(kernel, ""));
		} else if (optionName.equals("OptionBasic")) {
			return new Conf(new OptionBasic(kernel, ""));
		}
		Util.runtimeException(String.format("%s not found", optionName));
		return null; //k実行時例外により、ここは実行されない
	}

	public static void dispHeader(String msg) {

		System.out.println("\n----------------------------------------------------");
		System.out.printf("%s\n", msg);
		System.out.println("----------------------------------------------------");
	}

	public static void dispPrompt(Object o) {
		System.out.printf("%s> ", o.getClass().getName());
	}

	public static void dispPrompt(Object o, String msg) {
		System.out.printf("%s> %s\n", o.getClass().getName(), msg);
	}

	//************************************************
	//コンソール出力用
	//************************************************
	public static String toString(byte[] buf) {
		StringBuilder sb = new StringBuilder();
		if (buf == null) {
			sb.append("null");
		} else {
			for (byte b : buf) {
				sb.append(String.format("0x%02x ", b & 0xFF));
			}
		}
		return sb.toString();
	}

	public static String toString(String str) {
		str = str.replaceAll("\r", "/r");
		str = str.replaceAll("\n", "/n");
		return str;
	}
}
