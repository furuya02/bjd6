package bjd.sock;

import junit.framework.Assert;

import org.junit.Test;

import bjd.ThreadBase;
import bjd.ValidObjException;
import bjd.net.Ip;
import bjd.net.ProtocolKind;
import bjd.net.Ssl;
import bjd.util.TestUtil;
import bjd.util.Util;

//**************************************************
// Echoサーバを使用したテスト
//**************************************************
public final class UdpObjTest {
	class EchoServer extends ThreadBase {
		private SockServer sockServer;
		private String addr;
		private int port;

		public EchoServer(String addr, int port) {
			super(null);
			sockServer = new SockServer(ProtocolKind.Udp);
			this.addr = addr;
			this.port = port;
		}

		@Override
		public String getMsg(int no) {
			return null;
		}

		@Override
		protected boolean onStartThread() {
			return true;
		}

		@Override
		protected void onStopThread() {
			sockServer.close();
		}

		@Override
		protected void onRunThread() {
			Ip ip = null;
			try {
				ip = new Ip(addr);
			} catch (ValidObjException ex) {
				Assert.fail(ex.getMessage());
			}
			if (sockServer.bind(ip, port)) {
				System.out.println(String.format("EchoServer bind"));
				while (isLife()) {
					final SockUdp child = (SockUdp) sockServer.select(this);
					if (child == null) {
						break;
					}
					System.out.println(String.format("EchoServer child"));
					while (isLife() && child.getSockState() == SockState.CONNECT) {
						int len = child.length();
						if (len > 0) {
							System.out.println(String.format("EchoServer len=%d", len));
							byte[] buf = child.recv();
							child.send(buf);
							//送信が完了したら、この処理は終了
							break;
						}
					}
				}
			}
		}
	}
/*
	class EchoServer extends OneServer {

		public EchoServer(Conf conf, OneBind oneBind) {
			super(new Kernel(),"NAME",conf,oneBind);
		}

		@Override
		public String getMsg(int messageNo) {
			return null;
		}

		@Override
		protected void onStopServer() {
		}

		@Override
		protected boolean onStartServer() {
			return true;
		}

		@Override
		protected void onSubThread(SockObj sockObj) {
			SockUdp sockUdp = (SockUdp) sockObj;
			System.out.println(String.format("onSubThread"));

			byte[] buf = sockUdp.recv();
			sockUdp.send(buf);
		}
	}
*/

	/*
	@Test
	public void a001() {

		TestUtil.dispHeader("a001 Echoサーバに送信して、たまったデータサイズ（length）を確認する");

		String addr = "127.0.0.1";
		int port = 53; //TOCO DEBUG 9999^>53

		OneBind oneBind = new OneBind(new Ip(addr), ProtocolKind.Udp);
		OptionSample optionSample = new OptionSample(new Kernel(), "", "Sample");
		Conf conf = new Conf(optionSample);
		conf.set("port", port);
		conf.set("multiple", 10);
		conf.set("acl", new Dat(new CtrlType[0]));
		conf.set("enableAcl", 1);
		conf.set("timeOut", 3);
		
		EchoServer echoServer = new EchoServer(addr, port);
		//EchoServer echoServer = new EchoServer(conf,oneBind);
		echoServer.start();
		
		Util.sleep(1000);
		
		int max = 1000;
		byte[] tmp = new byte[max];

		int timeout = 100;
		Ssl ssl = null;
		SockUdp sock = new SockUdp(new Ip(addr), port, timeout, ssl, tmp);
		TestUtil.dispPrompt(this, "sock = new SockUdp()");

		for (int i = 0; i < 10; i++) {
			sock.send(tmp);
			TestUtil.dispPrompt(this, String.format("sock.send(%dbyte)", tmp.length));

			//送信データが到着するまで、少し待機する
			int sleep = 100; //あまり短いと、Testを全部一緒にまわしたときにエラーとなる
			Util.sleep(sleep);
			TestUtil.dispPrompt(this, String.format("Thread.sleep(%d)", sleep));

			TestUtil.dispPrompt(this, String.format("sock.length()=%d", sock.length()));
			Assert.assertEquals((i + 1) * max, sock.length());
		}
		TestUtil.dispPrompt(this, String.format("sock.close()"));
		sock.close();
		echoServer.stop();
	}
	*/
	@Test
	public void a002() {

		TestUtil.dispHeader("a002 Echoサーバにsend(送信)して、length分ずつRecv()する");

		String addr = "127.0.0.1";
		int port = 53;

//		OneBind oneBind = new OneBind(new Ip(addr), ProtocolKind.Udp);
//		OptionSample optionSample = new OptionSample(new Kernel(), "", "Sample");
//		Conf conf = new Conf(optionSample);
//		conf.set("port", port);
//		conf.set("multiple", 10);
//		conf.set("acl", new Dat(new CtrlType[0]));
//		conf.set("enableAcl", 1);
//		conf.set("timeOut", 3);
//		EchoServer echoServer = new EchoServer(conf, oneBind);
		EchoServer echoServer = new EchoServer(addr, port);
		echoServer.start();

		int timeout = 100;
		Ssl ssl = null;

		int max = 1500;
		int loop = 10;
		byte[] tmp = new byte[max];
		for (int i = 0; i < max; i++) {
			tmp[i] = (byte) i;
		}

		Ip ip = null;
		try {
			ip = new Ip(addr);
		} catch (ValidObjException ex) {
			Assert.fail(ex.getMessage());
		}		
		for (int i = 0; i < loop; i++) {
			SockUdp sockUdp = new SockUdp(ip, port, timeout, ssl, tmp);
			TestUtil.dispPrompt(this, String.format("sockUdp = new SockUdp(%dbyte)", tmp.length));
			int len = 0;
			while (len == 0) {
				len = sockUdp.length();
				Util.sleep(0);
			}
			TestUtil.dispPrompt(this, String.format("len=%d", len));
			byte[] b = sockUdp.recv();
			TestUtil.dispPrompt(this, String.format("b=recv()  b.length=%d", b.length));
			for (int m = 0; m < max; m += 10) {
				Assert.assertEquals(b[m], tmp[m]); //送信したデータと受信したデータが同一かどうかのテスト
			}
			sockUdp.close();
		}
		echoServer.stop();
	}

}
