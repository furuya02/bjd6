package bjd.sock;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

import bjd.net.Ip;
import bjd.net.Ssl;
import bjd.util.Util;

public final class SockUdp extends SockObj {

	private SockKind sockKind;

	private Selector selector = null;
	private DatagramChannel channel = null;
	private Object oneSsl;

	private ByteBuffer recvBuf = ByteBuffer.allocate(1600);

	@SuppressWarnings("unused")
	private SockUdp() {
		//隠蔽する
	}

	//ACCEPT
	public SockUdp(DatagramChannel channel) {
		sockKind = SockKind.ACCEPT;

		//************************************************
		//selector/channel生成
		//************************************************
		try {
			set(SockState.CONNECT, (InetSocketAddress) channel.getLocalAddress(), null);
			this.channel = channel;
			this.channel.configureBlocking(false);
			selector = Selector.open();
		} catch (Exception ex) {
			setException(ex);
			return;
		}

		//ACCEPTの場合は、既に受信しているので、これ以上待機する必要はない
		doRead(channel);
		//UDPの場合、doReadの中で、remoteAddressがセットされる

		//あとは、クローズされるまで待機
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				while (getSockState() == SockState.CONNECT) {
					Util.sleep(100);
				}
			}
		});
		t.start();
	}

	//CLIENT
	public SockUdp(Ip ip, int port, int timeout, Ssl ssl, byte[] buf) {
		//SSL通信を使用する場合は、このオブジェクトがセットされる 通常の場合は、null
		//this.ssl = ssl;

		sockKind = SockKind.CLIENT;

		//************************************************
		//selector/channel生成
		//************************************************
		try {
			selector = Selector.open();

			channel = DatagramChannel.open();
			channel.configureBlocking(false);

		} catch (Exception ex) {
			setException(ex);
			return;
		}
		//************************************************
		//送信処理
		//************************************************
		try {
			InetAddress inetAddress = ip.getInetAddress();
			InetSocketAddress remoteAddress = new InetSocketAddress(inetAddress, port);
			set(SockState.CONNECT, (InetSocketAddress) channel.socket().getLocalSocketAddress(), remoteAddress);
		} catch (UnknownHostException ex) {
			setException(ex);
		}

		//		InetSocketAddress remoteAddress = new InetSocketAddress(inetAddress, port);
		//		set(SockState.CONNECT, (InetSocketAddress) channel.socket().getLocalSocketAddress(), remoteAddress);
		//set(SockState.CONNECT, getLocalAddress(), remoteAddress);

		send(buf);

		//************************************************
		//read待機
		//************************************************
		try {
			channel.register(selector, SelectionKey.OP_READ);
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					while (getSockState() == SockState.CONNECT) {
						try {
							if (selector.select() <= 0) {
								break;
							}
						} catch (IOException ex) {
							setException(ex);
							return;
						}
						for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext();) {
							SelectionKey key = (SelectionKey) it.next();
							it.remove();
							if (key.isReadable()) {
								doRead(channel);
								break; // UDPの場合は、１度受信したら、もう待機しない
							}
						}
					}
					close();
				}
			});
			t.start();
		} catch (Exception ex) {
			setException(ex);
		}
	}

	//ACCEPT・CLIENT
	private void doRead(DatagramChannel channel) {
		recvBuf.clear();
		try {
			SocketAddress remoteAddress = channel.receive(recvBuf);
			//UDPの場合、受信した時点でRemoteAddressが判明する
			set(SockState.CONNECT, getLocalAddress(), (InetSocketAddress) remoteAddress);
		} catch (IOException ex) {
			setException(ex);
		}
	}

	public int length() {
		return recvBuf.position();
	}

	public byte[] recv() {
		byte[] buf = new byte[recvBuf.position()];
		recvBuf.flip();
		recvBuf.get(buf);
		return buf;
	}

	/**
	 * ミリ秒まで受信を待機する
	 * @param timeout ミリ秒
	 * @return
	 */
	public byte[] recv(int timeout) {
		for (int i = 0; i < timeout / 10; i++) {
			Util.sleep(10);
			if (recvBuf.position() != 0) {
				break;
			}
		}
		return recv();
	}

	//ACCEPTのみで使用する　CLIENTは、コンストラクタで送信する
	public int send(byte[] buf) {
		try {
			if (oneSsl != null) {
				//return oneSsl.Write(buffer, buffer.length);
			}
			if (getSockState() == SockState.CONNECT) {
				ByteBuffer byteBuffer = ByteBuffer.allocate(buf.length);
				byteBuffer.put(buf);
				byteBuffer.flip();
				return channel.send(byteBuffer, getRemoteAddress());
			}
		} catch (Exception ex) {
			setException(ex);
			//logger.set(LogKind.Error, this, 9000046, String.format("Length=%d %s", buffer.length, ex.getMessage()));
		}
		return -1;
	}

	@Override
	public void close() {
		//ACCEPT
		if (sockKind == SockKind.ACCEPT) {
			return;
		}
		//CLIENT
		if (channel != null && channel.isOpen()) {
			try {
				selector.wakeup();
				channel.close();
			} catch (IOException ex) {
				ex.printStackTrace(); //エラーは無視する
			}
		}
		setError("close()");
	}
}
