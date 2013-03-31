package bjd.sock;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.net.InetSocketAddress;

import junit.framework.Assert;

import org.junit.Test;

import bjd.Kernel;
import bjd.net.Ip;
import bjd.net.IpKind;
import bjd.net.ProtocolKind;
import bjd.util.Util;

public final class SockServerTest {

	@Test
	public void test() {
		Execute execute = new Execute();
		execute.startStop("a001 TCPサーバの 起動・停止時のSockState()の確認", ProtocolKind.Tcp);
		execute.startStop("a002 UDPサーバの 起動・停止時のSockState()の確認", ProtocolKind.Udp);
		execute.getLocalAddress("a003 TCPサーバのgetLocalAddress()の確認", ProtocolKind.Tcp);
		execute.getLocalAddress("a004 UDPサーバのgetLocalAddress()の確認", ProtocolKind.Udp);
	}

	class Execute {
		public void startStop(String title, final ProtocolKind protocolKind) {


			Ip ip = new Ip(IpKind.V4_LOCALHOST);
			final Ip bindIp = ip;
			final int port = 8881;
			final int listenMax = 10;

			final SockServer sockServer = new SockServer(new Kernel(), protocolKind);

			assertThat(sockServer.getSockState(), is(SockState.IDLE));
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					if (protocolKind == ProtocolKind.Tcp) {
						sockServer.bind(bindIp, port, listenMax);
					} else {
						sockServer.bind(bindIp, port);
					}
				}
			});
			t.start();

			while (sockServer.getSockState() == SockState.IDLE) {
				Util.sleep(100);
			}
			assertThat(sockServer.getSockState(), is(SockState.Bind));

			sockServer.close(); //bind()にThreadBaseのポインタを送っていないため、isLifeでブレイクできないので、selectで例外を発生させて終了する

			assertThat(sockServer.getSockState(), is(SockState.Error));
		}

		public void getLocalAddress(String title, final ProtocolKind protocolKind) {

			final Ip bindIp = new Ip(IpKind.V4_LOCALHOST);
			final int port = 9991;
			final int listenMax = 10;

			final SockServer sockServer = new SockServer(new Kernel(), protocolKind);

			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					if (protocolKind == ProtocolKind.Tcp) {
						sockServer.bind(bindIp, port, listenMax);
					} else {
						sockServer.bind(bindIp, port);
					}
				}
			});
			t.start();

			while (sockServer.getSockState() == SockState.IDLE) {
				Util.sleep(200);
			}

			InetSocketAddress localAddress = sockServer.getLocalAddress();
			assertThat(localAddress.toString(), is("/127.0.0.1:9991"));
			//bind()後 localAddressの取得が可能になる

			InetSocketAddress remoteAddress = sockServer.getRemoteAddress();
			Assert.assertNull(remoteAddress);
			//SockServerでは、remoteＡｄｄｒｅｓｓは常にnullになる

			sockServer.close();

		}
	}
}
