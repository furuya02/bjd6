package bjd.option;

import java.util.ArrayList;

import bjd.ValidObj;
import bjd.ValidObjException;
import bjd.util.IDispose;
import bjd.util.Util;

/**
 * オリジナルデータ型<br>
 * 複数の文字列と有効無効のフラグを保持する<br>
 * 
 * @author SIN
 *
 */
public final class OneDat extends ValidObj  implements IDispose {

	private boolean enable;
	private ArrayList<String> strList = new ArrayList<>();
	private boolean[] isSecretList;

	@Override
	public void dispose() {

	}
	
	/**
	 * 有効・無効の取得
	 * @return enable
	 */
	public boolean isEnable() {
		return enable;
	}

	// 必要になったら有効にする
	// public void setEnable(boolean enable) {
	// this.enable = enable;
	// }
	/**
	 * 文字列リストの取得
	 * @return 文字列リスト
	 */
	public ArrayList<String> getStrList() {
		return strList;
	}

	@SuppressWarnings("unused")
	private OneDat() {
		// デフォルトコンストラクタの隠蔽
	}

	/**
	 * コンストラクタ
	 * @param enable 有効・無効フラグ
	 * @param list 文字列による値
	 * @param isSecretList　秘匿カラムの指定
	 * @throws DatException 初期化失敗
	 */
	public OneDat(boolean enable, String[] list, boolean[] isSecretList) throws ValidObjException {

		if (list == null) {
			throw new ValidObjException("引数に矛盾があります  list=null");
		}
		if (isSecretList == null) {
			throw new ValidObjException("引数に矛盾があります  isSecretList == null");
		}
		if (list.length != isSecretList.length) {
			throw new ValidObjException("引数に矛盾があります  list.length != isSecretList.length");
		}

		this.enable = enable;
		this.isSecretList = new boolean[list.length];
		for (int i = 0; i < list.length; i++) {
			strList.add(list[i]);
			this.isSecretList[i] = isSecretList[i];
		}
	}

	/**
	 * 文字列による初期化<br>
	 * コンストラクタで定義した型に一致していないときfalseを返す<br>
	 * 
	 * @param str 初期化文字列
	 * @return 成否
	 */
	public boolean fromReg(String str) {
		
		if (str == null) {
			return false;
		}
		String[] tmp = str.split("\t");

		//カラム数確認
		if (tmp.length != strList.size() + 1) {
			return false;
		}
		
		//enableカラム
		switch (tmp[0]) {
			case "":
				enable = true;
				break;
			case "#":
				enable = false;
				break;
			default:
				return false;
		}
		//以降の文字列カラム
		strList = new ArrayList<String>();
		for (int i = 1; i < tmp.length; i++) {
			strList.add(tmp[i]);
		}
		return true;
	}

	/**
	 * 文字列化
	 * @param isSecret シークレットカラムを***で出力する
	 * @return 出力文字列
	 */
	public String toReg(boolean isSecret) {
		StringBuilder sb = new StringBuilder();
		if (!enable) {
			sb.append("#");
		}
		for (int i = 0; i < strList.size(); i++) {
			sb.append('\t');
			if (isSecret && isSecretList[i]) { // シークレットカラム
				sb.append("***");
			} else {
				sb.append(strList.get(i));
			}
		}
		return sb.toString();
	}

	/**
	 * toRegと誤って使用しないように注意
	 */
	@Override
	public String toString() {
		return "ERROR";
	}

	@Override
	protected void init() {
		strList.clear();
	}
}
