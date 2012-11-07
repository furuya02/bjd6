package bjd.plugins.sample;

import bjd.Kernel;
import bjd.net.OneBind;
import bjd.option.Conf;
import bjd.server.OneServer;
import bjd.sock.SockObj;
import bjd.sock.SockTcp;

public final class Server extends OneServer {
	public Server(Kernel kernel, Conf conf, OneBind oneBind) {
		super(kernel, "Sample", conf, oneBind);
		
		//Option.javaで定義したものが、読み込めているかどうかのチェック
		String tag = "sampleText";
		System.out.println(String.format("■CHECK => conf.get(%s)=%s", tag, conf.get(tag)));
	}

	//private Server() {
	//	super(null, "Sample", null, null);
	//}

	
	//	//リモート操作（データの取得）Toolダイログとのデータ送受
	//	override public String Cmd(String cmdStr) { return ""; }
	
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

		int timeout = (int) getConf().get("timeout");
		//１行受信
		byte[] buf = sockTcp.recv(sockTcp.length(), timeout);
		//.AsciiRecv(30, OperateCrlf.No,this);

		//１行送信
		sockTcp.send(buf);
		//.AsciiSend(str, OperateCrlf.No);

	}
}