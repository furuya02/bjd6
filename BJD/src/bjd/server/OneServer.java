package bjd.server;

import java.nio.charset.Charset;
import java.util.ArrayList;
<<<<<<< HEAD
=======

>>>>>>> work
import bjd.Kernel;
import bjd.ThreadBase;
import bjd.acl.AclKind;
import bjd.acl.AclList;
import bjd.ctrl.CtrlType;
import bjd.log.LogKind;
import bjd.log.Logger;
import bjd.log.OneLog;
import bjd.net.Ip;
import bjd.net.IpKind;
import bjd.net.OneBind;
import bjd.net.ProtocolKind;
import bjd.net.Ssl;
import bjd.option.Conf;
import bjd.option.Dat;
import bjd.option.OptionSample;
import bjd.sock.SockObj;
import bjd.sock.SockServer;
import bjd.sock.SockState;
import bjd.sock.SockTcp;
import bjd.sock.SockUdp;
import bjd.util.Inet;
import bjd.util.Timeout;
import bjd.util.Util;

/**
 * 
 * OneServer １つのバインドアドレス：ポートごとにサーバを表現するクラス<br>
 * 各サーバオブジェクトの基底クラス<br>
 * @author user1
 *
 */
public abstract class OneServer extends ThreadBase {

	private String nameTag;
	private Conf conf;
	private OneBind oneBind;
	private AclList aclList;
	private Ssl ssl = null;
	private Logger logger;
	private SockServer sockServer = null;
	private int timeout;
	private boolean isJp;

	protected final Conf getConf() {
		return conf;
	}

<<<<<<< HEAD
<<<<<<< HEAD
=======
=======
>>>>>>> work
	protected final Logger getLogger() {
		return logger;
	}

<<<<<<< HEAD
>>>>>>> work
=======
>>>>>>> work
	protected final String getNameTag() {
		return nameTag;
	}

	protected final boolean isJp() {
		return isJp;
	}
<<<<<<< HEAD
<<<<<<< HEAD
=======

	protected final int getTimeout() {
		return timeout;
	}
>>>>>>> work

=======

	protected final int getTimeout() {
		return timeout;
	}

>>>>>>> work
	public abstract String getMsg(int messageNo);

	//子スレッド管理
	private Object lock = new Object(); //排他制御用オブジェクト
	private ArrayList<Thread> childThreads = new ArrayList<Thread>();
	private int multiple; //同時接続数

	//ステータス表示用
	@Override
	public final String toString() {
		String stat = isJp() ? "+ サービス中 " : "+ In execution ";
		if (!isRunnig()) {
			stat = isJp() ? "- 停止 " : "- Initialization failure ";
		}
		return String.format("%s\t%20s\t[%s\t:%s %s]\tThread %d/%d", stat, getNameTag(), oneBind.getAddr(), oneBind
				.getProtocol().toString().toUpperCase(), (int) conf.get("port"), count(), multiple);
	}

	public final int count() {
		//チャイルドスレッドオブジェクトの整理
		for (int i = childThreads.size() - 1; i >= 0; i--) {
			if (!childThreads.get(i).isAlive()) {
				childThreads.remove(i);
			}
		}
		return childThreads.size();
	}

	//リモート操作(データの取得)
	public final String cmd(String cmdStr) {
		return "";
	}

	public final SockState getSockState() {
		if (sockServer == null) {
			return SockState.Error;
		}
		return sockServer.getSockState();
	}

	//コンストラクタ
	protected OneServer(Kernel kernel, String nameTag, Conf conf, OneBind oneBind) {
		super(kernel.createLogger(nameTag, true, null));

		this.nameTag = nameTag;
		this.conf = conf;
		this.oneBind = oneBind;
		this.isJp = kernel.isJp();

		//DEBUG用
		if (this.conf == null) {
			OptionSample optionSample = new OptionSample(kernel, "");
			this.conf = new Conf(optionSample);
			this.conf.set("port", 9990);
			this.conf.set("multiple", 10);
			this.conf.set("acl", new Dat(new CtrlType[0]));
			this.conf.set("enableAcl", 1);
			this.conf.set("timeOut", 3);
		}
		//DEBUG用
		if (this.oneBind == null) {
<<<<<<< HEAD
<<<<<<< HEAD
			Ip ip = null;
			try {
				ip = new Ip("127.0.0.1");
			} catch (ValidObjException ex) {
				//127.0.0.1で例外となるようなら設計問題とするしかない
				Util.runtimeException("new Ip(127.0.0.1)");
			}
=======
			Ip ip = new Ip(IpKind.V4_LOCALHOST);
>>>>>>> work
=======
			Ip ip = new Ip(IpKind.V4_LOCALHOST);
>>>>>>> work
			this.oneBind = new OneBind(ip, ProtocolKind.Tcp);
		}

		this.logger = kernel.createLogger(nameTag, (boolean) this.conf.get("useDetailsLog"), this);
		multiple = (int) this.conf.get("multiple");

		//ACLリスト 定義が無い場合は、aclListを生成しない
		Dat acl = (Dat) this.conf.get("acl");
		aclList = new AclList(acl, (int) this.conf.get("enableAcl"), logger);
		timeout = (int) this.conf.get("timeOut");
	}

