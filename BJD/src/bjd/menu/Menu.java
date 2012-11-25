package bjd.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import bjd.Kernel;
import bjd.RunMode;
import bjd.util.IDispose;

/**
 * メニューを管理するクラス
 * @author SIN
 *
 */
public final class Menu implements ActionListener, IDispose {
	private Kernel kernel;
	private JMenuBar menuBar;
	private ArrayList<JMenuItem> ar = new ArrayList<>();
	
	/**
	 * @param kernel
	 * @param menuBar
	 */
	public Menu(Kernel kernel, JMenuBar menuBar) {
		this.kernel = kernel;
		this.menuBar = menuBar;
	}

	/**
	 * 終了処理
	 */
	@Override
	public void dispose() {
		while (menuBar.getMenuCount() > 0) {
			JMenu m = menuBar.getMenu(0);
			m.removeAll();
			menuBar.remove(m);
		}
		menuBar.invalidate();
	}

	/**
	 * メニュー構築（内部テーブルの初期化） リモート用
	 */
	public void initializeRemote() {
		if (menuBar == null) {
			return;
		}

		//全削除
		menuBar.removeAll();

		ListMenu subMenu = new ListMenu();
		subMenu.add(new OneMenu("File_Exit", "終了", "Exit", 'X', null));

		//「ファイル」メニュー
		JMenu m = addTopMenu(new OneMenu("File", "ファイル", "File", 'F', null));
		addListMenu(m, subMenu);
	}
<<<<<<< HEAD
<<<<<<< HEAD
	
	
	
	//メニュー構築（内部テーブルの初期化）
=======
=======
>>>>>>> work

	/**
	 * メニュー構築（内部テーブルの初期化） 通常用
	 */
<<<<<<< HEAD
>>>>>>> work
=======
>>>>>>> work
	public void initialize() {
		if (menuBar == null) {
			return;
		}

		//全削除
<<<<<<< HEAD
<<<<<<< HEAD
		//menuBar.removeAll();
		//while (menuBar.getMenuCount() > 0) {
		//	JMenu m =  menuBar.getMenu(0);
		//	m.removeAll();
			//menuBar.remove(m);
		//}
=======
		ar.clear();
		dispose();
>>>>>>> work
=======
		ar.clear();
		dispose();
>>>>>>> work
		
		//「ファイル」メニュー
		JMenu m = addTopMenu(new OneMenu("File", "ファイル", "File", 'F', null));
		addListMenu(m, fileMenu());

		//「オプション」メニュー
		m = addTopMenu(new OneMenu("Option", "オプション", "Option", 'O', null));
		addListMenu(m, kernel.getListOption().getListMenu());

		//「ツール」メニュー
		m = addTopMenu(new OneMenu("Tool", "ツール", "Tool", 'T', null));
		//addListMenu(m, kernel.getListTool()getListMenu());

		//「起動/停止」メニュー
		m = addTopMenu(new OneMenu("StartStop", "起動/停止", "Start/Stop", 'S', null));
		addListMenu(m, startStopMenu());

		//「ヘルプ」メニュー
		m = addTopMenu(new OneMenu("Help", "ヘルプ", "Help", 'H', null));
		addListMenu(m, helpMenu());
<<<<<<< HEAD
<<<<<<< HEAD

		//setJang();
		setEnable(); //状況に応じた有効無効
=======
		
		menuBar.updateUI(); //メニューバーの再描画
>>>>>>> work
=======
		
		menuBar.updateUI(); //メニューバーの再描画
>>>>>>> work
	}

	/**
	 * ListMenuの追加 (再帰)
	 * @param owner 追加される親メニュ－
	 * @param subMenu 追加する子メニューのリスト
	 */
	void addListMenu(JMenu owner, ListMenu subMenu) {
		for (OneMenu o : subMenu) {
<<<<<<< HEAD
<<<<<<< HEAD
			addOneMenu(owner, o); 
=======
			addSubMenu(owner, o);
>>>>>>> work
=======
			addSubMenu(owner, o);
>>>>>>> work
		}
	}

