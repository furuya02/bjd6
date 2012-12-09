package bjd.plugins.dns;

import org.junit.internal.matchers.IsCollectionContaining;

import bjd.ctrl.CtrlDat;
import bjd.ctrl.OneCtrl;
import bjd.option.ListVal;
import bjd.option.OneVal;
import bjd.util.Util;

/**
 * リソース定義用にCtrlDatを拡張
 * @author SIN
 *
 */
public final class CtrlOrgDat extends CtrlDat {

	private OneCtrl type;
	private OneCtrl name;
	private OneCtrl alias;
	private OneCtrl address;
	private OneCtrl priority;

	public CtrlOrgDat(String help, ListVal listVal, int height, boolean isJp) {
		super(help, listVal, height, isJp);

		for (OneVal o : listVal.getList(null)) {
			if (o.getName().equals("type")) {
				type = o.getOneCtrl();
			} else if (o.getName().equals("name")) {
				name = o.getOneCtrl();
			} else if (o.getName().equals("alias")) {
				alias = o.getOneCtrl();
			} else if (o.getName().equals("address")) {
				address = o.getOneCtrl();
			} else if (o.getName().equals("priority")) {
				priority = o.getOneCtrl();
			}
		}
	}

	/**
	 * コントロールの入力内容に変化があった場合
	 */
	@Override
	public void onChange(OneCtrl oneCtrl) {

		switch (type.toText()) {
			case "0"://A
			case "1"://NS
			case "4"://AAAA
				name.setEnable(true);
				alias.setEnable(false);
				address.setEnable(true);
				priority.setEnable(false);
				break;
			case "3"://CNAME
				name.setEnable(true);
				alias.setEnable(true);
				address.setEnable(false);
				priority.setEnable(false);
				break;
			case "2"://MX
				name.setEnable(true);
				alias.setEnable(false);
				address.setEnable(true);
				priority.setEnable(true);
				break;
			default:
				Util.runtimeException(String.format("CtrlOrgDat.onChange() unknown type=[%s]", type.toText()));
		}
		super.onChange(oneCtrl);

	}

	/**
	 * コントロールの入力が完了しているか
	 */
	@Override
	public boolean isComplete() {
		boolean isComplete = true;

		switch (type.toText()) {
			case "0"://A
			case "1"://NS
			case "4"://AAAA
				try {
					priority.clear();
				} catch (Exception e) {
					//原因調査中 「Attempt to mutate in notification」が発生する
					System.out.println(String.format("Exception %s", e.getMessage()));
				}
				alias.fromText("");
				if (!name.isComplete()) {
					isComplete = false;
				}
				if (!address.isComplete()) {
					isComplete = false;
				}
				break;
			case "3"://CNAME
				try {
					priority.clear();
				} catch (Exception e) {
					//原因調査中 「Attempt to mutate in notification」が発生する
					System.out.println(String.format("Exception %s", e.getMessage()));
				}
				address.fromText("");
				if (!name.isComplete()) {
					isComplete = false;
				}
				if (!alias.isComplete()) {
					isComplete = false;
				}
				break;
			case "2"://MX
				alias.fromText("");
				if (!name.isComplete()) {
					isComplete = false;
				}
				if (!address.isComplete()) {
					isComplete = false;
				}
				if (!priority.isComplete()) {
					isComplete = false;
				}
				break;
			default:
				Util.runtimeException(String.format("CtrlOrgDat.isComplete() unknown type=[%s]", type.toText()));
				break;
		}
		return isComplete;
	}
}
