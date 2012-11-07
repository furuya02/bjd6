package bjd.wait;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 * 「しばらくお待ちください」のプログレスバー付きのダイアログを表示する
 * 
 * 使用方法
 * public void test() {
 *   final WaitDlg waitDlg = new WaitDlg(frame, "暫くお待ちください", 500);
 *   waitDlg.start(new IWaitDlg() {
 * 		public boolean loop(int i) {
 * 			String msg = String.format("i=%d", i);
 * 			System.out.println(msg);
 * 			waitDlg.setLabel(msg);//ラベル表示(この他にもWaitDlgのpublicメソッドにアクセス可能)
 * 			try {
 * 				Thread.sleep(10);
 * 			} catch (InterruptedException ie) {
 * 				System.out.println(String.format("Interrupted"));
 * 			}
 * 			return true; // return falseで直ちに終了
 * 		}
 * });
 * 
 * @author SIN
 *
 */
@SuppressWarnings("serial")
public final class WaitDlg extends JDialog implements WindowListener {

	private static int width = 560;
	private static int height = 125;

	private boolean life = true;
	private JProgressBar progressBar = null;
	private JLabel label = null;
	private int max;
	private JFrame frame;

	/**
	 * 表示中のダイアログから使用される<br>
	 * 利用者が使用することはない
	 * 
	 * @return
	 */
	public boolean getLife() {
		return life;
	}

	/**
	 * コンストラクタ
	 * @param frame 親フレーム
	 * @param title タイトル
	 * @param max ループが呼び出される回数
	 */
	public WaitDlg(JFrame frame, String title, int max) {
		super(frame);
		
		this.frame = frame;
		this.max = max;
		
		getContentPane().setLayout(null);
		setTitle(title);
		setSize(width, height);
		setLocationRelativeTo(frame); // 親ウインドウの中央）
		addWindowListener(this); // WindowListenerとして自分自身を登録

		
		JButton btnNewButton = new JButton("キャンセル");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				stop();
			}
		});
		btnNewButton.setBounds(231, 57, 91, 21);
		getContentPane().add(btnNewButton);

		progressBar = new JProgressBar(0, max);
		progressBar.setBounds(12, 33, 521, 14);
		getContentPane().add(progressBar);

		label = new JLabel("New label");
		label.setBounds(12, 10, 50, 13);
		getContentPane().add(label);
	}
	/**
	 * ラベルの変更
	 * @param msg
	 */
	public void setLabel(String msg) {
		label.setText(msg);
	}
	
	private SwingWorker<Void, Integer> worker = null;

	/**
	 * 開始
	 * @param waitDlg
	 */
	public void start(final IWaitDlg waitDlg) {
		frame.setEnabled(false); // 親ウインドウの無効化
		setVisible(true);
		worker = new SwingWorker<Void, Integer>() {
			@Override
			public Void doInBackground() {
				for (int i = 0; i < max && life; i++) {
					if (!waitDlg.loop(i)) { //外部から指定されたファンクション
						break;
					}
					publish(i);
				}
				stop();
				return null;
			}
			@Override
			public void process(java.util.List<Integer> i) {
				progressBar.setValue(i.get(0));
				//bar.paintImmediately(0,0,200,200);強制描画
            }
        };
        worker.execute();
	}

	/**
	 * 停止
	 */
	@SuppressWarnings("deprecation")
	public void stop() {
		life = false;
		setVisible(false);
		frame.setEnabled(true); // 親ウインドウの有効化
		frame.show(true);
	}


	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		//×で閉じられた
		stop();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}

}
