package bjd.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import bjd.ValidObj;
import bjd.ValidObjException;

/**
 * ログ１行を表現するクラス<br>
 * ValidObjを継承
 * 
 * @author SIN
 * 
 */
public final class OneLog extends ValidObj {
	private Calendar calendar;
	private LogKind logKind;
	private String nameTag;
	private long threadId;
	private String remoteHostname;
	private int messageNo;
	private String message;
	private String detailInfomation;

	/**
	 * 日付の取得(文字列)
	 * 
	 * @return yyyy/mm/dd hh:mm:ss
	 */
	public String getCalendar() {
		checkInitialise(); // 他のgetterは、これとセットで使用されるため、チェックはここだけにする
		return String.format("%04d/%02d/%02d %02d:%02d:%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE), calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
	}

	/**
	 * ログ種類の取得(文字列)
	 * 
	 * @return LogKind.toString()
	 */
	public String getLogKind() {
		return logKind.toString();
	}

	/**
	 * 詳細の取得（文字列）
	 * 
	 * @return 詳細情報
	 */
	public String getDetailInfomation() {
		return detailInfomation;
	}

	/**
	 * 名前タグの取得（文字列）
	 * 
	 * @return 名前タグ
	 */
	public String getNameTag() {
		return nameTag;
	}

	/**
	 * スレッドIDの取得（文字列）
	 * 
	 * @return スレッドID
	 */
	public String getThreadId() {
		return String.valueOf(threadId);
	}

	/**
	 * リモートホスト名の取得（文字列）
	 * 
	 * @return リモートホスト名
	 */
	public String getRemoteHostname() {
		return remoteHostname;
	}

	/**
	 * 日付の設定
	 * 
	 * @param calendar
	 */
	// public void setCalendar(Calendar calendar) {
	// this.calendar = calendar;
	// }

	/**
	 * メッセージ番号の取得
	 * 
	 * @return 0000001 (７桁)
	 */
	public String getMessageNo() {
		return String.format("%7d", messageNo);
	}

	/**
	 * メッセージの取得（文字列）
	 * 
	 * @return メッセージ
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param calendar　日付
	 * @param logKind　ログの種類
	 * @param nameTag　名前タグ
	 * @param threadId　スレッドID
	 * @param remoteHostname　リモートアドレス
	 * @param messageNo　メッセージ番号
	 * @param message　メッセージ
	 * @param detailInfomation　詳細情報
	 */
	public OneLog(Calendar calendar, LogKind logKind, String nameTag, long threadId, String remoteHostname, int messageNo, String message, String detailInfomation) {
		this.calendar = calendar;
		this.logKind = logKind;
		this.nameTag = nameTag;
		this.threadId = threadId;
		this.remoteHostname = remoteHostname;
		this.messageNo = messageNo;
		this.message = message;
		this.detailInfomation = detailInfomation;
	}

	/**
	 * コンストラクタ<br>
	 * １行の文字列(\t区切り)で指定される<br>
	 * 
	 * @param str
	 * @throws ValidObjException 初期化失敗
	 */
	public OneLog(String str) throws ValidObjException {
		String[] tmp = str.split("\t");
		if (tmp.length != 8) {
			throwException(str); // 初期化失敗
		}
		try {
			DateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = f.parse(tmp[0]);
			calendar = Calendar.getInstance();
			calendar.setTime(date);
			logKind = LogKind.valueOf(tmp[1]);
			threadId = Long.valueOf(tmp[2]);
			nameTag = tmp[3];
			remoteHostname = tmp[4];
			messageNo = Integer.valueOf(tmp[5]);
			message = tmp[6];
			detailInfomation = tmp[7];
		} catch (Exception ex) {
			throwException(str); // 初期化失敗
		}
	}

	/**
	 * 初期化
	 */
	@Override
	protected void init() {
		calendar = Calendar.getInstance();
		calendar.setTime(new Date(0)); // 1970.1.1で初期化
		logKind = LogKind.NORMAL;
		threadId = 0;
		nameTag = "UNKNOWN";
		remoteHostname = "";
		messageNo = 0;
		message = "";
		detailInfomation = "";
	}

	/**
	 * 文字列化<br>
	 * \t区切りで出力される<br>
	 */
	public String toString() {
		checkInitialise();
		return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s", getCalendar(), getLogKind(), getThreadId(), getNameTag(), getRemoteHostname(), getMessageNo(), getMessage(), getDetailInfomation());
	}

	/**
	 * セキュリティログかどうかの確認
	 * 
	 * @return セキュリティログの場合 true
	 */
	public boolean isSecure() {
		checkInitialise();
		if (logKind == LogKind.SECURE) {
			return true;
		}
		return false;
	}

}
