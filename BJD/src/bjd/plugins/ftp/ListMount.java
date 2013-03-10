package bjd.plugins.ftp;

import bjd.option.Dat;
import bjd.option.OneDat;
import bjd.util.ListBase;

public final class ListMount extends ListBase<OneMount> {

	public ListMount(Dat dat) {
		if (dat != null) {
			for (OneDat o : dat) {
				//有効なデータだけを対象にする
				if (o.isEnable()) {
					add(o.getStrList().get(0), o.getStrList().get(1));
				}
			}
		}
	}

	public void add(String fromFolder, String toFolder) {
		getAr().add(new OneMount(fromFolder, toFolder));
	}
}
