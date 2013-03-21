package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import bjd.Kernel;
import bjd.ValidObjException;
import bjd.net.Ip;
import bjd.net.IpKind;
import bjd.net.OneBind;
import bjd.net.ProtocolKind;
import bjd.option.Conf;
import bjd.sock.SockUdp;
import bjd.test.TestUtil;
import bjd.test.TmpOption;
import bjd.util.Util;

/**
 * このテストを成功させるには、c:\dev\bjd5\BJD\binにDnsServer.dllが必要
 * @author SIN
 *
 */
public final class ServerTest {

	private static TmpOption op = null; //設定ファイルの上書きと退避
	private static Server sv = null; //サーバ
	private SockUdp cl; //クライアント

	@BeforeClass
	public static void beforeClass() throws Exception {

		//named.caのコピー
		String src = String.format("%s\\DnsServer\\test\\named.ca", TestUtil.getProhjectDirectory());
		String dst = String.format("%s\\BJD\\bin\\named.ca", TestUtil.getProhjectDirectory());
		Util.fileCopy(new File(src), new File(dst));

		//設定ファイルの退避と上書き
		op = new TmpOption(String.format("%s\\DnsServer\\test\\DnsServerTest.ini", TestUtil.getProhjectDirectory()));
		OneBind oneBind = new OneBind(new Ip(IpKind.V4_LOCALHOST), ProtocolKind.Udp);
		Kernel kernel = new Kernel();
		Option option = new Option(kernel, "");
		Conf conf = new Conf(option);

		//サーバ起動
		sv = new Server(kernel, conf, oneBind);
		sv.start();

	}

	@AfterClass
	public static void afterClass() {

		//サーバ停止
		sv.stop();
		sv.dispose();

		//設定ファイルのリストア
		op.dispose();

	}

	private PacketDns lookup(DnsType dnsType, String name) throws IOException {
		return lookup(dnsType, name, false);
	}

	/**
	 * 共通メソッド<br>
	 * リクエスト送信して、サーバから返ったデータをDNSパケットとしてデコードする<br>
	 * レスポンスが無い場合は、1秒でタイムアウトしてnullを返す<br>
	 * @throws IOException 
	 * @throws ValidObjException 
	 * @throws Exception
	 */
	private PacketDns lookup(DnsType dnsType, String name, boolean rd) throws IOException {
		//乱数で識別子生成
		short id = (short) (new Random()).nextInt(100);
		//送信パケット生成
		PacketDns sp = new PacketDns(id, false, false, rd, false);
		//質問フィールド追加
		sp.addRR(RrKind.QD, new RrQuery(name, dnsType));
		//クライアントソケット生成、及び送信
		SockUdp cl = new SockUdp(new Ip(IpKind.V4_LOCALHOST), 53, null, sp.getBytes());
		//受信
		byte[] recvBuf = cl.recv(3);
		if (recvBuf.length == 0) { //受信データが無い場合
			return null;
		}
		//System.out.println(String.format("lookup(%s,\"%s\") recv().length=%d", dnsType, name, recvBuf.length));
		//デコード
		PacketDns p = new PacketDns(recvBuf);
		//System.out.println(print(p));
		return p;
	}

	/**
	 * 共通メソッド<br>
	 * リソースレコードの数を表示する<br>
	 * @param p
	 * @return
	 */
	private String print(PacketDns p) {
		return String.format("QD=%d AN=%d NS=%d AR=%d", p.getCount(RrKind.QD), p.getCount(RrKind.AN), p.getCount(RrKind.NS), p.getCount(RrKind.AR));
	}

	/**
	 * 共通メソッド<br>
	 * リソースレコードのtoString()
	 * @param p
	 * @param rrKind
	 * @param n
	 * @return
	 */
	private String print(PacketDns p, RrKind rrKind, int n) {
		OneRr o = p.getRR(rrKind, n);
		if (rrKind == RrKind.QD) {
			return ((RrQuery) o).toString();
		}
		return print(o);
	}

	/**
	 * 共通メソッド
	 * リソースレコードのtoString()
	 * @param o
	 * @return
	 */
	private String print(OneRr o) {
		switch (o.getDnsType()) {
			case A:
				return ((RrA) o).toString();
			case Aaaa:
				return ((RrAaaa) o).toString();
			case Ns:
				return ((RrNs) o).toString();
			case Mx:
				return ((RrMx) o).toString();
			case Ptr:
				return ((RrPtr) o).toString();
			case Soa:
				return ((RrSoa) o).toString();
			case Cname:
				return ((RrCname) o).toString();
			default:
				Util.runtimeException("not implement.");
				break;
		}
		return "";
	}

