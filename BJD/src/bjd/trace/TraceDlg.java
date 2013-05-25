package bjd.trace;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.filechooser.FileNameExtensionFilter;

import bjd.Dlg;
import bjd.Kernel;
import bjd.RunMode;
import bjd.ctrl.ListView;
import bjd.ctrl.StatusBar;
import bjd.log.LogKind;
import bjd.log.Logger;
import bjd.menu.IMenu;
import bjd.net.Ip;
import bjd.util.IDisposable;
import bjd.util.Msg;
import bjd.util.MsgKind;

@SuppressWarnings("serial")
public final class TraceDlg extends Dlg implements IDisposable, IMenu {

	private Object lock = new Object(); //排他制御用オブジェクト

	private static int width = 600;
	private static int height = 400;
	private Kernel kernel;
	//Timer _timer;
	private ArrayList<OneTrace> ar = new ArrayList<OneTrace>();
	private Logger logger;
	private Menu menu;
	private ListView listViewTrace;

	private ArrayList<String> colJp = new ArrayList<String>();
	private ArrayList<String> colEn = new ArrayList<String>();

	private Timer timer = null;

	public TraceDlg(Kernel kernel, JFrame frame) {
		super(frame, width, height);
		this.kernel = kernel;

		listViewTrace = new ListView("listViewTrace");
		StatusBar bar = new StatusBar();

		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		this.getContentPane().add(listViewTrace);
		this.getContentPane().add(bar, BorderLayout.PAGE_END);

		menu = new Menu(this, menuBar);

		colJp.add("送受");
		colJp.add("スレッドID");
		colJp.add("アドレス");
		colJp.add("データ");
		colEn.add("Direction");
		colEn.add("ThreadID");
		colEn.add("Address");
		colEn.add("Data");

		if (listViewTrace != null) {
			for (int i = 0; i < 4; i++) {
				listViewTrace.addColumn("");
			}

			listViewTrace.setColWidth(0, 80);
			listViewTrace.setColWidth(1, 80);
			listViewTrace.setColWidth(2, 100);
			listViewTrace.setColWidth(3, 500);
		}

		//オーナー描画
		//foreach (var t in _colJp){
		//    listViewTrace.Columns.Add(t);
		//}
		//listViewTrace.Columns[2].Width = 100;
		//listViewTrace.Columns[3].Width = 500;

		// 100msに１回のインターバルタイマ
		timer = new Timer();
		timer.schedule(new MyTimer(), 0, 100);

		//kernel.WindowSize.Read(this);//ウインドサイズの復元
		//kernel.WindowSize.Read(listViewTrace);//カラム幅の復元

	}

	@Override
	public void dispose() {
		//kernel.WindowSize.Save(this);//ウインドサイズの保存
		//kernel.WindowSize.Save(listViewTrace);//カラム幅の保存

		super.dispose();

	}

	public void open() {
		this.setVisible(true);

		setTitle((kernel.isJp()) ? "トレース表示" : "Trace Dialog");
		menu.initialize(kernel.isJp());

		//		PopupMenuCopy.Text = (kernel.isJp()) ? "コピー(&C)" : "&Copy";
		//		PopupMenuClear.Text = (kernel.isJp()) ? "クリア(&L)" : "C&lear";
		//		PopupMenuClose.Text = (kernel.isJp()) ? "閉じる(&C)" : "&Close";
		//		PopupMenuClose.Text = (kernel.isJp()) ? "名前を付けて保存(&S)" : "S&ave";
		//
		for (int i = 0; i < 3; i++) {
			listViewTrace.setColumnText(i, kernel.isJp() ? colJp.get(i) : colEn.get(i));
		}
		//		Show();
		//		Focus();

		if (kernel.getRunMode() == RunMode.Remote) {
			//トレース表示がオープンしたことをサーバーに送信する
			//kernel.getRemoteClient.VisibleTrace2(true);
		}
	}

	@Override
	protected boolean onOk() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	//トレースの追加(SockObj内から使用される)
	public void addTrace(TraceKind traceKind, String str, Ip ip) {
		if (!isVisible()) {
			return;
		}
		Thread th = Thread.currentThread();
		int threadId = (int) th.getId();

		//トレースの追加（内部共通処理）
		synchronized (lock) {
			ar.add(new OneTrace(traceKind, str, threadId, ip));
		}
	}

