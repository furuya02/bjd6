package bjd.ctrl;

import bjd.Kernel;



public final class CtrlFile extends CtrlBrowse {
	
	public CtrlFile(String help, int digits, Kernel kernel) {
		super(help, digits, kernel);
	}
	@Override
	public CtrlType getCtrlType() {
		return CtrlType.FILE;
	}

}
