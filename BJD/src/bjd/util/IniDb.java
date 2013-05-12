package bjd.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import bjd.ctrl.CtrlComboBox;
import bjd.ctrl.CtrlType;
import bjd.option.Crlf;
import bjd.option.ListVal;
import bjd.option.OneVal;

/**
 * ファイルを使用した設定情報の保存<br>
 * <br>
 * 1つのデフォルト値ファイルを使用して2つのファイルを出力する<br>
 * <br>
 * name.def　デフォルト設定<br>
 * name.ini 保存DB<br>
 * name.txt　秘匿情報を***表示したデバッグ用設定情報<br>
 * 
 * @author SIN
 *
 */
public final class IniDb {
	private String fileIni;
	private String fileDef;
	private String fileTxt;
	private String fileBak;
	
	public IniDb(String progDir, String fileName) {
		this.fileIni = progDir + "\\" + fileName + ".ini";
		this.fileDef = progDir + "\\" + fileName + ".def";
		this.fileTxt = progDir + "\\" + fileName + ".txt";
		//this.fileDef = progDir + "\\Option.def";
		//this.fileTxt = progDir + "\\Option.txt";
		this.fileBak = fileIni + ".bak";

		// 前回、iniファイルの削除後にハングアップした場合は、
		// iniファイルが無く、bakファイルのみ残っている場合は、bakファイルに戻す
		if (!(new File(fileIni)).exists() && (new File(fileBak)).exists()) {
			Util.fileCopy(new File(fileBak), new File(fileIni));
		}
	}

	private String ctrlType2Str(CtrlType ctrlType) {
		switch (ctrlType) {
			case CHECKBOX:
				return "BOOL";
			case TEXTBOX:
				return "STRING";
			case HIDDEN:
				return "HIDE_STRING";
			case COMBOBOX:
				return "LIST";
			case FOLDER:
				return "FOLDER";
			case FILE:
				return "FILE";
			case DAT:
				return "DAT";
			case INT:
				return "INT";
			case ADDRESSV4:
				return "ADDRESS_V4";
			case BINDADDR:
				return "BINDADDR";
			case FONT:
				return "FONT";
			case GROUP:
				return "GROUP";
			case LABEL:
				return "LABEL";
			case MEMO:
				return "MEMO";
			case RADIO:
				return "RADIO";
			case TABPAGE:
				return "TAB_PAGE";
			default:
				throw new UnsupportedOperationException("IniDb.java CtrlType2Str() コントロールの型名が実装されていません OneVal::TypeStr()　" + ctrlType);
		}
	}

	//	private CtrlType str2CtrlType(String str) {
	//		switch (str) {
	//			case "BOOL":
	//				return CtrlType.CHECKBOX;
	//			case "STRING":
	//				return CtrlType.TEXTBOX;
	//			case "HIDE_STRING":
	//				return CtrlType.HIDDEN;
	//			case "LIST":
	//				return CtrlType.COMBOBOX;
	//			case "FOLDER":
	//				return CtrlType.FOLDER;
	//			case "FILE":
	//				return CtrlType.FILE;
	//			case "DAT":
	//				return CtrlType.DAT;
	//			case "INT":
	//				return CtrlType.INT;
	//			case "ADDRESS_V4":
	//				return CtrlType.ADDRESSV4;
	//			case "BINDADDR":
	//				return CtrlType.BINDADDR;
	//			case "FONT":
	//				return CtrlType.FONT;
	//			case "GROUP":
	//				return CtrlType.GROUP;
	//			case "LABEL":
	//				return CtrlType.LABEL;
	//			case "MEMO":
	//				return CtrlType.MEMO;
	//			case "RADIO":
	//				return CtrlType.RADIO;
	//			case "TAB_PAGE":
	//				return CtrlType.TABPAGE;
	//			default:
	//				throw new UnsupportedOperationException(
	//						"IniDb.java str2CtrlType() コントロールの型名が実装されていません OneVal::TypeStr()　"
	//								+ str);
	//		}
	//	}

	/**
	 * １行を読み込むためのオブジェクト
	 * @author user1
	 *
	 */
	private class LineObject {
		// private CtrlType ctrlType;
		private String nameTag;
		private String name;
		private String valStr;

		// public CtrlType getCtrlType() {
		// return ctrlType;
		// }

		public String getNameTag() {
			return nameTag;
		}

		public String getName() {
			return name;
		}

		public String getValStr() {
			return valStr;
		}

		// public LineObject(CtrlType ctrlType, String nameTag, String name,String valStr) {
		public LineObject(String nameTag, String name, String valStr) {
			// this.ctrlType = ctrlType;
			this.nameTag = nameTag;
			this.name = name;
			this.valStr = valStr;
		}
	}

