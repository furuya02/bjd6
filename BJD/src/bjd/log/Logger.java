package bjd.log;

import java.util.Calendar;

import bjd.Kernel;
import bjd.server.OneServer;
import bjd.sock.SockObj;

/**
 * ログ出力用のクラス<br>
 * ファイルとディスプレイの両方を統括する<br>
 * テスト用に、Logger.create()でログ出力を処理を一切行わないインスタンスが作成される<br>
 * @author SIN
 */
public class Logger {
	private Kernel kernel;
	private LogLimit logLimit;
	private LogFile logFile;
	private LogView logView;
	private boolean isJp;
	private String nameTag;
	private boolean useDetailsLog;
	private boolean useLimitString;
	private ILogger logger;

	/**
	 * コンストラクタ<br>
	 * kernelの中でCreateLogger()を定義して使用する<br>
	 * @param logLimit 表示制限
	 * @param logFile ファイルへの保存クラス
	 * @param logView　ビューへの表示クラス
	 * @param isJp 日本語表示
	 * @param nameTag 名前
	 * @param useDetailsLog 詳細ログを表示するかどうか
	 * @param useLimitString 表示制限の結果をファイル保存に適用するかどうか
	 * @param logger ILoggerクラス
	 */
	public Logger(Kernel kernel, LogLimit logLimit, LogFile logFile, LogView logView, boolean isJp, String nameTag,
			boolean useDetailsLog, boolean useLimitString, ILogger logger) {
		this.kernel = kernel;
		this.logLimit = logLimit;
		this.logFile = logFile;
		this.logView = logView;
		this.isJp = isJp;
		this.nameTag = nameTag;
		this.useDetailsLog = useDetailsLog;
		this.useLimitString = useLimitString;
		this.logger = logger;
	}

	/**
	 * テスト用
	 */
	public Logger() {
		this.logLimit = null;
		this.logFile = null;
		this.logView = null;
		this.isJp = true;
		this.nameTag = "";
		this.useDetailsLog = false;
		this.useLimitString = false;
		this.logger = null;
	}

