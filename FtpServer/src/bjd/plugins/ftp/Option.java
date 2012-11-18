package bjd.plugins.ftp;

import java.util.ArrayList;

import bjd.Kernel;
import bjd.RunMode;
import bjd.ctrl.CtrlCheckBox;
import bjd.ctrl.CtrlComboBox;
import bjd.ctrl.CtrlDat;
import bjd.ctrl.CtrlFolder;
import bjd.ctrl.CtrlHidden;
import bjd.ctrl.CtrlTabPage;
import bjd.ctrl.CtrlTextBox;
import bjd.ctrl.OneCtrl;
import bjd.ctrl.OnePage;
import bjd.net.ProtocolKind;
import bjd.option.Crlf;
import bjd.option.ListVal;
import bjd.option.OneOption;
import bjd.option.OneVal;
import bjd.option.OptionIni;

public final class Option extends OneOption {

	@Override
	public String getJpMenu() {
		return "FTPサーバ";
	}

	@Override
	public String getEnMenu() {
		return "Ftp Server";
	}

	@Override
	public char getMnemonic() {
		return 'F';
	}

	public Option(Kernel kernel, String path) {
		super(kernel.isJp(), path, "Ftp");

		ArrayList<OnePage> pageList = new ArrayList<>();

		add(new OneVal("useServer", false, Crlf.NEXTLINE,
				new CtrlCheckBox(isJp() ? "FTPサーバを使用する" : "Use Sample Server")));
		pageList.add(page1("Basic", isJp() ? "基本設定" : "Basic"));
		pageList.add(page2("VirtualFolder", isJp() ? "仮想フォルダ" : "Virtual Folder", kernel.getRunMode(), kernel.getEditBrowse()));
		pageList.add(page3("User", isJp() ? "利用者" : "User", kernel.getRunMode(), kernel.getEditBrowse()));
		
		pageList.add(pageAcl());
		add(new OneVal("tab", null, Crlf.NEXTLINE, new CtrlTabPage("tabPage", pageList)));

		read(OptionIni.getInstance()); //　レジストリからの読み込み
	}

	private OnePage page1(String name, String title) {
		OnePage onePage = new OnePage(name, title);
		onePage.add(createServerOption(ProtocolKind.Tcp, 21, 60, 10)); //サーバ基本設定
		onePage.add(new OneVal("bannerMessage", "FTP ( $p Version $v ) ready", Crlf.NEXTLINE, new CtrlTextBox(
				isJp() ? "バナーメッセージ" : "Banner Message", 35)));
		onePage.add(new OneVal("useSyst", false, Crlf.NEXTLINE, new CtrlCheckBox(
				isJp() ? "SYSTコマンドを有効にする ( セキュリティリスクの高いオプションです。必要のない限りチェックしないでください。)" : "Validate a SYST command")));
		return onePage;
	}

	private OnePage page2(String name, String title, RunMode runMode, boolean editBrowse) {
		OnePage onePage = new OnePage(name, title);

		ListVal liseVal = new ListVal();
		liseVal.add(new OneVal("fromFolder", "", Crlf.NEXTLINE, new CtrlFolder(isJp(),
				isJp() ? "実フォルダ" : "Real Folder", 42, runMode, editBrowse)));
		liseVal.add(new OneVal("toFolder", "", Crlf.NEXTLINE, new CtrlFolder(isJp(), isJp() ? "マウント先" : "Mount Folder",
				42, runMode, editBrowse)));
		onePage.add(new OneVal("mountList", null, Crlf.NEXTLINE, new CtrlDat(isJp() ? "マウントの指定" : "Mount List",
				liseVal, 360, isJp())));

		return onePage;
	}

	private OnePage page3(String name, String title, RunMode runMode, boolean editBrowse) {
		OnePage onePage = new OnePage(name, title);

		ListVal liseVal = new ListVal();
		liseVal.add(new OneVal("accessControl", 0, Crlf.NEXTLINE, new CtrlComboBox(isJp() ? "アクセス制限" : "Access Control", new String[] { "FULL", "DOWN", "UP" }, 100)));
		liseVal.add(new OneVal("homeDirectory", "", Crlf.NEXTLINE, new CtrlFolder(isJp(), isJp() ? "ホームディレクトリ" : "Home Derectory",  40, runMode, editBrowse)));
		liseVal.add(new OneVal("userName", "", Crlf.NEXTLINE, new CtrlTextBox(isJp() ? "ユーザ名" : "User Name", 30)));
		liseVal.add(new OneVal("password", "", Crlf.NEXTLINE, new CtrlHidden(isJp() ? "パスワード" : "Password", 30)));
		onePage.add(new OneVal("user", null, Crlf.NEXTLINE, new CtrlDat(isJp() ? "利用者（アクセス権）の指定" : "User List", liseVal, 360, isJp())));

		return onePage;
	}

	@Override
	protected void abstractOnChange(OneCtrl oneCtrl) {
		boolean b = (boolean) getCtrl("useServer").read();
		getCtrl("tab").setEnable(b);

	}
}
/*
 * 

        {//PAGE 利用者 ////////////////////////////////////////////////////////////////////////////
            var list = new ListVal();
            {//DAT
                var l = new ListVal();
                l.Add(new OneVal("accessControl", 0, Crlf.NEXTLINE, new CtrlComboBox(isJp() ? "アクセス制限" : "Access Control", new List<string> { "FULL", "DOWN", "UP" })));
                l.Add(new OneVal("homeDirectory", "", Crlf.NEXTLINE, new CtrlBrowse(isJp() ? "ホームディレクトリ" : "Home Derectory", BrowseType.Folder, 400, kernel)));
                l.Add(new OneVal("user", "", Crlf.NEXTLINE, new CtrlTextBox(isJp() ? "ユーザ名" : "User Name", 100)));
                l.Add(new OneVal("pass", "", Crlf.NEXTLINE, new CtrlTextBox(isJp() ? "パスワード" : "Password", 100, true)));
                list.Add(new OneVal("user", null, Crlf.NEXTLINE, new CtrlDat(isJp() ? "利用者（アクセス権）の指定" : "User List", l, 600, 360, kernel.Jp)));
            }//DAT
            Add(new OneVal("User", list, Crlf.NEXTLINE, new CtrlTabPage(isJp() ? "利用者" : "User")));
        }//PAGE 利用者 ////////////////////////////////////////////////////////////////////////////

        Init();//ValListの初期化
    }
    //コントロールの変化
    override public void OnChange() {
        var b = (bool)GetCtrl("useServer").GetValue();
        GetCtrl("Basic").SetEnable(b);
    }
     * */
