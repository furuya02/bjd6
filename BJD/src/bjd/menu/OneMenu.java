package bjd.menu;

import bjd.util.IDispose;

public final class OneMenu implements IDispose {
	private String jpTitle;
	private String enTitle;
	private String name;
	private char mnemonic;
	private ListMenu subMenu = new ListMenu();
	private String strAccelerator;

	public char getMnemonic() {
		return mnemonic;
	}

	public ListMenu getSubMenu() {
		return subMenu;
	}

	public void setSubMenu(ListMenu subMenu) {
		this.subMenu = subMenu;
	}

	public String getTitle(boolean isJp) {
		String title = enTitle;
		if (isJp) {
			if (mnemonic == ' ') {
				title = String.format("%s", jpTitle);
			} else {
				title = String.format("%s(%c)", jpTitle, mnemonic);
			}
		}
		return title;
	}

	public String getName() {
		return name;
	}

	public String getStrAccelerator() {
		return strAccelerator;
	}

	//セパレータ用
	public OneMenu() {
		this.name = "-";
		this.jpTitle = "";
		this.enTitle = "";
		this.mnemonic = 'Z';
		this.subMenu = null;
		this.strAccelerator = "";
	}

	public OneMenu(String name, String jpTitle, String enTitle, char mnemonic, String strAccelerator) {
		this.name = name;
		this.jpTitle = jpTitle;
		this.enTitle = enTitle;
		this.mnemonic = mnemonic;
		this.subMenu = new ListMenu();
		this.strAccelerator = strAccelerator;
	}

	@Override
	public void dispose() {
	}
}
