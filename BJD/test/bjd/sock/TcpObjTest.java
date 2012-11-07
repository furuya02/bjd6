package bjd.sock;

import junit.framework.Assert;

import org.junit.Test;

import bjd.ThreadBase;
import bjd.ValidObjException;
import bjd.net.Ip;
import bjd.net.ProtocolKind;
import bjd.net.Ssl;
import bjd.util.TestUtil;

//**************************************************
// Echoサーバを使用したテスト
//**************************************************
public final class TcpObjTest {
	class EchoServer extends ThreadBase {
		private SockServer sockServer;
		private String addr;
		private int port;

		public EchoServer(String addr, int port) {
			super(null);
			sockServer = new SockServer(ProtocolKind.Tcp);
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
			if (sockServer.bind(ip, port, 1)) {
				//System.out.println(String.format("EchoServer bind"));
				while (isLife()) {
					final SockTcp child = (SockTcp) sockServer.select(this);
					if (child == null) {
						break;
					}
					//System.out.println(String.format("EchoServer child"));
					while (isLife() && child.getSockState() == SockState.CONNECT) {
						int len = child.length();
						if (len > 0) {
							//System.out.println(String.format("EchoServer len=%d", len));
							byte[] buf = child.recv(len, 100);
							child.send(buf);
						}
					}
				}
			}
		}
	}

	@Test
	public void a001() {

		TestUtil.dispHeader("a001 Echoサーバに送信して、たまったデータサイズ（length）を確認する");

		String addr = "127.0.0.1";
		int port = 9999;

		EchoServer echoServer = new EchoServer(addr, port);
		echoServer.start();

		int timeout = 100;
		Ssl ssl = null;
		Ip ip = null;
		try {
			ip = new Ip(addr);
		} catch (ValidObjException ex) {
			Assert.fail(ex.getMessage());
		}
		SockTcp sockTcp = new SockTcp(ip, port, timeout, ssl);
		TestUtil.dispPrompt(this, "tcpObj = new TcpObj()");

		int max = 1000;
		byte[] tmp = new byte[max];

		for (int i = 0; i < 3; i++) {
			sockTcp.send(tmp);
			TestUtil.dispPrompt(this, String.format("tcpObj.send(%dbyte)", tmp.length));

			//送信データが到着するまで、少し待機する
			int sleep = 100; //あまり短いと、Testを全部一緒にまわしたときにエラーとなる
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			TestUtil.dispPrompt(this, String.format("Thread.sleep(%d)", sleep));

			TestUtil.dispPrompt(this, String.format("tcpObj.length()=%d", sockTcp.length()));
			Assert.assertEquals((i + 1) * max, sockTcp.length());
		}
		TestUtil.dispPrompt(this, String.format("tcpObj.close()"));
		sockTcp.close();
		echoServer.stop();
	}

	@Test
	public void a002() {

		TestUtil.dispHeader("a002 Echoサーバにsend(送信)して、tcpQueueのlength分ずつRecv()する");

		String addr = "127.0.0.1";
		int port = 9992;

		EchoServer echoServer = new EchoServer(addr, port);
		echoServer.start();

		int timeout = 100;
		Ssl ssl = null;
		Ip ip = null;
		try {
			ip = new Ip(addr);
		} catch (ValidObjException ex) {
			Assert.fail(ex.getMessage());
		}
		SockTcp sockTcp = new SockTcp(ip, port, timeout, ssl);
		TestUtil.dispPrompt(this, "tcpObj = new TcpObj()");

		int max = 1000;
		int loop = 3;
		byte[] tmp = new byte[max];
		for (int i = 0; i < max; i++) {
			tmp[i] = (byte) i;
		}

		int recvCount = 0;
		for (int i = 0; i < loop; i++) {
			TestUtil.dispPrompt(this, String.format("tcpObj.send(%dbyte)", tmp.length));
			int len = sockTcp.send(tmp);
			Assert.assertEquals(len, tmp.length);

			int sleep = 100;
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			TestUtil.dispPrompt(this, String.format("Thread.sleep(%d)", sleep));
		
			
			byte[] b = sockTcp.recv(len, timeout);
			recvCount += b.length;
			TestUtil.dispPrompt(this, String.format("len=%d  recv()=%d", len, b.length));
			for (int m = 0; m < max; m += 10) {
				Assert.assertEquals(b[m], tmp[m]); //送信したデータと受信したデータが同一かどうかのテスト
			}
		}
		TestUtil.dispPrompt(this, String.format("loop*max=%dbyte  recvCount:%d", loop * max, recvCount));
		Assert.assertEquals(loop * max, recvCount); //送信したデータ数と受信したデータ数が一致するかどうかのテスト

		TestUtil.dispPrompt(this, String.format("tcpObj.close()"));
		sockTcp.close();
		echoServer.stop();
	}
}
