package bjd.plugins.dns;

import java.util.ArrayList;

import bjd.Kernel;
import bjd.ctrl.CtrlComboBox;
import bjd.ctrl.CtrlInt;
import bjd.ctrl.CtrlTabPage;
import bjd.ctrl.CtrlTextBox;
import bjd.ctrl.OneCtrl;
import bjd.ctrl.OnePage;
import bjd.option.Crlf;
import bjd.option.ListVal;
import bjd.option.OneOption;
import bjd.option.OneVal;
import bjd.option.OptionIni;

public final class OptionDnsResource extends OneOption {
	@Override
	public String getJpMenu() {
		return getNameTag();
	}

	@Override
	public String getEnMenu() {
		return getNameTag();
	}

	@Override
	public char getMnemonic() {
		return ' ';
	}

	public OptionDnsResource(Kernel kernel, String path, String nameTag) {
		super(kernel.isJp(), path, nameTag);

		ArrayList<OnePage> pageList = new ArrayList<>();
		pageList.add(page1("Resouce", isJp() ? "リソース設定" : "Resouce"));
		add(new OneVal("tab", null, Crlf.NEXTLINE, new CtrlTabPage("tabPage", pageList)));

		read(OptionIni.getInstance()); //　レジストリからの読み込み
	}

	private OnePage page1(String name, String title) {
		OnePage onePage = new OnePage(name, title);

		ListVal list = new ListVal();
		list.add(new OneVal("type", 0, Crlf.NEXTLINE, new CtrlComboBox("Type", new String[] { "A(PTR)", "NS", "MX", "CNAME", "AAAA" }, 80)));
		list.add(new OneVal("name", "", Crlf.CONTINUE, new CtrlTextBox("Name", 15)));
		list.add(new OneVal("alias", "", Crlf.NEXTLINE, new CtrlTextBox("Alias", 15)));
		list.add(new OneVal("address", "", Crlf.CONTINUE, new CtrlTextBox("Address", 15)));
		list.add(new OneVal("priority", 10, Crlf.NEXTLINE, new CtrlInt("Priority", 3)));
		onePage.add(new OneVal("resourceList", null, Crlf.NEXTLINE, new CtrlOrgDat("", list, 400, isJp())));

		return onePage;
	}

	//最初に１回だけ実行する
	private boolean initCtrl = true;

	@Override
	protected void abstractOnChange(OneCtrl oneCtrl) {
		if (initCtrl) {
			CtrlOrgDat o = (CtrlOrgDat) getCtrl("resourceList");
			o.onChange(null); //拡張コントロールの状態を初期化する
			initCtrl = false;
		}
	}
}