	@Test
	public void ステータス情報_toString_の出力確認() throws Exception {

		String expected = "+ サービス中 \t                 Dns\t[127.0.0.1\t:UDP 53]\tThread";

		//exercise
		String actual = sv.toString().substring(0, 56);
		//verify
		assertThat(actual, is(expected));

	}

	@Test
	public void localhostの検索_タイプA() throws Exception {

		//exercise
		PacketDns p = lookup(DnsType.A, "localhost");
		//verify
		assertThat(print(p), is("QD=1 AN=1 NS=1 AR=0"));
		assertThat(print(p, RrKind.QD, 0), is("Query A localhost."));
		assertThat(print(p, RrKind.AN, 0), is("A localhost. TTL=2400 127.0.0.1"));
		assertThat(print(p, RrKind.NS, 0), is("Ns localhost. TTL=2400 localhost."));
	}

	@Test
	public void localhostの検索_タイプAAAA() throws Exception {
		//exercise
		PacketDns p = lookup(DnsType.Aaaa, "localhost");

		//verify
		assertThat(print(p), is("QD=1 AN=1 NS=1 AR=0"));
		assertThat(print(p, RrKind.QD, 0), is("Query Aaaa localhost."));
		assertThat(print(p, RrKind.AN, 0), is("Aaaa localhost. TTL=2400 ::1"));
		assertThat(print(p, RrKind.NS, 0), is("Ns localhost. TTL=2400 localhost."));
	}

	@Test
	public void localhostの検索_タイプPTR() throws Exception {
		//exercise
		PacketDns p = lookup(DnsType.Ptr, "localhost");
		//verify
		assertThat(print(p), is("QD=1 AN=0 NS=0 AR=0"));
		assertThat(print(p, RrKind.QD, 0), is("Query Ptr localhost."));
	}

	@Test
	public void localhost_V4の検索_タイプPTR() throws Exception {
		//exercise
		PacketDns p = lookup(DnsType.Ptr, "1.0.0.127.in-addr.arpa");
		//verify
		assertThat(print(p), is("QD=1 AN=1 NS=0 AR=0"));
		assertThat(print(p, RrKind.QD, 0), is("Query Ptr 1.0.0.127.in-addr.arpa."));
		assertThat(print(p, RrKind.AN, 0), is("Ptr 1.0.0.127.in-addr.arpa. TTL=2400 localhost."));

	}

	@Test
	public void localhost_V6の検索_タイプPTR() throws Exception {
		//exercise
		PacketDns p = lookup(DnsType.Ptr, "1.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.ip6.arpa");
		//verify
		assertThat(print(p), is("QD=1 AN=1 NS=0 AR=0"));
		assertThat(print(p, RrKind.QD, 0), is("Query Ptr 1.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.ip6.arpa."));
		assertThat(print(p, RrKind.AN, 0), is("Ptr 1.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.ip6.arpa. TTL=2400 localhost."));
	}

	@Test
	public void 自ドメインの検索_タイプA_www_aaa_com() throws Exception {
		//exercise
		PacketDns p = lookup(DnsType.A, "www.aaa.com");

		//verify
		assertThat(print(p), is("QD=1 AN=1 NS=1 AR=1"));
		assertThat(print(p, RrKind.QD, 0), is("Query A www.aaa.com."));
		assertThat(print(p, RrKind.AN, 0), is("A www.aaa.com. TTL=2400 192.168.0.10"));
		assertThat(print(p, RrKind.NS, 0), is("Ns aaa.com. TTL=2400 ns.aaa.com."));
		assertThat(print(p, RrKind.AR, 0), is("A ns.aaa.com. TTL=2400 192.168.0.1"));
	}

	@Test
	public void 自ドメインの検索_タイプA_xxx_aaa_com_存在しない() throws Exception {
		//exercise
		PacketDns p = lookup(DnsType.A, "xxx.aaa.com");

		//verify
		assertThat(print(p), is("QD=1 AN=0 NS=1 AR=1"));
		assertThat(print(p, RrKind.QD, 0), is("Query A xxx.aaa.com."));
		assertThat(print(p, RrKind.NS, 0), is("Ns aaa.com. TTL=2400 ns.aaa.com."));
		assertThat(print(p, RrKind.AR, 0), is("A ns.aaa.com. TTL=2400 192.168.0.1"));
	}

