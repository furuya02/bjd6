package bjd.ctrl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;

public final class CtrlComboBox extends OneCtrl implements ActionListener {

	private String[] list = null;
	private int width;
	private JLabel label = null;
	private JComboBox<String> comboBox = null;

	public CtrlComboBox(String help, String[] list, int width) {
		super(help);
		this.list = list;
		this.width = width;
	}

	@Override
	public CtrlType getCtrlType() {
		return CtrlType.COMBOBOX;
	}

	public int getMax() {
		return list.length;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void abstractCreate(Object value) {
		int left = margin;
		int top = margin;

		// ラベルの作成 top+3 は後のテキストボックスとの整合のため
		label = (JLabel) create(panel, new JLabel(getHelp()), left, top + 3);
		// label.setBorder(new LineBorder(Color.RED, 2, true)); //Debug 赤枠
		left += label.getWidth() + margin; // オフセット移動

		// コンボボックスの配置
		comboBox = (JComboBox<String>) create(panel, new JComboBox<String>(list), left, top);
		comboBox.addActionListener(this);
		comboBox.setSize(width, comboBox.getHeight());
		left += comboBox.getWidth(); // オフセット移動

		//値の設定
		abstractWrite(value);

		// パネルのサイズ設定
		panel.setSize(left + margin, defaultHeight + margin);
	}

	@Override
	protected void abstractDelete() {
		remove(panel, label);
		remove(panel, comboBox);
		label = null;
		comboBox = null;
	}

	//***********************************************************************
	// コントロールの値の読み書き
	//***********************************************************************
	@Override
	protected Object abstractRead() {
		return comboBox.getSelectedIndex();
	}

	@Override
	protected void abstractWrite(Object value) {
		comboBox.setSelectedIndex((int) value);

	}

	//***********************************************************************
	// コントロールへの有効・無効
	//***********************************************************************
	protected void abstractSetEnable(boolean enabled) {
		if (comboBox != null) {
			comboBox.setEnabled(enabled);
		}
	}

	//***********************************************************************
	// OnChange関連
	//***********************************************************************
	@Override
	public void actionPerformed(ActionEvent e) {
		setOnChange();
	}

	//***********************************************************************
	// CtrlDat関連
	//***********************************************************************
	@Override
	protected boolean abstractIsComplete() {
		return true;
	}

	@Override
	protected String abstractToText() {
		return String.valueOf(comboBox.getSelectedIndex());
	}

	@Override
	protected void abstractFromText(String s) {
		int n = Integer.valueOf(s);
		comboBox.setSelectedIndex(n);
	}

	@Override
	protected void abstractClear() {
		comboBox.setSelectedIndex(0);
	}

}
