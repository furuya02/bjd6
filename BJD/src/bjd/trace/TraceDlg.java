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
import bjd.menu.IMenu;
import bjd.net.Ip;
import bjd.util.IDisposable;

@SuppressWarnings("serial")
public final class TraceDlg extends Dlg implements IDisposable, IMenu {

	private static int width = 600;
	private static int height = 400;
	private Kernel kernel;
	//Timer _timer;
	private ArrayList<OneTrace> ar = new ArrayList<OneTrace>();
	private Logger logger;
	private ArrayList<JMenuItem> _ar = new ArrayList<>();
	private Menu menu;
	private ListView listView;

	private ArrayList<String> colJp = new ArrayList<String>();
	private ArrayList<String> colEn = new ArrayList<String>();

	public TraceDlg(Kernel kernel, JFrame frame) {
		super(frame, width, height);
		this.kernel = kernel;

		listView = new ListView("listViewTrace");
		StatusBar bar = new StatusBar();

		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		this.getContentPane().add(listView);
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

		if (listView != null) {
			for (int i = 0; i < 3; i++) {
				listView.addColumn("");
			}

			listView.setColWidth(0, 80);
			listView.setColWidth(1, 100);
			listView.setColWidth(2, 500);
		}

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

		setTitle((kernel.isJp()) ? "トレース表示" : "Trace Dialog");
		menu.initialize(kernel.isJp());

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
		for (int i = 0; i < 3; i++) {
			listView.setColumnText(i, kernel.isJp() ? colJp.get(i) : colEn.get(i));
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

	public void addTrace(TraceKind traceKind, String str, Ip ip) {
		// TODO Auto-generated method stub

	}

	@Override
	public void menuOnClick(String cmd) {

		if (cmd.indexOf("File_Close") == 0) {
			this.setVisible(false);
		}
		// TODO 自動生成されたメソッド・スタブ

	}

}
