package bjd.sock;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

import bjd.ThreadBase;
import bjd.net.InetKind;
import bjd.net.Ip;
import bjd.net.ProtocolKind;
import bjd.util.Util;

public class SockServer extends SockObj {

	private Selector selector = null;
	private ProtocolKind protocolKind;

	private ServerSocketChannel serverChannel = null;
	private DatagramChannel datagramChannel = null;

	public final ProtocolKind getProtocolKind() {
		return protocolKind;
	}

	public SockServer(ProtocolKind protocolKind) {
		this.protocolKind = protocolKind;
		//************************************************
		//selector生成(channelの生成はbindで行う)
		//************************************************
		try {
			selector = Selector.open();
		} catch (Exception ex) {
			setException(ex);
			return;
		}
	}

	@Override
	public final void close() {
		if (serverChannel != null && serverChannel.isOpen()) {
			try {
				selector.wakeup();
				selector.close();
				serverChannel.close();
			} catch (IOException ex) {
				ex.printStackTrace(); //エラーは無視する
			}
		}
		if (datagramChannel != null && datagramChannel.isOpen()) {
			try {
				selector.wakeup();
				selector.close();
				datagramChannel.close();
			} catch (IOException ex) {
				ex.printStackTrace(); //エラーは無視する
			}
		}
		setError("close()");
	}

	public final boolean bind(Ip bindIp, int port, int listenMax) {
		if (protocolKind != ProtocolKind.Tcp) {
			Util.runtimeException(String.format("this object is %s", protocolKind));
		}
		try {
			//************************************************
			//channel生成
			//************************************************
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			//************************************************
			//bind
			//************************************************
			serverChannel.socket().bind(new InetSocketAddress(bindIp.getInetAddress(), port), listenMax);
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (Exception ex) {
			setException(ex);
			return false;
		}
		set(SockState.Bind, (InetSocketAddress) serverChannel.socket().getLocalSocketAddress(), null);
		return true;
	}

	public final boolean bind(Ip bindIp, int port) {
		if (protocolKind != ProtocolKind.Udp) {
			Util.runtimeException(String.format("this object is %s", protocolKind));
		}
		//InetSocketAddress l = new InetSocketAddress(bindIp.getInetAddress(), port);
		try {
			//************************************************
			//channel生成
			//************************************************
			if (bindIp.getInetKind() == InetKind.V4) {
				datagramChannel = DatagramChannel.open(StandardProtocolFamily.INET);
			} else {
				datagramChannel = DatagramChannel.open(StandardProtocolFamily.INET6);
			}
			datagramChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			datagramChannel.configureBlocking(false);
			//************************************************
			//bind
			//************************************************
			datagramChannel.socket().bind(new InetSocketAddress(bindIp.getInetAddress(), port));
			//datagramChannel.socket().bind(l);
			datagramChannel.register(selector, SelectionKey.OP_READ);
			
			//set(SockState.Bind, l, null);
		} catch (Exception ex) {
			setException(ex);
			return false;
		}
		set(SockState.Bind, (InetSocketAddress) datagramChannel.socket().getLocalSocketAddress(), null);
		return true;
	}

	public final SockObj select(ThreadBase threadBase) {
		while (threadBase.isLife()) {
			int n;
			try {
				n = selector.select(1);
			} catch (Exception ex) {
				setException(ex);
				break;
			}
			if (n < 0) {
				setError(String.format("selector.select(1)=%d", n));
				break;
			} else if (n == 0) {
				try {
					Thread.sleep(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else if (n > 0) {
				for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext();) {
					SelectionKey key = (SelectionKey) it.next();
					it.remove();
					if (protocolKind == ProtocolKind.Udp) {
						if (key.isReadable()) {
							return new SockUdp((DatagramChannel) key.channel());
						}
					} else {
						if (key.isAcceptable()) {
							try {
								return new SockTcp(serverChannel.accept()); //ACCEPT
							} catch (IOException ex) {
								//accept()が失敗した場合は処理を継続する
								ex.printStackTrace();
							}
						}
					}
				}
			}
		}
		setError("isLife()==false");
		return null;
	}
}
