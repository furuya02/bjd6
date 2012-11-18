package bjd.net;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Assert;
import org.junit.Test;

import bjd.sock.SockQueue;
import bjd.test.TestUtil;

public final class SockQueueTest {

	@Test
	public void SockQueue入出力() {

		TestUtil.prompt("sockQueue = new SockQueue()");
		SockQueue sockQueu = new SockQueue();

		int len = sockQueu.length();
		TestUtil.prompt(String.format("sockQueue.length = %s キューのバイト数は0", len));
		assertThat(len, is(0));

		TestUtil.prompt(String.format("byte[] buf = sockQueue.dequeu(100) 0のキューから100バイト取得"));
		byte[] buf = sockQueu.dequeue(100);

		TestUtil.prompt(String.format("buf.length=0 サイズ0のバッファが返される"));
		assertThat(buf.length, is(0));

		TestUtil.prompt(String.format("byte[] buf = new byte[100]{0,1,2,3....} テスト用データを作成"));
		buf = new byte[100];
		for (int i = 0; i < 100; i++) {
			buf[i] = (byte) i;
		}
		TestUtil.prompt(String.format("int len = sockQueue.enqueu(buf,100) 100バイトをキューに入れる"));
		len = sockQueu.enqueue(buf, 100);

		len = sockQueu.length();
		TestUtil.prompt(String.format("sockQueue.length = %s", len));
		assertThat(len, is(100));

		TestUtil.prompt(String.format("byte[] buf = sockQueue.dequeu(50) 50バイト、キューから取り出す"));
		buf = sockQueu.dequeue(50);

		len = buf.length;
		TestUtil.prompt(String.format("buf.length == %d", len));
		assertThat(len, is(50));

		TestUtil.prompt(String.format("byte[] buf = sockQueue.dequeu(30)　30バイト、キューから取り出す"));
		buf = sockQueu.dequeue(30);

		len = buf.length;
		TestUtil.prompt(String.format("buf.length == %d", len));
		assertThat(len, is(30));

		TestUtil.prompt(String.format("buf = {50,51,52....}　取り出した内容を確認する"));
		for (int i = 0; i < 30; i++) {
			Assert.assertSame(buf[i], (byte) (50 + i));
		}

		TestUtil.prompt(String.format("キューの残りは20バイト"));

		TestUtil.prompt(String.format("byte[] buf = sockQueue.dequeu(50) 残り20のキューから50を取得する"));
		buf = sockQueu.dequeue(50);

		len = buf.length;
		TestUtil.prompt(String.format("buf.length == %d 20バイトだけ取得できる", len));
		assertThat(len, is(20));

		len = sockQueu.length();
		TestUtil.prompt(String.format("sockQueue.length = %s キューの残りは0バイトになる", len));
		assertThat(len, is(0));

	}

	@Test
	public void SockQueue_スペース確認() {
		int max = 1048560;

		TestUtil.prompt("sockQueue = new SockQueue()");
		SockQueue sockQueu = new SockQueue();

		int space = sockQueu.getSpace();
		TestUtil.prompt(String.format("sockQueue.getSpqce()=%s キューの空きサイズ", space));
		assertThat(space, is(max));

		byte[] buf = new byte[max - 100];
		sockQueu.enqueue(buf, buf.length);
		TestUtil.prompt(String.format("sockQueue.enqueue(buf,%d)", buf.length));

		space = sockQueu.getSpace();
		TestUtil.prompt(String.format("sockQueue.getSpqce()=%s キューの空きサイズ", space));
		assertThat(space, is(100));

		int len = sockQueu.enqueue(buf, 200);
		TestUtil.prompt(String.format("sockQueue.enqueue(buf,200)=%s 空きサイズを超えて格納すると失敗する(※0が返る)", len));
		assertThat(len, is(0));

	}

	@Test
	public void SockQueue_行取得() {
		//int max = 1048560;

		TestUtil.prompt("sockQueue = new SockQueue()");
		SockQueue sockQueu = new SockQueue();

		byte[] lines = new byte[] { 0x61, 0x0d, 0x0a, 0x62, 0x0d, 0x0a, 0x63 };
		sockQueu.enqueue(lines, lines.length);
		TestUtil.prompt("sockQueue.enqueu(1/r/n2/r/n3) 2行と改行なしの1行で初期化");

		byte[] buf = sockQueu.dequeueLine();
		TestUtil.prompt("sockQueue.dequeuLine()=\"1/r/n\" 1行目取得");
		assertThat(buf, is(new byte[] { 0x61, 0x0d, 0x0a }));

		TestUtil.prompt("sockQueue.dequeuLine()=\"2/r/n\" 2行目取得 ");
		buf = sockQueu.dequeueLine();
		assertThat(buf, is(new byte[] { 0x62, 0x0d, 0x0a }));

		buf = sockQueu.dequeueLine();
		TestUtil.prompt("sockQueue.dequeuLine()=\"\" 3行目の取得は失敗する");
		assertThat(buf, is(new byte[0]));

		lines = new byte[] { 0x0d, 0x0a };
		sockQueu.enqueue(lines, lines.length);
		TestUtil.prompt("sockQueue.enqueu(/r/n) 改行のみ追加");

		buf = sockQueu.dequeueLine();
		TestUtil.prompt("sockQueue.dequeuLine()=\"3\" 3行目の取得に成功する");
		assertThat(buf, is(new byte[] { 0x63, 0x0d, 0x0a }));

	}

}
