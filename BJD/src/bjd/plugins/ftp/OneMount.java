package bjd.plugins.ftp;

import java.io.File;

import bjd.util.IDispose;

public final class OneMount implements IDispose {

	private String fromFolder;
	private String toFolder;

	public OneMount(String fromFolder, String toFolder) {
		this.fromFolder = fromFolder;
		this.toFolder = toFolder;
	}

	public String getFromFolder() {
		return fromFolder;
	}

	public String getToFolder() {
		return toFolder;
	}

	@Override
	public void dispose() {

	}

	public boolean isToFolder(String dir) {
		if ((toFolder + "\\").equals(dir)) {
			return true;
		}
		return false;
	}

	public String getName() {
		//return Path.GetFileName(fromFolder);
		return (new File(fromFolder)).getName();
	}
	//        public DirectoryInfo getInfo {
	//            return new DirectoryInfo(fromFolder);
	//        }
}
