package bjd.menu;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import bjd.Kernel;
import bjd.RunMode;

/**
 * メニューを管理するクラス
 * @author SIN
 *
 */
public final class Menu extends MenuBase {
	//private Kernel kernel;

	//	private JMenuBar menuBar;

	/**
	 * @param kernel
	 * @param menuBar
	 */
	public Menu(Kernel kernel, IMenu iMenu, JMenuBar menuBar) {
		super(iMenu, menuBar);
		//this.kernel = kernel;
	}

	/**
	 * メニュー構築（内部テーブルの初期化） リモート用
	 */
	public void initializeRemote(boolean isJp) {
		//全削除
		removeAll();

		ListMenu subMenu = new ListMenu();
		subMenu.add(new OneMenu("File_Exit", "終了", "Exit", 'X', null));

		//「ファイル」メニュー
		JMenu m = addTopMenu(new OneMenu("File", "ファイル", "File", 'F', null), isJp);
		addListMenu(m, subMenu, isJp);
	}

	/**
	 * メニュー構築（内部テーブルの初期化） 通常用
	 */
	public void initialize(ListMenu optionMenu, boolean isJp) {
		removeAll();

		//「ファイル」メニュー
		JMenu m = addTopMenu(new OneMenu("File", "ファイル", "File", 'F', null), isJp);
		addListMenu(m, fileMenu(), isJp);

		//「オプション」メニュー
		m = addTopMenu(new OneMenu("Option", "オプション", "Option", 'O', null), isJp);
		//addListMenu(m, kernel.getListOption().getListMenu(), isJp);
		addListMenu(m, optionMenu, isJp);

		//「ツール」メニュー
		m = addTopMenu(new OneMenu("Tool", "ツール", "Tool", 'T', null), isJp);
		//addListMenu(m, kernel.getListTool()getListMenu());

		//「起動/停止」メニュー
		m = addTopMenu(new OneMenu("StartStop", "起動/停止", "Start/Stop", 'S', null), isJp);
		addListMenu(m, startStopMenu(), isJp);

		//「ヘルプ」メニュー
		m = addTopMenu(new OneMenu("Help", "ヘルプ", "Help", 'H', null), isJp);
		addListMenu(m, helpMenu(), isJp);

		refresh(); //メニューバーの再描画
	}

	//	//メニュー選択時のイベント処理
	//	@Override
	//	public void actionPerformed(ActionEvent e) {
	//		kernel.menuOnClick(e.getActionCommand());
	//	}

	/**
	 * 状況に応じた有効/無効のセット
	 */
	public void setEnable(RunMode runMode, boolean isRunning) {
		if (runMode == RunMode.NormalRegist) { //サービス登録されている場合
			//サーバの起動停止はできない
			setEnabled("StartStop_Start", false);
			setEnabled("StartStop_Stop", false);
			setEnabled("StartStop_Restart", false);
			setEnabled("StartStop_Service", true);
			setEnabled("File_LogClear", false);
			setEnabled("File_LogCopy", false);
			setEnabled("File_Trace", false);
			setEnabled("Tool", false);
		} else if (runMode == RunMode.Remote) { //リモートクライアント
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
			//			boolean isRunning = kernel.getListServer().isRunnig();
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