	//トレースの追加（リモートサーバスレッドから使用される）
	public void addTrace(String buffer) {
		String[] tmp = buffer.split("\b", 4);
		if (tmp.length < 4) {
			return;
		}
		try {
			TraceKind traceKind = TraceKind.Send;
			if (TraceKind.Recv.toString().equals(tmp[0])) {
				traceKind = TraceKind.Recv;
			}
			int threadId = Integer.valueOf(tmp[1]);
			Ip ip = new Ip(tmp[2]);
			String str = tmp[3];
			//トレースの追加（内部共通処理）
			synchronized (lock) {
				ar.add(new OneTrace(traceKind, str, threadId, ip));
			}
		} catch (Exception ex) {

		}
	}

	@Override
	public void menuOnClick(String cmd) {

		if (cmd.indexOf("File_Close") == 0) {
			this.setVisible(false);
		} else if (cmd.indexOf("Edit_Clear") == 0) {
			listViewTrace.itemClear();
		} else if (cmd.indexOf("Edit_Copy") == 0) {
			String str = getText();
			if (str.length() > 0) {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				StringSelection selection = new StringSelection(str);
				clipboard.setContents(selection, null);
			}
		} else if (cmd.indexOf("Edit_SaveAs") == 0) {
			JFileChooser dlg = new JFileChooser();
			dlg.setSelectedFile(new File(".", "trace.txt"));
			FileNameExtensionFilter filter = new FileNameExtensionFilter("TraceFile(*.txt)", "*.txt");
			dlg.setFileFilter(filter);

			int selected = dlg.showSaveDialog(this);
			if (selected == JFileChooser.APPROVE_OPTION) {

				try {
					File file = dlg.getSelectedFile();

					if (file.exists()) {
						if (!file.isFile() || !file.canWrite()) {
							Msg.show(MsgKind.ERROR, "can't write file.");
							return;
						}
					}
					FileWriter filewriter = new FileWriter(file);
					filewriter.write(getText());
					filewriter.close();
				} catch (IOException ex) {
					if (logger == null) {
						logger = kernel.createLogger("TraceDlg", false, null);
					}
					logger.set(LogKind.ERROR, null, 9000041, ex.getMessage());
				}
			}

		}
	}

	//内容をテキスト形式で取得する
	String getText() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < listViewTrace.getRowCount(); i++) {
			for (int c = 0; c < colJp.size(); c++) {
				sb.append(listViewTrace.getText(i, c));
				sb.append("\t");
			}
			sb.append("\r\n");
		}
		return sb.toString();
	}

	//トレースの追加（内部共通処理）
	//タイマースレッドからのみ使用される
	void disp2(ArrayList<OneTrace> list) {
		if (list.size() <= 0) {
			return;
		}
		try {
			for (OneTrace oneTrace : list) {
				//リストビューへの出力                    
				String[] tmp = new String[4];
				tmp[0] = String.format("%s", oneTrace.getTraceKind());
				tmp[1] = String.format("%d", oneTrace.getThreadId());
				tmp[2] = String.format("%s", oneTrace.getIp());
				tmp[3] = oneTrace.getStr();
				listViewTrace.itemAdd(tmp);
			}
		} catch (Exception ex) {
			if (logger == null) {
				logger = kernel.createLogger("TraceDlg", false, null);
			}
			//Ver5.0.0-b3 トレース表示で発生した例外をログ出力で処理するように修正
			logger.set(LogKind.ERROR, null, 9000041, ex.getMessage());
		}
	}

	private class MyTimer extends TimerTask {
		@Override
		public void run() {

			if (ar.size() == 0) {
				return;
			}
			timer.cancel();
			//timer.Enabled = false;
			synchronized (lock) {

				if (!isVisible()) { //トレースダイアログが閉じている場内、蓄積されたデータは破棄される
					ar.clear();
					return;
				}
				//Ver5.1.2
				if (ar.size() > 2000) {
					while (ar.size() > 2000) {
						ar.remove(0);
					}
					listViewTrace.itemClear();
				} else {
					if (listViewTrace.getRowCount() > 3000) {
						while (listViewTrace.getRowCount() > 2000) {
							listViewTrace.remove(0);
						}
					}
				}
				//一回のイベントで処理する最大数は100行まで
				ArrayList<OneTrace> list = new ArrayList<OneTrace>();
				for (int i = 0; i < 100 && 0 < ar.size(); i++) {
					list.add(ar.get(0));
					ar.remove(0);
				}
				disp2(list);
			}
			//最終行が見えるようにスクロールする
			listViewTrace.displayLastLine();
			timer = new Timer();

			timer.schedule(new MyTimer(), 0, 100); //再開
		}
	}

}
