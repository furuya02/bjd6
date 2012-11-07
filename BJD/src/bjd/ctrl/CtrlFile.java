package bjd.ctrl;

import bjd.RunMode;


public final class CtrlFile extends CtrlBrowse {
	
	public CtrlFile(boolean isJp, String help, int digits, RunMode runMode, boolean editBrowse) {
		super(isJp, help, digits, runMode, editBrowse);
	}
	@Override
	public CtrlType getCtrlType() {
		return CtrlType.FILE;
	}

}
