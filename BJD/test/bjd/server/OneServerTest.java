package bjd.server;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Test;

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
import bjd.util.TestUtil;
import bjd.util.Util;

public class OneServerTest {

	class MyServer extends OneServer {
		public MyServer(Conf conf, OneBind oneBind) {
			super(new Kernel(), "TEST-SERVER", conf, oneBind);
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
			while (isLife()) {
				Util.sleep(0); //これが無いと、別スレッドでlifeをfalseにできない

				if (sockObj.getSockState() != SockState.CONNECT) {
					System.out.println(">>>>>sockAccept.getSockState()!=SockState.CONNECT");
					break;
				}
			}
		}
	}

	class MyClient {
		private Socket s = null;
		private String addr;
		private int port;
		private Thread t;
		private boolean life;

		public MyClient(String addr, int port) {
			this.addr = addr;
			this.port = port;
		}

		public void connet() {
			life = true;
			t = new Thread(new Runnable() {
				@Override
				public void run() {

					try {
						s = new Socket(addr, port);
					} catch (IOException e) {
						e.printStackTrace();
					}
					while (life) {
						Util.sleep(100);
					}
				}
			});
			t.start();
			//接続完了まで少し時間が必要
			Util.sleep(100);
		}

		public void dispose() {
			try {
				s.shutdownInput();
				s.shutdownOutput();
				s.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			life = false;

			while (t.isAlive()) {
				Util.sleep(0);
			}
		}
	}

	
	@Test
	public final void a001() {

		TestUtil.dispHeader("a001 start() stop()　の繰り返し(負荷テスト)"); //TESTヘッダ
		Ip ip = null;
		try {
			ip = new Ip("127.0.0.1");
		} catch (ValidObjException ex) {
			Assert.fail(ex.getMessage());
		}
		OneBind oneBind = new OneBind(ip, ProtocolKind.Tcp);
		Conf conf = TestUtil.createConf("OptionSample");
		conf.set("port", 9990);
		conf.set("multiple", 10);
		conf.set("acl", new Dat(new CtrlType[0]));
		conf.set("enableAcl", 1);
		conf.set("timeOut", 3);

		TestUtil.dispPrompt(this, String.format("new MyServer()"));
		MyServer myServer = new MyServer(conf, oneBind);

		for (int i = 0; i < 2; i++) {
			TestUtil.dispPrompt(this, String.format("[i=%d]", i));
			myServer.start();

			//			Util.sleep(500);

			TestUtil.dispPrompt(this, String.format("●sockState=%s", myServer.getSockState()));
			assertThat(myServer.isRunnig(), is(true));
			assertThat(myServer.getSockState(), is(SockState.Bind));

			myServer.stop();
			TestUtil.dispPrompt(this, String.format("●sockState=%s", myServer.getSockState()));
			assertThat(myServer.isRunnig(), is(false));
			assertThat(myServer.getSockState(), is(SockState.Error));

		}

		TestUtil.dispPrompt(this, String.format("myServer.despose()"));
		myServer.dispose();
	}

	@Test
	public final void a002() {

		TestUtil.dispHeader("a002 new start() stop()　dispose の繰り返し(負荷テスト)"); //TESTヘッダ
		Ip ip = null;
		try {
			ip = new Ip("127.0.0.1");
		} catch (ValidObjException ex) {
			Assert.fail(ex.getMessage());
		}
		OneBind oneBind = new OneBind(ip, ProtocolKind.Tcp);
		Conf conf = TestUtil.createConf("OptionSample");
		conf.set("port", 80);
		conf.set("multiple", 10);
		conf.set("acl", new Dat(new CtrlType[0]));
		conf.set("enableAcl", 1);
		conf.set("timeOut", 3);

		for (int i = 0; i < 3; i++) {
			TestUtil.dispPrompt(this, String.format("[i=%d]", i));
			TestUtil.dispPrompt(this, String.format("new MyServer()"));
			MyServer myServer = new MyServer(conf, oneBind);

			myServer.start();
			TestUtil.dispPrompt(this, String.format("myServer.start() => ●sockState=%s", myServer.getSockState()));
			assertThat(myServer.isRunnig(), is(true));
			assertThat(myServer.getSockState(), is(SockState.Bind));

			myServer.stop();
			TestUtil.dispPrompt(this, String.format("myServer.stop() => ●sockState=%s", myServer.getSockState()));
			assertThat(myServer.isRunnig(), is(false));
			assertThat(myServer.getSockState(), is(SockState.Error));

			TestUtil.dispPrompt(this, String.format("myServer.despose()"));
			myServer.dispose();
		}
	}

	@Test
	public final void a003() {

		TestUtil.dispHeader("a003 count() multipleを超えたリクエストは破棄される"); //TESTヘッダ
		int multiple = 2;
		final int port = 8889;
		final String address = "127.0.0.1";
		Ip ip = null;
		try {
			ip = new Ip(address);
		} catch (ValidObjException ex) {
			Assert.fail(ex.getMessage());
		}
		OneBind oneBind = new OneBind(ip, ProtocolKind.Tcp);
		Conf conf = TestUtil.createConf("OptionSample");
		conf.set("port", port);
		conf.set("multiple", multiple);
		conf.set("acl", new Dat(new CtrlType[0]));
		conf.set("enableAcl", 1);
		conf.set("timeOut", 3);

		TestUtil.dispPrompt(this, String.format("s = new OneServer() %s:%d multiple=%d", address, port, multiple));
		MyServer myServer = new MyServer(conf, oneBind);
		myServer.start();

		ArrayList<MyClient> ar = new ArrayList<>();

		for (int i = 0; i < 4; i++) {
			System.out.println(String.format("[%d] client.connet()", i));
			MyClient myClient = new MyClient(address, port);
			myClient.connet();
			ar.add(myClient);
		}

		System.out.println(String.format("s.count()=%d multiple以上は接続できない", multiple));
		assertThat(myServer.count(), is(multiple));

		myServer.stop();
		myServer.dispose();

		for (MyClient c : ar) {
			c.dispose();
		}
	}
}
