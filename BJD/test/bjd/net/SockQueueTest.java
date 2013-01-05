package bjd.net;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import bjd.sock.SockQueue;
import bjd.test.TestUtil;

public final class SockQueueTest {

	@Test
	public void 生成時のlengthは0になる() throws Exception {
		//setUp
		SockQueue sut = new SockQueue();
		int expected = 0;
		//exercise
		int actual = sut.length();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void lengthが0の時_dequeueで100バイト取得しても0バイトしか返らない() throws Exception {
		//setUp
		SockQueue sut = new SockQueue();
		int expected = 0;
		//exercise
		int actual = sut.dequeue(100).length;
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void lengthが50の時_dequeueで100バイト取得しても50バイトしか返らない() throws Exception {
		//setUp
		SockQueue sut = new SockQueue();
		sut.enqueue(new byte[50], 50);
		int expected = 50;
		//exercise
		int actual = sut.dequeue(100).length;
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void lengthが200の時_dequeueで100バイト取得すると100バイト返る() throws Exception {
		//setUp
		SockQueue sut = new SockQueue();
		sut.enqueue(new byte[200], 200);
		int expected = 100;
		//exercise
		int actual = sut.dequeue(100).length;
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void lengthが200の時_dequeueで100バイト取得すると残りは100バイトになる() throws Exception {
		//setUp
		SockQueue sut = new SockQueue();
		sut.enqueue(new byte[200], 200);
		sut.dequeue(100);
		int expected = 100;
		//exercise
		int actual = sut.length();
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void enqueueしたデータとdequeueしたデータの整合性を確認する() throws Exception {
		//setUp
		SockQueue sut = new SockQueue();
		byte[] expected = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		sut.enqueue(expected, 10);
		//exercise
		byte[] actual = sut.dequeue(10);
		//verify
		assertThat(actual, is(expected));
	}

	@Test
	public void enqueueしたデータの一部をdequeueしたデータの整合性を確認する() throws Exception {
		//setUp
		SockQueue sut = new SockQueue();
		byte[] buf = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		sut.enqueue(buf, 10);
		sut.dequeue(5); //最初に5バイト取得
		byte[] expected = new byte[] { 5, 6, 7, 8, 9 };
		//exercise
		byte[] actual = sut.dequeue(5);
		//verify
		assertThat(actual, is(expected));
	}


	@Test
	public void SockQueue_スペース確認() {
		int max = 2000000;

		SockQueue sockQueu = new SockQueue();

		int space = sockQueu.getSpace();
		//キューの空きサイズ
		assertThat(space, is(max));

		byte[] buf = new byte[max - 100];
		sockQueu.enqueue(buf, buf.length);
		
		space = sockQueu.getSpace();
		//キューの空きサイズ
		assertThat(space, is(100));

		int len = sockQueu.enqueue(buf, 200);
		//空きサイズを超えて格納すると失敗する(※0が返る)
		assertThat(len, is(0));

	}

	@Test
	public void SockQueue_行取得() {
		//int max = 1048560;

		SockQueue sockQueu = new SockQueue();

		byte[] lines = new byte[] { 0x61, 0x0d, 0x0a, 0x62, 0x0d, 0x0a, 0x63 };
		sockQueu.enqueue(lines, lines.length);
		//2行と改行なしの1行で初期化

		byte[] buf = sockQueu.dequeueLine();
		//sockQueue.dequeuLine()=\"1/r/n\" 1行目取得
		assertThat(buf, is(new byte[] { 0x61, 0x0d, 0x0a }));

		//sockQueue.dequeuLine()=\"2/r/n\" 2行目取得 
		buf = sockQueu.dequeueLine();
		assertThat(buf, is(new byte[] { 0x62, 0x0d, 0x0a }));

		buf = sockQueu.dequeueLine();
		//sockQueue.dequeuLine()=\"\" 3行目の取得は失敗する
		assertThat(buf, is(new byte[0]));

		lines = new byte[] { 0x0d, 0x0a };
		sockQueu.enqueue(lines, lines.length);
		//"sockQueue.enqueu(/r/n) 改行のみ追加

		buf = sockQueu.dequeueLine();
		//sockQueue.dequeuLine()=\"3\" 3行目の取得に成功する"
		assertThat(buf, is(new byte[] { 0x63, 0x0d, 0x0a }));

	}

}
