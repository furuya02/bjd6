package bjd.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import bjd.Kernel;
import bjd.util.IDispose;

public final class Menu implements ActionListener, IDispose {
	private Kernel kernel;
	private JMenuBar menuBar;

	public Menu(Kernel kernel, JMenuBar menuBar) {
		this.kernel = kernel;
		this.menuBar = menuBar;
	}

	@Override
	public void dispose() {
		while (menuBar.getMenuCount() > 0) {
			JMenu m =  menuBar.getMenu(0);
			m.removeAll();
			menuBar.remove(m);
		}
		menuBar.invalidate();		
	}

	//メニュー構築（内部テーブルの初期化）
	public void initializeRemote() {
		if (menuBar == null) {
			return;
		}

		//全削除
		menuBar.removeAll();

		ListMenu subMenu = new ListMenu();
		subMenu.add(new OneMenu("File_Exit", "終了", "Exit", 'X', null));

		//「ファイル」メニュー
		JMenu m = addOneMenu(new OneMenu("File", "ファイル", "File", 'F', null));
		addListMenu(m, subMenu);
	}
	
	//メニュー構築（内部テーブルの初期化）
	public void initialize() {
		if (menuBar == null) {
			return;
		}

		//全削除
		menuBar.removeAll();

		//「ファイル」メニュー
		JMenu m = addOneMenu(new OneMenu("File", "ファイル", "File", 'F', null));
		addListMenu(m, fileMenu());

		//「オプション」メニュー
		m = addOneMenu(new OneMenu("Option", "オプション", "Option", 'O', null));
		addListMenu(m, kernel.getListOption().getListMenu());

		//「ツール」メニュー
		m = addOneMenu(new OneMenu("Tool", "ツール", "Tool", 'T', null));
		//addListMenu(m, kernel.getListTool()getListMenu());

		//「起動/停止」メニュー
		m = addOneMenu(new OneMenu("StartStop", "起動/停止", "Start/Stop", 'S', null));
		addListMenu(m, startStopMenu());

		//「ヘルプ」メニュー
		m = addOneMenu(new OneMenu("Help", "ヘルプ", "Help", 'H', null));
		addListMenu(m, helpMenu());

		setJang();
		setEnable(); //状況に応じた有効無効
	}

//	//メニュー選択のイベントを発生させる
//	public void enqueueMenu(String name, boolean synchro) {
////		var item = new ToolStripMenuItem{Name = name};
////		if (synchro) {
////			if (OnClick != null) {
////				OnClick(item);
////			}
////		} else {
////			_queue.Enqueue(item);//キューに格納する
////		}
//	}
	//タイマー起動でキューに入っているメニューイベントを実行する
//	void timerElapsed(Object sender, System.Timers.ElapsedEventArgs e) {
//		if(_queue.Count>0){
//			if (OnClick != null) {
//				var q = _queue.Dequeue();
//				OnClick(q);
//			}
//		}
//	}

	//ListMenuの追加
	void addListMenu(JMenu owner, ListMenu subMenu) {
		for (OneMenu o : subMenu) {
			addOneMenu(owner, o);
		}
	}
	//OneMenuの追加
//	public void add(JMenu owner, String str, String name, char mnemonic, String strAccelerator) {
	JMenuItem addOneMenu(JMenu owner, OneMenu oneMenu) {
		
		if (oneMenu.getName().equals("-")) {
			owner.addSeparator();
			return null;
		}
		
		JMenuItem menuItem = new JMenuItem(oneMenu.getTitle(kernel.isJp()));
		menuItem.setActionCommand(oneMenu.getName());
		menuItem.setMnemonic(oneMenu.getMnemonic());
		if (oneMenu.getStrAccelerator() != null) {
			//KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK)
			//String strAccelerator = keyStroke.toString();
			menuItem.setAccelerator(KeyStroke.getKeyStroke(oneMenu.getStrAccelerator()));
        }
		menuItem.addActionListener(this);
		menuItem.setName(oneMenu.getTitle(kernel.isJp()));

		
		JMenuItem item = owner.add(menuItem);
		
		//TODO addListMenu(item, oneMenu.getSubMenu()); //再帰処理(o.SubMenu.Count==0の時、処理なしで戻ってくる)
		
		return item;
	}
	