	/**
	 * 
	 * @param str
	 * @return 解釈に失敗した場合はnullを返す
	 */
	private LineObject readLine(String str) {
		int index = str.indexOf("=");
		if (index == -1) {
			return null;
		}
		//		CtrlType ctrlType = str2CtrlType(str.substring(0, index));
		str = str.substring(index + 1);
		index = str.indexOf("=");
		if (index == -1) {
			return null;
		}
		String buf = str.substring(0, index);
		String[] tmp = buf.split("\b");
		if (tmp.length != 2) {
			return null;
		}
		String nameTag = tmp[0];
		String name = tmp[1];
		String valStr = str.substring(index + 1);
		return new LineObject(nameTag, name, valStr);
	}

	private boolean read(String fileName, String nameTag, ListVal listVal) {
		boolean isRead = false;
		File file = new File(fileName);
		if (file.exists()) {
			ArrayList<String> lines = null;
			try {
				lines = Util.textFileRead(file);
			} catch (IOException e) {
				Util.runtimeException(String.format("InitDb.read() IOException %s", e.getMessage()));
			}
			for (String s : lines) {
				LineObject o = readLine(s);
				if (o != null) {
					if (o.getNameTag().equals(nameTag)) {
						OneVal oneVal = listVal.search(o.getName());
						if (oneVal != null) {
							oneVal.fromReg(o.getValStr());
							isRead = true; // 1件でもデータを読み込んだ場合にtrue
						}
					}
				}
			}
		}
		return isRead;
	}

	// iniファイルの削除
	public void deleteIni() {
		File file = new File(fileIni);
		if (file.exists()) {
			Util.fileCopy(file, new File(fileBak));
			file.delete();
		}
	}

	/**
	 * bakファイルの削除
	 */
	public void deleteBak() {
		File file = new File(fileBak);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * txtファイルの削除
	 */
	public void deleteTxt() {
		File file = new File(fileTxt);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 読込み
	 * @param nameTag
	 * @param listVal
	 */
	public void read(String nameTag, ListVal listVal) {
		boolean isRead = read(fileIni, nameTag, listVal);
		if (!isRead) { // １件も読み込まなかった場合
			// defファイルには、Web-local:80のうちのWeb (-の前の部分)がtagとなっている
			String n = nameTag.split("-")[0];
			read(fileDef, n, listVal); // デフォルト設定値を読み込む
		}
	}

	/**
	 * 保存
	 * @param nameTag
	 * @param listVal
	 */
	public void save(String nameTag, ListVal listVal) {
		// Ver5.0.1 デバッグファイルに対象のValListを書き込む
		for (int i = 0; i < 2; i++) {
			String target = (i == 0) ? fileIni : fileTxt;
			boolean isSecret = i != 0;

			// 対象外のネームスペース行を読み込む
			ArrayList<String> lines = new ArrayList<>();
			File file = new File(target);
			if (file.exists()) {
				ArrayList<String> l = null;
				try {
					l = Util.textFileRead(file);
				} catch (IOException e) {
					Util.runtimeException(String.format("InitDb.save() IOException %s", e.getMessage()));
				}
				for (String s : l) {
					LineObject o;
					try {
						o = readLine(s);
						if (!o.getNameTag().equals(nameTag)) { // nameTagが違う場合、listに追加
							lines.add(s);
						}
					} catch (Exception e) {
						//TODO エラー処理未処理
					}
				}
			}
			// 対象のValListを書き込む
			for (OneVal o : listVal.getSaveList(null)) {
				// nullで初期化され、実行中に一度も設定されていない値は、保存の対象外となる
				//if (o.getValue() == null) {
				//	continue;
				//}

				// データ保存の必要のない型は省略する（下位互換のため）
				CtrlType ctrlType = o.getOneCtrl().getCtrlType();
				if (ctrlType == CtrlType.TABPAGE || ctrlType == CtrlType.GROUP || ctrlType == CtrlType.LABEL) {
					continue;
				}

				String ctrlStr = ctrlType2Str(ctrlType);
				lines.add(String.format("%s=%s\b%s=%s", ctrlStr, nameTag, o.getName(), o.toReg(isSecret)));
			}
			Util.textFileSave(new File(target), lines);
		}
	}

	/**
	 * 設定ファイルから"lang"の値を読み出す
	 * @return isJp
	 */
	public boolean isJp() {
		ListVal listVal = new ListVal();
		listVal.add(new OneVal("lang", 0, Crlf.NEXTLINE, new CtrlComboBox("Language", new String[] { "Japanese", "English" }, 80)));
		read("Basic", listVal);
		OneVal oneVal = listVal.search("lang");
		return ((int) oneVal.getValue() == 0) ? true : false;
	}
}
