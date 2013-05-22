package bjd.trace;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import bjd.Kernel;
import bjd.menu.ListMenu;
import bjd.menu.MenuBase;
import bjd.menu.OneMenu;

public class Menu extends MenuBase {
	private Kernel kernel;

	/**
	 * @param kernel
	 * @param menuBar
	 */
	public Menu(Kernel kernel, JMenuBar menuBar) {
		super(kernel, menuBar);
		this.kernel = kernel;
	}
	/**
	 * メニュー構築（内部テーブルの初期化） 通常用
	 */
	public void initialize() {
		removeAll();

		//「ファイル」メニュー
		JMenu m = addTopMenu(new OneMenu("File", "ファイル", "File", 'F', null));
		addListMenu(m, fileMenu());

		//「編集」メニュー
		m = addTopMenu(new OneMenu("Edit", "編集", "Edit", 'E', null));
		addListMenu(m, editMenu());


		refresh();//メニューバーの再描画
	}

	/**
	 * 「ファイル」のサブメニュー
	 * @return ListMenu
	 */
	ListMenu fileMenu() {
		ListMenu subMenu = new ListMenu();
		subMenu.add(new OneMenu("File_Close", "閉じる", "Close", 'C', null));
		return subMenu;
	}

	/**
	 * 「編集」のサブメニュー
	 * @return ListMenu
	 */
	ListMenu editMenu() {
		ListMenu subMenu = new ListMenu();
		subMenu.add(new OneMenu("Edit_Copy", "コピー", "Copy", 'C', null));
		subMenu.add(new OneMenu("Edit_Clear", "クリア", "Clear", 'L', null));
		subMenu.add(new OneMenu("Edit_SaveAs", "名前を付けて保存", "SaveAs", 'A', null));
		return subMenu;
	}

}
