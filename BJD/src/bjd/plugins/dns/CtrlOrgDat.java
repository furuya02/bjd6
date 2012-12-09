package bjd.plugins.dns;

import bjd.ctrl.CtrlDat;
/**
 * リソース定義用にCtrlDatを拡張
 * @author SIN
 *
 */
public class CtrlOrgDat extends CtrlDat{

}
/*
//リソース定義用にCtrlDatを拡張
class CtrlOrgDat : CtrlDat {
    readonly OneCtrl _type;
    readonly OneCtrl _name;
    readonly OneCtrl _alias;
    readonly OneCtrl _address;
    readonly OneCtrl _priority;

    public CtrlOrgDat(string help, ListVal listVal, int width, int height, bool jp)
        : base(help, listVal, width, height, jp) {
        foreach (var o in listVal.Vals) {
            if (o.Name == "type") {
                _type = o.OneCtrl;
            } else if (o.Name == "name") {
                _name = o.OneCtrl;
            } else if (o.Name == "alias") {
                _alias = o.OneCtrl;
            } else if (o.Name == "address") {
                _address = o.OneCtrl;
            } else if (o.Name == "priority") {
                _priority = o.OneCtrl;
            }
        }
    }

    //コントロールの入力内容に変化があった場合
    override public void ListValOnChange(object sender, EventArgs e) {
        switch ((int)_type.GetValue()) {
            case 0://A
            case 1://NS
            case 4://AAAA
                _name.SetEnable(true);
                _alias.SetEnable(false);
                _address.SetEnable(true);
                _priority.SetEnable(false);
                break;
            case 3://CNAME
                _name.SetEnable(true);
                _alias.SetEnable(true);
                _address.SetEnable(false);
                _priority.SetEnable(false);
                break;
            case 2://MX
                _name.SetEnable(true);
                _alias.SetEnable(false);
                _address.SetEnable(true);
                _priority.SetEnable(true);
                break;
        }

        base.ListValOnChange(sender, e);
    }

    //コントロールの入力が完了しているか
    override protected bool IsComplete() {
        //コントロールの入力が完了しているか
        bool isComplete = true;

        switch ((int)_type.GetValue()) {
            case 0://A
            case 1://NS
            case 4://AAAA
                _priority.SetValue(0);
                _alias.SetValue("");
                if (!_name.IsComplete())
                    isComplete = false;
                if (!_address.IsComplete())
                    isComplete = false;
                break;
            case 3://CNAME
                _priority.SetValue(0);
                _address.SetValue("");
                if (!_name.IsComplete())
                    isComplete = false;
                if (!_alias.IsComplete())
                    isComplete = false;
                break;
            case 2://MX
                _alias.SetValue("");
                if (!_name.IsComplete())
                    isComplete = false;
                if (!_address.IsComplete())
                    isComplete = false;
                if (!_priority.IsComplete())
                    isComplete = false;
                break;
        }
        return isComplete;
    }
}
*/