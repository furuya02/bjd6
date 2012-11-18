package bjd.util;

import java.io.File;
import java.util.ArrayList;

/**
 * ワイルドカードを使用してファイルを列挙するクラ
 * @author SIN
 *
 */
public final class FileSearch {

	private String path;
	private ArrayList<File> ar;

	/**
	 * コンストラクタ
	 * @param path 対象パス
	 */
	public FileSearch(String path) {
		this.path = path;
	}

	/**
	 * ワイルドカードでパターンを指定して一覧を取得
	 * @param pattern パターン指定（*.txtなど）
	 * @return Fileの一覧
	 * @param pattern
	 */
	public File[] listFiles(String pattern) {
		return listFiles(pattern, false);
	}

	/**
	 * ワイルドカードでパターンを指定して一覧を取得(再帰処理して階層下まで取得する)
	 * @param pattern パターン指定（*.txtなど）
	 * @return Fileの一覧
	 * @param pattern
	 */
	public File[] listFilesRecursive(String pattern) {
		return listFiles(pattern, true);
	}
	
	/**
	 * listFilesの共通処理
	 * @param pattern パターン指定（*.txtなど）
	 * @param recursive 再起処理を行うかどうか
	 * @return Fileの一覧
	 */
	private File[] listFiles(String pattern, boolean recursive) {
		ar = new ArrayList<>();
		if (pattern != null) {
			pattern = pattern.replace(".", "\\.");
			pattern = pattern.replace("*", ".*");
			pattern = pattern.replace("?", ".");
		}
		return func(path, pattern, recursive);
	}

	private File[] func(String path, String pattern, boolean recursive) {
		for (File file : (new File(path)).listFiles()) {
			if (pattern != null && !file.getName().matches(pattern)) {
				continue;
			}
			ar.add(file);
			if (file.isDirectory()) { //再帰処理
				if (recursive) {
					func(file.getAbsolutePath(), pattern, recursive);
				}
			}
		}
		return (File[]) ar.toArray(new File[ar.size()]);
	}
}
