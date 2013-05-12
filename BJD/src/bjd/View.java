package bjd;

import java.awt.Color;

import bjd.ctrl.ListView;
import bjd.option.Conf;
import bjd.util.IDisposable;
import bjd.util.Util;

public final class View implements IDisposable {
	private Kernel kernel;
	private MainForm mainForm;
	private ListView listView;

	//private NotifyIcon notifyIcon;

	public ListView getListView() {
		return listView;
	}

	public MainForm getMainForm() {
		return mainForm;
	}

	//    public View(Kernel kernel, MainForm mainForm, ListView listView, NotifyIcon notifyIcon) {
	public View(Kernel kernel, MainForm mainForm, ListView listView) {
		this.kernel = kernel;
		this.mainForm = mainForm;
		this.listView = listView;
		//this.notifyIcon = notifyIcon;

		if (listView == null) {
			return;
		}
		for (int i = 0; i < 8; i++) {
			listView.addColumn("");
		}
		listView.setColWidth(0, 120);
		listView.setColWidth(1, 60);
		listView.setColWidth(2, 60);
		listView.setColWidth(3, 80);
		listView.setColWidth(4, 80);
		listView.setColWidth(5, 70);
		listView.setColWidth(6, 200);
		listView.setColWidth(7, 300);
	}

	//フォームがアクティブにされた時
	private boolean isFirst = true; //最初の１回だげ実行する

	public void activated() {
		if (isFirst) {
			isFirst = false;

			//デフォルトで表示
			//if (notifyIcon != null)
			//    notifyIcon.Visible = false;

			if (kernel.getRunMode() != RunMode.Remote) {
				Conf conf = kernel.createConf("Basic");
				if (conf != null) {
					boolean useLastSize = (boolean) conf.get("useLastSize");
					if (useLastSize) {
						//						_kernel.WindowSize.Read(_listView);//カラム幅の復元
						//						_kernel.WindowSize.Read(_mainForm);//終了時のウインドウサイズの復元
					}
					//「起動時にウインドウを開く」が指定されていない場合は、アイコン化する
					if (!(boolean) conf.get("isWindowOpen")) {
						setVisible(false); //非表示
					}
				}
			}
		}
	}

	@Override
	public void dispose() {
		if (listView == null) {
			return;
		}
		//タスクトレイのアイコンを強制的に非表示にする
		//notifyIcon.Visible = false;

		//kernel.WindowSize.Save(mainForm);//ウインドウサイズの保存
		//kernel.WindowSize.Save(listView);//カラム幅の保存
	}

	//リストビューのカラー変更  
	public void setColor() {
		if (listView == null) {
			return;
		}
		RunMode runMode = kernel.getRunMode();
		Color color = Color.white;
		switch (runMode) {
		case Normal:
			//サーバプログラムが１つも起動していない場合
			if (!kernel.getListServer().isRunnig()) {
				color = Color.LIGHT_GRAY;
			}
			//リモート接続を受けている場合
			if (kernel.getRemoteServer() != null) {
				color = Color.CYAN;
			}
			break;
		case NormalRegist:
			color = Color.BLUE;
			break;
		case Remote:
			//color = (kernel.RemoteClient!=null && _kernel.RemoteClient.IsConected) ? Color.LightGreen : Color.DarkGreen;
			break;
		default:
			Util.runtimeException(String.format("undefind runMode=%s", runMode));
			break;
		}
		listView.setBackground(color);
	}

	/**
	 * カラムのタイトル初期化
	 */
	public void setColumnText() {
		if (listView == null) {
			return;
		}
		listView.setColumnText(0, kernel.isJp() ? "日時" : "DateTime");
		listView.setColumnText(1, kernel.isJp() ? "種類" : "Kind");
		listView.setColumnText(2, kernel.isJp() ? "スレッドID" : "Thread ID");
		listView.setColumnText(3, kernel.isJp() ? "機能（サーバ）" : "Function(Server)");
		listView.setColumnText(4, kernel.isJp() ? "アドレス" : "Address");
		listView.setColumnText(5, kernel.isJp() ? "メッセージID" : "Message ID");
		listView.setColumnText(6, kernel.isJp() ? "説明" : "Explanation");
		listView.setColumnText(7, kernel.isJp() ? "詳細情報" : "Detailed information");

	}
	
	

	public void activate() {
		if (listView == null) {
			return;
		}
		//mainForm.Activate();
	}

	public void setVisible(boolean enabled) {
		if (listView == null) {
			return;
		}

		if (kernel.getRunMode() == RunMode.Remote) {
			//リモートクライアントはタスクトレイに格納しない
			//mainForm.WindowState = !enabled ? FormWindowState.Minimized : FormWindowState.Normal;
		} else {
			//            if (!enabled) {
			//                _mainForm.Visible = false;//非表示
			//                _notifyIcon.Visible = true;//タスクトレイにアイコン表示
			//                _mainForm.ShowInTaskbar = false;//タスクバーにアイコン非表示
			//
			//            } else {
			//                _notifyIcon.Visible = false;//タスクトレイにアイコン非表示
			//                _mainForm.ShowInTaskbar = true;//タスクバーにアイコン表示
			//                _mainForm.Visible = true;//表示
			//            }
		}
	}

	public void save(WindowSize windowSize) {
		if (mainForm == null || listView == null || windowSize == null) {
			return;
		}
		windowSize.save(mainForm.getFrame());
		windowSize.save(listView);
	}

	public void read(WindowSize windowSize) {
		if (mainForm == null || listView == null || windowSize == null) {
			return;
		}
		windowSize.read(mainForm.getFrame());
		windowSize.read(listView);
	}

}