	@Override
	public final void start() {

		super.start();

		//bindが完了するまで待機する
		while (sockServer == null || sockServer.getSockState() == SockState.IDLE) {
			Util.sleep(100);
		}
	}

	@Override
	public final void stop() {
		if (sockServer == null) {
			return; //すでに終了処理が終わっている
		}
		super.stop(); //life=false ですべてのループを解除する
		sockServer.close();

		// 全部の子スレッドが終了するのを待つ
		while (count() > 0) {
			Util.sleep(500);
		}
		sockServer = null;

	}

	@Override
	public final void dispose() {
		// super.dispose()は、ThreadBaseでstop()が呼ばれるだけなので必要ない
		stop();
	}

	//スレッド停止処理
	protected abstract void onStopServer(); //スレッド停止処理

	@Override
	protected final void onStopThread() {
		onStopServer(); //子クラスのスレッド停止処理
		if (ssl != null) {
			ssl.dispose();
		}
	}

	//スレッド開始処理
	//サーバが正常に起動できる場合(isInitSuccess==true)のみスレッド開始できる
	protected abstract boolean onStartServer(); //スレッド開始処理

	@Override
	protected final boolean onStartThread() {
		return onStartServer(); //子クラスのスレッド開始処理
	}

	@Override
	protected final void onRunThread() {

		int port = (int) conf.get("port");
		String bindStr = String.format("%s:%d %s", oneBind.getAddr(), port, oneBind.getProtocol());
		logger.set(LogKind.NORMAL, (SockObj) null, 9000000, bindStr);

		//DOSを受けた場合、multiple数まで連続アクセスまでは記憶してしまう
		//DOSが終わった後も、その分だけ復帰に時間を要する

		sockServer = new SockServer(oneBind.getProtocol());

		if (sockServer.getSockState() != SockState.Error) {
			if (sockServer.getProtocolKind() == ProtocolKind.Tcp) {
				runTcpServer(port);
			} else {
				runUdpServer(port);
			}
		}
		logger.set(LogKind.NORMAL, (SockObj) null, 9000001, bindStr);

	}

