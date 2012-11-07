package bjd.log;

import java.util.ArrayList;

import bjd.option.Dat;
import bjd.option.OneDat;

/**
 * 表示制限文字列のリストを保持し、表示対象かどうかをチェックするクラス
 * @author user1
 *
 */
public final class LogLimit {

	private String[] limitStr;
	private boolean isDisplay; //ヒットした場合に表示するかどうか

	/**
	 * 
	 * @param dat 制限文字列
	 * @param isDisplay　ヒットした場合の動作（表示/非表示)
	 */
	public LogLimit(Dat dat, boolean isDisplay) {
		this.isDisplay = isDisplay;

		ArrayList<String> tmp = new ArrayList<>();
		if (dat != null) {
			for (OneDat o : dat) {
				if (o.isEnable()) { //有効なデータだけを対象にする
					tmp.add(o.getStrList().get(0));
				}
			}
		}
		limitStr = tmp.toArray(new String[0]);
	}

	/**
	 * 指定した文字列を表示するか否かの判断
	 * @param str 検査する文字列
	 * @return　表示：true　　非表示:false
	 */
	public boolean isDisplay(String str) {
		if (str == null) {
			return false;
		}
		for (String s : limitStr) {
			if (str.indexOf(s) != -1) {
				return (isDisplay) ? true : false;
			}
		}
		return (isDisplay) ? false : true;
	}
}
