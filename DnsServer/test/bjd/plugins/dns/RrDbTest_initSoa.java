package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import bjd.net.Ip;

public final class RrDbTest_initSoa {
	//private boolean[] isSecret = new boolean[] { false, false, false, false, false };
	private String domainName = "aaa.com.";

	@Test
	public void 予め同一ドメインのNSレコードが有る場合成功する() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		boolean expected = true;
		sut.add(new RrNs("aaa.com.", 0, "ns.aaa.com."));
		//exercise
		boolean actual = RrDbTest.initSoa(sut, "aaa.com.", "mail.", 1, 2, 3, 4, 5);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 予め同一ドメインのNSレコードが無い場合失敗する_レコードが無い() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		boolean expected = false;
		//exercise
		boolean actual = RrDbTest.initSoa(sut, "aaa.com.", "mail.", 1, 2, 3, 4, 5);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 予め同一ドメインのNSレコードが無い場合失敗する_NSレコードはあるがドメインが違う() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		boolean expected = false;
		sut.add(new RrNs("bbb.com.", 0, "ns.bbb.com.")); //NSレコードはあるがドメインが違う
		//exercise
		boolean actual = RrDbTest.initSoa(sut, "aaa.com.", "mail.", 1, 2, 3, 4, 5);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 予め同一ドメインのNSレコードが無い場合失敗する_ドメインは同じだがNSレコードではない() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		boolean expected = false;
		sut.add(new RrA("aaa.com.", 0, new Ip("192.168.0.1"))); //ドメインは同じだがNSレコードではない
		//exercise
		boolean actual = RrDbTest.initSoa(sut, "aaa.com.", "mail.", 1, 2, 3, 4, 5);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 追加に成功したばあのSOAレコードの検証() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		sut.add(new RrNs("aaa.com.", 0, "ns.aaa.com."));
		//exercise
		RrDbTest.initSoa(sut, "aaa.com.", "root@aaa.com", 1, 2, 3, 4, 5);
		//verify
		assertThat(RrDbTest.size(sut), is(2)); //NS及びSOAの2件になっている
		RrSoa o = (RrSoa) RrDbTest.get(sut, 1);
		assertThat(o.getNameServer(), is("ns.aaa.com."));
		assertThat(o.getPostMaster(), is("root.aaa.com.")); //変換が完了している(@=>. 最後に.追加）
		assertThat(o.getSerial(), is(1)); 
		assertThat(o.getRefresh(), is(2)); 
		assertThat(o.getRetry(), is(3)); 
		assertThat(o.getExpire(), is(4)); 
		assertThat(o.getMinimum(), is(5)); 
		
	}
}
