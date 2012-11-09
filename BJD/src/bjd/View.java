package bjd;

import bjd.ctrl.ListView;
import bjd.option.Conf;
import bjd.util.IDispose;

public final class View implements IDispose {
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

		setLang();
		setColor();

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
		//        if (listView.InvokeRequired) {
		//            listView.BeginInvoke(new MethodInvoker(SetColor));
		//        } else {
		//            var color = SystemColors.Window;
		//            switch (_kernel.RunMode) {
		//                case RunMode.Normal:
		//                    //サーバプログラムが１つも起動していない場合
		//                    if (!_kernel.ListServer.IsRunnig)
		//                        color = Color.LightGray;
		//                    //リモート接続を受けている場合
		//                    if(_kernel.RemoteServer!=null)
		//                        color = Color.LightCyan;
		//                    break;
		//                case RunMode.NormalRegist:
		//                    color = Color.LightSkyBlue;
		//                    break;
		//                case RunMode.Remote:
		//                    color = (_kernel.RemoteClient!=null && _kernel.RemoteClient.IsConected) ? Color.LightGreen : Color.DarkGreen;
		//                    break;
		//            }
		//            _listView.BackColor = color;
		//        }
	}

	public void setLang() {
		if (listView == null) {
			return;
		}

		//        if (_listView.InvokeRequired) {
		//            _listView.Invoke(new MethodInvoker(SetLang));
		//        } else {
		//            //リストビューのカラム初期化（言語）
		//            _listView.Columns[0].Text = (_kernel.Jp) ? "日時" : "DateTime";
		//            _listView.Columns[1].Text = (_kernel.Jp) ? "種類" : "Kind";
		//            _listView.Columns[2].Text = (_kernel.Jp) ? "スレッドID" : "Thread ID";
		//            _listView.Columns[3].Text = (_kernel.Jp) ? "機能(サーバ)" : "Function(Server)";
		//            _listView.Columns[4].Text = (_kernel.Jp) ? "アドレス" : "Address";
		//            _listView.Columns[5].Text = (_kernel.Jp) ? "メッセージID" : "Message ID";
		//            _listView.Columns[6].Text = (_kernel.Jp) ? "説明" : "Explanation";
		//            _listView.Columns[7].Text = (_kernel.Jp) ? "詳細情報" : "Detailed information";
		//        }
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
