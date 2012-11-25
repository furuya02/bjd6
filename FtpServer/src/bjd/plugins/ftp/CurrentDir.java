package bjd.plugins.ftp;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import bjd.util.FileSearch;

/**
 * カレントディレクトリ<br>
 * プログラム内部で保持している現在のディレクトリ（\\表記）<br>
 * 最後は必ず\\になるように管理されている<br>
 * <br>
 * @author SIN
 *
 */
final class CurrentDir {
	private String current = "";

	//ユーザのホームディレクトリ
	private String homeDir;

	//仮想フォルダ関連
	private ListMount listMount; //設定一覧
	private OneMount oneMount; //仮想フォルダ外にいる場合 null

	//ホームディレクトリで初期化される
	public CurrentDir(String homeDir, ListMount listMount) {
		this.listMount = listMount;
		if (homeDir.charAt(homeDir.length() - 1) != '\\') {
			this.homeDir = homeDir + "\\";
		} else {
			this.homeDir = homeDir;
		}
		current = this.homeDir;
	}

	//ディレクトリ変更(変更の階層は１つのみ)
	boolean cwd1(String name) {
		boolean isDir = true;
		if (oneMount == null) {
			String path = createPath(current, name, isDir);
			//矛盾が発生した場合は、nullとなる
			if (path != null) {
				//ホームディレクトリ階層下のディレクトリへの移動のみ許可
				if (path.indexOf(homeDir) == 0) {
					//ホームディレクトリより上位のディレクトリへの移動は許可しない
					if (homeDir.length() <= path.length()) {
						//ディレクトリの存在確認          
						if ((new File(path)).exists()) {
							//if (Directory.Exists(path)) {
							current = path;
							return true;
						}
					}
				}
			}
			//仮想フォルダへの移動を確認する
			for (OneMount a : listMount) {
				if (a.isToFolder(current)) {
					if (String.format("%s%s\\", current, a.getName()).equals(path)) {
						current = a.getFromFolder() + "\\";
						oneMount = a;
						return true;
					}
				}
			}
		} else {

			//パラメータから新しいディレクトリ名を生成する
			String path = createPath(current, name, isDir);
			//矛盾が発生した場合は、nullとなる
			if (path != null) {
				//仮想フォルダのマウント先の階層下のディレクトリへの移動のみ許可
				if (path.indexOf(oneMount.getFromFolder()) == 0) {
					//仮想フォルダのマウント先より上位のディレクトリへの移動は許可しない
					if (oneMount.getFromFolder().length() <= path.length()) {
						//ディレクトリの存在確認
						if ((new File(path)).exists()) {
							//if (Directory.Exists(path)) {
							current = path;
							return true;
						}
					}
				}
			}
			//仮想フォルダ外への移動の場合

			//マウント位置を追加して、仮想pathを生成する
			String s = current.substring(oneMount.getFromFolder().length());
			path = String.format("%s\\%s%s", oneMount.getToFolder(), oneMount.getName(), s);

			path = createPath(path, name, isDir);
			//矛盾が発生した場合は、nullとなる
			if (path != null) {
				//ホームディレクトリ階層下のディレクトリへの移動のみ許可
				if (path.indexOf(homeDir) == 0) {
					//ホームディレクトリより上位のディレクトリへの移動は許可しない
					if (homeDir.length() <= path.length()) {
						//ディレクトリの存在確認                  
						if ((new File(path).exists())) {
							//if (Directory.Exists(path)) {
							oneMount = null;
							current = path;
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	//ディレクトリ変更
	public boolean cwd(String paramStr) {
		//失敗した場合、移動しない
		String keepCurrent = current;
		OneMount keepOneMount = oneMount;

		//絶対パス指定の場合、いったんルートまで戻る
		if (paramStr.charAt(0) == '/') {
			if (!cwd1("/")) {
				current = keepCurrent;
				oneMount = keepOneMount;
				return false;
			}
			paramStr = paramStr.substring(1);
		}
<<<<<<< HEAD
		//１階層づつ処理する
		String[] tmp = paramStr.split("[\\|/]");
		for (String name : tmp) { //.Split(new[] { '\\', '/' }, StringSplitOptions.RemoveEmptyEntries)) {
			if (!cwd1(name)) {
				current = keepCurrent;
				oneMount = keepOneMount;
				return false;
=======
		if (!paramStr.equals("")) {
			//１階層づつ処理する
			String[] tmp = paramStr.split("[\\|/]");
			for (String name : tmp) { //.Split(new[] { '\\', '/' }, StringSplitOptions.RemoveEmptyEntries)) {
				if (!cwd1(name)) {
					current = keepCurrent;
					oneMount = keepOneMount;
					return false;
				}
>>>>>>> work
			}
		}
		return true;
	}

	//カレントディレクトリ（表示テキスト表現）
	public String getPwd() {
		String path = current;
		//仮想フォルダ内の場合
		if (oneMount != null) {
			//マウント位置を追加して、仮想pathを生成する
			String s = current.substring(oneMount.getFromFolder().length());
			path = String.format("%s\\%s%s", oneMount.getToFolder(), oneMount.getName(), s);

		}
		//パスのうちホームディレレクトリ部分以降が表示用のカレントディレクトリとなる
		String tmpStr = path.substring(homeDir.length() - 1);
		//表示用に\\を/に置き換える
		//tmpStr = Util.SwapChar('\\', '/', tmpStr);
		tmpStr = tmpStr.replace('\\', '/');
		//ルートディレクト以外は、最後の/を出力しない
		if (!tmpStr.equals("/")) {
			tmpStr = tmpStr.substring(0, tmpStr.length() - 1);
		}
		return tmpStr;
	}

	public String dateStr(Calendar c) {
		String[] monthName = { "", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

		Calendar now = Calendar.getInstance();
		if (now.get(Calendar.YEAR) == c.get(Calendar.YEAR)) {
			return String.format("%s %02d %02d:%02d",
					monthName[c.get(Calendar.MONTH)],
					c.get(Calendar.DATE),
					c.get(Calendar.HOUR_OF_DAY),
					c.get(Calendar.MINUTE));
		}
		return String.format("%s %02d %04d",
				monthName[c.get(Calendar.MONTH)],
				c.get(Calendar.DATE),
				c.get(Calendar.YEAR));
	}

	//ファイル一覧取得
	public ArrayList<String> list(String mask, boolean wideMode) {
		ArrayList<String> ar = new ArrayList<>();

		FileSearch fileSearch = new FileSearch(current);

		//ディレクトリ一覧取得 　*.* の場合は、*を使用する
		String dirMask = (mask.equals("*.*")) ? "*" : mask;
		for (File file : fileSearch.listFiles(dirMask)) {
			if (file.isDirectory()) {
				if (wideMode) {
					Calendar c = Calendar.getInstance();
					c.setTime(new Date(file.lastModified()));
					ar.add(String.format("drwxrwxrwx 1 nobody nogroup 0 %s %s", dateStr(c), file.getName()));
				} else {
					ar.add(file.getName());
				}
			}
		}

		//仮想フォルダ外の場合、仮想フォルダがヒットした時、一覧に追加する
		if (oneMount == null) {
			for (OneMount a : listMount) {
				if (a.isToFolder(current)) {
					if (wideMode) {
						Calendar c = Calendar.getInstance();
						File file = new File(a.getFromFolder());
						c.setTime(new Date(file.lastModified()));
						ar.add(String.format("drwxrwxrwx 1 nobody nogroup 0 %s %s", dateStr(c), a.getName()));
					} else {
						ar.add(a.getName());
					}
				}
			}
		}

		for (File file : fileSearch.listFiles(mask)) {
			if (!file.isDirectory()) {
				if (wideMode) {
					Calendar c = Calendar.getInstance();
					c.setTime(new Date(file.lastModified()));
					ar.add(String.format("-rwxrwxrwx 1 nobody nogroup %d %s %s", file.length(), dateStr(c),
							file.getName()));
				} else {
					ar.add(file.getName());
				}
			}
		}
		return ar;
	}

	//string strの中の文字 c文字が連続している場合1つにする 
	public String margeChar(char c, String str) {
		char[] buf = new char[] { c, c };
		String tmpStr = new String(buf);

		while (true) {
			int index = str.indexOf(tmpStr);
			if (index < 0) {
				break;
			}
			str = str.substring(0, index) + str.substring(index + 1);
		}
		return str;
	}

	/**
	 * パスの生成<br>
	 * 失敗した時nullが返される<br>
	 * 
	 * @param path 元のパス
	 * @param param 追加するパス
	 * @param isDir ディレクトリかどうか
	 * @return 生成されたパス
	 */
	public String createPath(String path, String param, boolean isDir) {
		//特別処理（後程リファクタリングの対象とする）
		if (path == null) {
			path = current;
		}

		// 「homeDir」「CurrentDir」及び「newDir」は '\\' 区切り 「param」は、'/'区切りで取り扱われる

		//paramの'/'を'\\'に変換する
		//param = Util.SwapChar('/', '\\', param);
		param = param.replace('/', '\\');

		//paramの'\\'が連続している個所を１つにまとめる
		param = margeChar('\\', param);

		if (isDir) {
			//パラメータの最後が\\でない場合は付加する
			if (param.charAt(param.length() - 1) != '\\') {
				param = param + "\\";
			}
		}

		//相対パスで指定されている場合
		String tmpPath = path + param;
		// フルパスで指定されている場合
		if (param.charAt(0) == '\\') {
			tmpPath = homeDir + param;
		}

		//isDir==falseの時、ディレクトリ + ファイル名として処理する(FileName には..の処理をしない)
		String dir = tmpPath;
		String fileName = "";
		if (!isDir) {
			int index = dir.lastIndexOf('\\');
			if (index < 0) {
				return null;
			}
			fileName = dir.substring(index + 1);
			dir = dir.substring(0, index + 1);
		}
		// .. を処理する
		while (true) {
			int p1 = dir.indexOf("..");
			if (p1 < 0) {
				break;
			}
			//..の前の最後の\\を消した文字列を作業対象にする
			String tmpStr = dir.substring(0, p1 - 1);
			int p2 = tmpStr.lastIndexOf('\\');
			if (p2 < 0) {
				return null;
			}
			//最後の\\の前までを残す
			tmpStr = tmpStr.substring(0, p2);
			if (dir.length() > p1 + 2) {
				tmpStr = tmpStr + dir.substring(p1 + 2); //..の2文字を消して、..以降の文字列を戻す
			}
			dir = tmpStr;
		}
		String newPath = dir + fileName;

		//newDirの'\\'が連続している個所を１つにまとめる
		newPath = margeChar('\\', newPath);
		//\\.\\は\\にまとめる
		//newPath = Util.SwapStr("\\.\\", "\\", newPath);
		newPath = newPath.replace("\\.\\", "\\");
		//先頭の"."は削除する
		//if (newDir.Length >= 2 && newDir[0] == '.' && newDir[1] != '.') {
		//    newDir = newDir.substring(1);
		//}

		return newPath;
	}
}
