package bjd.plugins.dns;

import java.lang.reflect.Method;

import junit.framework.Assert;

import org.junit.Test;

import bjd.option.OneDat;

public final class RrDbTest_initSoa {
	//private boolean[] isSecret = new boolean[] { false, false, false, false, false };
	private String domainName = "aaa.com.";

	//リフレクションを使用してプライベートメソッドにアクセスする RrDb.addOneDat(String,OneDat)
	void initSoa(RrDb sut, String domainName, String mail, int serial, int refresh, int retry, int expire, int minimum) throws Exception {
		Class<RrDb> c = RrDb.class;
		Method m = c.getDeclaredMethod("addOneDat", new Class[] { String.class, String.class, int.class, int.class, int.class, int.class, int.class });
		m.setAccessible(true);
		m.invoke(sut, domainName, mail, serial, refresh, retry, expire, minimum);
	}

	@Test
	public void 予めNSレコードが有る場合成功する() throws Exception {

		Assert.fail("未実装");

	}

	@Test
	public void 予めNSレコードが無い場合失敗する() throws Exception {

		Assert.fail("未実装");

	}
}
