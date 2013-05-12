package bjd.plugins.sample;

import bjd.Kernel;
import bjd.net.OneBind;
import bjd.option.Conf;
import bjd.server.OneServer;
import bjd.sock.SockObj;
import bjd.sock.SockTcp;

public final class Server extends OneServer {
	public Server(Kernel kernel, Conf conf, OneBind oneBind) {
		super(kernel, conf, oneBind);
		
		//Option.javaで定義したものが、読み込めているかどうかのチェック
		//String tag = "sampleText";
		//System.out.println(String.format("■CHECK => conf.get(%s)=%s", tag, conf.get(tag)));
	}

	@Override
	public String getMsg(int messageNo) {
		switch (messageNo) {
			case 1:
				return isJp() ? "日本語" : "English"; //この形式でログ用のメッセージ追加できます。
			default:
				break;
		}
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
		//UDPサーバの場合は、UdpObjで受ける
		SockTcp sockTcp = (SockTcp) sockObj;

		//オプションから「sampleText」を取得する
		String sampleText = (String) getConf().get("sampleText");

		//１行送信
		sockTcp.lineSend(sampleText.getBytes());

		//５秒間待機して、１行受信に成功したら、その内容をechoして終了する
		int sec = 5; //５秒間
		//１行受信
		byte[] buf = sockTcp.lineRecv(sec, this);
		//１行送信
		sockTcp.send(buf);

	}
}