	@Test
	public void 自ドメインの検索_タイプNS_aaa_com() throws Exception {
		//exercise
		PacketDns p = lookup(DnsType.Ns, "aaa.com");

		//verify
		assertThat(print(p), is("QD=1 AN=1 NS=0 AR=1"));
		assertThat(print(p, RrKind.QD, 0), is("Query Ns aaa.com."));
		assertThat(print(p, RrKind.AN, 0), is("Ns aaa.com. TTL=2400 ns.aaa.com."));
		assertThat(print(p, RrKind.AR, 0), is("A ns.aaa.com. TTL=2400 192.168.0.1"));
	}

	@Test
	public void 自ドメインの検索_タイプMX_aaa_com() throws Exception {
		//exercise
		PacketDns p = lookup(DnsType.Mx, "aaa.com");

		//verify
		assertThat(print(p), is("QD=1 AN=1 NS=0 AR=1"));
		assertThat(print(p, RrKind.QD, 0), is("Query Mx aaa.com."));
		assertThat(print(p, RrKind.AN, 0), is("Mx aaa.com. TTL=2400 15 smtp.aaa.com."));
		assertThat(print(p, RrKind.AR, 0), is("A smtp.aaa.com. TTL=2400 192.168.0.2"));
	}

	@Test
	public void 自ドメインの検索_タイプAAAA_www_aaa_com() throws Exception {
		//exercise
		PacketDns p = lookup(DnsType.Aaaa, "www.aaa.com");

		//verify
		assertThat(print(p), is("QD=1 AN=1 NS=1 AR=1"));
		assertThat(print(p, RrKind.QD, 0), is("Query Aaaa www.aaa.com."));
		assertThat(print(p, RrKind.AN, 0), is("Aaaa www.aaa.com. TTL=2400 fe80::3882:6dac:af18:cba6"));
		assertThat(print(p, RrKind.NS, 0), is("Ns aaa.com. TTL=2400 ns.aaa.com."));
		assertThat(print(p, RrKind.AR, 0), is("A ns.aaa.com. TTL=2400 192.168.0.1"));
	}

	@Test
	public void 自ドメインの検索_タイプAAAA_xxx_aaa_com_存在しない() throws Exception {
		//exercise
		PacketDns p = lookup(DnsType.Aaaa, "xxx.aaa.com");

		//verify
		assertThat(print(p), is("QD=1 AN=0 NS=1 AR=1"));
		assertThat(print(p, RrKind.QD, 0), is("Query Aaaa xxx.aaa.com."));
		assertThat(print(p, RrKind.NS, 0), is("Ns aaa.com. TTL=2400 ns.aaa.com."));
		assertThat(print(p, RrKind.AR, 0), is("A ns.aaa.com. TTL=2400 192.168.0.1"));
	}

	@Test
	public void 自ドメインの検索_タイプCNAME_www2_aaa_com() throws Exception {
		//exercise
		PacketDns p = lookup(DnsType.Cname, "www2.aaa.com");

		//verify
		assertThat(print(p), is("QD=1 AN=1 NS=1 AR=3"));
		assertThat(print(p, RrKind.QD, 0), is("Query Cname www2.aaa.com."));
		assertThat(print(p, RrKind.AN, 0), is("Cname www2.aaa.com. TTL=2400 www.aaa.com."));
		assertThat(print(p, RrKind.NS, 0), is("Ns aaa.com. TTL=2400 ns.aaa.com."));
		assertThat(print(p, RrKind.AR, 0), is("A www.aaa.com. TTL=2400 192.168.0.10"));
		assertThat(print(p, RrKind.AR, 1), is("Aaaa www.aaa.com. TTL=2400 fe80::3882:6dac:af18:cba6"));
		assertThat(print(p, RrKind.AR, 2), is("A ns.aaa.com. TTL=2400 192.168.0.1"));
	}

