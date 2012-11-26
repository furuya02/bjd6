package bjd.server;

import org.junit.Test;

import bjd.Kernel;
import bjd.ctrl.CtrlType;
import bjd.net.Ip;
import bjd.net.IpKind;
import bjd.net.OneBind;
import bjd.net.ProtocolKind;
import bjd.option.Conf;
import bjd.option.Dat;
import bjd.sock.SockObj;
import bjd.sock.SockState;
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
import bjd.util.TestUtil;
=======
import bjd.test.TestUtil;
>>>>>>> work
=======
import bjd.test.TestUtil;
>>>>>>> work
=======
import bjd.test.TestUtil;
>>>>>>> work
import bjd.util.Util;

public final class ServerTest {

	//サーバ動作確認用
	class MyServer extends OneServer {
		public MyServer(Conf conf, OneBind oneBind) {
			super(new Kernel(), "TEST-SERVER", conf, oneBind);
		}

		@Override
		protected boolean onStartServer() {
			return true;
		}

		@Override
		protected void onStopServer() {
		}

		@Override
		public String getMsg(int messageNo) {
			return "";
		}

<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
//		@Override
//		protected void onSubThread(SockAccept sockAccept) {
//			for (int i = 3; i >= 0 && isLife(); i--) {
//				if (sockAccept.getSockState() != SockState.CONNECT) {
//					TestUtil.dispPrompt(this, String.format("接続中...sockAccept.getSockState!=Connect"));
//					break;
//				}
//
//				TestUtil.dispPrompt(this, String.format("接続中...あと%d回待機", i));
//				Util.sleep(1000);
//			}
//		}
=======
=======
>>>>>>> work
=======
>>>>>>> work
		//		@Override
		//		protected void onSubThread(SockAccept sockAccept) {
		//			for (int i = 3; i >= 0 && isLife(); i--) {
		//				if (sockAccept.getSockState() != SockState.CONNECT) {
		//					TestUtil.prompt( String.format("接続中...sockAccept.getSockState!=Connect"));
		//					break;
		//				}
		//
		//				TestUtil.prompt( String.format("接続中...あと%d回待機", i));
		//				Util.sleep(1000);
		//			}
		//		}
<<<<<<< HEAD
<<<<<<< HEAD
>>>>>>> work
=======
>>>>>>> work
=======
>>>>>>> work

		//		@Override
		//		public void read(DatagramChannel channel, SockUdpServer sockUdpServer) {
		//			// TODO 自動生成されたメソッド・スタブ
		//			
		//		}

		@Override
		protected void onSubThread(SockObj sockObj) {
			for (int i = 3; i >= 0 && isLife(); i--) {
				if (sockObj.getSockState() != SockState.CONNECT) {
					TestUtil.prompt(String.format("接続中...sockAccept.getSockState!=Connect"));
					break;
				}

<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
				TestUtil.dispPrompt(this, String.format("接続中...あと%d回待機", i));
=======
				TestUtil.prompt(String.format("接続中...あと%d回待機", i));
>>>>>>> work
=======
				TestUtil.prompt(String.format("接続中...あと%d回待機", i));
>>>>>>> work
=======
				TestUtil.prompt(String.format("接続中...あと%d回待機", i));
>>>>>>> work
				Util.sleep(1000);
			}
		}

	}

	@Test
	public void a001() {
		Ip ip = new Ip(IpKind.V4_LOCALHOST);
		OneBind oneBind = new OneBind(ip, ProtocolKind.Tcp);
		Conf conf = TestUtil.createConf("OptionSample");
		conf.set("protocolKind", ProtocolKind.Tcp.getIntValue());
		conf.set("port", 8888);
		conf.set("multiple", 10);
		conf.set("acl", new Dat(new CtrlType[0]));
		conf.set("enableAcl", 1);
		conf.set("timeOut", 3);

		MyServer myServer = new MyServer(conf, oneBind);
		myServer.start();
		for (int i = 10; i > 0; i--) {
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
			TestUtil.dispPrompt(this, String.format("test() loop..あと%d回 isRunning()=%s Count()=%d", i, myServer.isRunnig(), myServer.count()));
            Util.sleep(1);
=======
			TestUtil.prompt(String.format("test() loop..あと%d回 isRunning()=%s Count()=%d", i, myServer.isRunnig(), myServer.count()));
			Util.sleep(1);
>>>>>>> work
=======
			TestUtil.prompt(String.format("test() loop..あと%d回 isRunning()=%s Count()=%d", i, myServer.isRunnig(), myServer.count()));
			Util.sleep(1);
>>>>>>> work
=======
			TestUtil.prompt(String.format("test() loop..あと%d回 isRunning()=%s Count()=%d", i, myServer.isRunnig(), myServer.count()));
			Util.sleep(1);
>>>>>>> work
		}
		myServer.dispose();
	}

}
