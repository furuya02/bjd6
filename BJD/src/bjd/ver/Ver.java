package bjd.ver;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import bjd.util.Msg;
import bjd.util.MsgKind;

//バージョン管理クラス
public class Ver {
	//Kernel kernel;
	private ArrayList<String> ar = new ArrayList<String>();

	//	String executablePath = getProgDir();
	//	executablePath = executablePath.replaceAll("\\\\", "\\\\\\\\");

	private String progDir;

	public Ver(String progDir) {
		this.progDir = progDir;
		//this.kernel = kernel;

		File f = new File(progDir + "\\plugins");
		File[] files = f.listFiles();
		for (File file : files) {
			ar.add(file.getPath());
		}
		//		String[] files = Directory.GetFiles(Path.GetDirectoryName(dir, "*.dll");
		//		for (String file : files) {
		//			ar.add(Path.GetFileNameWithoutExtension(file));
		//		}
		Collections.sort(ar);

	}

	public final String getVersion() {
		return "1.0.0";
	}

	final String fullPath(String name) {
		return String.format("%s\\%s.dll", progDir, name);
	}

	//ファイルの最終更新日時を文字列で取得する
	final String fileDate(String fileName) {
		//var info = new FileInfo(fileName);
		//return info.LastWriteTime.Ticks.tostring();

		File fl = new File(fileName);
		Date da = new Date(fl.lastModified());
		return String.valueOf(da.getTime());

	}

	//ファイル日付の検証
	final boolean checkDate(String ticks, String fileName) {

		File fl = new File(fileName);
		Date dt = new Date(fl.lastModified());
		if (dt.getTime() == Long.valueOf(ticks)) {
			return true;
		}
		//		var info = new FileInfo(fileName);
		//		if (info.LastWriteTime == dt)
		//			return true;
		return false;
	}

	//【バージョン情報】（リモートサーバがクライアントに送信する）
	public final String verData() {
		StringBuilder sb = new StringBuilder();

		sb.append(getVersion() + "\t"); //バージョン文字列
		sb.append(fileDate(progDir + "\t")); //BJD.EXEのファイル日付
		for (String name : ar) {
			sb.append(name + "\t"); //DLL名
			//sb.append(fileDate(FullPath(name)) + "\t"); //DLLのファイル日付
			sb.append(fileDate(name) + "\t"); //DLLのファイル日付
		}
		return sb.toString();
	}

	//【バージョン情報の確認】（受け取ったバージョン情報を検証する）
	public final boolean verData(String verDataStr) {
		boolean match = true;
		StringBuilder sb = new StringBuilder();
		//String[] tmp = verDataStr.split('\t', StringSplitOptions.RemoveEmptyEntries);
		String[] tmp = verDataStr.split("\\t");
		int c = 0;

		//バージョン文字列
		String verStr = tmp[c++];
		if (!verStr.equals(getVersion())) {
			sb.append(String.format("\r\nA version does not agree. (Server:%s Client:%s)", verStr, getVersion()));
			match = false;
		}

		//BJD.EXEのファイル日付
		String ticks = tmp[c++];
		//Ver5.7.0 解凍する際のアーカイバによってファイル日付が変化する可能性があるため、この確認は実施しない
		//if (!CheckDate(ticks, Application.ExecutablePath)) {
		//    sb.append(String.format("\r\n[BJD.EXE]  Timestamp is different"));
		//    match = false;
		//}

		for (; c < tmp.length; c += 2) {
			String name = tmp[c];
			ticks = tmp[c + 1];
			if (ar.indexOf(name) == -1) { //DLL名（存在確認）
				sb.append(String.format("\r\n[%s.dll] not found", name));
				match = false;
			} else { //DLLのファイル日付確認
				//Ver5.7.0 解凍する際のアーカイバによってファイル日付が変化する可能性があるため、この確認は実施しない
				//if (!CheckDate(ticks, FullPath(name))) {
				//    sb.append(String.format("\r\n[{0}.dll] Timestamp is different", name));
				//    match = false;
				//}
			}
		}

		if (!match) {
			Msg.show(MsgKind.ERROR, "リモートクライアントを使用することはできません。\r\n" + sb);
		}
		return match;
	}
}
