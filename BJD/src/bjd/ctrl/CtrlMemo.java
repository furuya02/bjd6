package bjd.ctrl;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public final class CtrlMemo extends OneCtrl implements DocumentListener {
	private int height;
	private int width;
	private JLabel label;
	private JTextArea textArea;
	private JScrollPane scrollPane;

	public CtrlMemo(String help, int width, int height) {
		super(help);
		this.width = width;
		this.height = height;
	}

	@Override
	public CtrlType getCtrlType() {
		return CtrlType.MEMO;
	}

	@Override
	protected void abstractCreate(Object value) {
		int left = margin;
		int top = margin;

		// ラベルの作成 top+3 は後のテキストボックスとの整合のため
		label = (JLabel) create(panel, new JLabel(getHelp()), left, top + 3);
		// label.setBorder(new LineBorder(Color.RED, 2, true)); //Debug 赤枠
		left += label.getWidth() + margin; // オフセット移動

		// テキストエリアの配置
		textArea = new JTextArea();
		textArea.getDocument().addDocumentListener(this);
		scrollPane = (JScrollPane) create(panel, new JScrollPane(textArea), left, top);
		scrollPane.setSize(width, height);
		panel.add(scrollPane);

		// オフセット移動
		left += scrollPane.getWidth();
		top += height;

		//値の設定
		abstractWrite(value);
		// パネルのサイズ設定
		panel.setSize(left + margin, top + margin);
	}

	@Override
	protected void abstractDelete() {
		remove(panel, label);
		remove(panel, scrollPane);
		label = null;
		scrollPane = null;

		textArea = null;
	}

	//***********************************************************************
	// コントロールの値の読み書き
	//***********************************************************************
	@Override
	protected Object abstractRead() {
		return textArea.getText();
	}

	@Override
	protected void abstractWrite(Object value) {
		textArea.setText((String) value);
		textArea.setCaretPosition(0);
	}

	//***********************************************************************
	// コントロールへの有効・無効
	//***********************************************************************
	protected void abstractSetEnable(boolean enabled) {
		if (textArea != null) {
			textArea.setEditable(enabled);
		}
	}

	//***********************************************************************
	// OnChange関連
	//***********************************************************************
	@Override
	public void changedUpdate(DocumentEvent arg0) {
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		setOnChange();
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		setOnChange();
	}

	//***********************************************************************
	// CtrlDat関連
	//***********************************************************************
	@Override
	protected boolean abstractIsComplete() {
		if (textArea.getText().equals("")) {
			return false;
		}
		return true;
	}

	@Override
	protected String abstractToText() {
		return textArea.getText();
	}

	@Override
	protected void abstractFromText(String s) {
		textArea.setText(s);
	}

	@Override
	protected void abstractClear() {
		textArea.setText("");
	}
}
