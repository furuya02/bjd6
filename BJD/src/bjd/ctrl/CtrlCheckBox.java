package bjd.ctrl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

public final class CtrlCheckBox extends OneCtrl implements ActionListener {

	private JCheckBox checkBox = null;

	public CtrlCheckBox(String help) {
		super(help);
	}

	@Override
	public CtrlType getCtrlType() {
		return CtrlType.CHECKBOX;
	}

	@Override
	protected void abstractCreate(Object value) {
       
	    int left = margin;
		int top = margin;

		// チェックボックス作成
		checkBox = (JCheckBox) create(panel, new JCheckBox(getHelp()), left, top);
		checkBox.addActionListener(this);
		left += checkBox.getWidth() + margin; // オフセット移動

		//値の設定
		abstractWrite(value);

		// パネルのサイズ設定
		panel.setSize(left + margin, defaultHeight + margin * 2);

	}

	@Override
	protected void abstractDelete() {
		remove(panel, checkBox);
		checkBox = null;
	}

	//***********************************************************************
	// コントロールの値の読み書き
	//***********************************************************************
	@Override
	protected Object abstractRead() {
		return checkBox.isSelected();
	}

	@Override
	protected void abstractWrite(Object value) {
		checkBox.setSelected((boolean) value);
	}

	//***********************************************************************
	// コントロールへの有効・無効
	//***********************************************************************
	protected void abstractSetEnable(boolean enabled) {
		if (checkBox != null) {
			checkBox.setEnabled(enabled);
		}
	}

	//***********************************************************************
	// OnChange関連
	//***********************************************************************
	@Override
	public void actionPerformed(ActionEvent arg0) {
		setOnChange();
	}

	//***********************************************************************
	// CtrlDat関連
	//***********************************************************************
	@Override
	protected boolean abstractIsComplete() {
		return true; // チェックの有無は、常にCompleteしている
	}

	@Override
	protected String abstractToText() {
		return String.valueOf(checkBox.isSelected());
	}

	@Override
	protected void abstractFromText(String s) {
		checkBox.setSelected(Boolean.valueOf(s));
	}

	@Override
	protected void abstractClear() {
		checkBox.setSelected(false);
	}
}
