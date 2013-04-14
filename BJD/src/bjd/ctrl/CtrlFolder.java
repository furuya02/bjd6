package bjd.ctrl;

import bjd.Kernel;


public final class CtrlFolder extends CtrlBrowse {
	
	public CtrlFolder(String help, int digits, Kernel kernel) {
		super(help, digits, kernel);
	}

	@Override
	public CtrlType getCtrlType() {
		return CtrlType.FOLDER;
	}
	
}
