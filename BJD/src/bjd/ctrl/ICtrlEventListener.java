package bjd.ctrl;

import java.util.EventListener;

public interface ICtrlEventListener extends EventListener {
    void onChange(OneCtrl oneCtrl);
}

