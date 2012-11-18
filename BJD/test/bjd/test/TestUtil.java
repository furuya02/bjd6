package bjd.test;

import junit.framework.Assert;
import bjd.Kernel;
import bjd.ValidObjException;
import bjd.net.Ip;
import bjd.option.Conf;
import bjd.option.OptionBasic;
import bjd.option.OptionLog;
import bjd.option.OptionSample;
import bjd.util.Util;

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

	/**
	 * テスト用のプロンプト
	 * @param str 詳細情報
	 */
	public static void prompt(String msg) {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();

		String className = "";
		String methodName = "";

		String str = ste[2].getClassName();
		String[] n = str.split("\\$");
		if (n.length == 1) {
			String[] m = str.split("\\.");
			className = m[m.length - 1];
			methodName = ste[2].getMethodName();

		} else {
			String[] m = n[0].split("\\.");
			className = m[m.length - 1];
			methodName = n[1];
		}
		System.out.println(String.format("%s %s> %s", className, methodName, msg));
	}

	/**
	 * テスト用のIpオブジェクトの生成<br>
	 * パラメータ不良による制外発生をAssertで吸収
	 * @param ipStr 初期化文字列
	 * @return Ipオブジェクト
	 */
	public static Ip createIp(String ipStr) {
		Ip ip = null;
		try {
			ip = new Ip(ipStr);
		} catch (ValidObjException e) {
			Assert.fail(String.format("%s %s", e.getClass(), e.getMessage()));
		}
		return ip;
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
