package bjd.util;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

/**
 * 各種ユーティリティ
 * @author SIN
 *
 */
public final class Util {

	private Util() {
		// コンストラクタの隠蔽
	}

	/**
	 * ボタンコントロールの生成
	 * @param owner
	 * @param text
	 * @param actionCommand
	 * @param actionListener
	 * @param width
	 * @return
	 */
	public static JButton createButton(JComponent owner, String text, String actionCommand, ActionListener actionListener, int width) {
		JButton btn = new JButton(text);
		btn.setActionCommand(actionCommand);
		btn.addActionListener(actionListener);
		btn.setPreferredSize(new Dimension(75, 25));
		owner.add(btn);
		return btn;
	}

	/**
	 * 設計上の問題によりプログラム停止
	 * @param msg
	 */
	public static void runtimeException(String msg) {
		Msg.show(MsgKind.ERROR, msg);
		System.exit(-1);
		throw new RuntimeException("RuntimeException" + msg);
	}

	/**
	 * ２つの配列の結合
	 * @param a
	 * @param b
	 * @return
	 */
	public static <T> ArrayList<T> merge(T[] a, T[] b) {
		ArrayList<T> ar = new ArrayList<>();
		for (T o : a) {
			ar.add(o);
		}
		for (T o : b) {
			ar.add(o);
		}
		return ar;
	}

	/**
	 * ファイル選択ダイログの表示<br>
	 * 前回の選択を記憶している
	 */
	private static File selectedFile = null; //前回選択したファイル
	public static File fileChooser(File file) {
		JFileChooser dlg = new JFileChooser();
		//初期化
		if (file != null) { //ファイルの指定がある場合は、それを使用する
			dlg.setSelectedFile(file);
		} else if (selectedFile != null) { //前回選択したものがある場合は、それを使用する
			dlg.setSelectedFile(selectedFile);
		}
		if (dlg.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			selectedFile = dlg.getSelectedFile();
			return selectedFile;
		}
		return null;
	}
	/**
	 * テキストファイルを読み込んで、文字列リストに格納する<br>
	 * このメソッドを使用する前に、ファイルの有効性を確認してあれば、(file.exists() && file.isFile() && file.canRead())<br>
	 * 例外が発生時に、RuntailExceptionとして処理できる<br>
	 * 
	 * @param file 対象ファイル
	 * @return　文字列リスト
	 * @throws IOException ファイルが存在しない、読み込み失敗　など
	 */
	public static ArrayList<String> textFileRead(File file) throws IOException {
		ArrayList<String> lines = new ArrayList<>();
		if (file.exists()) {
			BufferedReader br = new BufferedReader(new FileReader(file));
			while (true) {
				String str = br.readLine();
				if (str == null) {
					break;
				}
				lines.add(str);
			}
			br.close();
		}
		return lines;
	}

	/**
	 * テキストファイルの保存
	 * @param file
	 * @param lines
	 * @return
	 */
	public static boolean textFileSave(File file, ArrayList<String> lines) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for (String l : lines) {
				bw.write(l);
				bw.newLine();
			}
			bw.close();
		} catch (Exception ex) {
			System.out.println(ex);
			return false;
		}
		return true;
	}

	/**
	 * ディレクトリを指定した場合、内部のファイルもすべて削除する
	 * @param file
	 */
	public static void fileDelete(File file) {
		if (!file.exists()) {
			return;
		}

		if (file.isFile()) {
			file.delete();
		}

		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				fileDelete(f); //再帰処理
			}
			file.delete();
		}
	}

	/**
	 * ファイルのコピー
	 * @param src
	 * @param dest
	 */
	public static void fileCopy(File src, File dest) {

		FileChannel rs = null;
		FileChannel ws = null;

		try {
			rs = new FileInputStream(src).getChannel();
			ws = new FileOutputStream(dest).getChannel();
			rs.transferTo(0, rs.size(), ws);

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (IOException e) {
				}
			}
			if (ws != null) {
				try {
					ws.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
