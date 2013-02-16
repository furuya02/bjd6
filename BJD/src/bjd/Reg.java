package bjd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import bjd.util.IDisposable;
import bjd.util.Util;

/**
 * オプションを記憶するＤＢ<br>
 * 明示的にDispose()若しくはSave()を呼ばないと、保存されない<br>
 * コンストラクタで指定したファイルが存在しない場合は、新規に作成される
 * 
 * @author SIN
 *
 */
public final class Reg implements IDisposable {

	private String path;
	private HashMap<String, String> ar = new HashMap<>();

	/**
	 * pathに指定したファイルが見つからない場合は、新規に作成される<br>
	 * 
	 * @param path 記憶ファイル名
	 * @throws IOException ファイルが新規作成できない、読み込みできない
	 */
	public Reg(String path) throws IOException {
		this.path = path;
		File file = new File(path);

		if (!file.exists()) {
			//ファイルが存在しない場合は、新規に作成する
			file.createNewFile();
		}
		ArrayList<String> lines = Util.textFileRead(file);
		for (String s : lines) {
			int index = s.indexOf("=");
			if (index < 1) {
				break;
			}
			String key = s.substring(0, index);
			String val = s.substring(index + 1);
			ar.put(key, val);
		}
	}

	/**
	 * 終了処理
	 */
	@Override
	public void dispose() {
		save();
	}

	/**
	 * 保存
	 */
	public void save() {
		ArrayList<String> lines = new ArrayList<>();
		for (String key : ar.keySet()) {
			String str = String.format("%s=%s", key, ar.get(key));
			lines.add(str);
		}
		Util.textFileSave(new File(path), lines);
	}

	/**
	 * String値を読み出す<br>
	 * <br>
	 * 指定したKeyが無効(（key==null、Key=="")の場合、例外(RegExceptionKind.InvalidKey)がスローされる<br>
	 * 値が見つからなかった場合、例外(RegExceptionKind.ValueNotFound)がスローされる<br>
	 * 
	 * @param key　Key指定
	 * @return 取得した値
	 * @throws RegException 
	 */
	public String getString(String key) throws RegException {
		if (key == null || key.equals("")) {
			throw new RegException("", RegExceptionKind.InvalidKey);
		}
		String ret = ar.get(key);
		if (ret == null) {
			//検索結果がヒットしなかった場合、例外がスローされる
			throw new RegException(String.format("key=%s", key), RegExceptionKind.ValueNotFound);
		}
		return ret;
	}

	/**
	 * String値の設定(既に値が設定されている場合は、上書きとなる)<br>
	 * 指定したKeyが無効(（key==null、Key=="")の場合、例外(RegExceptionKind.InvalidKey)がスローされる<br>
	 * val==nullの場合は、val=""として保存される<br>
	 * 
	 * @param key　Key指定
	 * @throws RegException  
	 */
	public void setString(String key, String val) throws RegException {
		if (key == null || key.equals("")) {
			throw new RegException("", RegExceptionKind.InvalidKey);
		}
		if (val == null) { //val==nullの場合は、""を保存する
			val = "";
		}
		ar.remove(key);
		ar.put(key, val);
	}

	/**
	 * int値を読み出す<br>
	 * 指定したKeyが無効(（key==null、Key=="")の場合、例外(RegExceptionKind.InvalidKey)がスローされる<br>
	 * 値が見つからなかった場合、例外(RegExceptionKind.ValueNotFound)がスローされる<br>
	 * 読み出した値がｉｎｔ型でなかった場合、例外(RegExceptionKind.NotNumberFormat)がされる<br>
	 * 
	 * @param key キー指定
	 * @return 読み出した値
	 * @throws RegException 
	 */
	public int getInt(String key) throws RegException {
		String str = getString(key);

		try {
			return Integer.valueOf(str);
		} catch (NumberFormatException ex) {
			throw new RegException(String.format("val=%s", str), RegExceptionKind.NotNumberFormat);
		}
	}

	/**
	 * int値を設定する(既に値が設定されている場合は、上書きとなる)<br>
	 * 
	 * 指定したKeyが無効(（key==null、Key=="")の場合、例外(RegExceptionKind.InvalidKey)がスローされる<br>
	 * 
	 * @param key キー指定
	 * @param val 設定する値
	 * @throws RegException 
	 */
	public void setInt(String key, int val) throws RegException {
		setString(key, String.valueOf(val));
	}
}
