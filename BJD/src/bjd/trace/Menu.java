package bjd.trace;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import bjd.menu.IMenu;
import bjd.menu.ListMenu;
import bjd.menu.MenuBase;
import bjd.menu.OneMenu;

public class Menu extends MenuBase {

	/**
	 * @param kernel
	 * @param menuBar
	 */
	public Menu(IMenu iMenu, JMenuBar menuBar) {
		super(iMenu, menuBar);
	}

	/**
	 * メニュー構築（内部テーブルの初期化） 通常用
	 */
	public final void initialize(boolean isJp) {
		removeAll();

		//「ファイル」メニュー
		JMenu m = addTopMenu(new OneMenu("File", "ファイル", "File", 'F', null), isJp);
		addListMenu(m, fileMenu(), isJp);

		//「編集」メニュー
		m = addTopMenu(new OneMenu("Edit", "編集", "Edit", 'E', null), isJp);
		addListMenu(m, editMenu(), isJp);

		refresh(); //メニューバーの再描画
	}

	/**
	 * 「ファイル」のサブメニュー
	 * @return ListMenu
	 */
	final ListMenu fileMenu() {
		ListMenu subMenu = new ListMenu();
		subMenu.add(new OneMenu("File_Close", "閉じる", "Close", 'C', null));
		return subMenu;
	}

	/**
	 * 「編集」のサブメニュー
	 * @return ListMenu
	 */
	final ListMenu editMenu() {
		ListMenu subMenu = new ListMenu();
		subMenu.add(new OneMenu("Edit_Copy", "コピー", "Copy", 'C', null));
		subMenu.add(new OneMenu("Edit_Clear", "クリア", "Clear", 'L', null));
		subMenu.add(new OneMenu("Edit_SaveAs", "名前を付けて保存", "SaveAs", 'A', null));
		return subMenu;
	}

}
