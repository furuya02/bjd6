package bjd.log;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import bjd.ctrl.ListView;
import bjd.util.IDispose;
import bjd.util.Msg;
import bjd.util.MsgKind;

public final class LogView implements IDispose {

	private ListView listView = null;
	private Timer timer = null;
	private ArrayList<OneLog> ar = new ArrayList<OneLog>();
	private Object lock = new Object();

	public LogView(ListView listView) {
		if (listView == null) {
			return;
		}
		this.listView = listView;
		//タイマー（表示）イベント処理
		timer = new Timer();
		timer.schedule(new MyTimer(), 0, 100);
	}

	//タイマー(表示)イベント
	class MyTimer extends TimerTask {
		public void run() {
			if (listView == null) {
				return;
			}
			if (ar.size() == 0) {
				return;
			}
			timer.cancel(); //一時停止
			synchronized (lock) {
				//TODO 適当な方法が分からないのでコメントアウト	listView.BeginUpdate();

				//一回のイベントで処理する最大数は100行まで
				ArrayList<OneLog> list = new ArrayList<>();
				for (int i = 0; i < 300 && 0 < ar.size(); i++) {
					list.add(ar.get(0));
					ar.remove(0);
				}
				disp(list);

				//TODO 適当な方法が分からないのでコメントアウト	listView.EndUpdate();

			}
			//最終行が見えるようにスクロールする
			listView.displayLastLine();
			timer.schedule(new MyTimer(), 0, 100); //再開
		}

	}

	public void dispose() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (listView != null) {
			listView.dispose();
		}

	}

	public void setFont(Font font) {
		//TODO kernel経由で呼び出されるのであれば　kernel.viewから呼んだ方がいいのでは？
		if (listView != null) {
			listView.setFont(font);
		}
	}

	//ログビューへの表示(リモートからも使用される)
	public void append(OneLog oneLog) {
		if (listView == null) {
			return;
		}
		synchronized (lock) {
			ar.add(oneLog);
		}
	}

	//選択されたログをクリップボードにコピーする
	public void setClipboard() {
		if (listView == null) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		int colMax = listView.getColumnCount();
		for (int c = 0; c < colMax; c++) {
			sb.append(listView.getColumnText(c));
			sb.append("\t");
		}
		sb.append("\r\n");
		int [] rows = listView.getSelectedRows();
		for (int i = 0; i < rows.length; i++) {
			for (int c = 0; c < colMax; c++) {
				sb.append(listView.getText(rows[i], c));
				sb.append("\t");
			}
			sb.append("\r\n");
		}
		//Clipboard.SetText(sb.toString());
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection selection = new StringSelection(sb.toString());
		clipboard.setContents(selection, null);
	}

	//表示ログをクリア
	public void clear() {
		if (listView == null) {
			return;
		}
		listView.itemClear();
	}

	//このメソッドはタイマースレッドからのみ使用される
	void disp(ArrayList<OneLog> list) {
		try {
			for (OneLog oneLog : list) {
				if (listView == null) {
					break;
				}
				//リストビューへの出力       
				String [] s = new String[8];
				s[0] = oneLog.getCalendar();
				s[1] = oneLog.getLogKind();
				s[2] = oneLog.getThreadId();
				s[3] = oneLog.getNameTag();
				s[4] = oneLog.getRemoteHostname();
				s[5] = oneLog.getMessageNo();
				s[6] = oneLog.getMessage();
				s[7] = oneLog.getDetailInfomation();
				listView.itemAdd(s);
				
			}
		} catch (Exception ex) {
			StringBuilder sb = new StringBuilder();
			sb.append(ex.getMessage() + "\r\n");
			for (OneLog oneLog : list) {
				sb.append(String.format("%s %s\r\n", oneLog.getMessageNo(), oneLog.getMessage()));
			}
			Msg.show(MsgKind.ERROR, sb.toString());
		}
	}

}
