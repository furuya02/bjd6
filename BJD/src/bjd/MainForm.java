package bjd;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import bjd.ctrl.ListView;
import bjd.ctrl.StatusBar;
import bjd.option.Conf;
import bjd.util.Msg;
import bjd.util.MsgKind;
import bjd.util.Util;
import bjd.wait.IWaitDlg;
import bjd.wait.WaitDlg;

public final class MainForm implements WindowListener {

	private JFrame frame;
	private Kernel kernel;

	public JFrame getFrame() {
		return frame;
	}

	/**
	 * アプリケーション起動
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainForm window = new MainForm();
					window.frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	private MainForm() {
		initialize();

		//*************************************************
		//UI設定
		//例外が発生した場合も、起動プロセスはそのまま継続する
		//*************************************************
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		//*************************************************
		//画面構成
		//*************************************************
		ListView listView = new ListView("listViewLog");
<<<<<<< HEAD
<<<<<<< HEAD
		listView.addColumn("日時");
		listView.addColumn("種類");
		listView.addColumn("スレッドID");
		listView.addColumn("機能（サーバ）");
		listView.addColumn("アドレス");
		listView.addColumn("メッセージID");
		listView.addColumn("説明");
		listView.addColumn("詳細情報");
		listView.setColWidth(0, 120);
		listView.setColWidth(1, 60);
		listView.setColWidth(2, 60);
		listView.setColWidth(3, 80);
		listView.setColWidth(4, 80);
		listView.setColWidth(5, 70);
		listView.setColWidth(6, 200);
		listView.setColWidth(7, 300);
=======
=======
>>>>>>> work
//		listView.addColumn("日時");
//		listView.addColumn("種類");
//		listView.addColumn("スレッドID");
//		listView.addColumn("機能（サーバ）");
//		listView.addColumn("アドレス");
//		listView.addColumn("メッセージID");
//		listView.addColumn("説明");
//		listView.addColumn("詳細情報");
//		listView.setColWidth(0, 120);
//		listView.setColWidth(1, 60);
//		listView.setColWidth(2, 60);
//		listView.setColWidth(3, 80);
//		listView.setColWidth(4, 80);
//		listView.setColWidth(5, 70);
//		listView.setColWidth(6, 200);
//		listView.setColWidth(7, 300);
<<<<<<< HEAD
>>>>>>> work
=======
>>>>>>> work

		JMenuBar menuBar = new JMenuBar();
		StatusBar bar = new StatusBar();

		frame.setJMenuBar(menuBar);
		frame.getContentPane().add(listView);
		frame.getContentPane().add(bar, BorderLayout.PAGE_END);


		//*************************************************
		// kernel初期化
		//*************************************************
		kernel = new Kernel(this, listView, menuBar);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("\u30BF\u30A4\u30C8\u30EB");
		frame.setBounds(100, 100, 746, 368);
		frame.addWindowListener(this);

		//×を押したときにWindowClosingを発生さた後、ウインドウを閉じる
		//mainForm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//×を押したときにWindowClosingを発生させる（終了はしない）
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}

	//終了処理
	public void exit() {

		//終了確認
		Conf conf = kernel.createConf("Basic");
		boolean useExitDlg = (boolean) conf.get("useExitDlg");
		if (useExitDlg) {
			if (0 != Msg.show(MsgKind.QUESTION, kernel.isJp() ? "プログラムを終了してよろしいですか" : "May I finish a program?")) {
				return; //キャンセル
			}
		}
		kernel.dispose();
		System.exit(0);

	}

	public void test() {
		final WaitDlg waitDlg = new WaitDlg(frame, "暫くお待ちください", 500);
		waitDlg.start(new IWaitDlg() {
			public boolean loop(int i) {
				String msg = String.format("i=%d", i);
				System.out.println(msg);
				waitDlg.setLabel(msg);
				Util.sleep(3);
				return true;
			}
		});
	}

	public void test2() {
		//カレントフォルダ表示
		Msg.show(MsgKind.INFOMATION, kernel.getProgDir());
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		//System.out.println(String.format("mainForm.windowActivated()"));
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		//System.out.println(String.format("mainForm.windowClosed()"));
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		//System.out.println(String.format("mainForm.windowClosing()"));
		exit(); //終了
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		//System.out.println(String.format("mainForm.windowDeactivated()"));
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		//System.out.println(String.format("mainForm.windowDeiconified()"));
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		//System.out.println(String.format("mainForm.windowIconified()"));
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		//System.out.println(String.format("mainForm.windowOpend()"));
	}

}
