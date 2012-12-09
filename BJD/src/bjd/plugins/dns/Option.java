package bjd.plugins.dns;

import java.util.ArrayList;

import bjd.Kernel;
import bjd.RunMode;
import bjd.ctrl.CtrlCheckBox;
import bjd.ctrl.CtrlComboBox;
import bjd.ctrl.CtrlDat;
import bjd.ctrl.CtrlFolder;
import bjd.ctrl.CtrlGroup;
import bjd.ctrl.CtrlHidden;
import bjd.ctrl.CtrlInt;
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
		return "DNSサーバ";
	}

	@Override
	public String getEnMenu() {
		return "Dns Server";
	}

	@Override
	public char getMnemonic() {
		return 'D';
	}

	public Option(Kernel kernel, String path) {
		super(kernel.isJp(), path, "Dns");

		ArrayList<OnePage> pageList = new ArrayList<>();

		add(new OneVal("useServer", false, Crlf.NEXTLINE,
				new CtrlCheckBox(isJp() ? "DNSサーバを使用する" : "Use Sample Server")));
		pageList.add(page1("Basic", isJp() ? "基本設定" : "Basic"));

		pageList.add(pageAcl());
		add(new OneVal("tab", null, Crlf.NEXTLINE, new CtrlTabPage("tabPage", pageList)));

		read(OptionIni.getInstance()); //　レジストリからの読み込み
	}

	private OnePage page1(String name, String title) {
		OnePage onePage = new OnePage(name, title);
		onePage.add(createServerOption(ProtocolKind.Udp, 53, 10, 10)); //サーバ基本設定
		
		onePage.add(new OneVal("rootCache", "named.ca", Crlf.NEXTLINE, new CtrlTextBox(isJp() ? "ルートキャッシュ" : "Root Cache",45)));
		onePage.add(new OneVal("useRD", true, Crlf.NEXTLINE, new CtrlCheckBox(isJp() ? "再帰要求を使用する" : "Use Recurrence")));
		
		ListVal list = new ListVal();
		list.add(new OneVal("soaMail", "postmaster", Crlf.NEXTLINE, new CtrlTextBox(isJp() ? "管理者メールアドレス" : "MailAddress(Admin)", 45)));
		list.add(new OneVal("soaSerial", 1, Crlf.NEXTLINE, new CtrlInt(isJp() ? "連続番号" : "Serial", 5)));
		list.add(new OneVal("soaRefresh", 3600, Crlf.CONTINUE, new CtrlInt(isJp() ? "更新時間(秒)" : "Refresh(sec)", 5)));
		list.add(new OneVal("soaRetry", 300, Crlf.NEXTLINE, new CtrlInt(isJp() ? "再試行(秒)" : "Retry(sec)", 5)));
		list.add(new OneVal("soaExpire", 360000, Crlf.CONTINUE, new CtrlInt(isJp() ? "終了時間(秒)" : "Expire(sec)", 5)));
		list.add(new OneVal("soaMinimum", 3600, Crlf.NEXTLINE, new CtrlInt(isJp() ? "最小時間(秒)" : "Minimum(sec)", 5)));

		onePage.add(new OneVal("GroupSoa", null,Crlf.NEXTLINE,new CtrlGroup(isJp() ? "ゾーン管理情報(この設定はすべてのドメインのSOAレコードとして使用されます)": "Group SOA",list)));
		
		
        
		
		return onePage;
	}

	@Override
	protected void abstractOnChange(OneCtrl oneCtrl) {
		boolean b = (boolean) getCtrl("useServer").read();
		getCtrl("tab").setEnable(b);
		
		/*
		 * GetCtrl("port").SetEnable(false);
		*/

	}
}
