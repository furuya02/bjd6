package bjd.ctrl;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import bjd.option.Dat;
import bjd.option.ListVal;
import bjd.option.OneDat;
import bjd.option.OneVal;
import bjd.util.Msg;
import bjd.util.MsgKind;
import bjd.util.Util;

//public class CtrlDat extends OneCtrl implements ActionListener, ListSelectionListener, ICtrlEventListener {
public final class CtrlDat extends OneCtrl implements ActionListener, ICtrlEventListener {

	private JPanel border = null;
	private JButton[] buttonList = null;
	private CheckListBox checkListBox = null;
	private ListVal listVal;

	private int height;
	//private Kernel kernel;
	private boolean isJp;
	private static final int ADD = 0;
	private static final int EDIT = 1;
	private static final int DEL = 2;
	private static final int IMPORT = 3;
	private static final int EXPORT = 4;
	private static final int CLEAR = 5;
	private String[] tagList = new String[] { "Add", "Edit", "Del", "Import", "Export", "Clear" };
	private String[] strList = new String[] { "追加", "変更", "削除", "インポート", "エクスポート", "クリア" };

	public CtrlType[] getCtrlTypeList() {
		CtrlType[] ctrlTypeList = new CtrlType[listVal.size()];
		int i = 0;
		for (OneVal o : listVal) {
			ctrlTypeList[i++] = o.getOneCtrl().getCtrlType();
		}
		return ctrlTypeList;
	}

	public CtrlDat(String help, ListVal listVal, int height, boolean isJp) {
		super(help);
		this.listVal = listVal;
		this.height = height;
		this.isJp = isJp;
	}

	//OnePage(CtrlTabPage.pageList) CtrlGroup CtrlDatにのみ存在する
	public ListVal getListVal() {
		return listVal;
	}

	@Override
	public CtrlType getCtrlType() {
		return CtrlType.DAT;
	}

	@Override
	protected void abstractCreate(Object value) {
		int left = margin;
		int top = margin;

		// ボーダライン（groupPanel）の生成
		border = (JPanel) create(panel, new JPanel(new GridLayout()), left, top);
		border.setBorder(BorderFactory.createTitledBorder(getHelp()));
		border.setSize(getDlgWidth() - 32, height); // サイズは、コンストラクタで指定されている

		//Datに含まれるコントロールを配置

		//ボーダーの中でのオフセット移動
		left += 8;
		top += 12;
		listVal.createCtrl(border, left, top);
		listVal.setListener(this); //コントロール変化のイベントをこのクラスで受信してボタンの初期化に利用する

		//オフセット移動
		Dimension dimension = listVal.getSize();
		top += dimension.height;

		//ボタン配置
		buttonList = new JButton[tagList.length];

		for (int i = 0; i < tagList.length; i++) {
			String btnText = isJp ? strList[i] : tagList[i];
			buttonList[i] = (JButton) create(border, new JButton(btnText), left + (i * 86), top);
			buttonList[i].setActionCommand(tagList[i]);
			buttonList[i].addActionListener(this);
			buttonList[i].setSize(85, buttonList[i].getHeight());
		}

		//オフセット移動
		top += buttonList[0].getHeight() + margin;

		//チェックリストボックス配置
		checkListBox = (CheckListBox) create(border, new CheckListBox(), left, top);
		checkListBox.setSize(getDlgWidth() - 52, height - top - 15);
		//		checkListBox.addListSelectionListener(this);
		checkListBox.addActionListener(this);

		//値の設定
		abstractWrite(value);

		// パネルのサイズ設定
		panel.setSize(border.getWidth() + margin * 2, border.getHeight() + margin * 2);

		buttonsInitialise(); //ボタン状態の初期化
	}

	@Override
	protected void abstractDelete() {
		listVal.deleteCtrl(); //これが無いと、グループの中のコントロールが２回目以降表示されなくなる

		if (buttonList != null) {
			for (int i = 0; i < buttonList.length; i++) {
				remove(border, buttonList[i]);
				buttonList[i] = null;
			}
		}
		remove(panel, border);
		remove(panel, checkListBox);
		border = null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String cmd = e.getActionCommand();
		String source = e.getSource().getClass().getName();

		if (source.indexOf("JButton") != -1) {
			actionButton(cmd); //ボタンのイベント
		} else if (source.indexOf("CheckListBox") != -1) {
			actionCheckListBox(cmd); //チェックリストボックスのイベント
		}

	}

