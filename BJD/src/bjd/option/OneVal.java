package bjd.option;

import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JPanel;

import bjd.ValidObjException;
import bjd.ctrl.CtrlComboBox;
import bjd.ctrl.CtrlDat;
import bjd.ctrl.CtrlGroup;
import bjd.ctrl.CtrlTabPage;
import bjd.ctrl.CtrlType;
import bjd.ctrl.ICtrlEventListener;
import bjd.ctrl.OneCtrl;
import bjd.ctrl.OnePage;
import bjd.net.BindAddr;
import bjd.net.Ip;
import bjd.util.Crypt;
import bjd.util.IDisposable;
import bjd.util.Msg;
import bjd.util.MsgKind;

/**
 * 1つの値を表現するクラス<br>
 * ListValと共に再帰処理が可能になっている<br>
 * 
 * @author SIN
 *
 */
public final class OneVal implements IDisposable {

	private String name;
	private Object value;
	private Crlf crlf;
	private OneCtrl oneCtrl;

	/**
	 * コンストラクタ
	 * @param name 名前
	 * @param value 値
	 * @param oneCtrl コントロール
	 * @param crlf 改行指示
	 */
	public OneVal(String name, Object value, Crlf crlf, OneCtrl oneCtrl) {
		this.name = name;
		this.value = value;
		this.crlf = crlf;
		this.oneCtrl = oneCtrl;
		oneCtrl.setName(name);

		//*************************************************************
		//仕様上、階層構造をなすOneValの名前は、ユニークである必要がる
		//プログラム作成時に重複を発見できるように、重複があった場合、ここでエラーをポップアップする
		//*************************************************************

		//名前一覧
		ArrayList<String> tmp = new ArrayList<String>();

		//このlistの中に重複が無いかどうかをまず確認する
		ArrayList<OneVal> list = getList(null);
		for (OneVal o : list) {
			if (0 <= tmp.indexOf(o.getName())) { //名前一覧に重複は無いか
				Msg.show(MsgKind.ERROR, String.format("OneVal(OnePage)の名前に重複があります %s", o.getName()));
			}
			tmp.add(o.getName()); //名前一覧への蓄積
			//			if (o != this) { // 自分自身は検査対象外とする
			//				if (name.equals(o.getName())) {
			//					Msg.show(MsgKind.ERROR, String.format("OneVal(OnePage)の名前に重複があります %s", name));
			//				}
			//			}
		}
		//CtrlTabPageの場合は、array+ist<OnePage>の重複を確認する
		if (oneCtrl.getCtrlType() == CtrlType.TABPAGE) {
			for (OnePage onePage : ((CtrlTabPage) oneCtrl).getPageList()) {
				if (0 <= tmp.indexOf(onePage.getName())) { //名前一覧に重複は無いか
					Msg.show(MsgKind.ERROR, String.format("OneVal(OnePage)の名前に重複があります %s", onePage.getName()));
				}
				tmp.add(onePage.getName());
			}
		}
	}

	/**
	 * 階層下のOneValを一覧する
	 */
	public ArrayList<OneVal> getList(ArrayList<OneVal> list) {
		if (list == null) {
			list = new ArrayList<>();
		}

		if (oneCtrl.getCtrlType() == CtrlType.DAT) {
			list = ((CtrlDat) oneCtrl).getListVal().getList(list);
		} else if (oneCtrl.getCtrlType() == CtrlType.GROUP) {
			list = ((CtrlGroup) oneCtrl).getListVal().getList(list);
		} else if (oneCtrl.getCtrlType() == CtrlType.TABPAGE) {
			ArrayList<OnePage> pageList = ((CtrlTabPage) oneCtrl).getPageList();
			for (OnePage onePage : pageList) {
				list = onePage.getListVal().getList(list);
			}
		}
		list.add(this);
		return list;
	}
	/**
	 * 階層下のOneValを一覧する<br>
	 * Datの階層下は再帰しない<br>
	 * toREg()用に使用される<br>
	 */
	public ArrayList<OneVal> getSaveList(ArrayList<OneVal> list) {
		if (list == null) {
			list = new ArrayList<>();
		}

		//if (oneCtrl.getCtrlType() == CtrlType.DAT) {
		//	list = ((CtrlDat) oneCtrl).getListVal().getList(list);
		if (oneCtrl.getCtrlType() == CtrlType.GROUP) {
			list = ((CtrlGroup) oneCtrl).getListVal().getList(list);
		} else if (oneCtrl.getCtrlType() == CtrlType.TABPAGE) {
			ArrayList<OnePage> pageList = ((CtrlTabPage) oneCtrl).getPageList();
			for (OnePage onePage : pageList) {
				list = onePage.getListVal().getSaveList(list);
			}
		}
		list.add(this);
		return list;
	}

	/**
	 * 入力を完了しているかどうか
	 * @return
	 */
	public boolean isComplete() {
		for (OneVal oneVal : getList(null)) {
			if (oneVal != this) { // 自分自身はループになるので対象外とする
				if (!oneVal.isComplete()) {
					return false;
				}
			}
		}
		return oneCtrl.isComplete();
	}

	@Override
	public void dispose() {
	}

	public OneCtrl getOneCtrl() {
		return oneCtrl;
	}

	public Crlf getCrlf() {
		return crlf;
	}

	public Object getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

