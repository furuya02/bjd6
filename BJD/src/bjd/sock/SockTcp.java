package bjd.sock;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Calendar;
import java.util.Iterator;

import bjd.net.Ip;
import bjd.net.OperateCrlf;
import bjd.net.Ssl;
import bjd.server.OneServer;
import bjd.util.Bytes;
import bjd.util.Util;

public final class SockTcp extends SockObj {

	private Selector selector = null;
	private SocketChannel channel = null; //ACCEPTの場合は、コンストラクタでコピーされる
	private Object oneSsl;
	private SockQueue sockQueue = new SockQueue();
	private ByteBuffer recvBuf = ByteBuffer.allocate(sockQueue.getMax());

	@SuppressWarnings("unused")
	private SockTcp() {
		//隠蔽
	}

	//CLIENT
	public SockTcp(Ip ip, int port, int timeout, Ssl ssl) {
		//SSL通信を使用する場合は、このオブジェクトがセットされる 通常の場合は、null
		//this.ssl = ssl;

		//************************************************
		//selector/channel生成
		//************************************************
		try {
			selector = Selector.open();

			channel = SocketChannel.open();
			channel.configureBlocking(false);

		} catch (Exception ex) {
			setException(ex);
			return;
		}
		//************************************************
		//connect
		//************************************************
		try {
			InetSocketAddress address = new InetSocketAddress(ip.getInetAddress(), port);
			channel.connect(address);
			int msec = timeout;
			while (!channel.finishConnect()) {
				Thread.sleep(10);
				msec -= 10;
				if (msec < 0) {
					setError("timeout");
					return;
				}
			}
		} catch (Exception ex) {
			setException(ex);
			return;
		}
		//************************************************
		//ここまでくると接続が完了している
		//************************************************
		set(SockState.CONNECT, (InetSocketAddress) channel.socket().getLocalSocketAddress(), (InetSocketAddress) channel.socket().getRemoteSocketAddress());

		//************************************************
		//read待機
		//************************************************
		try {
			channel.register(selector, SelectionKey.OP_READ);
		} catch (Exception ex) {
			setException(ex);
			return;
		}
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				selectLoop();
			}
		});
		t.start();
	}

	//ACCEPT
	public SockTcp(SocketChannel channel) {

		//************************************************
		//selector/channel生成
		//************************************************
		try {
			this.channel = channel;
			this.channel.configureBlocking(false);
			selector = Selector.open();
		} catch (Exception ex) {
			setException(ex);
			return;
		}
		//************************************************
		//ここまでくると接続が完了している
		//************************************************
		set(SockState.CONNECT, (InetSocketAddress) channel.socket().getLocalSocketAddress(), (InetSocketAddress) channel.socket().getRemoteSocketAddress());

		//************************************************
		//read待機
		//************************************************
		try {
			channel.register(selector, SelectionKey.OP_READ);
		} catch (Exception ex) {
			setException(ex);
			return;
		}
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				selectLoop();
			}
		});
		t.start();
	}

	private void selectLoop() {

		//Acceptの場合は、Connectの間だけループする
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
				}
			}
		}
	}

	private void doRead(SocketChannel channel) {
		recvBuf.limit(sockQueue.getSpace()); //受信できるのは、TcpQueueの空きサイズ分だけ
		try {
			recvBuf.clear();
			if (channel.read(recvBuf) < 0) {
				//切断されている
				setError("channel.read()<0");
				return;
			}

			byte[] buf = new byte[recvBuf.position()];
			recvBuf.flip();
			recvBuf.get(buf);

			sockQueue.enqueue(buf, buf.length);

		} catch (Exception ex) {
			setException(ex);
		}
	}

	public int length() {
		try {
			Thread.sleep(1); //次の動作が実行されるようにsleepを置く
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
		return sockQueue.length();
	}

	public byte[] recv(int len, int timeout) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.SECOND, timeout);

		byte[] buffer = new byte[0];
		try {
			if (len <= sockQueue.length()) {
				// キューから取得する
				buffer = sockQueue.dequeue(len);
			} else {
				while (true) {
					Thread.sleep(0);
					if (0 < sockQueue.length()) {
						//size=受信が必要なバイト数
						int size = len - buffer.length;
						//受信に必要なバイト数がバッファにない場合
						if (size > sockQueue.length()) {
							size = sockQueue.length(); //とりあえずバッファサイズ分だけ受信する
						}
						byte[] tmp = sockQueue.dequeue(size);
						buffer = Bytes.create(buffer, tmp);
						if (len <= buffer.length) {
							break;
						}
					} else {
						if (getSockState() != SockState.CONNECT) {
							return null;
						}
						Thread.sleep(10);
					}
					if (c.compareTo(Calendar.getInstance()) < 0) {
						buffer = sockQueue.dequeue(len); //タイムアウト
						break;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		//trace(TraceKind.Recv, buffer, false);//noEncode = false;テキストかバイナリかは不明
		return buffer;
	}

	public int send(byte[] buf) {
		try {
			if (oneSsl != null) {
				//return oneSsl.Write(buffer, buffer.length);
			}
			if (getSockState() == SockState.CONNECT) {
				ByteBuffer byteBuffer = ByteBuffer.allocate(buf.length);
				byteBuffer.put(buf);
				byteBuffer.flip();
				int len = channel.write(byteBuffer);
				Thread.sleep(1); //次の動作が実行されるようにsleepを置く
				return len;
			}
		} catch (Exception ex) {
			setException(ex);
			//logger.set(LogKind.Error, this, 9000046, String.format("Length=%d %s", buffer.length, ex.getMessage()));
		}
		return -1;
	}

	@Override
	public void close() {
		//ACCEPT・CLIENT
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

	public byte[] lineRecv(int timeout, OperateCrlf yes, OneServer oneServer) {
		Util.runtimeException("未実装");
		return null;
	}
}