	private void runTcpServer(int port) {

		int listenMax = 5;

		if (!sockServer.bind(oneBind.getAddr(), port, listenMax)) {
			logger.set(LogKind.ERROR, sockServer, 9000006, sockServer.getLastEror());
		} else {
			while (isLife()) {
				final SockTcp child = (SockTcp) sockServer.select(this);
				if (child == null) {
					break;
				}
				if (count() >= multiple) {
					logger.set(LogKind.SECURE, sockServer, 9000004,
							String.format("count:%d/multiple:%d", count(), multiple));
					//同時接続数を超えたのでリクエストをキャンセルします
					child.close();
					continue;
				}

				// ACL制限のチェック
				if (aclCheck(child) == AclKind.Deny) {
					child.close();
					continue;
				}

				synchronized (lock) {
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							subThread((SockTcp) child);
						}
					});
					t.start();
					childThreads.add(t);
				}
			}

		}
	}

	private void runUdpServer(int port) {

		if (!sockServer.bind(oneBind.getAddr(), port)) {
			System.out.println(String.format("bind()=false %s", sockServer.getLastEror()));
		} else {

			while (isLife()) {
				final SockUdp child = (SockUdp) sockServer.select(this);
				if (child == null) {
					break;
				}
				if (count() >= multiple) {
					logger.set(LogKind.SECURE, sockServer, 9000004,
							String.format("count:%d/multiple:%d", count(), multiple));
					//同時接続数を超えたのでリクエストをキャンセルします
					child.close();
					continue;
				}

				// ACL制限のチェック
				if (aclCheck(child) == AclKind.Deny) {
					child.close();
					continue;
				}

				synchronized (lock) {
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							subThread((SockUdp) child);
						}
					});
					t.start();
					childThreads.add(t);
				}
			}

		}
	}

	/**
	 * ACL制限のチェック
	 * @param sockObj 検査対象のソケット
	 * @return AclKind
	 */
	private AclKind aclCheck(SockObj sockObj) {
		AclKind aclKind = AclKind.Allow;
		if (aclList != null) {
			Ip ip = new Ip(sockObj.getRemoteAddress().getAddress());
			aclKind = aclList.check(ip);
		}

		if (aclKind == AclKind.Deny) {
			denyAddress = sockObj.getRemoteAddress().getAddress().toString();
		}
		return aclKind;
	}

	protected abstract void onSubThread(SockObj sockObj);

	private String denyAddress = ""; //Ver5.3.5 DoS対処

	/**
	 * １リクエストに対する子スレッドとして起動される
	 * @param sockObj
	 */
	public final void subThread(SockObj sockObj) {

		//クライアントのホスト名を逆引きする
		sockObj.resolve((boolean) conf.get("useResolve"), logger);
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> work

		//_subThreadの中でSockObjは破棄する（ただしUDPの場合は、クローンなのでClose()してもsocketは破棄されない）
		logger.set(LogKind.DETAIL, sockObj, 9000002, String.format("count=%d Local=%s Remote=%s", count(), sockObj
				.getLocalAddress().toString(), sockObj.getRemoteAddress().toString()));

		onSubThread(sockObj); //接続単位の処理
		sockObj.close();

		logger.set(LogKind.DETAIL, sockObj, 9000003, String.format("count=%d Local=%s Remote=%s", count(), sockObj
				.getLocalAddress().toString(), sockObj.getRemoteAddress().toString()));

<<<<<<< HEAD
=======

		//_subThreadの中でSockObjは破棄する（ただしUDPの場合は、クローンなのでClose()してもsocketは破棄されない）
		logger.set(LogKind.DETAIL, sockObj, 9000002, String.format("count=%d Local=%s Remote=%s", count(), sockObj
				.getLocalAddress().toString(), sockObj.getRemoteAddress().toString()));

		onSubThread(sockObj); //接続単位の処理
		sockObj.close();

		logger.set(LogKind.DETAIL, sockObj, 9000003, String.format("count=%d Local=%s Remote=%s", count(), sockObj
				.getLocalAddress().toString(), sockObj.getRemoteAddress().toString()));

>>>>>>> work
=======
>>>>>>> work
	}

	//RemoteServerでのみ使用される
	//public virtual void Append(OneLog oneLog){}

	/**
	 * 1行読込待機
	 * 
	 * @param sockTcp
	 * @return
	 */
	public final Cmd waitLine(SockTcp sockTcp) {
		Timeout tout = new Timeout(timeout * 1000);

		while (isLife()) {
			Cmd cmd = recvCmd(sockTcp);
			if (cmd == null) {
				return null;
			}
			if (!(cmd.getCmdStr().equals(""))) {
				return cmd;
			}
			if (tout.isFinish()) {
				return null;
			}
			Util.sleep(100);
		}
		return null;
	}

	//TODO RecvCmdのパラメータ形式を変更するが、これは、後ほど、Web,Ftp,SmtpのServerで使用されているため影響がでる予定
	/**
	 * コマンド取得<br>
	 * コネクション切断などエラーが発生した時はnullが返される<br>
	 * 
	 * @param sockTcp
	 * @return Cmd コマンド
	 */
	protected final Cmd recvCmd(SockTcp sockTcp) {
		if (sockTcp.getSockState() != SockState.CONNECT) { //切断されている
			return null;
		}
		byte[] recvbuf = sockTcp.lineRecv(timeout, this);
<<<<<<< HEAD
<<<<<<< HEAD
=======
		//切断された場合
>>>>>>> work
		if (recvbuf == null) {
			return null;
		}

		//受信待機中の場合
		if (recvbuf.length == 0) {
			return new Cmd("", "", "");
		}
		
=======
		//切断された場合
		if (recvbuf == null) {
			return null;
		}

		//受信待機中の場合
		if (recvbuf.length == 0) {
			return new Cmd("", "", "");
		}

>>>>>>> work
		//CRLFの排除
		recvbuf = Inet.trimCrlf(recvbuf);

		String str = new String(recvbuf, Charset.forName("Shift-JIS"));
		if (str.equals("")) {
			return new Cmd("", "", "");
		}
		//受信行をコマンドとパラメータに分解する（コマンドとパラメータは１つ以上のスペースで区切られている）
		String cmdStr = null;
		String paramStr = null;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == ' ') {
				if (cmdStr == null) {
					cmdStr = str.substring(0, i);
				}
			}
			if (cmdStr == null || str.charAt(i) == ' ') {
				continue;
			}
			paramStr = str.substring(i);
			break;
		}
		if (cmdStr == null) { //パラメータ区切りが見つからなかった場合
			cmdStr = str; //全部コマンド
		}
		return new Cmd(str, cmdStr, paramStr);
	}

	//未実装
	public final void append(OneLog oneLog) {
		Util.runtimeException("OneServer.append(OneLog) 未実装");
	}
}
