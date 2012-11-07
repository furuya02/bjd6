package bjd.ctrl;

import bjd.RunMode;

public final class CtrlFolder extends CtrlBrowse {
	
	public CtrlFolder(boolean isJp, String help, int digits, RunMode runMode, boolean editBrowse) {
		super(isJp, help, digits, runMode, editBrowse);
	}

	@Override
	public CtrlType getCtrlType() {
		return CtrlType.FOLDER;
	}
	
}
