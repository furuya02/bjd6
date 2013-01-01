package bjd.sock;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Test;

import bjd.ILife;
import bjd.ThreadBase;
import bjd.ValidObjException;
import bjd.net.Ip;
import bjd.net.ProtocolKind;
import bjd.net.Ssl;
import bjd.test.TestUtil;
import bjd.util.Util;

public final class SockTcpTest implements ILife {
	/**
	 * テスト用のEchoサーバ
	 * @author SIN
	 *
	 */
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
				while (super.isLife()) {
					final SockTcp child = (SockTcp) sockServer.select(this);
					if (child == null) {
						break;
					}
					while (super.isLife() && child.getSockState() == SockState.CONNECT) {
						int len = child.length();
						if (len > 0) {
							byte[] buf = child.recv(len, 100, this);
							child.send(buf);
						}
					}
				}
			}
		}
	}

	@Test
	public void Echoサーバに送信して溜まったデータサイズ_lengthを確認する() throws Exception {
		//setUp
		String addr = "127.0.0.1";
		int port = 9982;
		EchoServer sv = new EchoServer(addr, port);
		sv.start();

		SockTcp sut = new SockTcp(new Ip(addr), port, 100, null);
		int max = 1000;
		for (int i = 0; i < 3; i++) {
			sut.send(new byte[max]);
		}
		Util.sleep(200);

		int expected = max * 3;

		//exercise
		int actual = sut.length();

		//verify
		assertThat(actual, is(expected));

		//tearDown
		sut.close();
		sv.stop();
	}

	@Test
	public void Echoサーバにsendで送信てtcpQueueのlength分ずつRecvする() {
		String addr = "127.0.0.1";
		int port = 9981;

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
		TestUtil.prompt("tcpObj = new TcpObj()");

		int max = 1000;
		int loop = 3;
		byte[] tmp = new byte[max];
		for (int i = 0; i < max; i++) {
			tmp[i] = (byte) i;
		}

		int recvCount = 0;
		for (int i = 0; i < loop; i++) {
			TestUtil.prompt(String.format("tcpObj.send(%dbyte)", tmp.length));
			int len = sockTcp.send(tmp);
			Assert.assertEquals(len, tmp.length);

			Util.sleep(10);

			byte[] b = sockTcp.recv(len, timeout, this);
			recvCount += b.length;
			TestUtil.prompt(String.format("len=%d  recv()=%d", len, b.length));
			for (int m = 0; m < max; m += 10) {
				Assert.assertEquals(b[m], tmp[m]); //送信したデータと受信したデータが同一かどうかのテスト
			}
		}
		TestUtil.prompt(String.format("loop*max=%dbyte  recvCount:%d", loop * max, recvCount));
		Assert.assertEquals(loop * max, recvCount); //送信したデータ数と受信したデータ数が一致するかどうかのテスト

		TestUtil.prompt(String.format("tcpObj.close()"));
		sockTcp.close();
		echoServer.stop();
	}

	@Test
	public void EchoサーバにstringSendで1行送信してstringRecvで1行受信する() throws Exception {
		//setUp
		String addr = "127.0.0.1";
		int port = 9993;

		EchoServer sv = new EchoServer(addr, port);
		sv.start();
		SockTcp sut = new SockTcp(new Ip(addr), port, 100, null);
		sut.stringSend("本日は晴天なり", "UTF-8");
		Util.sleep(10);

		String expected = "本日は晴天なり\r\n";

		//exercise
		String actual = sut.stringRecv("UTF-8", 1, this);

		//verify
		assertThat(actual, is(expected));

		//tearDown
		sut.close();
		sv.stop();
	}

	@Test
	public void EchoサーバにlineSendで1行送信してlineRecvで1行受信する() throws Exception {
		//setUp
		String addr = "127.0.0.1";
		int port = 9993;

		EchoServer sv = new EchoServer(addr, port);
		sv.start();
		SockTcp sut = new SockTcp(new Ip(addr), port, 100, null);
		sut.lineSend("本日は晴天なり".getBytes("UTF-8"));
		Util.sleep(10);

		String expected = "本日は晴天なり\r\n";

		//exercise
		byte[] bytes = sut.lineRecv(1, this);
		String actual = new String(bytes, "UTF-8");

		//verify
		assertThat(actual, is(expected));

		//tearDown
		sut.close();
		sv.stop();
	}

	@Override
	public boolean isLife() {
		return true;
	}
}
