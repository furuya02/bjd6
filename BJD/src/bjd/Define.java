package bjd;

import java.util.Calendar;

public final class Define {
	//デフォルトコンストラクタの隠蔽
	private Define() {
	}
//	static String executablePath = Application.ExecutablePath;
//	static String productVersion  = Application.ProductVersion;
//
//	//Test用
//	public static void SetEnv(String path,String ver) {
//	    executablePath = path;
//	    productVersion = ver;
//	}

	public static String getCopyright() {
	    return "Copyright(c) 1998/05.. by SIN/SapporoWorks";
	}

	public static String getApplicationName() {
	    return "BlackJumboDog";
	}
	//public static String ProductVersion() {
	//    return productVersion;
	//}

	public static String getDate() {
		Calendar c = Calendar.getInstance(); //現在時間で初期化される
		return String.format("%04d/%02d/%02d %02d:%02d:%02d",
				c.get(Calendar.YEAR),
				c.get(Calendar.MONTH) + 1,
				c.get(Calendar.DATE),
				c.get(Calendar.HOUR_OF_DAY),
				c.get(Calendar.MINUTE),
				c.get(Calendar.SECOND));
	}
	
//	public static string ServerAddress() {
//	    InitLocalInformation();//メンバ変数「localAddress」の初期化
//	    if (_localAddress.Count > 0)
//	        return _localAddress[0];
//	    return "127.0.0.1";
//	}
//	public static List<string> ServerAddressList() {
//	    InitLocalInformation();//メンバ変数「localAddress」の初期化
//	    return _localAddress;
//	}
	public static String getWebHome() {
	    return "http://www.sapporoworks.ne.jp/spw/";
	}
	public static String getWebDocument() {
	    return "http://www.sapporoworks.ne.jp/spw/?page_id=517";
	}
	public static String getWebSupport() {
	    return "http://www.sapporoworks.ne.jp/sbbs/sbbs.cgi?book=bjd";
	}

}