	/**
	 * ログ出力<br>
	 * Override可能（テストで使用）<br>
	 * @param logKind
	 * @param sockBase
	 * @param messageNo
	 * @param detailInfomation
	 */
	public void set(LogKind logKind, SockObj sockBase, int messageNo, String detailInfomation) {
		//デバッグ等でkernelが初期化されていない場合、処理なし
		if (logFile == null && logView == null) {
			return;
		}
		//詳細ログが対象外の場合、処理なし
		if (logKind == LogKind.DETAIL) {
			if (!useDetailsLog) {
				return;
			}
		}
		long threadId = Thread.currentThread().getId(); //TODO DEBUG GetCurrentThreadId();
		String message = isJp ? "定義されていません" : "Message is not defined";
		if (messageNo < 9000000) {
			if (logger != null) {
				message = logger.getMsg(messageNo); //デリゲートを使用した継承によるメッセージ取得
			}
		} else { //(9000000以上)共通番号の場合の処理
			switch (messageNo) {
				case 9000000:
					message = isJp ? "サーバ開始" : "Server started it";
					break;
				case 9000001:
					message = isJp ? "サーバ停止" : "Server stopped";
					break;
				case 9000002:
					message = "_subThread() started.";
					break;
				case 9000003:
					message = "_subThread() stopped.";
					break;
				case 9000004:
					message = isJp ? "同時接続数を超えたのでリクエストをキャンセルします"
							: "Because the number of connection exceeded it at the same time, the request was canceled.";
					break;
				case 9000005:
					message = isJp ? "受信文字列が長すぎます（不正なリクエストの可能性があるため切断しました)"
							: "Reception character string is too long (cut off so that there was possibility of an unjust request in it)";
					break;
				case 9000006:
					message = isJp ? "このポートは、既に他のプログラムが使用しているため使用できません" : "Cannot use this port so that other programs already use it";
					break;
				case 9000007:
					message = isJp ? "callBack関数が指定されていません[UDP]" : "It is not appointed in callback function [UDP]";
					break;
				case 9000008:
					message = isJp ? "プラグインをインストールしました" : "setup initialize plugin";
					break;
				case 9000009:
					message = isJp ? "Socket.Bind()でエラーが発生しました。[TCP]" : "An error occurred in Socket.Bind() [TCP]";
					break;
				case 9000010:
					message = isJp ? "Socket.Listen()でエラーが発生しました。[TCP]" : "An error occurred in Socket..Listen() [TCP]";
					break;
				case 9000011:
					message = "tcpQueue().Dequeue()=null";
					break;
				case 9000012:
					message = "tcpQueue().Dequeue() SocektObjState != SOCKET_OBJ_STATE.CONNECT break";
					break;
				case 9000013:
					message = "tcpQueue().Dequeue()";
					break;
				//			case 9000014:
				//				message = "SendBinaryFile(string fileName) socket.Send()";
				//				break;
				//			case 9000015:
				//				message = "SendBinaryFile(string fileName,long rangeFrom,long rangeTo) socket.Send()";
				//				break;
				case 9000016:
					message = isJp ? "このアドレスからの接続は許可されていません(ACL)" : "Connection from this address is not admitted.(ACL)";
					break;
				case 9000017:
					message = isJp ? "このアドレスからの接続は許可されていません(ACL)" : "Connection from this address is not admitted.(ACL)";
					break;
				case 9000018:
					message = isJp ? "この利用者のアクセスは許可されていません(ACL)" : "Access of this user is not admitted (ACL)";
					break;
				case 9000019:
					message = isJp ? "アイドルタイムアウト" : "Timeout of an idle";
					break;
				case 9000020:
					message = isJp ? "送信に失敗しました" : "Transmission of a message failure";
					break;
				case 9000021:
					message = isJp ? "ThreadBase::loop()で例外が発生しました" : "An exception occurred in ThreadBase::Loop()";
					break;
				case 9000022:
					message = isJp ? "ウインドウ情報保存ファイルにIOエラーが発生しました"
							: "An IO error occurred in a window information save file";
					break;
				case 9000023:
					message = isJp ? "証明書の読み込みに失敗しました" : "Reading of a certificate made a blunder";
					break;
				//case 9000024: message = isJp ? "SSLネゴシエーションに失敗しました" : "SSL connection procedure makes a blunder"; break;
				//case 9000025: message = isJp ? "ファイル（秘密鍵）が見つかりません" : "Private key is not found"; break;
				case 9000026:
					message = isJp ? "ファイル（証明書）が見つかりません" : "A certificate is not found";
					break;
				//case 9000027: message = isJp ? "OpenSSLのライブラリ(ssleay32.dll,libeay32.dll)が見つかりません" : "OpenSSL library (ssleay32.dll,libeay32.dll) is not found"; break;
				case 9000028:
					message = isJp ? "SSLの初期化に失敗しています" : "Initialization of SSL made a blunder";
					break;
				case 9000029:
					message = isJp ? "指定された作業ディレクトリが存在しません" : "A work directory is not found";
					break;
				case 9000030:
					message = isJp ? "起動するサーバが見つかりません" : "A starting server is not found";
					break;
				case 9000031:
					message = isJp ? "ログファイルの初期化に失敗しました" : "Failed in initialization of logfile";
					break;
				case 9000032:
					message = isJp ? "ログ保存場所" : "a save place of LogFile";
					break;
				case 9000033:
					message = isJp ? "ファイル保存時にエラーが発生しました" : "An error occurred in a File save";
					break;
				case 9000034:
					message = isJp ? "ACL指定に問題があります" : "ACL configuration failure";
					break;
				case 9000035:
					message = isJp ? "Socket()でエラーが発生しました。[TCP]" : "An error occurred in Socket() [TCP]";
					break;
				case 9000036:
					message = isJp ? "Socket()でエラーが発生しました。[UDP]" : "An error occurred in Socket() [UDP]";
					break;
				case 9000037:
					message = isJp ? "_subThread()で例外が発生しました" : "An exception occurred in _subThread()";
					break;
				case 9000038:
					message = isJp ? "【例外】" : "[Exception]";
					break;
				case 9000039:
					message = isJp ? "【STDOUT】" : "[STDOUT]";
					break;
				case 9000040:
					message = isJp ? "拡張SMTP適用範囲の指定に問題があります" : "ESMTP range configuration failure";
					break;
				case 9000041:
					message = isJp ? "disp2()で例外が発生しました" : "An exception occurred in disp2()";
					break;
				case 9000042:
					message = isJp ? "初期化に失敗しているためサーバを開始できません" : "Can't start a server in order to fail in initialization";
					break;
				case 9000043:
					message = isJp ? "クライアント側が切断されました" : "The client side was cut off";
					break;
				case 9000044:
					message = isJp ? "サーバ側が切断されました" : "The server side was cut off";
					break;
				case 9000045:
					message = isJp ? "「オプション(O)-ログ表示(L)-基本設定-ログの保存場所」が指定されていません" : "\"log save place\" is not appointed";
					break;
				case 9000046:
					message = isJp ? "socket.send()でエラーが発生しました" : "socket.send()";
					break;
				case 9000047:
					message = isJp ? "ユーザ名が無効です" : "A user name is null and void";
					break;
				case 9000048:
					message = isJp ? "ThreadBase::Loop()で例外が発生しました" : "An exception occurred in ThreadBase::Loop()";
					break;
				case 9000049:
					message = isJp ? "【例外】" : "[Exception]";
					break;
				case 9000050:
					message = isJp ? "ファイルにアクセスできませんでした" : "Can't open a file";
					break;
				case 9000051:
					message = isJp ? "インスタンスの生成に失敗しました" : "Can't create instance";
					break;
				case 9000052:
					message = isJp ? "名前解決に失敗しました" : "Non-existent domain";
					break;
				case 9000053:
					message = isJp ? "【例外】SockObj.Resolve()" : "[Exception] SockObj.Resolve()";
					break;
				case 9000054:
					message = isJp ? "Apache Killerによる攻撃の可能性があります"
							: "There is possibility of attack by Apache Killer in it";
					break;
				case 9000055:
					message = isJp ? "【自動拒否】「ACL」の禁止する利用者（アドレス）に追加しました" : "Add it to a deny list automatically";
					break;
				case 9000056:
					message = isJp ? "不正アクセスを検出しましたが、ACL「拒否」リストは追加されませんでした"
							: "I detected possibility of Attack, but the ACL [Deny] list was not added";
					break;
				case 9000057:
					message = isJp ? "【例外】" : "[Exception]";
					break;
				case 9000058:
					message = isJp ? "メールの送信に失敗しました" : "Failed in the transmission of a message of an email";
					break;
				case 9000059:
					message = isJp ? "メールの保存に失敗しました" : "Failed in a save of an email";
					break;
				case 9000060:
					message = isJp ? "【例外】" : "[Exception]";
					break;
				//case 9000061:
				//	message = isJp ? "ファイルの作成に失敗しました" : "Failed in making of a file";
				//	break;
				default:
					break;
			}
		}
		Calendar calendar = Calendar.getInstance(); //現在時間
		String remoteHostname = (sockBase == null) ? "-" : sockBase.getRemoteHostname();
		OneLog oneLog = new OneLog(calendar, logKind, nameTag, threadId, remoteHostname, messageNo, message,
				detailInfomation);

		// 表示制限にヒットするかどうかの確認
		boolean isDisplay = true;
		if (!oneLog.isSecure()) { //セキュリティログは表示制限の対象外
			if (logLimit != null) {
				isDisplay = logLimit.isDisplay(oneLog.toString());
			}
		}
		if (logView != null && isDisplay) {
			//isDisplayの結果に従う
			logView.append(oneLog);
		}

		//リモートクライアントへのログ送信
		if (kernel != null && kernel.getRemoteConnect() != null && kernel.getListServer() != null) {
			//クライアントから接続されている場合
			OneServer sv = kernel.getListServer().get("Remote");
			if (sv != null) {
				sv.append(oneLog);
			}
		}

		if (logFile != null) {
			if (useLimitString) { //表示制限が有効な場合
				if (isDisplay) { //isDisplayの結果に従う
					logFile.append(oneLog);
				}
			} else { //表示制限が無効な場合は、すべて保存される
				logFile.append(oneLog);
			}
		}
	}
}
