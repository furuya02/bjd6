package bjd.plugins.dns;

import java.util.ArrayList;

import bjd.Kernel;
import bjd.ctrl.CtrlDat;
import bjd.ctrl.CtrlTabPage;
import bjd.ctrl.CtrlTextBox;
import bjd.ctrl.OneCtrl;
import bjd.ctrl.OnePage;
import bjd.option.Crlf;
import bjd.option.ListVal;
import bjd.option.OneOption;
import bjd.option.OneVal;
import bjd.option.OptionIni;

public final class OptionDnsDomain extends OneOption {
	@Override
	public String getJpMenu() {
		return "ドメインの追加と削除";
	}

	@Override
	public String getEnMenu() {
		return "Add or Remove Domains";
	}

	@Override
	public char getMnemonic() {
		return 'A';
	}

	public OptionDnsDomain(Kernel kernel, String path, String nameTag) {
		super(kernel.isJp(), path, nameTag);

		ArrayList<OnePage> pageList = new ArrayList<>();
		pageList.add(page1("Basic", isJp() ? "ドメイン" : "Domain"));
		add(new OneVal("tab", null, Crlf.NEXTLINE, new CtrlTabPage("tabPage", pageList)));

		read(OptionIni.getInstance()); //　レジストリからの読み込み
	}

	private OnePage page1(String name, String title) {
		OnePage onePage = new OnePage(name, title);

		ListVal list = new ListVal();
		list.add(new OneVal("name", "", Crlf.NEXTLINE, new CtrlTextBox(isJp() ? "ドメイン名" : "Domain Name", 52)));
		onePage.add(new OneVal("domainList", null, Crlf.NEXTLINE, new CtrlDat("", list, 400, isJp())));

		return onePage;
	}

	@Override
	protected void abstractOnChange(OneCtrl oneCtrl) {
		;
	}
}