	/**
	 * OneMenuの追加
	 * @param owner 追加される親メニュ－
	 * @param oneMenu 追加する子メニュー
	 */
	void addSubMenu(JMenu owner, OneMenu oneMenu) {

		if (oneMenu.getName().equals("-")) {
			owner.addSeparator();
			return;
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
		ar.add(item);
	}

	/**
	 * メニューバーへの追加
	 * @param oneMenu 追加する子メニュー
	 * @return
	 */
	JMenu addTopMenu(OneMenu oneMenu) {
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

<<<<<<< HEAD
<<<<<<< HEAD
	//言語設定
	//void setJang() {
		//while (menuBar.getMenuCount() > 0) {
			//JMenu m =  menuBar.getMenu(0);
			//m.set
			//m.removeAll();
			//menuBar.remove(m);
		//}
//
//		for (OneMenu oneMenu : subar) {
//			o.Value.Text = kernel.Jp?o.Key.JpTitle:o.Key.EnTitle;
//		}
	//}
	
	//状況に応じた有効/無効
=======
	/**
	 * 状況に応じた有効/無効のセット
	 */
>>>>>>> work
=======
	/**
	 * 状況に応じた有効/無効のセット
	 */
>>>>>>> work
	public void setEnable() {
		if (kernel.getRunMode() == RunMode.NormalRegist) { //サービス登録されている場合
			//サーバの起動停止はできない
			setEnabled("StartStop_Start", false);
			setEnabled("StartStop_Stop", false);
			setEnabled("StartStop_Restart", false);
			setEnabled("StartStop_Service", true);
			setEnabled("File_LogClear", false);
			setEnabled("File_LogCopy", false);
			setEnabled("File_Trace", false);
			setEnabled("Tool", false);
		} else if (kernel.getRunMode() == RunMode.Remote) { //リモートクライアント
			//サーバの再起動のみ
			setEnabled("StartStop_Start", false);
			setEnabled("StartStop_Stop", false);
			setEnabled("StartStop_Restart", true);
			setEnabled("StartStop_Service", false);
			setEnabled("File_LogClear", true);
			setEnabled("File_LogCopy", true);
			setEnabled("File_Trace", true);
			setEnabled("Tool", true);
		} else { //通常起動
			//Util.sleep(0); //起動・停止が全部完了してから状態を取得するため
			boolean isRunning = kernel.getListServer().isRunnig();
			setEnabled("StartStop_Start", !isRunning);
			setEnabled("StartStop_Stop", isRunning);
			setEnabled("StartStop_Restart", isRunning);
			setEnabled("StartStop_Service", !isRunning);
			setEnabled("File_LogClear", true);
			setEnabled("File_LogCopy", true);
			setEnabled("File_Trace", true);
			setEnabled("Tool", true);
		}
	}

	/**
	 * 有効/無効
	 * @param name アクションコマンドに指定した名前
	 * @param enabled 有効無効
	 */
	void setEnabled(String name, boolean enabled) {
		for (JMenuItem m : ar) {
			String s = m.getActionCommand(); //ActionCommandは、OneMenuのnameで初期化されている
			if (s.equals(name)) {
				m.setEnabled(enabled);
			}
		}
	}

	/**
	 * 「ファイル」のサブメニュー
	 * @return ListMenu
	 */
	ListMenu fileMenu() {
		ListMenu subMenu = new ListMenu();
		subMenu.add(new OneMenu("File_LogClear", "ログクリア", "Loglear", 'C', "F1"));
		subMenu.add(new OneMenu("File_LogCopy", "ログコピー", "LogCopy", 'L', "F2"));
		subMenu.add(new OneMenu("File_Trace", "トレース表示", "Trace", 'T', null));
		subMenu.add(new OneMenu()); // セパレータ
		subMenu.add(new OneMenu("File_Exit", "終了", "Exit", 'X', null));
		return subMenu;
	}

	/**
	 * 「起動/停止」のサブメニュー
	 * @return ListMenu
	 */
	ListMenu startStopMenu() {
		ListMenu subMenu = new ListMenu();
		subMenu.add(new OneMenu("StartStop_Start", "サーバ起動", "Start", 'S', null));
		subMenu.add(new OneMenu("StartStop_Stop", "サーバ停止", "Stop", 'P', null));
		subMenu.add(new OneMenu("StartStop_Restart", "サーバ再起動", "Restart", 'R', null)); 
		subMenu.add(new OneMenu("StartStop_Service", "サービス設定", "Service", 'S', null));
		return subMenu;
	}

	/**
	 * 「ヘルプ」のサブメニュー
	 * @return ListMenu
	 */
	ListMenu helpMenu() {
		ListMenu subMenu = new ListMenu();
		subMenu.add(new OneMenu("Help_Homepage", "ホームページ", "HomePage", 'H', null));
		subMenu.add(new OneMenu("Help_Document", "ドキュメント", "Document", 'D', null));
		subMenu.add(new OneMenu("Help_Support", "サポート掲示板", "Support", 'S', null));
		subMenu.add(new OneMenu("Help_Version", "バージョン情報", "Version", 'V', null));
		return subMenu;
	}

}
