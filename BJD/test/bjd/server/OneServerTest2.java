package bjd.server;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import junit.framework.Assert;

import org.junit.Test;

import bjd.ILife;
import bjd.Kernel;
import bjd.ValidObjException;
import bjd.ctrl.CtrlType;
import bjd.net.Ip;
import bjd.net.OneBind;
import bjd.net.ProtocolKind;
import bjd.option.Conf;
import bjd.option.Dat;
import bjd.sock.SockObj;
import bjd.sock.SockState;
import bjd.sock.SockTcp;
import bjd.sock.SockUdp;
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
import bjd.util.TestUtil;
import bjd.util.Util;

public class OneServerTest2 implements ILife{
=======
=======
>>>>>>> work
=======
>>>>>>> work
import bjd.test.TestUtil;
import bjd.util.Util;

public class OneServerTest2 implements ILife {
<<<<<<< HEAD
<<<<<<< HEAD
>>>>>>> work
=======
>>>>>>> work
=======
>>>>>>> work
	class EchoServer extends OneServer {
		private ProtocolKind protocolKind;

		public EchoServer(Conf conf, OneBind oneBind) {
			super(new Kernel(), "EchoServer", conf, oneBind);

			protocolKind = oneBind.getProtocol();
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
			if (protocolKind == ProtocolKind.Tcp) {
				tcp((SockTcp) sockObj);
			} else {
				udp((SockUdp) sockObj);
			}
		}

