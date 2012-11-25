package bjd.sock;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import bjd.ILife;
import bjd.net.Ip;
import bjd.net.Ssl;
import bjd.util.Bytes;
import bjd.util.Timeout;
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
				Util.sleep(10);
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
		set(SockState.CONNECT, (InetSocketAddress) channel.socket().getLocalSocketAddress(),
				(InetSocketAddress) channel.socket().getRemoteSocketAddress());

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
		set(SockState.CONNECT, (InetSocketAddress) channel.socket().getLocalSocketAddress(),
				(InetSocketAddress) channel.socket().getRemoteSocketAddress());

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
		//キューのスペース確認
		int space = sockQueue.getSpace();
		if (space <20000) {
			Util.sleep(10);
			return;
		}
		try {

			recvBuf.clear();
			recvBuf.limit(space); //受信できるのは、TcpQueueの空きサイズ分だけ
			if (channel.read(recvBuf) < 0) {
				//切断されている
				setError("channel.read()<0");
				return;
			}

			byte[] buf = new byte[recvBuf.position()];
			recvBuf.flip();
			recvBuf.get(buf);

			int q = sockQueue.enqueue(buf, buf.length);
			if(q!=buf.length){
				Util.runtimeException("SockTcp.doRead() sockQueue.enqueue()!=buf.length");
			}

		} catch (Exception ex) {
			setException(ex);
		}
	}

	public int length() {
		Util.sleep(1); //次の動作が実行されるようにsleepを置く
		return sockQueue.length();
	}

	/**
	 * 受信<br>
	 * 切断・タイムアウトでnullが返される
	 * 
	 * @param len 受信を希望するデータサイズ
	 * @param timeout タイムアウト値(ms)
	 * @param iLife 継続確認インターフェース
	 * @return 受信データ
	 */
	public byte[] recv(int len, int timeout, ILife iLife) {
<<<<<<< HEAD
<<<<<<< HEAD
		Calendar c = Calendar.getInstance();
		c.add(Calendar.SECOND, timeout);
=======

		Timeout tout = new Timeout(timeout);
>>>>>>> work
=======

		Timeout tout = new Timeout(timeout);
>>>>>>> work

		byte[] buffer = new byte[0];
		try {
			if (len <= sockQueue.length()) {
				// キューから取得する
				buffer = sockQueue.dequeue(len);

			} else {
				while (iLife.isLife()) {
					Util.sleep(0);
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
						Util.sleep(10);
					}
					if (tout.isFinish()) {
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

<<<<<<< HEAD
=======
	//	public byte[] recv(int len, int timeout, ILife iLife) {
	//
	//		Timeout tout = new Timeout(timeout);
	//
	//		byte[] buffer = new byte[0];
	//		try {
	//			if (len <= sockQueue.length()) {
	//				// キューから取得する
	//				buffer = sockQueue.dequeue(len);
	//			} else {
	//				while (iLife.isLife()) {
	//					Util.sleep(0);
	//					if (0 < sockQueue.length()) {
	//						//size=受信が必要なバイト数
	//						int size = len - buffer.length;
	//						//受信に必要なバイト数がバッファにない場合
	//						if (size > sockQueue.length()) {
	//							size = sockQueue.length(); //とりあえずバッファサイズ分だけ受信する
	//						}
	//						byte[] tmp = sockQueue.dequeue(size);
	//						buffer = Bytes.create(buffer, tmp);
	//						if (len <= buffer.length) {
	//							break;
	//						}
	//					} else {
	//						if (getSockState() != SockState.CONNECT) {
	//							return null;
	//						}
	//						Util.sleep(10);
	//					}
	//					if (tout.isFinish()) {
	//						buffer = sockQueue.dequeue(len); //タイムアウト
	//						break;
	//					}
	//				}
	//			}
	//		} catch (Exception ex) {
	//			ex.printStackTrace();
	//			return null;
	//		}
	//		//trace(TraceKind.Recv, buffer, false);//noEncode = false;テキストかバイナリかは不明
	//		return buffer;
	//	}
>>>>>>> work
	/**
	 * １行受信<br>
	 * 切断・タイムアウトでnullが返される
	 * 
<<<<<<< HEAD
<<<<<<< HEAD
	 * @param timeout タイムアウト値(ms)
	 * @param iLife 継続確認インターフェース
	 * @return 受信データ
	 */
	public byte[] lineRecv(int timeout, ILife iLife) {
		//Socket.ReceiveTimeout = timeout * 1000;

		Calendar c = Calendar.getInstance();
		c.add(Calendar.MILLISECOND, timeout);
=======
=======
>>>>>>> work
	 * @param sec タイムアウト値(sec)
	 * @param iLife 継続確認インターフェース
	 * @return 受信データ
	 */
	public byte[] lineRecv(int sec, ILife iLife) {
		//Socket.ReceiveTimeout = timeout * 1000;

		Timeout tout = new Timeout(sec * 1000);
<<<<<<< HEAD
>>>>>>> work
=======
>>>>>>> work

		while (iLife.isLife()) {
			//Ver5.1.6
			if (sockQueue.length() == 0) {
				Util.sleep(100);
			}
			byte[] buf = sockQueue.dequeueLine();
			//noEncode = false;//テキストである事が分かっている
			//Trace(TraceKind.Recv, buf, false);//トレース表示
			if (buf.length != 0) {
				return buf;
			}
			if (getSockState() != SockState.CONNECT) {
				return null;
			}
<<<<<<< HEAD
<<<<<<< HEAD
			if (c.compareTo(Calendar.getInstance()) < 0) {
=======
			if (tout.isFinish()) {
>>>>>>> work
=======
			if (tout.isFinish()) {
>>>>>>> work
				return null; //タイムアウト
			}
			Util.sleep(1);
		}
		return null;
	}

<<<<<<< HEAD
	public int send(byte[] buf) {
=======
	/**
	 * １行のString受信
	 * @param charsetName エンコード名
	 * @param sec タイムアウト（秒）
	 * @param iLife ILifeオブジェクトへのポインタ
	 * @return String 受信文字列
	 */
	public String stringRecv(String charsetName, int sec, ILife iLife) {
		try {
			byte[] bytes = lineRecv(sec, iLife);
			return new String(bytes, charsetName);
		} catch (Exception e) {
			Util.runtimeException(this, e);
		}
		return null;
	}

	/**
	 * １行受信(ASCII)
	 * @param sec タイムアウト（秒）
	 * @param iLife ILifeオブジェクトへのポインタ
	 * @return String 受信文字列
	 */
	public String stringRecv(int sec, ILife iLife) {
		return stringRecv("ASCII", sec, iLife);
	}

	public int send(byte[] buf, int start, int length) {
>>>>>>> work
		try {
			if (oneSsl != null) {
				//return oneSsl.Write(buffer, buffer.length);
			}
<<<<<<< HEAD
			if (getSockState() == SockState.CONNECT) {
				ByteBuffer byteBuffer = ByteBuffer.allocate(buf.length);
				byteBuffer.put(buf);
				byteBuffer.flip();
				int len = channel.write(byteBuffer);
				Util.sleep(1); //次の動作が実行されるようにsleepを置く
				return len;
=======
			ByteBuffer byteBuffer = ByteBuffer.allocate(length);
			byteBuffer.put(buf, start, length);
			byteBuffer.flip();
			int size = 0;
			while (byteBuffer.hasRemaining()) {
				int written = channel.write(byteBuffer);
				if (written == 0) {
					Util.sleep(10);
				}
				size += written;
>>>>>>> work
			}
			return size;
		} catch (Exception ex) {
			setException(ex);
			//logger.set(LogKind.Error, this, 9000046, String.format("Length=%d %s", buffer.length, ex.getMessage()));
		}
		return -1;
	}

<<<<<<< HEAD
<<<<<<< HEAD
=======
=======
	public int send(byte[] buf) {
		return send(buf, 0, buf.length);
	}

>>>>>>> work
	/**
	 * 1行送信<br>
	 * 内部でCRLFの２バイトが付かされる<br>
	 * @param buf　送信データ
	 * @return 送信バイト数
	 */
<<<<<<< HEAD
>>>>>>> work
=======
>>>>>>> work
	public int lineSend(byte[] buf) {
		int s1 = send(buf);
		int s2 = send(new byte[] { 0x0d, 0x0a });
		return s1 + s2;
	}

<<<<<<< HEAD
<<<<<<< HEAD
=======
=======
>>>>>>> work
	/**
	 * １行のString送信
	 * @param str 送信文字列
	 * @param charsetName エンコード名
	 * @return boolean 成功・失敗
	 */
	public boolean stringSend(String str, String charsetName) {
		try {
			byte[] buf = str.getBytes(charsetName);
<<<<<<< HEAD
			int l = lineSend(buf);
=======
			lineSend(buf);
>>>>>>> work
			return true;
		} catch (UnsupportedEncodingException e) {
			Util.runtimeException(this, e);
		}
		return false;
	}

	/**
	 * １行送信(ASCII)
	 * @param str 送信文字列
	 * @return boolean　成否
	 */
	public boolean stringSend(String str) {
		return stringSend(str, "ASCII");
	}

<<<<<<< HEAD
>>>>>>> work
=======
>>>>>>> work
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
<<<<<<< HEAD

=======
>>>>>>> work
}
