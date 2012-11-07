package bjd.ctrl;

import bjd.option.ListVal;
import bjd.option.OneVal;

/**
 * オプションダイアログに表示するタブの１ページを表現するクラス
 * @author SIN
 *
 */
public final class OnePage {
	private String name;
	private String title;
	private ListVal listVal = new ListVal();

	/**
	 * タブ表示テキストの取得
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 名前の取得
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name　名前
	 * @param title　タブ表示テキスト
	 */
	public OnePage(String name, String title) {
		this.name = name;
		this.title = title;
	}

	/**
	 * OnePage(CtrlTabPage.pageList) CtrlGroup CtrlDatにのみ存在する
	 * @return
	 */
    public ListVal getListVal() {
        return listVal;
    }
    /**
     * OneValの追加
     * @param oneVal
     */
    public void add(OneVal oneVal) {
		listVal.add(oneVal);
	}
    

}
