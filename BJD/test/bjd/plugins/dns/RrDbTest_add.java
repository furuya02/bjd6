package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;

import bjd.net.Ip;

public final class RrDbTest_add {

	//リフレクションを使用してプライベートメソッドにアクセスする RrDb.get(int)
	OneRr get(RrDb sut, int index) throws Exception {
		Class<RrDb> c = RrDb.class;
		Method m = c.getDeclaredMethod("get", new Class[] { int.class });
		m.setAccessible(true);
		return (OneRr) m.invoke(sut, index);
	}

	//リフレクションを使用してプライベートメソッドにアクセスする RrDb.size()
	int size(RrDb sut) throws Exception {
		Class<RrDb> c = RrDb.class;
		Method m = c.getDeclaredMethod("size");
		m.setAccessible(true);
		return (int) m.invoke(sut);
	}

	@Test
	public void 新規のリソース追加は成功する() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		boolean expected = true; //成功
		//exercise
		boolean actual = sut.add(new RrA("domain", 100, new Ip("1.2.3.4")));
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 同一リソースの追加_TTLが0の場合は失敗する() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		boolean expected = false; //失敗
		//exercise
		int ttl = 0; //最初のリソースはTTL=0
		sut.add(new RrA("domain", ttl, new Ip("1.2.3.4")));
		boolean actual = sut.add(new RrA("domain", 100, new Ip("1.2.3.4")));
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void 同一リソースの追加_TTLが0以外の場合は上書きされる() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		//exercise
		int ttl = 10; //最初のリソースはTTL=0以外
		sut.add(new RrA("domain", ttl, new Ip("1.2.3.4")));
		sut.add(new RrA("domain", 20, new Ip("1.2.3.4")));
		//verify
		assertThat(size(sut), is(1)); //件数は１件になる
		assertThat(get(sut, 0).getTtl(), is(20)); //TTLは後から追加した20になる
	}

	@Test
	public void 異なるリソースの追加() throws Exception {
		//setUp
		RrDb sut = new RrDb();
		int expected = 3; //全部で3件になる
		//exercise
		sut.add(new RrA("domain", 10, new Ip("1.2.3.4")));
		sut.add(new RrA("domain", 10, new Ip("3.4.5.6")));
		sut.add(new RrNs("domain", 10, "ns"));
		int actual = size(sut);
		//verify
		assertThat(actual, is(expected));
	}

}
