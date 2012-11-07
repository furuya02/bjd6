package bjd.ctrl;

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import bjd.util.Crypt;

public final class CtrlHidden extends OneCtrl implements DocumentListener {

	private int digits;
	private JLabel label = null;
	private JPasswordField passwordField = null;

	public CtrlHidden(String help, int digits) {
		super(help);
		this.digits = digits;
	}

	@Override
	public CtrlType getCtrlType() {
		return CtrlType.HIDDEN;
	}

	@Override
	protected void abstractCreate(Object value) {

		int left = margin;
		int top = margin;

		// ラベルの作成 top+3 は後のテキストボックスとの整合のため
		label = (JLabel) create(panel, new JLabel(getHelp()), left, top + 3);
		left += label.getWidth() + margin; // オフセット移動

		// テキストボックスの配置
		passwordField = (JPasswordField) create(panel, new JPasswordField(digits), left, top);
		passwordField.getDocument().addDocumentListener(this);
		left += passwordField.getWidth(); // オフセット移動

		//値の設定
		abstractWrite(value);

		// パネルのサイズ設定
		panel.setSize(left + margin, defaultHeight + margin);
	}

	@Override
	protected void abstractDelete() {
		remove(panel, label);
		remove(panel, passwordField);
		label = null;
		passwordField = null;
	}

	//***********************************************************************
	// コントロールの値の読み書き
	//***********************************************************************
	@SuppressWarnings("deprecation")
	@Override
	protected Object abstractRead() {
		return passwordField.getText();
	}

	@Override
	protected void abstractWrite(Object value) {
		passwordField.setText(String.valueOf(value));
	}
	
	//***********************************************************************
	// コントロールへの有効・無効
	//***********************************************************************
	protected void abstractSetEnable(boolean enabled) {
		if (passwordField != null) {
			passwordField.setEditable(enabled);
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
		insertUpdate(e);
	}

	//***********************************************************************
	// CtrlDat関連
	//***********************************************************************
	@SuppressWarnings("deprecation")
	@Override
	protected boolean abstractIsComplete() {
		if (passwordField.getText().equals("")) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected String abstractToText() {
		String str = "";
		try {
			str = Crypt.encrypt(passwordField.getText());
		} catch (Exception e) {
			str = "ERROR";
		}
		return str;
	}

	@Override
	protected void abstractFromText(String s) {
		String str = "";
		try {
			str = Crypt.decrypt(s);
		} catch (Exception e) {
			str = "ERROR";
		}
		passwordField.setText(str);
	}

	@Override
	protected void abstractClear() {
		passwordField.setText("");
	}
}
