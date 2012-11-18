package bjd.ctrl;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

import bjd.ValidObjException;
import bjd.net.Ip;
import bjd.net.IpKind;
import bjd.util.Util;

/**
 * IPv4アドレス　コントロール
 * @author SIN
 *
 */
public final class CtrlAddress extends OneCtrl implements DocumentListener {

	private JLabel label = null;
	private JTextField[] textFieldList = null;

	/**
	 * 
	 * @param help 表示テキスト
	 */
	public CtrlAddress(String help) {
		super(help);
	}
	
	/**
	 * コントロールのタイプ取得
	 */
	@Override
	public CtrlType getCtrlType() {
		return CtrlType.ADDRESSV4;
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
		textFieldList = new JTextField[4];
		int digits = 3;
		for (int i = 0; i < 4; i++) {
			textFieldList[i] = (JTextField) create(panel, new JTextField(digits), left, top);
			textFieldList[i].getDocument().addDocumentListener(this);
			left += 5;
			((AbstractDocument) textFieldList[i].getDocument()).setDocumentFilter(new IntegerDocumentFilter(digits));
			left += textFieldList[i].getWidth(); // オフセット移動
		}

		//値の設定
		abstractWrite(value);

		// パネルのサイズ設定
		panel.setSize(left + margin, defaultHeight + margin);
	}

	@Override
	protected void abstractDelete() {
		remove(panel, label);
		for (int i = 0; i < 4; i++) {
			remove(panel, textFieldList[i]);
			textFieldList[i] = null;
		}
		label = null;
		textFieldList = null;
	}

	//***********************************************************************
	// コントロールの値の読み書き
	//***********************************************************************
	@Override
	protected void abstractWrite(Object value) {
		Ip ip = (Ip) value;
		byte[] ipV4 = ip.getIpV4();
		for (int i = 0; i < 4; i++) {
			textFieldList[i].setText(String.valueOf((ipV4[i] & 0xff)));
		}
	}

	@Override
	protected Object abstractRead() {
		try {
			String ipStr = String.format("%d.%d.%d.%d", Integer.valueOf(textFieldList[0].getText()), Integer.valueOf(textFieldList[1].getText()),
					Integer.valueOf(textFieldList[2].getText()), Integer.valueOf(textFieldList[3].getText()));
			return new Ip(ipStr);
		} catch (Exception e) {
			//ここでの例外は、設計の問題
			Util.runtimeException(this, e);
			return null;
		}
	}

	//***********************************************************************
	// コントロールへの有効・無効
	//***********************************************************************
	protected void abstractSetEnable(boolean enabled) {
		for (int i = 0; i < 4; i++) {
			if (textFieldList[i] != null) {
				textFieldList[i].setEditable(enabled);
			}
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
		for (int i = 0; i < 4; i++) {
			if (textFieldList[i].getText().equals("")) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected String abstractToText() {
		Ip ip = (Ip) abstractRead();
		return ip.toString();
	}

	@Override
	protected void abstractFromText(String s) {
		Ip ip = null;
		try {
			ip = new Ip(s);
		} catch (ValidObjException e) {
			ip = new Ip(IpKind.V4_0);
		}
		abstractWrite(ip);
	}

	@Override
	protected void abstractClear() {
		for (int i = 0; i < 4; i++) {
			textFieldList[i].setText("0");
		}
	}
}
