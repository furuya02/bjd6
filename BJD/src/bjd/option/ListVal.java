package bjd.option;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JPanel;

import bjd.ctrl.ICtrlEventListener;
import bjd.util.ListBase;
import bjd.util.Msg;
import bjd.util.MsgKind;

/**
 * OneValのリストを表現するクラス<br>
 * OneValと共に再帰処理が可能になっている<br>
 * @author SIN
 *
 */
public final class ListVal extends ListBase<OneVal> {

	private Dimension dimension = null;

	public void add(OneVal oneVal) {

		// 追加オブジェクトの一覧
		ArrayList<OneVal> list = oneVal.getList(null);

		for (OneVal o : list) {
			if (null != search(o.getName())) {
				Msg.show(MsgKind.ERROR, String.format("ListVal.add(%s) 名前が重複しているため追加できませんでした", o.getName()));
			}
		}
		// 重複が無いので追加する
		getAr().add(oneVal);
	}

	/**
	 * 階層下のOneValを一覧する<br>
	 * 
	 * @param list
	 * @return
	 */
	public ArrayList<OneVal> getList(ArrayList<OneVal> list) {
		if (list == null) {
			list = new ArrayList<>();
		}
		for (OneVal o : getAr()) {
			list = o.getList(list);
		}
		return list;
	}

	/**
	 * 階層下のOneValを一覧する<br>
	 * Datの階層下は再帰しない<br>
	 * toREg()用に使用される<br>
	 * 
	 * @param list
	 * @return
	 */
	public ArrayList<OneVal> getSaveList(ArrayList<OneVal> list) {
		if (list == null) {
			list = new ArrayList<>();
		}
		for (OneVal o : getAr()) {
			list = o.getSaveList(list);
		}
		return list;
	}

	/**
	 * 階層下のOneValを検索する<br>
	 * 見つからないときnullが返る<br>
	 * この処理は多用されるため、スピードアップのため、例外を外してnullを返すようにした<br>
	 * 
	 * @param name
	 * @return 
	 */
	public OneVal search(String name) {
		for (OneVal o : getList(null)) {
			if (o.getName().equals(name)) {
				return o;
			}
		}
		//例外では、処理が重いので、nullを返す
		return null;
		//throw new Exception();
	}

	// コントロール生成
	public void createCtrl(JPanel mainPanel, int baseX, int baseY) {

		// オフセット計算用
		int x = baseX;
		int y = baseY;
		int h = y; // １行の中で一番背の高いオブジェクトの高さを保持する・
		int w = x; // xオフセットの最大値を保持する
		for (OneVal o : getAr()) {

			o.createCtrl(mainPanel, x, y);

			// すべてのコントロールを作成した総サイズを求める
			Dimension dimension = o.getSize();
			if (h < y + dimension.height) {
				h = y + dimension.height;
			}
			x += dimension.width;
			if (w < x) {
				w = x;
			}

			if (o.getCrlf() == Crlf.NEXTLINE) {
				y = h;
				x = baseX;
			}
		}
		// 開始位置から移動したオフセットで、このListValオブジェクトのwidth,heightを算出する
		dimension = new Dimension(w - baseX, h - baseY);
	}

	// コントロール破棄
	public void deleteCtrl() {
		for (OneVal o : getAr()) {
			o.deleteCtrl();
		}
	}

	// コントロールからの値のコピー(isComfirm==true 確認のみ)
	public boolean readCtrl(boolean isComfirm) {
		for (OneVal o : getAr()) {
			if (!o.readCtrl(isComfirm)) {
				return false;
			}
		}
		return true;
	}

	public Dimension getSize() {
		if (dimension == null) {
			throw new ExceptionInInitializerError();
		}
		return dimension;
	}

	public boolean isComplete() {
		for (OneVal o : getAr()) {
			if (!o.isComplete()) {
				return false;
			}
		}
		return true;
	}

	public void setListener(ICtrlEventListener listener) {
		for (OneVal o : getAr()) {
			o.setListener(listener);
		}
	}

}
