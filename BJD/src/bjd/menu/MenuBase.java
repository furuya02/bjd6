package bjd.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import bjd.util.IDisposable;

/**
 * メニュー構築のための基底クラス
 * @author Seishin
 *
 */

public class MenuBase implements ActionListener, IDisposable {
	private ArrayList<JMenuItem> ar = new ArrayList<>();

	private IMenu iMenu;
	private JMenuBar menuBar;

	protected MenuBase(IMenu iMenu, JMenuBar menuBar) {
		this.iMenu = iMenu;
		this.menuBar = menuBar;

	}

	/**
	 * 有効/無効
	 * @param name アクションコマンドに指定した名前
	 * @param enabled 有効無効
	 */
	public final void setEnabled(String name, boolean enabled) {
		for (JMenuItem m : ar) {
			String s = m.getActionCommand(); //ActionCommandは、OneMenuのnameで初期化されている
			if (s.equals(name)) {
				m.setEnabled(enabled);
			}
		}
	}

	/**
	 * ListMenuの追加 (再帰)
	 * @param owner 追加される親メニュ－
	 * @param subMenu 追加する子メニューのリスト
	 */
	protected final void addListMenu(JMenu owner, ListMenu subMenu, boolean isJp) {
		for (OneMenu o : subMenu) {
			addSubMenu(owner, o, isJp);
		}
	}

	/**
	 * OneMenuの追加
	 * @param owner 追加される親メニュ－
	 * @param oneMenu 追加する子メニュー
	 */
	final void addSubMenu(JMenu owner, OneMenu oneMenu, boolean isJp) {

		if (oneMenu.getName().equals("-")) {
			owner.addSeparator();
			return;
		}
		if (oneMenu.getSubMenu().size() != 0) {
			JMenu m = createMenu(oneMenu, isJp);
			addListMenu(m, oneMenu.getSubMenu(), isJp); //再帰処理
			owner.add(m);
		} else {
			JMenuItem menuItem = createMenuItem(oneMenu, isJp);
			JMenuItem item = (JMenuItem) owner.add(menuItem);
			ar.add(item);
		}
	}

	final JMenu createMenu(OneMenu oneMenu, boolean isJp) {
		JMenu m = new JMenu(oneMenu.getTitle(isJp));
		m.setActionCommand(oneMenu.getName());
		m.setMnemonic(oneMenu.getMnemonic());
		//		JMenuにはアクセラレータを設定できない
		//		if (oneMenu.getStrAccelerator() != null) {
		//			m.setAccelerator(KeyStroke.getKeyStroke(oneMenu.getStrAccelerator()));
		//		}
		m.addActionListener(this);
		m.setName(oneMenu.getTitle(isJp));
		return m;
	}

	final JMenuItem createMenuItem(OneMenu oneMenu, boolean isJp) {
		JMenuItem menuItem = new JMenuItem(oneMenu.getTitle(isJp));
		menuItem.setActionCommand(oneMenu.getName());
		menuItem.setMnemonic(oneMenu.getMnemonic());
		if (oneMenu.getStrAccelerator() != null) {
			menuItem.setAccelerator(KeyStroke.getKeyStroke(oneMenu.getStrAccelerator()));
		}
		menuItem.addActionListener(this);
		menuItem.setName(oneMenu.getTitle(isJp));
		return menuItem;
	}

	/**
	 * メニューバーへの追加
	 * @param oneMenu 追加する子メニュー
	 * @return
	 */
	protected final JMenu addTopMenu(OneMenu oneMenu, boolean isJp) {
		JMenu menu = new JMenu(oneMenu.getTitle(isJp));
		menu.setMnemonic(oneMenu.getMnemonic());
		if (menuBar != null) {
			menuBar.add(menu);
		}
		return menu;
	}

	//メニュー選択時のイベント処理
	@Override
	public final void actionPerformed(ActionEvent e) {
		iMenu.menuOnClick(e.getActionCommand());
	}

	//final void clear() {
	//	ar.clear();
	//}

	protected final void removeAll() {
		if (menuBar == null) {
			return;
		}

		//全削除
		menuBar.removeAll();
		ar.clear();
		dispose();
	}

	protected final void refresh() {
		if (menuBar != null) {
			menuBar.updateUI(); //メニューバーの再描画
		}
	}

	/**
	 * 終了処理
	 */
	@Override
	public final void dispose() {
		if (menuBar == null) {
			return;
		}
		while (menuBar.getMenuCount() > 0) {
			JMenu m = menuBar.getMenu(0);
			m.removeAll();
			menuBar.remove(m);
		}
		menuBar.invalidate();
	}

}
