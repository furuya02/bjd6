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
	 */
	public File[] listFiles(String pattern) {
		ar = new ArrayList<>();
		if (pattern != null) {
			pattern = pattern.replace(".", "\\.");
			pattern = pattern.replace("*", ".*");
			pattern = pattern.replace("?", ".");
		}
		return func(path, pattern);
	}

	private File[] func(String path, String pattern) {
		for (File file : (new File(path)).listFiles()) {
			if (pattern != null && !file.getName().matches(pattern)) {
				continue;
			}
			ar.add(file);
			if (file.isDirectory()) { //再帰処理
				func(file.getAbsolutePath(), pattern);
			}
		}
		return (File[]) ar.toArray(new File[ar.size()]);
	}
}
