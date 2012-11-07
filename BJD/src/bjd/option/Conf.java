package bjd.option;

import java.util.ArrayList;
import java.util.HashMap;

import bjd.ctrl.CtrlType;
import bjd.util.Util;

/**
 * Optionクラスへ結合を排除するためのクラス<br>
 * Optionの値を個別に設定できる（テスト用）<br>
 * 
 * @author SIN
 *
 */
public final class Conf {
	private HashMap<String, Object> ar = new HashMap<>();

	public Conf(OneOption oneOption) {
		ArrayList<OneVal> list = oneOption.getListVal().getList(null);
		for (OneVal o : list) {
			CtrlType ctrlType = o.getOneCtrl().getCtrlType();
			switch (ctrlType) {
				case CHECKBOX:
				case TEXTBOX:
				case ADDRESSV4:
				case ADDRESSV6:
				case BINDADDR:
				case FOLDER:
				case FILE:
				case COMBOBOX:
				case DAT:
				case INT:
				case MEMO:
				case FONT:
				case RADIO:
				case HIDDEN:
					ar.put(o.getName(), o.getValue());
					break;
				case TABPAGE:
				case GROUP:
				case LABEL:
					break;
				default:
					Util.runtimeException(String.format("未定義 %s", ctrlType));
			}
		}
	}

	/**
	 * 値の取得<br>
	 * 存在しないタグを指定すると実行事例がが発生する<br>
	 * 
	 * @param name　タグ名
	 * @return 取得した値 
	 */
	public Object get(String name) {
		if (!ar.containsKey(name)) { //HashMapの存在確認
			Util.runtimeException(String.format("未定義 %s", name));
		}
		return ar.get(name);
	}

	/**
	 * 値の設定<br>
	 * 存在しないタグを指定すると実行事例がが発生する<br>
	 * 
	 * @param name タグ名
	 * @param value 値
	 */
	public void set(String name, Object value) {
		if (!ar.containsKey(name)) { //HashMapの存在確認
			Util.runtimeException(String.format("未定義 %s", name));
		}
		ar.put(name, value);
	}
}
