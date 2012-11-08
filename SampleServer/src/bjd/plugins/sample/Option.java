package bjd.plugins.sample;

import java.util.ArrayList;

import bjd.Kernel;
import bjd.ctrl.CtrlCheckBox;
import bjd.ctrl.CtrlTabPage;
import bjd.ctrl.CtrlTextBox;
import bjd.ctrl.OneCtrl;
import bjd.ctrl.OnePage;
import bjd.net.ProtocolKind;
import bjd.option.Crlf;
import bjd.option.OneOption;
import bjd.option.OneVal;
import bjd.option.OptionIni;

public final class Option extends OneOption {

	@Override
	public String getJpMenu() {
		return "SAMPLEサーバ";
	}

	@Override
	public String getEnMenu() {
		return "Sample Server";
	}

	@Override
	public char getMnemonic() {
		return 'Z';
	}

	public Option(Kernel kernel, String path) {
		super(kernel.isJp(), path, "Sample");

		ArrayList<OnePage> pageList = new ArrayList<>();

		add(new OneVal("useServer", false, Crlf.NEXTLINE, new CtrlCheckBox(isJp() ? "SAMPLEサーバを使用する" : "Use Sample Server")));

		pageList.add(page1("Basic", isJp() ? "基本設定" : "Basic"));
		pageList.add(pageAcl());
		add(new OneVal("tab", null, Crlf.NEXTLINE, new CtrlTabPage("tabPage", pageList)));

		read(OptionIni.getInstance()); //　レジストリからの読み込み
	}

	private OnePage page1(String name, String title) {
		OnePage onePage = new OnePage(name, title);
		onePage.add(createServerOption(ProtocolKind.Tcp, 9999, 30, 10)); //サーバ基本設定
		onePage.add(new OneVal("sampleText", "Sample Server", Crlf.NEXTLINE, new CtrlTextBox(isJp() ? "サンプルメッセージ" : "SampleMessage", 30)));
		return onePage;
	}

	@Override
	protected void abstractOnChange(OneCtrl oneCtrl) {
		boolean b = (boolean) getCtrl("useServer").read();
		getCtrl("tab").setEnable(b);
	}
}