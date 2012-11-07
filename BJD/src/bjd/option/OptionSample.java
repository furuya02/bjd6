package bjd.option;

import java.util.ArrayList;

import bjd.Kernel;
import bjd.ctrl.CtrlTabPage;
import bjd.ctrl.OneCtrl;
import bjd.ctrl.OnePage;
import bjd.net.ProtocolKind;

public final class OptionSample extends OneOption {
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

	public OptionSample(Kernel kernel, String path) {
		super(kernel.isJp(), path, "Sample");

		ArrayList<OnePage> pageList = new ArrayList<>();
		pageList.add(page1("Basic", isJp() ? "基本設定" : "Basic"));
		pageList.add(pageAcl());
		add(new OneVal("tab", null, Crlf.NEXTLINE, new CtrlTabPage("tabPage", pageList)));

		read(OptionIni.getInstance()); //　レジストリからの読み込み

	}

	private OnePage page1(String name, String title) {
		OnePage onePage = new OnePage(name, title);
		onePage.add(createServerOption(ProtocolKind.Tcp, 80, 30, 50)); //サーバ基本設定
		return onePage;
	}

	@Override
	protected void abstractOnChange(OneCtrl oneCtrl) {
	}
}
