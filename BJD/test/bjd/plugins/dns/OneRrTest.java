package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;

import bjd.net.Ip;

public final class OneRrTest {

	/**
	 * テスト用にOneRrを継承したクラスを定義する
	 */
	class RrTest extends OneRr {
		public RrTest(String name, DnsType dnsType, int ttl, String data) {
			super(name, dnsType, ttl, data.getBytes());
		}

	}

	@Test
	public void isEffective_ttlが0の場合_どんな時間で確認してもtrueが返る() throws Exception {
		//setUp
		int ttl = 0;
		RrTest sut = new RrTest("name", DnsType.A, ttl, "123");
		boolean expected = true;
		long now = 1; //nowはいくつであっても結果は変わらない
		//exercise
		boolean actual = sut.isEffective(now);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void isEffective_ttlが10秒の場合_10秒後で確認するとtrueが返る() throws Exception {
		//setUp
		long now = Calendar.getInstance().getTimeInMillis();//現在時間
		int ttl = 10; //生存時間は10秒
		RrTest sut = new RrTest("name", DnsType.A, ttl, "123");
		boolean expected = true;
		//exercise
		boolean actual = sut.isEffective(now + 10 * 1000); //10秒後
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void isEffective_ttlが10秒の場合_11秒後で確認するとfalseが返る() throws Exception {
		//setUp
		long now = Calendar.getInstance().getTimeInMillis(); //現在時間
		int ttl = 10; //生存時間は10秒
		RrTest sut = new RrTest("name", DnsType.A, ttl, "123");
		boolean expected = false;
		//exercise
		boolean actual = sut.isEffective(now + 11 * 1000); //11秒後
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void cloneでAレコードの複製を作成() throws Exception {
		//setUp
		DnsType expected = DnsType.A;
		RrTest sut = new RrTest("name", expected, 10, "123");
		//exercise
		OneRr o = sut.clone(100);
		//verify
		assertThat(o.getTtl(), is(100)); //TTLは100に変化している
		assertThat(o.getName(), is("name")); //その他は同じ
		assertThat(o.getDnsType(), is(expected)); //その他は同じ
		assertThat(o.getData(), is("123".getBytes())); //その他は同じ
	}

	@Test
	public void cloneでAAAAレコードの複製を作成() throws Exception {
		//setUp
		DnsType expected = DnsType.Aaaa;
		RrTest sut = new RrTest("name", expected, 10, "123");
		//exercise
		OneRr o = sut.clone(100);
		//verify
		assertThat(o.getTtl(), is(100)); //TTLは100に変化している
		assertThat(o.getName(), is("name")); //その他は同じ
		assertThat(o.getDnsType(), is(expected)); //その他は同じ
		assertThat(o.getData(), is("123".getBytes())); //その他は同じ
	}

	@Test
	public void cloneでNSレコードの複製を作成() throws Exception {
		//setUp
		DnsType expected = DnsType.Ns;
		RrTest sut = new RrTest("name", expected, 10, "123");
		//exercise
		OneRr o = sut.clone(100);
		//verify
		assertThat(o.getTtl(), is(100)); //TTLは100に変化している
		assertThat(o.getName(), is("name")); //その他は同じ
		assertThat(o.getDnsType(), is(expected)); //その他は同じ
		assertThat(o.getData(), is("123".getBytes())); //その他は同じ
	}

	@Test
	public void cloneでMxレコードの複製を作成() throws Exception {
		//setUp
		DnsType expected = DnsType.Mx;
		RrTest sut = new RrTest("name", expected, 10, "123");
		//exercise
		OneRr o = sut.clone(100);
		//verify
		assertThat(o.getTtl(), is(100)); //TTLは100に変化している
		assertThat(o.getName(), is("name")); //その他は同じ
		assertThat(o.getDnsType(), is(expected)); //その他は同じ
		assertThat(o.getData(), is("123".getBytes())); //その他は同じ
	}

	@Test
	public void cloneでCnameレコードの複製を作成() throws Exception {
		//setUp
		DnsType expected = DnsType.Cname;
		RrTest sut = new RrTest("name", expected, 10, "123");
		//exercise
		OneRr o = sut.clone(100);
		//verify
		assertThat(o.getTtl(), is(100)); //TTLは100に変化している
		assertThat(o.getName(), is("name")); //その他は同じ
		assertThat(o.getDnsType(), is(expected)); //その他は同じ
		assertThat(o.getData(), is("123".getBytes())); //その他は同じ
	}

	@Test
	public void cloneでSoaレコードの複製を作成() throws Exception {
		//setUp
		DnsType expected = DnsType.Soa;
		RrTest sut = new RrTest("name", expected, 10, "123");
		//exercise
		OneRr o = sut.clone(100);
		//verify
		assertThat(o.getTtl(), is(100)); //TTLは100に変化している
		assertThat(o.getName(), is("name")); //その他は同じ
		assertThat(o.getDnsType(), is(expected)); //その他は同じ
		assertThat(o.getData(), is("123".getBytes())); //その他は同じ
	}

	@Test
	public void equalsで同一のオブジェクトを比較するとtrueが返る() throws Exception {
		//setUp
		RrA sut = new RrA("name", 10, new Ip("192.168.0.1"));
		boolean expected = true;
		//exercise
		boolean actual = sut.equals(sut);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void equalsでnullトを比較するとfalseが返る() throws Exception {
		//setUp
		RrA sut = new RrA("name", 10, new Ip("192.168.0.1"));
		boolean expected = false;
		//exercise
		boolean actual = sut.equals(null);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void equalsでデータが異なるオブジェクトを比較するとfalseが返る() throws Exception {
		//setUp
		RrA sut = new RrA("name", 10, new Ip("192.168.0.1"));
		boolean expected = false;
		//exercise
		boolean actual = sut.equals(new RrA("name", 10, new Ip("192.168.0.2")));
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void equalsで名前が異なるオブジェクトを比較するとfalseが返る() throws Exception {
		//setUp
		RrA sut = new RrA("name", 10, new Ip("192.168.0.1"));
		boolean expected = false;
		//exercise
		boolean actual = sut.equals(new RrA("other", 10, new Ip("192.168.0.1")));
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void equalsでDnsTYpeが異なるオブジェクトを比較するとfalseが返る() throws Exception {
		//setUp
		RrA sut = new RrA("name", 10, new Ip("0.0.0.1"));
		boolean expected = false;
		//exercise
		boolean actual = sut.equals(new RrAaaa("name", 10, new Ip("::1")));
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void equalsでTTLが異なるオブジェクトを比較するとfalseが返る() throws Exception {
		//setUp
		RrA sut = new RrA("name", 10, new Ip("0.0.0.1"));
		boolean expected = false;
		//exercise
		boolean actual = sut.equals(new RrA("name", 20, new Ip("0.0.0.1")));
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void equalsでDataが異なるオブジェクトを比較するとfalseが返る() throws Exception {
		//setUp
		RrTest sut = new RrTest("name", DnsType.A, 10, "123");
		boolean expected = false;
		//exercise
		boolean actual = sut.equals(new RrTest("name", DnsType.A, 10, "12"));
		//verify
		assertThat(actual, is(expected));
	}
}
