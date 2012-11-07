package bjd.ctrl;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;

import say.swing.JFontChooser;
import bjd.util.Util;

public final class CtrlFont extends OneCtrl implements ActionListener {

	private boolean isJp;
	private JLabel label;
	private JButton button;
	private Font font = null;

	public CtrlFont(String help, boolean isJp) {
		super(help);
		this.isJp = isJp;
	}

	@Override
	public CtrlType getCtrlType() {
		return CtrlType.FONT;
	}

	@Override
	protected void abstractCreate(Object value) {
		int left = margin;
		int top = margin;

		// ラベルの作成(topの+3は、後のテキストボックスとの高さ調整)
		if (getHelp().length() != 0) {
			label = (JLabel) create(panel, new JLabel(getHelp()), left, top + 3);
			left += label.getWidth() + margin; // オフセット移動
		}

		// ボタンの配置(topの-2は、前のテキストボックスとの高さ調整)
		String buttonText = isJp ? "フォント" : "Font";
		button = (JButton) create(panel, new JButton(buttonText), left, top - 3);
		
		button.addActionListener(this);

		//TODO CtrlFont ボタンの横にフォントの内容をテキスト表示する
		
		//		button.addActionListener(new ActionListener() {
		//			@Override
		//			public void actionPerformed(ActionEvent e) {
		//				JFontChooser dlg = new JFontChooser();
		//				if (font != null) {
		//					dlg.setSelectedFont(font);
		//				}
		//				if (JFontChooser.OK_OPTION == dlg.showDialog(panel)) {
		//					font = dlg.getSelectedFont();
		//					System.out.println("Selected Font : " + font);
		//					
		//					setOnChange();//コントロールの変換
		//				}
		//			}
		//		});

		left += button.getWidth() + margin; // オフセット移動

		//値の設定
		abstractWrite(value);

		// パネルのサイズ設定
		panel.setSize(left + margin, defaultHeight + margin);
	}

	@Override
	protected void abstractDelete() {
		remove(panel, label);
		remove(panel, button);
		label = null;
		button = null;
	}

	//***********************************************************************
	// コントロールの値の読み書き
	//***********************************************************************
	@Override
	protected Object abstractRead() {
		return font;
	}

	@Override
	protected void abstractWrite(Object value) {
		font = (Font) value;
	}

	//***********************************************************************
	// コントロールへの有効・無効
	//***********************************************************************
	protected void abstractSetEnable(boolean enabled) {
		button.setEnabled(enabled);
	}

	//***********************************************************************
	// OnChange関連
	//***********************************************************************
	@Override
	public void actionPerformed(ActionEvent e) {
		JFontChooser dlg = new JFontChooser();
		if (font != null) {
			dlg.setSelectedFont(font);
		}
		if (JFontChooser.OK_OPTION == dlg.showDialog(panel)) {
			font = dlg.getSelectedFont();
			System.out.println("Selected Font : " + font);

			setOnChange(); //コントロールの変換
		}
	}

	//***********************************************************************
	// CtrlDat関連
	//***********************************************************************
	@Override
	protected boolean abstractIsComplete() {
		return (font == null) ? false : true;
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
		Util.runtimeException("未実装");
	}

}
