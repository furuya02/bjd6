package bjd.trace;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import bjd.Dlg;
import bjd.Kernel;
import bjd.RunMode;
import bjd.ctrl.ListView;
import bjd.ctrl.StatusBar;
import bjd.log.Logger;
import bjd.net.Ip;
import bjd.util.IDisposable;

@SuppressWarnings("serial")
public final class TraceDlg extends Dlg implements IDisposable {

	private static int width = 600;
	private static int height = 400;
	private Kernel kernel;
	//Timer _timer;
	private ArrayList<OneTrace> ar = new ArrayList<OneTrace>();
	private Logger logger;
	private ArrayList<JMenuItem> _ar = new ArrayList<>();
	Menu menu;

	private ArrayList<String> colJp = new ArrayList<String>();
	private ArrayList<String> colEn = new ArrayList<String>();

	public TraceDlg(Kernel kernel, JFrame frame) {
		super(frame, width, height);
		this.kernel = kernel;

		ListView listView = new ListView("listViewTrace");
		StatusBar bar = new StatusBar();
		

		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		this.getContentPane().add(listView);
		this.getContentPane().add(bar, BorderLayout.PAGE_END);

		menu = new Menu(kernel, menuBar);

		colJp.add("送受");
		colJp.add("スレッドID");
		colJp.add("アドレス");
		colJp.add("データ");
		colEn.add("Direction");
		colEn.add("ThreadID");
		colEn.add("Address");
		colEn.add("Data");

		//オーナー描画
		//foreach (var t in _colJp){
		//    listViewTrace.Columns.Add(t);
		//}
		//listViewTrace.Columns[2].Width = 100;
		//listViewTrace.Columns[3].Width = 500;

		//_timer = new Timer{Enabled = true, Interval = 100};
		//_timer.Tick += TimerTick;

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
		//		Text = (kernel.isJp()) ? "トレース表示" : "Trace Dialog";

		menu.initialize();

		//JMenu m = addTopMenu(new OneMenu("File", "ファイル", "File", 'F', null));
		//		MainMenuFile.Text = (kernel.isJp()) ? "ファイル(&F)" : "&File";
		//		MainMenuClose.Text = (kernel.isJp()) ? "閉じる(&C)" : "&Close";
		//		MainMenuEdit.Text = (kernel.isJp()) ? "編集(&E)" : "&Edit";
		//		MainMenuCopy.Text = (kernel.isJp()) ? "コピー(&C)" : "&Copy";
		//		MainMenuClear.Text = (kernel.isJp()) ? "クリア(&L)" : "C&lear";
		//		MainMenuSave.Text = (kernel.isJp()) ? "名前を付けて保存(&S)" : "S&ave";
		//
		//		PopupMenuCopy.Text = (kernel.isJp()) ? "コピー(&C)" : "&Copy";
		//		PopupMenuClear.Text = (kernel.isJp()) ? "クリア(&L)" : "C&lear";
		//		PopupMenuClose.Text = (kernel.isJp()) ? "閉じる(&C)" : "&Close";
		//		PopupMenuClose.Text = (kernel.isJp()) ? "名前を付けて保存(&S)" : "S&ave";
		//
		//		for (int i = 0; i < colJp.size(); i++) {
		//			listViewTrace.Columns[i].Text = (kernel.isJp()) ? colJp.get(i) : colEn.get(i);
		//		}
		//
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

	public void addTrace(TraceKind traceKind, String str, Ip ip) {
		// TODO Auto-generated method stub

	}

}
