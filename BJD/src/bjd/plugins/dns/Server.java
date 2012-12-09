package bjd.plugins.dns;

import bjd.Kernel;
import bjd.net.OneBind;
import bjd.option.Conf;
import bjd.server.OneServer;
import bjd.sock.SockObj;
import bjd.sock.SockUdp;

public final class Server extends OneServer {


	public Server(Kernel kernel, Conf conf, OneBind oneBind) {
		super(kernel, "Dns", conf, oneBind);
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
		SockUdp sockUdp = (SockUdp)sockObj;
		//セッションごとの情報
		//Session session = new Session((SockTcp) sockObj);

		//このコネクションの間、１つづつインクメントしながら使用される
		//本来は、切断したポート番号は再利用可能なので、インクリメントの必要は無いが、
		//短時間で再利用しようとするとエラーが発生する場合があるので、これを避ける目的でインクリメントして使用している

		//while (isLife()) {
			//このループは最初にクライアントからのコマンドを１行受信し、最後に、
			//sockCtrl.LineSend(resStr)でレスポンス処理を行う
			//continueを指定した場合は、レスポンスを返さずに次のコマンド受信に入る（例外処理用）
			//breakを指定した場合は、コネクションの終了を意味する（QUIT ABORT 及びエラーの場合）
		//}
		sockUdp.close();
	}

	public String getMsg(int messageNo) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
}