		private void tcp(SockTcp sockTcp) {
<<<<<<< HEAD
<<<<<<< HEAD
			while (isLife() && sockTcp.getSockState() == SockState.CONNECT) {
=======
			while (super.isLife() && sockTcp.getSockState() == SockState.CONNECT) {
>>>>>>> work
=======
			while (super.isLife() && sockTcp.getSockState() == SockState.CONNECT) {
>>>>>>> work
				Util.sleep(0); //これが無いと、別スレッドでlifeをfalseにできない
				int len = sockTcp.length();
				if (0 < len) {
					int timeout = 10;
					byte[] buf = sockTcp.recv(len, timeout, this);
					sockTcp.send(buf);
					break; //echoしたらセッションを閉じる
				}
			}
		}

		private void udp(SockUdp sockUdp) {
			byte[] buf = sockUdp.recv();
			sockUdp.send(buf);
			//echoしたらセッションを閉じる
		}
	}
	

	@Test
<<<<<<< HEAD
<<<<<<< HEAD
	public final void EchoServer_TCP() {
=======
	public final void OneServerを継承したEchoServer_TCP版_を使用して接続する() {
>>>>>>> work
=======
	public final void OneServerを継承したEchoServer_TCP版_を使用して接続する() {
>>>>>>> work

		String addr = "127.0.0.1";
		int port = 9999;
		int timeout = 300;
		Ip ip = null;
		try {
			ip = new Ip(addr);
		} catch (ValidObjException ex) {
			Assert.fail(ex.getMessage());
		}
		OneBind oneBind = new OneBind(ip, ProtocolKind.Tcp);
		Conf conf = TestUtil.createConf("OptionSample");
		conf.set("port", port);
		conf.set("multiple", 10);
		conf.set("acl", new Dat(new CtrlType[0]));
		conf.set("enableAcl", 1);
		conf.set("timeOut", timeout);

		EchoServer echoServer = new EchoServer(conf, oneBind);
		echoServer.start();

		//TCPクライアント

		int max = 10000;
		byte[] buf = new byte[max];
		buf[8] = 100; //CheckData
		for (int i = 0; i < 3; i++) {
			SockTcp sockTcp = new SockTcp(ip, port, timeout, null);
			TestUtil.prompt(String.format("[%d] sockTcp = new SockTcp(%s,%d)", i, addr, port));

			int len = sockTcp.send(buf);
			TestUtil.prompt(String.format("sockTcp.send(%dbyte)", len));

			while (sockTcp.length() == 0) {
				Util.sleep(100);
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
				TestUtil.dispPrompt("Thread.sleep(100)");
=======
				TestUtil.prompt("Thread.sleep(100)");
>>>>>>> work
=======
				TestUtil.prompt("Thread.sleep(100)");
>>>>>>> work
=======
				TestUtil.prompt("Thread.sleep(100)");
>>>>>>> work
			}

			len = sockTcp.length();
			if (0 < len) {
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
				byte[] b = sockTcp.recv(len, timeout,this);
				TestUtil.dispPrompt(this, String.format("sockTcp.recv()=%dbyte", b.length));
=======
				byte[] b = sockTcp.recv(len, timeout, this);
				TestUtil.prompt(String.format("sockTcp.recv()=%dbyte", b.length));
>>>>>>> work
=======
				byte[] b = sockTcp.recv(len, timeout, this);
				TestUtil.prompt(String.format("sockTcp.recv()=%dbyte", b.length));
>>>>>>> work
=======
				byte[] b = sockTcp.recv(len, timeout, this);
				TestUtil.prompt(String.format("sockTcp.recv()=%dbyte", b.length));
>>>>>>> work
				assertThat(b[8], is(buf[8])); //CheckData
			}
			assertThat(max, is(len));

			sockTcp.close();

		}

		echoServer.dispose();

	}

	@Test
<<<<<<< HEAD
<<<<<<< HEAD
	public final void EchoServer_UDP() {
=======
	public final void OneServerを継承したEchoServer_UDP版_を使用して接続する() {
>>>>>>> work
=======
	public final void OneServerを継承したEchoServer_UDP版_を使用して接続する() {
>>>>>>> work

		String addr = "127.0.0.1";
		int port = 9991;
		int timeout = 300;
		Ip ip = null;
		try {
			ip = new Ip(addr);
		} catch (ValidObjException ex) {
			Assert.fail(ex.getMessage());
		}
		OneBind oneBind = new OneBind(ip, ProtocolKind.Udp);
		Conf conf = TestUtil.createConf("OptionSample");
		conf.set("port", port);
		conf.set("multiple", 10);
		conf.set("acl", new Dat(new CtrlType[0]));
		conf.set("enableAcl", 1);
		conf.set("timeOut", timeout);

		EchoServer echoServer = new EchoServer(conf, oneBind);
		echoServer.start();

		//TCPクライアント

		int max = 1600;
		byte[] buf = new byte[max];
		buf[8] = 100; //CheckData

		for (int i = 0; i < 3; i++) {
			SockUdp sockUdp = new SockUdp(ip, port, timeout, null, buf);
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
			TestUtil.dispPrompt(this,
=======
			TestUtil.prompt(
>>>>>>> work
=======
			TestUtil.prompt(
>>>>>>> work
=======
			TestUtil.prompt(
>>>>>>> work
					String.format("[%d] sockUdp = new SockUdp(%s,%d,%dbytes)", i, addr, port, buf.length));

			while (sockUdp.length() == 0) {
				Util.sleep(100);
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
				TestUtil.dispPrompt("Thread.sleep(100)");
=======
				TestUtil.prompt("Thread.sleep(100)");
>>>>>>> work
=======
				TestUtil.prompt("Thread.sleep(100)");
>>>>>>> work
=======
				TestUtil.prompt("Thread.sleep(100)");
>>>>>>> work
			}

			byte[] b = sockUdp.recv();
			TestUtil.prompt(String.format("sockUdp.recv()=%dbyte", b.length));
			assertThat(b[8], is(buf[8])); //CheckData
			assertThat(max, is(b.length));

			sockUdp.close();
		}

		echoServer.dispose();

	}

	@Override
<<<<<<< HEAD
<<<<<<< HEAD
	public boolean isLife() {
=======
	public final boolean isLife() {
>>>>>>> work
=======
	public final boolean isLife() {
>>>>>>> work
		return true;
	}

}
