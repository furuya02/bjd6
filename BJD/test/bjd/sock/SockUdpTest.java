package bjd.sock;

import junit.framework.Assert;

import org.junit.Test;

import bjd.ThreadBase;
import bjd.ValidObjException;
import bjd.net.Ip;
import bjd.net.ProtocolKind;
import bjd.net.Ssl;
import bjd.test.TestUtil;
import bjd.util.Util;

//**************************************************
// Echoサーバを使用したテスト
//**************************************************
public final class SockUdpTest {
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
				while (isLife()) {
					final SockUdp child = (SockUdp) sockServer.select(this);
					if (child == null) {
						break;
					}
					while (isLife() && child.getSockState() == SockState.CONNECT) {
						int len = child.length();
						if (len > 0) {
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



	@Test
	public void Echoサーバにsendしてlength分ずつRecvする() throws Exception {
		//setUp
		String addr = "127.0.0.1";
		int port = 53;
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

		Ip ip = ip = new Ip(addr);
		for (int i = 0; i < loop; i++) {
			SockUdp sockUdp = new SockUdp(ip, port, timeout, ssl, tmp);
			int len = 0;
			while (len == 0) {
				len = sockUdp.length();
				Util.sleep(0);
			}
			byte[] b = sockUdp.recv();
			
			//verify
			for (int m = 0; m < max; m += 10) {
				Assert.assertEquals(b[m], tmp[m]); //送信したデータと受信したデータが同一かどうかのテスト
			}
			sockUdp.close();
		}
		
		//TearDown
		echoServer.stop();
	}

}
