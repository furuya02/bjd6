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

import bjd.ILife;
import bjd.Kernel;
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

	//***************************************************************************
	//パラメータのKernelはSockObjにおけるTrace()のためだけに使用されているので、
	//Traceしない場合は削除することができる
	//***************************************************************************

	public SockServer(Kernel kernel, ProtocolKind protocolKind) {
		super(kernel);
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
			Util.runtimeException("use udp version bind()");
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
		} catch (IOException ex) {
			setException(ex);
			return false;
		}
		set(SockState.Bind, (InetSocketAddress) serverChannel.socket().getLocalSocketAddress(), null);
		return true;
	}

	public final boolean bind(Ip bindIp, int port) {
		if (protocolKind != ProtocolKind.Udp) {
			Util.runtimeException("use tcp version bind()");
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

	public final SockObj select(ILife iLife) {
		while (iLife.isLife()) {
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
				Util.sleep(0);
			} else if (n > 0) {
				for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext();) {
					SelectionKey key = (SelectionKey) it.next();
					it.remove();
					if (protocolKind == ProtocolKind.Udp) {
						if (key.isReadable()) {
							return new SockUdp(getKernel(), (DatagramChannel) key.channel());
						}
					} else {
						if (key.isAcceptable()) {
							try {
								return new SockTcp(getKernel(), serverChannel.accept()); //ACCEPT
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

	/**
	 * 指定したアドレス・ポートで待ち受けて、接続されたら、そのソケットを返す<br>
	 * 失敗した時nullが返る
	 * @param ip 待ち受けるアドレス
	 * @param port 待ち受けるポート
	 * @param iLife ILifeインターフェースオブジェクト
	 * @return SockTcp
	 */
	public static SockTcp createConnection(Kernel kernel, Ip ip, int port, ILife iLife) {
		SockServer sockServer = new SockServer(kernel, ProtocolKind.Tcp);
		if (sockServer.getSockState() != SockState.Error) {
			int listenMax = 1;
			if (sockServer.bind(ip, port, listenMax)) {
				while (iLife.isLife()) {
					final SockTcp child = (SockTcp) sockServer.select(iLife);
					if (child == null) {
						break;
					}
					sockServer.close(); //これ大丈夫？
					return child;
				}
			}
		}
		sockServer.close();
		return null;
	}

	/**
	 * bindが可能かどうかの確認
	 * @param ip
	 * @param port
	 * @return
	 */
	public static boolean isAvailable(Kernel kernel, Ip ip, int port) {
		SockServer sockServer = new SockServer(kernel, ProtocolKind.Tcp);
		if (sockServer.getSockState() != SockState.Error) {
			int listenMax = 1;
			if (sockServer.bind(ip, port, listenMax)) {
				sockServer.close();
				return true;
			}
		}
		sockServer.close();
		return false;
	}

}