	//チェックリストボックスのイベント
	void actionCheckListBox(String cmd) {
		if (cmd.equals("cahngeSelectIndex")) {
			int index = checkListBox.getSelectedIndex();
			buttonsInitialise(); //ボタン状態の初期化
			//チェックリストの内容をコントロールに転送する
			if (index >= 0) {
				textToControl(checkListBox.getItemText(index));
			}
		} else {
			this.setOnChange();
		}
	}

	//ボタンのイベント
	void actionButton(String cmd) {
		int selectedIndex = checkListBox.getSelectedIndex(); // 選択行
		if (cmd.equals(tagList[ADD])) {
			//コントロールの内容をテキストに変更したもの
			String s = controlToText();
			if (s.equals("")) {
				return;
			}
			//同一のデータがあるかどうかを確認する
			if (checkListBox.indexOf(s) != -1) {
				Msg.show(MsgKind.ERROR, isJp ? "既に同一内容のデータが存在します。" : "There is already the same data");
				return;
			}
			//チェックリストボックスへの追加
			int index = checkListBox.add(s);
			checkListBox.setItemChecked(index, true); //最初にチェック（有効）状態にする
			checkListBox.setSelectedIndex(index); //選択状態にする
		} else if (cmd.equals(tagList[EDIT])) {
			//コントロールの内容をテキストに変更したもの
			String str = controlToText();
			if (str.equals("")) {
				return;
			}
			if (checkListBox.getItemText(selectedIndex).equals(str)) {
				Msg.show(MsgKind.ERROR, isJp ? "変更内容はありません" : "There is not a change");
				return;
			}
			//同一のデータがあるかどうかを確認する
			if (checkListBox.indexOf(str) != -1) {
				Msg.show(MsgKind.ERROR, isJp ? "既に同一内容のデータが存在します" : "There is already the same data");
				return;
			}
			checkListBox.setItemText(selectedIndex, str);

		} else if (cmd.equals(tagList[DEL])) {
			for (OneVal v : listVal) { //コントロールの内容をクリア
				v.getOneCtrl().clear();
			}
			if (selectedIndex >= 0) {
				checkListBox.remove(selectedIndex);
			}
		} else if (cmd.equals(tagList[IMPORT])) {
			File file = Util.fileChooser(null);
			if (file != null) {
				try {
					ArrayList<String> lines = Util.textFileRead(file);
					importDat(lines);
				} catch (IOException e) {
					Msg.show(MsgKind.ERROR, String.format("ファイルの読み込みに失敗しました[%s]", file.getPath()));
				}
			}
		} else if (cmd.equals(tagList[EXPORT])) {
			File file = Util.fileChooser(null);
			if (file != null) {
				boolean isExecute = true;
				if (file.exists()) {
					if (0 != Msg.show(MsgKind.QUESTION, isJp ? "上書きして宜しいですか?" : "May I overwrite?")) {
						isExecute = false; //キャンセル
					}
				}
				if (isExecute) {
					ArrayList<String> lines = exportDat();
					Util.textFileSave(file, lines);

				}
			}
		} else if (cmd.equals(tagList[CLEAR])) {
			int n = Msg.show(MsgKind.QUESTION, isJp ? "すべてのデータを削除してよろしいですか" : "May I eliminate all data?");
			if (n == 0) {
				checkListBox.removeAll();
			}
			for (OneVal v : listVal) { //コントロールの内容をクリア
				v.getOneCtrl().clear();
			}
		}
	}

	//チェックボックス用のテキストを入力コントロールに戻す
	private void textToControl(String str) {
		String[] tmp = str.split("\t");
		if (listVal.size() != tmp.length) {
			Msg.show(MsgKind.ERROR, (isJp) ? "項目数が一致しません" : "The number of column does not agree");
			return;
		}
		int i = 0;
		for (OneVal v : listVal) {
			v.getOneCtrl().fromText(tmp[i++]);
		}
	}

	//入力コントロールの内容をチェックボックス用のテキストに変換する
	private String controlToText() {

		StringBuilder sb = new StringBuilder();
		for (OneVal v : listVal) {
			if (sb.length() != 0) {
				sb.append("\t");
			}
			sb.append(v.getOneCtrl().toText());
		}
		return sb.toString();

	}

	//コントロールの入力内容に変化があった場合
	@Override
	public void onChange(OneCtrl oneCtrl) {
		buttonsInitialise(); //ボタン状態の初期化
	}

	//ボタン状態の初期化
	private void buttonsInitialise() {
		//コントロールの入力が完了しているか
		boolean isComplete = listVal.isComplete();
		//チェックリストボックスのデータ件数
		int count = checkListBox.getItemCount();
		//チェックリストボックスの選択行
		int index = checkListBox.getSelectedIndex();

		buttonList[ADD].setEnabled(isComplete);
		buttonList[EXPORT].setEnabled(count > 0);
		buttonList[CLEAR].setEnabled(count > 0);
		buttonList[DEL].setEnabled(index >= 0);
		buttonList[EDIT].setEnabled(index >= 0 && isComplete);
	}

