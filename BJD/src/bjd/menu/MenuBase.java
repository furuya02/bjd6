package bjd.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import bjd.Kernel;
import bjd.util.IDisposable;

/**
 * メニュー構築のための基底クラス
 * @author Seishin
 *
 */

public class MenuBase implements ActionListener, IDisposable {
	private ArrayList<JMenuItem> ar = new ArrayList<>();

	private Kernel kernel;
	private JMenuBar menuBar;

	protected MenuBase(Kernel kernel, JMenuBar menuBar) {
		this.kernel = kernel;
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
	protected final void addListMenu(JMenu owner, ListMenu subMenu) {
		for (OneMenu o : subMenu) {
			addSubMenu(owner, o);
		}
	}

	/**
	 * OneMenuの追加
	 * @param owner 追加される親メニュ－
	 * @param oneMenu 追加する子メニュー
	 */
	final void addSubMenu(JMenu owner, OneMenu oneMenu) {

		if (oneMenu.getName().equals("-")) {
			owner.addSeparator();
			return;
		}
		if (oneMenu.getSubMenu().size() != 0) {
			JMenu m = createMenu(oneMenu);
			addListMenu(m, oneMenu.getSubMenu()); //再帰処理
			owner.add(m);
		} else {
			JMenuItem menuItem = createMenuItem(oneMenu);
			JMenuItem item = (JMenuItem) owner.add(menuItem);
			ar.add(item);
		}
	}

	final JMenu createMenu(OneMenu oneMenu) {
		JMenu m = new JMenu(oneMenu.getTitle(kernel.isJp()));
		m.setActionCommand(oneMenu.getName());
		m.setMnemonic(oneMenu.getMnemonic());
		//		JMenuにはアクセラレータを設定できない
		//		if (oneMenu.getStrAccelerator() != null) {
		//			m.setAccelerator(KeyStroke.getKeyStroke(oneMenu.getStrAccelerator()));
		//		}
		m.addActionListener(this);
		m.setName(oneMenu.getTitle(kernel.isJp()));
		return m;
	}

	final JMenuItem createMenuItem(OneMenu oneMenu) {
		JMenuItem menuItem = new JMenuItem(oneMenu.getTitle(kernel.isJp()));
		menuItem.setActionCommand(oneMenu.getName());
		menuItem.setMnemonic(oneMenu.getMnemonic());
		if (oneMenu.getStrAccelerator() != null) {
			menuItem.setAccelerator(KeyStroke.getKeyStroke(oneMenu.getStrAccelerator()));
		}
		menuItem.addActionListener(this);
		menuItem.setName(oneMenu.getTitle(kernel.isJp()));
		return menuItem;
	}

	/**
	 * メニューバーへの追加
	 * @param oneMenu 追加する子メニュー
	 * @return
	 */
	protected final JMenu addTopMenu(OneMenu oneMenu) {
		JMenu menu = new JMenu(oneMenu.getTitle(kernel.isJp()));
		menu.setMnemonic(oneMenu.getMnemonic());
		menuBar.add(menu);
		return menu;
	}

	//メニュー選択時のイベント処理
	@Override
	public final void actionPerformed(ActionEvent e) {
		kernel.menuOnClick(e.getActionCommand());
	}

	//final void clear() {
	//	ar.clear();
	//}
	
	protected final void removeAll(){
		if (menuBar == null) {
			return;
		}
		
		//全削除
		menuBar.removeAll();
		ar.clear();
		dispose();
	}
	
	protected final void refresh(){
		menuBar.updateUI(); //メニューバーの再描画
	}
	

	/**
	 * 終了処理
	 */
	@Override
	public final void dispose() {
		while (menuBar.getMenuCount() > 0) {
			JMenu m = menuBar.getMenu(0);
			m.removeAll();
			menuBar.remove(m);
		}
		menuBar.invalidate();
	}

}
