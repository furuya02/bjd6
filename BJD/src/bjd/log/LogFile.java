package bjd.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import bjd.util.FileSearch;
import bjd.util.IDispose;
import bjd.util.Util;

public final class LogFile implements IDispose {

	private Object lock = new Object(); //排他制御用オブジェクト
	private String saveDirectory;
	private int normalLogKind;
	private int secureLogKind;
	private int saveDays;

	private OneLogFile normalLog = null; // 通常ログ
	private OneLogFile secureLog = null; // セキュアログ
	private Calendar dt = null; // インスタンス生成時に初期化し、日付が変化したかどうかの確認に使用する
	private Calendar lastDelete = null; // new DateTime(0);
	private Timer timer = null;

	/**
	 * 保存ディレクトリとファイル名の種類を指定する。例外が発生した場合は、事後のappend()等は、すべて失敗する
	 * @param saveDirectory 保存ディレクトリ
	 * @param normalFileKind　通常ログのファイルル名の種類
	 * @param secureFileKind　セキュリティログのファイルル名の種類
	 * @param saveDays ログの自動削除で残す日数　0を指定した場合、自動削除は行わない
	 * @throws IOException　ファイル保存に失敗した場合、例外が発生する
	 */
	public LogFile(String saveDirectory, int normalLogKind, int secureLogKind, int saveDays) throws IOException {
		this.saveDirectory = saveDirectory;
		this.normalLogKind = normalLogKind;
		this.secureLogKind = secureLogKind;
		this.saveDays = saveDays;

		File file = new File(saveDirectory);
		if (!file.exists()) {
			throw new IOException(String.format("directory not found. \"%s\"", saveDirectory));
		}
		//ｓａｖｅＤｉｒｅｃｔｏｒｙで例外が発生した場合は、タイマーは起動しない

		//logOpenで例外が発生した場合も、タイマーは起動しない
		logOpen();

		// 5分に１回のインターバルタイマ
		timer = new Timer();
		timer.schedule(new MyTimer(), 0, 1000 * 60 * 5);
	}

	/**
	 * ログファイルへの追加
	 * @param oneLog 保存するログ（１行）
	 * @return 失敗した場合はfalseが返される
	 */
	public boolean append(OneLog oneLog) {
		//コンストラクタで初期化に失敗している場合、falseを返す
		if (timer == null) {
			return false;
		}
		synchronized (lock) {
			try {
				// セキュリティログは、表示制限に関係なく書き込む
				if (secureLog != null && oneLog.isSecure()) {
					secureLog.set(oneLog.toString());
				}
				// 通常ログの場合
				if (normalLog != null) {
					// ルール適用除外　もしくは　表示対象になっている場合
					normalLog.set(oneLog.toString());
				}
			} catch (IOException e) {
				return false;
			}
			return true;
		}
	}

	private void logOpen() throws IOException {
		// ログファイルオープン
		dt = Calendar.getInstance(); // 現在時間で初期化される
		String fileName = "";

		switch (normalLogKind) {
		case 0:// bjd.yyyy.mm.dd.log
			fileName = String.format("%s\\bjd.%04d.%02d.%02d.log", saveDirectory, dt.get(Calendar.YEAR),
					(dt.get(Calendar.MONTH) + 1), dt.get(Calendar.DATE));
			break;
		case 1:// bjd.yyyy.mm.log
			fileName = String.format("%s\\bjd.%04d.%02d.log", saveDirectory, dt.get(Calendar.YEAR),
					(dt.get(Calendar.MONTH) + 1));
			break;
		case 2:// BlackJumboDog.Log
			fileName = String.format("%s\\BlackJumboDog.Log", saveDirectory);
			break;
		default:
			Util.runtimeException(String.format("nomalLogKind=%d", normalLogKind));
		}
		try {
			normalLog = new OneLogFile(fileName);
		} catch (IOException e) {
			normalLog = null;
			throw new IOException(String.format("file open error. \"%s\"", fileName));
		}

		switch (secureLogKind) {
		case 0:// secure.yyyy.mm.dd.log
			fileName = String.format("%s\\secure.%04d.%02d.%02d.log", saveDirectory, dt.get(Calendar.YEAR),
					(dt.get(Calendar.MONTH) + 1), dt.get(Calendar.DATE));
			break;
		case 1:// secure.yyyy.mm.log
			fileName = String.format("%s\\secure.%04d.%02d.log", saveDirectory, dt.get(Calendar.YEAR),
					(dt.get(Calendar.MONTH) + 1));
			break;
		case 2:// secure.Log
			fileName = String.format("%s\\secure.Log", saveDirectory);
			break;
		default:
			Util.runtimeException(String.format("secureLogKind=%d", secureLogKind));
		}
		try {
			secureLog = new OneLogFile(fileName);
		} catch (IOException e) {
			secureLog = null;
			throw new IOException(String.format("file open error. \"%s\"", fileName));
		}
	}