	JMenu addOneMenu(OneMenu oneMenu) {
		JMenu menu = new JMenu(oneMenu.getTitle(kernel.isJp()));
		menu.setMnemonic(oneMenu.getMnemonic());
		menuBar.add(menu);
		return menu;
	}


	//メニュー選択時のイベント処理
	@Override
	public void actionPerformed(ActionEvent e) {
		kernel.menuOnClick(e.getActionCommand());
	}

	//言語設定
	void setJang() {
//		foreach (var o in _ar) {
//			o.Value.Text = kernel.Jp?o.Key.JpTitle:o.Key.EnTitle;
//		}
	}
	
	//状況に応じた有効/無効
	public void setEnable() {
//		if (kernel.RunMode == RunMode.NormalRegist) {//サービス登録されている場合
//			//サーバの起動停止はできない
//			SetEnabled("StartStop_Start", false);
//			SetEnabled("StartStop_Stop", false);
//			SetEnabled("StartStop_Restart", false);
//			SetEnabled("StartStop_Service", true);
//			SetEnabled("File_LogClear", false);
//			SetEnabled("File_LogCopy", false);
//			SetEnabled("File_Trace", false);
//			SetEnabled("Tool", false);
//		}else if (kernel.RunMode == RunMode.Remote) {//リモートクライアント
//			//サーバの再起動のみ
//			SetEnabled("StartStop_Start", false);
//			SetEnabled("StartStop_Stop", false);
//			SetEnabled("StartStop_Restart", true);
//			SetEnabled("StartStop_Service", false);
//			SetEnabled("File_LogClear", true);
//			SetEnabled("File_LogCopy", true);
//			SetEnabled("File_Trace", true);
//			SetEnabled("Tool", true);
//		} else {//通常起動
//			SetEnabled("StartStop_Start", !kernel.ListServer.IsRunnig);
//			SetEnabled("StartStop_Stop", kernel.ListServer.IsRunnig);
//			SetEnabled("StartStop_Restart", kernel.ListServer.IsRunnig);
//			SetEnabled("StartStop_Service", !kernel.ListServer.IsRunnig);
//			SetEnabled("File_LogClear", true);
//			SetEnabled("File_LogCopy", true);
//			SetEnabled("File_Trace", true);
//			SetEnabled("Tool", true);
//		}
	}
	//有効/無効
	void setEnabled(String name, boolean enabled) {
//		foreach (var o in _ar) {
//			if (o.Key.Name == name) {
//				o.Value.Enabled = enabled;
//				return;
//			}
//		}
	}


	//「ファイル」メニュー
	ListMenu fileMenu() {
		ListMenu subMenu = new ListMenu();
		subMenu.add(new OneMenu("File_LogClear", "ログクリア", "Loglear", 'C', "F1")); 
		subMenu.add(new OneMenu("File_LogCopy", "ログコピー", "LogCopy", 'L', "F2")); 
		subMenu.add(new OneMenu("File_Trace", "トレース表示", "Trace", 'T', null));
		subMenu.add(new OneMenu()); // セパレータ
		subMenu.add(new OneMenu("File_Exit", "終了", "Exit", 'X', null));
		return subMenu;
	}
	//「起動/停止」メニュー
	ListMenu startStopMenu() {
		ListMenu subMenu = new ListMenu();
		subMenu.add(new OneMenu("StartStop_Start", "サーバ起動", "Start", 'S', null));
		subMenu.add(new OneMenu("StartStop_Stop", "サーバ停止", "Stop", 'P', null));
		subMenu.add(new OneMenu("StartStop_Restart", "サーバ再起動", "Restart", 'R', null)); //TODO keys: Keys.Control | Keys.X
		subMenu.add(new OneMenu("StartStop_Service", "サービス設定", "Service", 'S', null));
		return subMenu;
	}

	//「ヘルプ」メニュー
	ListMenu helpMenu() {
		ListMenu subMenu = new ListMenu();
		subMenu.add(new OneMenu("Help_Homepage", "ホームページ", "HomePage", 'H', null));
		subMenu.add(new OneMenu("Help_Document", "ドキュメント", "Document", 'D', null));
		subMenu.add(new OneMenu("Help_Support", "サポート掲示板", "Support", 'S', null));
		subMenu.add(new OneMenu("Help_Version", "バージョン情報", "Version", 'V', null));
		return subMenu;
	}

}
