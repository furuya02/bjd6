package bjd.menu;

import bjd.util.ListBase;

public final class ListMenu extends ListBase<OneMenu> {

	public OneMenu add(OneMenu o) {
		getAr().add(o);
		return o;
	}

	public OneMenu insert(int index, OneMenu o) {
		getAr().add(index, o);
		return o;
	}
}
