package bjd.option;

import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import bjd.Dlg;

/**
 * オプション設定用ダイアログ
 * 
 * @author SIN
 *
 */
@SuppressWarnings("serial")
public final class OptionDlg extends Dlg {

	private OneOption oneOption;

	/**
	 * @param frame　親フレーム
	 * @param oneOption 対象オプション
	 */
	public OptionDlg(JFrame frame, OneOption oneOption) {
		super(frame, width(), height());

		this.oneOption = oneOption;

		//ダイアログ作成時の処理
		oneOption.createDlg(getMainPanel());

		//		JList listBox = new JList(new String[]{"1","2","3","4","5","6","7","8","9","10"});
		//		JScrollPane srl = new JScrollPane(listBox);
		//		srl.setSize(100,100);
		//		mainPanel.add(srl);

		/*
		 * //メニューの項目名をダイアログのタイトルにする var text =
		 * kernel.Jp?oneOption.JpMenu:oneOption.EnMenu;
		 * 
		 * var index = text.LastIndexOf(','); Text = index != 0 ?
		 * text.Substring(index+1) : text; //(&R)のようなショートカット指定を排除する index =
		 * Text.IndexOf('('); if (0 <= index) { Text = Text.Substring(0, index);
		 * } //&を排除する Text = Util.SwapChar('&','\b',Text);
		 * 
		 * _oneOption = oneOption; oneOption.DlgCreate(panelMain);
		 * 
		 * buttonCancel.Text = (kernel.Jp) ? "キャンセル" : "Cancel";
		 */
	}

	/**
	 * オプションダイアログのサイズ（定数）
	 * @return 幅
	 */
	public static int width() {
		return 600;
	}

	/**
	 * オプションダイアログのサイズ（定数）
	 * @return 高さ
	 */
	public static int height() {
		return 500;
	}

	/**
	 * ダイアログ破棄時の処理<br>
	 * WindowListenerから継承<br>
	 */
	@Override
	public void windowClosed(WindowEvent arg0) {
		oneOption.deleteDlg();
	}

	/**
	 * ダイアログでOKボタンが押された時の処理
	 */
	@Override
	protected boolean onOk() {
		boolean isComfirm = true; // コントロールのデータが全て正常に読めるかどうかの確認(エラーの場合は、ポップアップ表示)
		if (!oneOption.onOk(isComfirm)) {
			return false;
		}
		return oneOption.onOk(false); //値の読み込み
	}
}
