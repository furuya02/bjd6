package bjd.ctrl;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

/**
 * 数値コントロール
 * @author SIN
 *
 */
public final class CtrlInt extends OneCtrl implements DocumentListener {

	private int digits;
	private JLabel label = null;
	private JTextField textField = null;

	/**
	 * 
	 * @param help 表示テキスト
	 * @param digits 桁数
	 */
	public CtrlInt(String help, int digits) {
		super(help);
		this.digits = digits;
	}

	/**
	 * コントロールのタイプ取得
	 */
	@Override
	public CtrlType getCtrlType() {
		return CtrlType.INT;
	}

	@Override
	protected void abstractCreate(Object value) {

		int left = margin;
		int top = margin;

		// ラベルの作成 top+3 は後のテキストボックスとの整合のため
		label = (JLabel) create(panel, new JLabel(getHelp()), left, top + 3);
		// label.setBorder(new LineBorder(Color.RED, 2, true)); //Debug 赤枠
		left += label.getWidth() + margin; // オフセット移動

		// テキストボックスの配置
		textField = (JTextField) create(panel, new JTextField(digits), left, top);
		((AbstractDocument) textField.getDocument()).setDocumentFilter(new IntegerDocumentFilter(digits));
		textField.getDocument().addDocumentListener(this);

		left += textField.getWidth(); // オフセット移動

		//値の設定
		abstractWrite(value);

		// パネルのサイズ設定
		panel.setSize(left + margin, defaultHeight + margin);
	}

	@Override
	protected void abstractDelete() {
		remove(panel, label);
		remove(panel, textField);
		label = null;
		textField = null;
	}

	//***********************************************************************
	// コントロールの値の読み書き
	//***********************************************************************
	@Override
	protected Object abstractRead() {
		String str = textField.getText();
		if (str.equals("")) {
			str = "0";
		}
		try {
			return Integer.valueOf(str);
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	protected void abstractWrite(Object value) {
		textField.setText(String.valueOf(value));
	}
	//***********************************************************************
	// コントロールへの有効・無効
	//***********************************************************************
	protected void abstractSetEnable(boolean enabled) {
		if (textField != null) {
			textField.setEditable(enabled);
			label.setEnabled(enabled);
		}
	}
	//***********************************************************************
	// OnChange関連
	//***********************************************************************
	@Override
	public void changedUpdate(DocumentEvent e) {
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		setOnChange();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		setOnChange();
	}

	//***********************************************************************
	// CtrlDat関連
	//***********************************************************************
	@Override
	protected boolean abstractIsComplete() {
		if (textField.getText().equals("")) {
			return false;
		}
		return true;
	}

	@Override
	protected String abstractToText() {
		int n = (int) abstractRead();
		return String.valueOf(n);
	}

	@Override
	protected void abstractFromText(String s) {
		textField.setText(s);
	}

	@Override
	protected void abstractClear() {
		textField.setText("");
	}
}