	/**
	 * オープンしているログファイルを全てクローズする
	 */
	private void logClose() {
		if (normalLog != null) {
			normalLog.dispose();
			normalLog = null;
		}
		if (secureLog != null) {
			secureLog.dispose();
			secureLog = null;
		}
	}

	/**
	 * 過去ログの自動削除
	 */
	private void logDelete() {

		// 0を指定した場合、自動削除を処理しない
		if (saveDays == 0) {
			return;
		}

		logClose();

		// ログディレクトリの検索
		FileSearch fs = new FileSearch(saveDirectory);
		// 一定
		ArrayList<File> files = Util.merge(fs.listFiles("BlackJumboDog.Log"), fs.listFiles("secure.Log"));
		for (File f : files) {
			tail(f.getPath(), saveDays, Calendar.getInstance()); // saveDays日分以外を削除
		}
		// 日ごと
		files = Util.merge(fs.listFiles("bjd.????.??.??.Log"),
				fs.listFiles("secure.????.??.??.Log"));
		for (File f : files) {
			String[] tmp = f.getName().split("\\.");
			if (tmp.length == 5) {
				try {
					int year = Integer.valueOf(tmp[1]);
					int month = Integer.valueOf(tmp[2]);
					int day = Integer.valueOf(tmp[3]);

					deleteLogFile(year, month, day, saveDays, f.getPath());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		// 月ごと
		files = Util.merge(fs.listFiles("bjd.????.??.Log"),
				fs.listFiles("secure.????.??.Log"));
		for (File f : files) {
			String[] tmp = f.getName().split("\\.");
			if (tmp.length == 4) {
				try {
					int year = Integer.valueOf(tmp[1]);
					int month = Integer.valueOf(tmp[2]);
					int day = 30;
					deleteLogFile(year, month, day, saveDays, f.getPath());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private void tail(String fileName, int saveDays, Calendar now) {
		Calendar targetDt = Calendar.getInstance(); //インスタンスの生成（初期化時間は関係ない）
		DateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

		File file = new File(fileName);
		try {
			ArrayList<String> lines = new ArrayList<>();
			if (file.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(file));
				boolean isNeed = false;
				while (true) {
					String str = br.readLine();
					if (str == null) {
						break;
					}
					if (isNeed) {
						lines.add(str);
					} else {
						String[] tmp = str.split("\t");
						if (tmp.length > 1) {
							targetDt.setTime((Date) f.parse(tmp[0]));
							targetDt.add(Calendar.DAY_OF_MONTH, saveDays);
							if (now.getTimeInMillis() < targetDt.getTimeInMillis()) {
								isNeed = true;
								lines.add(str);
							}
						}
					}
				}
				br.close();
			}
			Util.textFileSave(file, lines);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 指定日以前のログファイルを削除する
	 * @param year
	 * @param month
	 * @param day
	 * @param saveDays
	 * @param fullName 
	 */
	private void deleteLogFile(int year, int month, int day, int saveDays, String fullName) {

		Calendar targetDt = Calendar.getInstance(); // 現在時間で初期化される
		targetDt.set(year, month, day);
		targetDt.add(Calendar.DAY_OF_MONTH, saveDays);

		if (dt.getTimeInMillis() > targetDt.getTimeInMillis()) {
			File file = new File(fullName);
			file.delete();
		}
	}

	private class MyTimer extends TimerTask {
		@Override
		public void run() {

			Calendar now = Calendar.getInstance();

			if (lastDelete == null) {
				return;
			}
			// 日付が変わっている場合は、ファイルを初期化する
			if (lastDelete.getTime().getTime() != 0 && lastDelete.get(Calendar.DATE) == now.get(Calendar.DATE)) {
				return;
			}

			synchronized (lock) {

				logClose(); // クローズ
				logDelete(); // 過去ログの自動削除
				try {
					logOpen();
				} catch (IOException e) {
					Util.runtimeException("ここでログオープンに失敗するのは、設計に問題がある？");
				} // オープン
				lastDelete = now;
			}
		}
	}

	/**
	 * 終了処理<br>
	 * 過去ログの自動削除が行われる
	 */
	public void dispose() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		logClose();
		logDelete(); // 過去ログの自動削除
	}
}