	@Test
	public void 自ドメインの検索_タイプCNAME_www_aaa_com_逆検索() throws Exception {
		//exercise
		PacketDns p = lookup(DnsType.Cname, "www.aaa.com");

		//verify
		assertThat(print(p), is("QD=1 AN=0 NS=1 AR=1"));
		assertThat(print(p, RrKind.QD, 0), is("Query Cname www.aaa.com."));
		assertThat(print(p, RrKind.NS, 0), is("Ns aaa.com. TTL=2400 ns.aaa.com."));
		assertThat(print(p, RrKind.AR, 0), is("A ns.aaa.com. TTL=2400 192.168.0.1"));
	}

	@Test
	public void 自ドメインの検索_タイプPTR_192_168_0_1() throws Exception {
		//exercise
		PacketDns p = lookup(DnsType.Ptr, "1.0.168.192.in-addr.arpa");

		//verify
		assertThat(print(p), is("QD=1 AN=2 NS=0 AR=0"));
		assertThat(print(p, RrKind.QD, 0), is("Query Ptr 1.0.168.192.in-addr.arpa."));
		assertThat(print(p, RrKind.AN, 0), is("Ptr 1.0.168.192.in-addr.arpa. TTL=2400 ns.aaa.com."));
		assertThat(print(p, RrKind.AN, 1), is("Ptr 1.0.168.192.in-addr.arpa. TTL=2400 ws0.aaa.com."));
	}

	@Test
	public void 自ドメインの検索_タイプPTR_192_168_0_222_存在しない() throws Exception {
		//exercise
		PacketDns p = lookup(DnsType.Ptr, "222.0.168.192.in-addr.arpa");

		//verify
		Assert.assertNull(p); //レスポンスが無いことを確認する

	}

	@Test
	public void 他ドメインの検索_タイプA() throws Exception {
		//exercise
		PacketDns p = lookup(DnsType.A, "www.google.com", true);

		//verify
		assertThat(print(p), is("QD=1 AN=5 NS=4 AR=4"));
		assertThat(print(p, RrKind.QD, 0), is("Query A www.google.com."));

		ArrayList<String> ar = new ArrayList<String>();
		for (int i = 0; i < 5; i++) {
			ar.add(print(p, RrKind.AN, i));
		}
		Collections.sort(ar);

		assertThat(ar.get(0), is("A www.google.com. TTL=300 173.194.38.112"));
		assertThat(ar.get(1), is("A www.google.com. TTL=300 173.194.38.113"));
		assertThat(ar.get(2), is("A www.google.com. TTL=300 173.194.38.114"));
		assertThat(ar.get(3), is("A www.google.com. TTL=300 173.194.38.115"));
		assertThat(ar.get(4), is("A www.google.com. TTL=300 173.194.38.116"));

		ar.clear();
		for (int i = 0; i < 4; i++) {
			ar.add(print(p, RrKind.NS, i));
		}
		Collections.sort(ar);
		assertThat(ar.get(0), is("Ns google.com. TTL=172800 ns1.google.com."));
		assertThat(ar.get(1), is("Ns google.com. TTL=172800 ns2.google.com."));
		assertThat(ar.get(2), is("Ns google.com. TTL=172800 ns3.google.com."));
		assertThat(ar.get(3), is("Ns google.com. TTL=172800 ns4.google.com."));

		ar.clear();
		for (int i = 0; i < 4; i++)
			ar.add(print(p, RrKind.AR, i));
		Collections.sort(ar);
		assertThat(ar.get(0), is("A ns1.google.com. TTL=172800 216.239.32.10"));
		assertThat(ar.get(1), is("A ns2.google.com. TTL=172800 216.239.34.10"));
		assertThat(ar.get(2), is("A ns3.google.com. TTL=172800 216.239.36.10"));
		assertThat(ar.get(3), is("A ns4.google.com. TTL=172800 216.239.38.10"));

	}

	@Test
	public void 他ドメインの検索_タイプMX() throws Exception {
		//exercise
		PacketDns p = lookup(DnsType.Mx, "sapporoworks.ne.jp", true);

		//verify
		assertThat(print(p), is("QD=1 AN=1 NS=0 AR=1"));
		assertThat(print(p, RrKind.QD, 0), is("Query Mx sapporoworks.ne.jp."));

		assertThat(print(p, RrKind.AN, 0), is("Mx sapporoworks.ne.jp. TTL=3600 10 sapporoworks.ne.jp."));
		assertThat(print(p, RrKind.AR, 0), is("A sapporoworks.ne.jp. TTL=3600 59.106.27.208"));

	}

}
