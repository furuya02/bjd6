package bjd.ctrl;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import bjd.option.ListVal;
import bjd.util.Util;

public final class CtrlGroup extends OneCtrl {

	private JPanel border = null;
	private ListVal listVal;

	public CtrlGroup(String help, ListVal listVal) {
		super(help);
		this.listVal = listVal;
	}

    //OnePage(CtrlTabPage.pageList) CtrlGroup CtrlDatにのみ存在する
    public ListVal getListVal() {
        return listVal;
    }

	@Override
	public CtrlType getCtrlType() {
		return CtrlType.GROUP;
	}

	@Override
	protected void abstractCreate(Object value) {

		int left = margin;
		int top = margin;

		// ボーダライン（groupPanel）の生成
		border = (JPanel) create(panel, new JPanel(new GridLayout(0, 1)), left, top);
		border.setBorder(BorderFactory.createTitledBorder(getHelp()));

		//グループに含まれるコントロールを描画する
		int x = left + 8;
		int y = top + 12;
		listVal.createCtrl(border, x, y);
		Dimension dimension = listVal.getSize();

		// borderのサイズ指定
		border.setSize(getDlgWidth() - 22, (int) dimension.getHeight() + 25); // 横はコンストラクタ、縦は、含まれるコントロールで決まる

		// オフセット移動
		left += border.getWidth();
		top += border.getHeight();

		//値の設定
		abstractWrite(value);

		// パネルのサイズ設定
		//panel.setSize(left + width + margin, top + height + margin * 2);
		panel.setSize(left + margin, top + margin);
	}

	@Override
	protected void abstractDelete() {
		listVal.deleteCtrl(); //これが無いと、グループの中のコントロールが２回目以降表示されなくなる

		remove(panel, border);
		border = null;
	}

	//***********************************************************************
	// コントロールの値の読み書き
	//***********************************************************************
	@Override
	protected Object abstractRead() {
		listVal.readCtrl(false);
		return 0; //nullを返すと無効値になってしまうのでダミー値(0)を返す
	}

	@Override
	protected void abstractWrite(Object value) {

	}

	//***********************************************************************
	// コントロールへの有効・無効
	//***********************************************************************
	protected void abstractSetEnable(boolean enabled) {
		if (border != null) {
			//CtrlGroupの場合は、disableで非表示にする
			panel.setVisible(enabled);
			//border.setEnabled(enabled);
		}
	}

	//***********************************************************************
	// OnChange関連
	//***********************************************************************
	// 必要なし
	//***********************************************************************
	// CtrlDat関連
	//***********************************************************************
	@Override
	protected boolean abstractIsComplete() {
		return true;
	}

	@Override
	protected String abstractToText() {
		Util.runtimeException("使用禁止");
		return "";
}

	@Override
	protected void abstractFromText(String s) {
		Util.runtimeException("使用禁止");
	}

	@Override
	protected void abstractClear() {
	}

}
