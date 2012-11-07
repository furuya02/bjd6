package bjd;

import java.io.IOException;

import javax.swing.JFrame;

import bjd.ctrl.ListView;
import bjd.option.Conf;
import bjd.util.IDispose;
import bjd.util.Util;

/**
 * ウインドウサイズ及びGridViewのカラム幅を記憶するクラス<br>
 * 存在しないファイルを指定した場合は新規作成される<br>
 * 保存ファイルのIOエラーが発生した場合は、例外（IOException）が発生する<br>
 * 例外発生以後は、このオブジェクトを使用しても、何も処理されない
 * 
 * @author SIN
s */
public final class WindowSize implements IDispose {

	//Windowの外観を保存・復元する
	private Conf conf;
	private Reg reg; //記録する仮想レジストリ

	/**
	 * @param conf オプション設定
	 * @param path 保存ファイル
	 * @throws IOException 保存ファイルのIOエラー　(存在しない場合は新規作成されるので例外とはならない)
	 */
	public WindowSize(Conf conf, String path) throws IOException {
		this.conf = conf;
		//ウインドサイズ等を記録する仮想レジストリ
		try {
			reg = new Reg(path);
		} catch (IOException e) {
			reg  = null; // reg=nullとし、事後、アクセス不能とする
			throw e;
		}
	}

	/**
	 * 終了処理<br>
	 * Regが保存される<br>
	 */
	@Override
	public void dispose() {
		if (reg == null) { //初期化に失敗している
			return;
		}
		//明示的に呼ばないと、保存されない
		reg.dispose(); //Regの保存
	}

	/**
	 * ウインドウサイズの復元
	 * @param frame 対象フレーム
	 */
	public void read(JFrame frame) {
		if (reg == null) { //初期化に失敗している
			return;
		}
		if (frame == null) {
			return;
		}
		if (conf == null) {
			return; //リモート操作の時、ここでオプション取得に失敗する
		}

		boolean useLastSize = (boolean) conf.get("useLastSize");
		if (!useLastSize) {
			return;
		}

		String n = frame.getTitle();
		int w = 0;
		int h = 0;
		try {
			w = reg.getInt(String.format("%s_width", n));
			h = reg.getInt(String.format("%s_hight", n));
		} catch (RegException ex) {
			w = -1;
			h = -1;
		}
		if (h <= 0) {
			h = 400;
		}
		if (w <= 0) {
			w = 800;
		}
		frame.setSize(w, h);

		try {
			int y = reg.getInt(String.format("%s_top", n));
			int x = reg.getInt(String.format("%s_left", n));
			if (y <= 0) {
				y = 0;
			}
			if (x <= 0) {
				x = 0;
			}
			frame.setLocation(x, y);
		} catch (RegException ex) {
			// 読み込めない場合は、何も処理しない

		}
	}

	/**
	 * カラム幅の復元
	 * @param listView 対象ListView
	 */
	public void read(ListView listView) {
		if (reg == null) { //初期化に失敗している
			return;
		}
		if (listView == null) {
			return;
		}
		if (conf == null) {
			return; //リモート操作の時、ここでオプション取得に失敗する
		}

		boolean useLastSize = (boolean) conf.get("useLastSize");
		if (!useLastSize) {
			return;
		}
		for (int i = 0; i < listView.getColumnCount(); i++) {
			String key = String.format("%s_col-%03d", listView.getName(), i);
			try {
				int width = reg.getInt(key);
				if (width <= 0) {
					width = 100; //最低100を確保する
				}
				listView.setColWidth(i, width);
			} catch (RegException ex) {
				listView.setColWidth(i, 100); //デフォルト値
			}
		}
	}


	/**
	 * ウインドウサイズの保存
	 * @param frame 対象フレーム
	 */
	public void save(JFrame frame) {
		if (reg == null) { //初期化に失敗している
			return;
		}
		if (frame == null) {
			return;
		}
		String n = frame.getTitle();
		int w = frame.getWidth();
		int h = frame.getHeight();
		int x = frame.getX();
		int y = frame.getY();
		try {
			reg.setInt(String.format("%s_width", n), w);
			reg.setInt(String.format("%s_hight", n), h);
			reg.setInt(String.format("%s_top", n), y);
			reg.setInt(String.format("%s_left", n), x);
		} catch (RegException e) {
			Util.runtimeException("WindowSize.save()");
		}

		//		if (form.WindowState == FormWindowState.Normal) {
		//			reg.SetInt(String.format("%s_width", form.Text), form.Width);
		//			reg.SetInt(String.format("%s_hight", form.Text), form.Height);
		//
		//			//Ver5.5.3 終了位置の保存
		//			reg.SetInt(String.format("%s_top", form.Text), form.Top);
		//			reg.SetInt(String.format("%s_left", form.Text), form.Left);
		//		}

	}

	/**
	 * カラム幅の保存
	 * 
	 * @param listView　対象ListView
	 */
	public void save(ListView listView) {
		if (reg == null) { //初期化に失敗している
			return;
		}
		if (listView == null) {
			return;
		}
		for (int i = 0; i < listView.getColumnCount(); i++) {
			String key = String.format("%s_col-%03d", listView.getName(), i);
			try {
				reg.setInt(key, listView.getColWidth(i));
			} catch (RegException e) {
				Util.runtimeException("WindowSaze.save()");
			}
		}
	}
}
