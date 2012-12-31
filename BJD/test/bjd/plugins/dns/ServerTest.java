package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import bjd.Kernel;
import bjd.net.Ip;
import bjd.net.IpKind;
import bjd.net.OneBind;
import bjd.net.ProtocolKind;
import bjd.option.Conf;
import bjd.sock.SockTcp;
import bjd.sock.SockUdp;
import bjd.test.TmpOption;
import bjd.util.Inet;
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

		//設定ファイルの退避と上書き
		op = new TmpOption("c:\\dev\\bjd6\\DnsServer\\test\\DnsServerTest.ini");
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

	/**
	 * 共通メソッド<br>
	 * リクエスト送信
	 * @throws IOException 
	 * @throws Exception
	 */
	private PacketDns lookup(DnsType dnsType, String name) throws IOException {
		//乱数で識別子生成
		short id = (short) (new Random()).nextInt(100);
		//送信パケット生成
		PacketDns sp = new PacketDns(id, false, false, false, false);
		//質問フィールド追加
		sp.addRR(RrKind.QD, new RrQuery(name, dnsType));
		//クライアントソケット生成、及び送信
		SockUdp cl = new SockUdp(new Ip(IpKind.V4_LOCALHOST), 53, 100, null, sp.getBytes());
		//受信
		byte[] recvBuf = cl.recv(1000);
		//Debug Print
		System.out.println(String.format("lookup(%s,\"%s\") recv().length=%d", dnsType, name, recvBuf.length));
		//デコード
		PacketDns p = new PacketDns(recvBuf);
		//Debug Print
		System.out.println(print(p));
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
	public void localhost_V4の検索_タイプPTR() throws Exception {
		//exercise
		PacketDns p = lookup(DnsType.Ptr, "1.0.0.127.in-addr.arpa.");
		//verify
		assertThat(print(p), is("QD=1 AN=1 NS=1 AR=0")); 
		assertThat(print(p, RrKind.QD, 0), is("Query Ptr 1.0.0.127.in-addr.arpa."));
		assertThat(print(p, RrKind.AN, 0), is("Ptr 1.0.0.127.in-addr.arpa. TTL=0 localhost."));
		//assertThat(print(p, RrKind.NS, 0), is("Ns localhost. TTL=0 localhost."));

	}

	@Test
	public void localhost_V6の検索_タイプPTR() throws Exception {
		//exercise
		PacketDns p = lookup(DnsType.Ptr, "1.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.ip6.arpa");
		//verify
		assertThat(print(p, RrKind.QD, 0), is("Query Ptr 1.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.ip6.arpa."));
		assertThat(print(p, RrKind.AN, 0), is("Ptr 1.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.ip6.arpa. TTL=0 localhost."));
		//assertThat(print(p, RrKind.NS, 0), is("Ns localhost. TTL=0 localhost."));
	}

	//public void localhostの検索_タイプNS() throws Exception {
	//public void localhostの検索_タイプMX() throws Exception {
	//public void localhostの検索_タイプSOA() throws Exception {

	//	@Test
	//	public void 自ドメインの検索_タイプA_www_aaa_com() throws Exception {
	//		//exercise
	//		PacketDns rp = lookup(DnsType.A, "www.aaa.com");
	//		RrA o = (RrA) rp.getRR(RrKind.AN, 0);
	//
	//		//verify
	//		assertThat(o.getName(), is("www.aaa.com."));
	//		assertThat(o.getIp().toString(), is("192.168.0.10"));
	//	}

}
