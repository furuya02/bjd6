package bjd;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import bjd.util.Util;

/**
 * 下部パネルに「OK」及び「キャンセル」を表示するダイアログボックスの基底クラス<br>
 * 
 * @author SIN
 *
 */
@SuppressWarnings("serial")
public abstract class Dlg extends JDialog implements WindowListener, ActionListener {

	// ダイアログの戻り値（OKボタンで閉じたかどうか）
	private boolean isOk = false;
	private JPanel mainPanel = new JPanel();

	/**
	 * 
	 * @param frame　親フレーム
	 * @param width 幅
	 * @param height　高さ
	 */
	public Dlg(JFrame frame, int width, int height) {
		super(frame);
		setSize(width, height);
		setLocationRelativeTo(frame); // 親ウインドウの中央）

		// WindowListenerとして自分自身を登録
		addWindowListener(this);

		// ESCキーで閉じる
		InputMap imap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
		getRootPane().getActionMap().put("escape", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		mainPanel.setLayout(null);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mainPanel, BorderLayout.CENTER);

		// ボタンパネル
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		// OK　ボタン
		JButton btn = Util.createButton(buttonPanel, "OK", "OK", this, 75);
		getRootPane().setDefaultButton(btn);
		// Cancel　ボタン
		Util.createButton(buttonPanel, "Cancel", "CANCEL", this, 75);
	}

	/**
	 * ダイログボックスのパネル
	 * @return
	 */
	protected final JPanel getMainPanel() {
		return mainPanel;
	}

	/**
	 * モーダル表示
	 * 
	 * @return OKボタンで終了した場合trueが返る
	 */
	public final boolean showDialog() {
		setModal(true);
		setVisible(true);
		return isOk;
	}

	/**
	 * OKボタンが押された時のアクション
	 * 
	 * @return falseが返された場合、ダイアログのクローズはキャンセルされる
	 */
	protected abstract boolean onOk();

	/**
	 * ActionListenerからの継承
	 */
	@Override
	public final void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("OK")) {
			if (!onOk()) {
				return;
			}
			isOk = true;
		}
		dispose();

	}

	/**
	 * WindowListenerからの継承
	 */
	@Override
	public final void windowActivated(WindowEvent arg0) {
		// System.out.println("windowActivated");

	}

	/**
	 * WindowListenerからの継承
	 */
	@Override
	public void windowClosed(WindowEvent arg0) {
		// System.out.println("windowClosed");

	}

	/**
	 * WindowListenerからの継承
	 */
	@Override
	public final void windowClosing(WindowEvent arg0) {
		// System.out.println("windowClosing");
		// ×ボタンが押されたとき、ここしか通らないので、ここで破棄する
		dispose();
	}

	/**
	 * WindowListenerからの継承
	 */
	@Override
	public final void windowDeactivated(WindowEvent arg0) {
		// System.out.println("windowDeactivated");

	}

	/**
	 * WindowListenerからの継承
	 */
	@Override
	public final void windowDeiconified(WindowEvent arg0) {
		// System.out.println("windowDeiconified");

	}

	/**
	 * WindowListenerからの継承
	 */
	@Override
	public final void windowIconified(WindowEvent arg0) {
		// System.out.println("windowIconified");

	}

	/**
	 * WindowListenerからの継承
	 */
	@Override
	public final void windowOpened(WindowEvent arg0) {
		// System.out.println("windowOpened");
	}
}
