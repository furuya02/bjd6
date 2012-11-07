package bjd.option;

import java.util.ArrayList;

import bjd.Kernel;
import bjd.ctrl.CtrlCheckBox;
import bjd.ctrl.CtrlComboBox;
import bjd.ctrl.CtrlHidden;
import bjd.ctrl.CtrlTabPage;
import bjd.ctrl.CtrlTextBox;
import bjd.ctrl.OneCtrl;
import bjd.ctrl.OnePage;
import bjd.net.ProtocolKind;

/**
 * 基本オプション 
 * @author SIN
 *
 */
public final class OptionBasic extends OneOption {
	
	@Override
	public String getJpMenu() {
		return "基本オプション";
	}

	@Override
	public String getEnMenu() {
		return "Basic Option";
	}

	@Override
	public char getMnemonic() {
		return 'O';
	}

	public OptionBasic(Kernel kernel, String path) {
		super(kernel.isJp(), path, "Basic");

		// 基本オプションの場合、この時点でkernelのJapanは、まだ読込まれていないので、強制的に初期化する
		// var iniDb = new IniDb(kernel.ProgDir(), "Option");
		// var lv = new ListVal();
		// lv.Add(new OneVal("lang", 0,Crlf.Nextline,new
		// CtrlComboBox("Language",new List<string>{"Japanese", "English"})));
		// iniDb.Read("Basic",lv);
		// var japan = ((int)lv.Vals[0].Value)==0;

		ArrayList<OnePage> pageList = new ArrayList<>();
		pageList.add(page1("Basic", isJp() ? "基本設定" : "Basic"));
		pageList.add(page2("Test", isJp() ? "テスト" : "Test"));
		pageList.add(pageAcl());
		add(new OneVal("tab", null, Crlf.NEXTLINE, new CtrlTabPage("tabPage", pageList)));

		read(OptionIni.getInstance()); //　レジストリからの読み込み

	}

	private OnePage page1(String name, String title) {
		OnePage onePage = new OnePage(name, title);
		onePage.add(new OneVal("useExitDlg", false, Crlf.NEXTLINE, new CtrlCheckBox(isJp() ? "終了確認のメッセージを表示する" : "Display a message of end confirmation")));
		onePage.add(new OneVal("useLastSize", true, Crlf.NEXTLINE, new CtrlCheckBox(isJp() ? "前回起動時のウインドウサイズを記憶する" : "Memorize size of a wind in last time start")));
		onePage.add(new OneVal("isWindowOpen", true, Crlf.NEXTLINE, new CtrlCheckBox(isJp() ? "起動時にウインドウを開く" : "Open a window in start")));
		onePage.add(new OneVal("useAdminPassword", false, Crlf.NEXTLINE, new CtrlCheckBox(isJp() ? "ウインドウ表示時に管理者パスワードを使用する" : "At the time of window indication, a password is necessary")));
		onePage.add(new OneVal("password", "", Crlf.NEXTLINE, new CtrlHidden(isJp() ? "管理者パスワード" : "password", 20)));
		onePage.add(new OneVal("serverName", "", Crlf.NEXTLINE, new CtrlTextBox(isJp() ? "サーバ名" : "Server Name", 20)));
		onePage.add(new OneVal("editBrowse", false, Crlf.NEXTLINE, new CtrlCheckBox(isJp() ? "フォルダ・ファイル選択を編集にする" : "can edit browse control")));
		onePage.add(new OneVal("lang", 0, Crlf.NEXTLINE, new CtrlComboBox(isJp() ? "言語" : "Language", new String[] { "Japanese", "English" }, 80)));
		return onePage;
	}

	private OnePage page2(String name, String title) {
		OnePage onePage = new OnePage(name, title);
		onePage.add(createServerOption(ProtocolKind.Tcp, 80, 30, 50)); //サーバ基本設定
		return onePage;
	}

	@Override
	protected void abstractOnChange(OneCtrl oneCtrl) {
		boolean b = (boolean) getCtrl("useAdminPassword").read();
		getCtrl("password").setEnable(b);
	}
}