	/**
	 * コントロール生成
	 * @param mainPanel
	 * @param baseX
	 * @param baseY
	 */
	public void createCtrl(JPanel mainPanel, int baseX, int baseY) {
		oneCtrl.create(mainPanel, baseX, baseY, value);
	}

	/**
	 * コントロール破棄
	 */
	public void deleteCtrl() {
		oneCtrl.delete();
	}

	/**
	 * コントロールからの値のコピー (isComfirm==true 確認のみ)
	 * @param isConfirm
	 * @return
	 */
	public boolean readCtrl(boolean isConfirm) {
		Object o = oneCtrl.read();
		if (o == null) {
			if (isConfirm) { // 確認だけの場合は、valueへの値セットは行わない
				Msg.show(MsgKind.ERROR, String.format("データに誤りがあります 「%s」", oneCtrl.getHelp()));
			}
			return false;
		}
		value = o; // 値の読込
		return true;
	}

	public Dimension getSize() {
		return oneCtrl.getCtrlSize();
	}

	public void setListener(ICtrlEventListener listener) {
		for (OneVal oneVal : getList(null)) {
			oneVal.getOneCtrl().setListener(listener);
		}
	}

	/**
	 * 設定ファイル(Option.ini)への出力
	 * 
	 * @param isSecret
	 *            デバッグ用の設定ファイル出力用（パスワード等を***で表現する）
	 */
	public String toReg(boolean isSecret) {
		switch (oneCtrl.getCtrlType()) {
			case DAT:
				if (value == null) {
					Dat d = new Dat(((CtrlDat) oneCtrl).getCtrlTypeList());
					return d.toReg(isSecret);
				}
				return ((Dat) value).toReg(isSecret);
			case CHECKBOX:
				return String.valueOf(value);
			case FONT:
				if (value != null) {
					Font font = (Font) value;
					return String.format("%s,%s,%s", font.getName(), font.getStyle(), font.getSize());
				}
				return "";
			case FILE:
			case FOLDER:
			case TEXTBOX:
				return (String) value;
			case HIDDEN:
				if (isSecret) {
					return "***";
				}
				try {
					return Crypt.encrypt((String) value);
				} catch (Exception e) {
					return "ERROR";
				}

			case MEMO:
				return ((String) value).replaceAll("\r\n", "\t");
			case RADIO:
			case COMBOBOX:
			case INT:
				return String.valueOf(value);
			case BINDADDR:
				return value.toString();
			case ADDRESSV4:
				return value.toString();
			case TABPAGE:
			case GROUP:
				return "";
			default:
				return ""; // "実装されていないCtrlTypeが指定されました OneVal.toReg()"
		}
	}

	/**
	 * 出力ファイル(Option.ini)からの入力用<br>
	 * 不正な文字列があった場合は、無効行として無視される<br>
	 * 
	 * @param str
	 *            　読み込み行
	 * @return 成否
	 * @throws Exception
	 */
	public boolean fromReg(String str) {
		if (str == null) {
			value = null;
			return false;
		}
		switch (oneCtrl.getCtrlType()) {
			case DAT:
				CtrlDat ctrlDat = (CtrlDat) oneCtrl;
				Dat dat = new Dat(ctrlDat.getCtrlTypeList());
				if (!dat.fromReg(str)) {
					value = null;
					return false;
				}
				value = dat;
				break;
			case CHECKBOX:
				if (str.equalsIgnoreCase("false") || str.equalsIgnoreCase("true")) {
					value = Boolean.parseBoolean(str);
				} else {
					return false;
				}
				break;
			case FONT:
				value = null;
				String[] tmp = str.split(",");
				if (tmp.length == 3) {
					String name = tmp[0];
					int style = Integer.parseInt(tmp[1]);
					int size = Integer.parseInt(tmp[2]);
					value = new Font(name, style, size);
					// 検証
					Font f = (Font) value;
					if (f.getStyle() != style || f.getSize() < 0) {
						value = null;
						return false;
					}
				}
				break;
			case MEMO:
				try {
					value = str.replaceAll("\t", "\r\n");
				} catch (Exception ex) {
					value = "";
					return false;
				}
				break;
			case FILE:
			case FOLDER:
			case TEXTBOX:
				value = str;
				break;
			case HIDDEN:
				try {
					value = Crypt.decrypt(str);
				} catch (Exception e1) {
					value = "";
					return false;
				}
				break;
			case RADIO:
				try {
					value = Integer.parseInt(str);
				} catch (Exception e) {
					value = 0;
					return false;
				}
				if ((int) value < 0) {
					value = 0;
					return false;
				}
				break;
			case COMBOBOX:
				try {
					int max = ((CtrlComboBox) oneCtrl).getMax();
					int n = Integer.parseInt(str);
					if (n < 0 || max <= n) {
						value = 0;
						return false;
					}
					value = n;
				} catch (Exception e) {
					value = 0;
					return false;
				}
				break;
			case INT:
				try {
					value = Integer.parseInt(str);
				} catch (Exception e) {
					value = 0;
					return false;
				}
				break;
			case BINDADDR:
				try {
					value = new BindAddr(str);
				} catch (ValidObjException ex) {
					value = 0;
					return false;
				}
				break;
			case ADDRESSV4:
				try {
					value = new Ip(str);
				} catch (ValidObjException ex) {
					value = null;
					return false;
				}
				break;
			case TABPAGE:
			case GROUP:
				break;
			default:
				value = 0;
				return false;
				// "実装されていないCtrlTypeが指定されました OneVal.fromReg()"
		}
		return true;
	}
	
}
