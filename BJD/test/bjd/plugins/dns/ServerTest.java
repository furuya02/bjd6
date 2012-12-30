package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

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

public class ServerTest {

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

	@Before
	public void setUp() {
		//クライアント起動
		//cl = Inet.connect(new Ip(IpKind.V4_LOCALHOST), 21, 10, null, this);
		//クライアントの接続が完了するまで、少し時間がかかる
		//Util.sleep(10);

	}

	@After
	public void tearDown() {
		//クライアント停止
		//cl.close();
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
	public void タイプA_localhostの検索() throws Exception {
		//Setup
		PacketDns sp = new PacketDns((short) 0x0001, false, false, false, false);
		sp.addRR(RrKind.QD, new RrQuery("localhost", DnsType.A));

		//exercise
		SockUdp cl = new SockUdp(new Ip(IpKind.V4_LOCALHOST), 53, 100, null, sp.getBytes());
		PacketDns rp = new PacketDns(cl.recv(3000));
		RrA o = (RrA)rp.getRR(RrKind.AN,0);
		//verify
		assertThat(o.getName(),is("localhost."));
		assertThat(o.getIp().toString(),is("127.0.0.1"));
	}
	
	@Test
	public void タイプA_www_aaa_comの検索() throws Exception {
		//Setup
		PacketDns sp = new PacketDns((short) 0x0002, false, false, false, false);
		sp.addRR(RrKind.QD, new RrQuery("www.aaa.com", DnsType.A));

		//exercise
		SockUdp cl = new SockUdp(new Ip(IpKind.V4_LOCALHOST), 53, 100, null, sp.getBytes());
		PacketDns rp = new PacketDns(cl.recv(3000));
		RrA o = (RrA)rp.getRR(RrKind.AN,0);
		//verify
		assertThat(o.getName(),is("www.aaa.com."));
		assertThat(o.getIp().toString(),is("192.168.0.101"));
	}

}
