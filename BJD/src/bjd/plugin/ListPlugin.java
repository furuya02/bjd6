package bjd.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import bjd.util.FileSearch;
import bjd.util.ListBase;
import bjd.util.Util;

/**
 * プラグインフォルダ内のjarファイルを列挙するクラス
 * @author SIN
 *
 */
public final class ListPlugin extends ListBase<OnePlugin> {

	/**
	 * @param dir 検索対象となるpluginsフォルダ
	 */
	public ListPlugin(String dir) {
		//フォルダが存在しない場合、初期化終了
		if (!(new File(dir)).exists()) {
			return;
		}

		FileSearch fileSearch = new FileSearch(dir);

		//pluginsの中のjarファイルの検索
		for (File file : fileSearch.listFiles("*.jar")) {

			//jarファイルに含まれるクラスを列挙する
			String[] classNameList = getClassNameList(file);

			String classNameOption = null;
			String classNameServer = null;

			for (String className : classNameList) {
				if (className.indexOf("Option") != -1) {
					classNameOption = className;
				} else if (className.indexOf("Server") != -1) {
					classNameServer = className;
				}
			}
			if (classNameOption != null && classNameServer != null) {
				getAr().add(new OnePlugin(file, classNameOption, classNameServer));
			}
		}
	}

	/**
	 * 名前によるプラグイン情報オブジェクト（OnePlugin）の検索<br>
	 * <font color=red>一覧に存在しない名前で検索を行った場合、設計上の問題として処理される</font>
	 * 
	 * @param name 名前
	 * @return プラグイン情報(OnePlugin)
	 */
	public OnePlugin get(String name) {
		for (OnePlugin o : getAr()) {
			if (o.getName().equals(name)) {
				return o;
			}
		}
		Util.runtimeException(String.format("ListPlugin.get(%s)==null", name));
		return null;
	}

	/**
	 * jarファイルに梱包されているクラスの列挙
	 * @param file 対象jarファイル
	 * @return クラス名配列
	 */
	private String[] getClassNameList(File file) {

		//パッケージ名の生成
		//sample.jar
		String packageName = file.getName();
		//sample
		packageName = packageName.substring(0, packageName.length() - 4);
		//bjd.plubgins.sample
		packageName = String.format("bjd.plugins.%s", packageName);

		ArrayList<String> ar = new ArrayList<>();
		try {
			//jarファイル内のファイルを列挙
			JarInputStream jarIn = new JarInputStream(new FileInputStream(file));
			JarEntry entry;
			while ((entry = jarIn.getNextJarEntry()) != null) {
				if (!entry.isDirectory()) { //ディレクトリは対象外
					//　Server.class
					String className = entry.getName();
					//　Server　　.classを外す
					int index = className.indexOf(".class");
					if (index != -1) {
						className = className.substring(0, index);
					}
					className = className.replace("/", ".");
					ar.add(className);
				}
			}
			return (String[]) ar.toArray(new String[0]);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new String[0];
	}

}
