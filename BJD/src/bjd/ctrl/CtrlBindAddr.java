package bjd.ctrl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import bjd.net.BindAddr;
import bjd.net.BindStyle;
import bjd.net.Ip;
import bjd.util.Util;

/**
 *　バインドアドレス コントロール
 * @author SIN
 *
 */
public final class CtrlBindAddr extends OneCtrl implements ActionListener {

	private JLabel label = null;
	private JRadioButton[] radioButtonList = new JRadioButton[3];
	private JLabel[] labelList = new JLabel[2];
	@SuppressWarnings("rawtypes")
	private JComboBox[] comboBoxList = new JComboBox[2];
	private Ip[] listV4;
	private Ip[] listV6;

	/**
	 * 
	 * @param help 表示テキスト
	 * @param listV4　IPv4アドレスのリスト
	 * @param listV6　IPv6アドレスのリスト
	 */
	public CtrlBindAddr(String help, Ip[] listV4, Ip[] listV6) {
		super(help);
		this.listV4 = listV4;
		this.listV6 = listV6;
	}

	/**
	 * コントロールのタイプ取得
	 */
	@Override
	public CtrlType getCtrlType() {
		return CtrlType.BINDADDR;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void abstractCreate(Object value) {

		int left = margin;
		int top = margin;

		// ラベルの作成 top+3 は後のテキストボックスとの整合のため
		label = (JLabel) create(panel, new JLabel(getHelp()), left, top + 3);
		// label.setBorder(new LineBorder(Color.RED, 2, true)); //Debug 赤枠
		left += label.getWidth() + margin; // オフセット移動

		//ラジオボタンの配置
		ButtonGroup buttonGroup = new ButtonGroup(); // ボタンのグループ化
		String[] protoList = new String[] { "IPv4", "IPv6", "Dual" };
		for (int i = 0; i < 3; i++) {
			radioButtonList[i] = (JRadioButton) create(panel, new JRadioButton(protoList[i]), left + i * 65, top);
			radioButtonList[i].addActionListener(this);
			buttonGroup.add(radioButtonList[i]);
		}
		radioButtonList[0].setSelected(true);
		top += defaultHeight + 2; // オフセット移動

		//ComBox配置
		String[] labelStr = new String[] { "IPv4", "IPv6" };
		for (int i = 0; i < 2; i++) {
			left = margin;
			labelList[i] = (JLabel) create(panel, new JLabel(labelStr[i]), left, top);
			left += labelList[i].getWidth() + margin; // オフセット移動

			// コンボボックスの配置
			comboBoxList[i] = (JComboBox) create(panel, new JComboBox(), left, top);
			int w = 80;
			for (Ip ip : (i == 0) ? listV4 : listV6) {
				String ipStr = ip.toString();
				comboBoxList[i].addItem(ipStr);
				if ((ipStr.length() * 12) > w) {
					w = ipStr.length() * 12;
				}
			}
			comboBoxList[i].setSize(w, comboBoxList[i].getHeight());
			comboBoxList[i].addActionListener(this);

			top += defaultHeight + 2;
		}
		// オフセット移動
		left += labelList[1].getWidth() + comboBoxList[1].getWidth();
		if (left < 330) {
			left = 330; //最低でもRadioButtnで330は必要
		}

		//値の設定
		abstractWrite(value);

		radioButtonCheckedChanged();

		// パネルのサイズ設定
		panel.setSize(left + margin, top + margin);
	}

	@Override
	protected void abstractDelete() {
		remove(panel, label);
		label = null;
		for (int i = 0; i < 3; i++) {
			remove(panel, radioButtonList[i]);
			radioButtonList[i] = null;
		}
		for (int i = 0; i < 2; i++) {
			remove(panel, comboBoxList[i]);
			comboBoxList[i] = null;
			remove(panel, labelList[i]);
			labelList[i] = null;
		}
	}

	//***********************************************************************
	// コントロールの値の読み書き
	//***********************************************************************
	@Override
	protected Object abstractRead() {
		BindStyle byndStyle = BindStyle.V46DUAL;
		if (radioButtonList[0].isSelected()) {
			byndStyle = BindStyle.V4ONLY;
		} else if (radioButtonList[1].isSelected()) {
			byndStyle = BindStyle.V6ONLY;
		}
		Ip ipV4 = listV4[comboBoxList[0].getSelectedIndex()];
		Ip ipV6 = listV6[comboBoxList[1].getSelectedIndex()];

		return new BindAddr(byndStyle, ipV4, ipV6);
	}

	@Override
	protected void abstractWrite(Object value) {
		if (value == null) {
			return;
		}
		BindAddr bindAddr = (BindAddr) value;
		switch (bindAddr.getBindStyle()) {
			case V4ONLY:
				radioButtonList[0].setSelected(true);
				break;
			case V6ONLY:
				radioButtonList[1].setSelected(true);
				break;
			case V46DUAL:
				radioButtonList[2].setSelected(true);
				break;
			default:
				Util.runtimeException(String.format("bindAddr.getBindStyle()=%s", bindAddr.getBindStyle()));
		}
		for (int i = 0; i < 2; i++) {
			Ip[] list = (i == 0) ? listV4 : listV6;
			Ip ip = (i == 0) ? bindAddr.getIpV4() : bindAddr.getIpV6();
			int index = -1;
			for (int n = 0; n < list.length; n++) {
				if (list[n].equals(ip)) {
					index = n;
					break;
				}
			}
			if (index == -1) {
				index = 0;
			}
			comboBoxList[i].setSelectedIndex(index);
		}
		setDisable(); //無効なオプションの表示

	}

	private void radioButtonCheckedChanged() {
		comboBoxList[0].setEnabled(true);
		comboBoxList[1].setEnabled(true);

		if (radioButtonList[0].isSelected()) { //IpV4 only
			comboBoxList[1].setEnabled(false);
		} else if (radioButtonList[1].isSelected()) { //IpV6 only
			comboBoxList[0].setEnabled(false);
		}
		setDisable(); //無効なオプションの表示
	}

	//無効なオプションの表示
	private void setDisable() {
		if (comboBoxList[0].getItemCount() == 1) { //IPv4無効
			radioButtonList[0].setEnabled(false);
			radioButtonList[2].setEnabled(false);
			comboBoxList[0].setEditable(false);
			comboBoxList[0].setSelectedIndex(0);
		}
		if (comboBoxList[1].getItemCount() == 1) { //IPv6無効
			radioButtonList[1].setEnabled(false);
			radioButtonList[2].setEnabled(false);
			comboBoxList[1].setEditable(false);
			comboBoxList[1].setSelectedIndex(0);
		}
	}

	//***********************************************************************
	// コントロールへの有効・無効
	//***********************************************************************
	protected void abstractSetEnable(boolean enabled) {
		for (int i = 0; i < 3; i++) {
			radioButtonList[i].setEnabled(enabled);
		}
		for (int i = 0; i < 2; i++) {
			comboBoxList[i].setEnabled(enabled);
		}
		setDisable(); //無効なオプションの表示
	}

	//***********************************************************************
	// OnChange関連
	//***********************************************************************
	@Override
	public void actionPerformed(ActionEvent e) {
		setOnChange();
		radioButtonCheckedChanged(); //ラジオボタンの変化によってコントロールの有効無効を設定する
	}

	//***********************************************************************
	// CtrlDat関連
	//***********************************************************************
	@Override
	protected boolean abstractIsComplete() {
		//未設定状態は存在しない
		return true;
	}

	@Override
	protected String abstractToText() {
		Util.runtimeException("未実装");
		return "";
	}

	@Override
	protected void abstractFromText(String s) {
		Util.runtimeException("未実装");
	}

	@Override
	protected void abstractClear() {
		radioButtonList[0].setSelected(true);
		comboBoxList[0].setSelectedIndex(0);
		comboBoxList[1].setSelectedIndex(0);
	}
}