	//***********************************************************************
	// Import Export
	//***********************************************************************
	//インポート
	private void importDat(ArrayList<String> lines) {
		for (String s : lines) {
			String str = s;
			boolean isChecked = str.charAt(0) != '#';
			str = str.substring(2);

			//カラム数の確認
			String[] tmp = str.split("\t");
			if (listVal.size() != tmp.length) {
				Msg.show(MsgKind.ERROR, String.format("%s [ %s ] ", isJp ? "カラム数が一致しません。この行はインポートできません。"
						: "The number of column does not agree and cannot import this line.", str));
				continue;
			}
			//Ver5.0.0-a9 パスワード等で暗号化されていない（平文の）場合は、ここで
			boolean isChange = false;
			if (isChange) {
				StringBuilder sb = new StringBuilder();
				for (String l : tmp) {
					if (sb.length() != 0) {
						sb.append('\t');
					}
					sb.append(l);
				}
				str = sb.toString();
			}
			//同一のデータがあるかどうかを確認する
			if (checkListBox.indexOf(str) != -1) {
				Msg.show(MsgKind.ERROR, String.format("%s [ %s ] ", isJp ? "データ重複があります。この行はインポートできません。"
						: "There is data repetition and cannot import this line.", str));
				continue;
			}
			int index = checkListBox.add(str);
			//最初にチェック（有効）状態にする
			checkListBox.setItemChecked(index, isChecked);
			checkListBox.setSelectedIndex(index);
		}
	}

	//エクスポート
	private ArrayList<String> exportDat() {

		//チェックリストボックスの内容からDatオブジェクトを生成する
		ArrayList<String> lines = new ArrayList<>();
		for (int i = 0; i < checkListBox.getItemCount(); i++) {
			String s = checkListBox.getItemText(i);
			lines.add(checkListBox.getItemChecked(i) ? String.format(" \t%s", s) : String.format("#\t%s", s));
		}
		return lines;
	}

	//***********************************************************************
	//コントロールの入力が完了しているか
	//***********************************************************************
	//@Override
	//public boolean isComplete() {
	//	return listVal.isComplete();
	//}

	//***********************************************************************
	// コントロールの値の読み書き
	//***********************************************************************
	@Override
	protected Object abstractRead() {
		Dat dat = new Dat(getCtrlTypeList());
		//チェックリストボックスの内容からDatオブジェクトを生成する
		for (int i = 0; i < checkListBox.getItemCount(); i++) {
			boolean enable = checkListBox.getItemChecked(i);
			if (!dat.add(enable, checkListBox.getItemText(i))) {
				Util.runtimeException("CtrlDat abstractRead() 外部入力からの初期化ではないので、このエラーは発生しないはず");
			}
		}
		return dat;
	}

	@Override
	protected void abstractWrite(Object value) {
		if (value == null) {
			return;
		}
		Dat dat = (Dat) value;
		for (OneDat d : dat) {
			StringBuilder sb = new StringBuilder();
			ArrayList<String> strList = d.getStrList();
			for (String s : strList) {
				if (sb.length() != 0) {
					sb.append("\t");
				}
				sb.append(s);
			}
			int i = checkListBox.add(sb.toString());
			checkListBox.setItemChecked(i, d.isEnable());
		}
		//データがある場合は、１行目を選択する
		if (checkListBox.getItemCount() > 0) {
			checkListBox.setSelectedIndex(0);
		}

	}

	//***********************************************************************
	// コントロールへの有効・無効
	//***********************************************************************
	protected void abstractSetEnable(boolean enabled) {
		if (border != null) {
			//CtrlDatの場合は、disableで非表示にする
			panel.setVisible(enabled);
			//border.setEnabled(enabled);
		}
	}

	//***********************************************************************
	// OnChange関連
	//***********************************************************************
	// 必要なし
	//***********************************************************************
	// CtrlDat関連
	//***********************************************************************
	@Override
	protected boolean abstractIsComplete() {
		Util.runtimeException("使用禁止");
		return false;
	}

	@Override
	protected String abstractToText() {
		Util.runtimeException("使用禁止");
		return "";
	}

	@Override
	protected void abstractFromText(String s) {
		Util.runtimeException("使用禁止");
	}

	@Override
	protected void abstractClear() {
		Util.runtimeException("使用禁止");
	}
}
