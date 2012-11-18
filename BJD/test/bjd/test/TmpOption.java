package bjd.test;

import java.io.File;

import bjd.Kernel;
import bjd.util.IDispose;
import bjd.util.Util;

public final class TmpOption implements IDispose {
	private Kernel kernel = new Kernel();
	private File origin;
	private File backup;
	private File target = null;
	private String testDataPath;

	public TmpOption(String fileName) {
		String currentDir = new File(".").getAbsoluteFile().getParent(); // カレントディレクトリ
		testDataPath = String.format("%s\\testData", currentDir);

		String originName = String.format("%s\\Option.ini", kernel.getProgDir());
		String backupName = String.format("%s\\Option.bak", testDataPath);

		//オリジナルファイル
		origin = new File(originName);
		//BACKUPファイル
		backup = new File(backupName);
		//上書きファイル
		String targetName = String.format("%s\\%s", testDataPath, fileName);
		target = new File(targetName);

		if (!target.exists()) {
			throw new IllegalArgumentException(String.format("指定されたファイルが見つかりません。 %s", target.getPath()));
		}

		//バックアップ作成
		if (origin.exists()) {
			Util.fileCopy(origin, backup);
		}
		//上書き
		Util.fileCopy(target, origin);
	}

	public void dispose() {
		if (backup.exists()) {
			Util.fileCopy(backup, origin);
			backup.delete();
		}
	}

}
