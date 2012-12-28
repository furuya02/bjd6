package bjd.test;

import java.io.File;
import java.io.IOException;

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
	 * テンポラリディレクトリの作成<br>
	 * 最初に呼ばれたとき、ディレクトリが存在しないので、新規に作成される
	 * @return
	 */
	public static String getTmpDir(String tmpDir) {
		String currentDir = new File(".").getAbsoluteFile().getParent(); // カレントディレクトリ
		String dir = String.format("%s\\%s", currentDir, tmpDir);
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdir();
		}
		return dir;
	}

	/**
	 * 指定したテンポラリディレクトリ(tmpDir)の中での作成可能なテンポラリファイル(もしくはディレクトリ)名を生成する
	 * @return テンポラリファイル（ディレクトリ）名(パス)
	 * @throws IOException 
	 */
	public static String getTmpPath(String tmpDir) throws IOException {
		final String prefix = "test";
		final String suffix = ".ts";
		File file = File.createTempFile(prefix, suffix, new File(getTmpDir(tmpDir)));
		if (file.exists()) {
			file.delete();
		}
		return file.getPath();
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

	static String lastBanner = "";

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
		String banner = String.format("%s %s", className, methodName);
		if (!banner.equals(lastBanner)) {
			lastBanner = banner;
			System.out.println("-------------------------------------------------------------------");
			System.out.println(banner);
			System.out.println("-------------------------------------------------------------------");
		}
		System.out.println(msg);
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
	//パケットストリームの変換
	//************************************************
	public static byte[] hexStream2Bytes(String str) {
		byte[] buf = new byte[str.length() / 2];
		for (int i = 0; i < buf.length; i++) {
			buf[i] = (byte) Integer.parseInt(str.substring(i * 2, (i + 1) * 2), 16);
		}
		return buf;
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

	//************************************************
	//待ち時間が長い場合に、コンソールの＊を表示する
	//************************************************
	/**
	 * 待ち時間が長い場合に、コンソールの＊を表示する
	 * @param msg msg==nullの時、改行のみ表示（TearDown用）
	 */
	public static void waitDisp(String msg) {
		if (msg == null) {
			System.out.println("");
		} else {
			System.out.print(msg);
			Runnable r = new Runnable() {
				public void run() {
					while (true) {
						System.out.print("*");
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
						}
					}
				}
			};
			Thread thr1 = new Thread(r);
			thr1.start();
		}
	}
}
