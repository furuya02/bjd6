package bjd.plugins.ftp;

import java.io.File;
import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Test;

public final class CurrentDirTest {

	@Test
	public void contentTypeTest() {
		//....\\FtpServerTest\\TestDir
		String dir = new File(".").getAbsoluteFile().getParentFile().getParent(); //c:\dev\bjd6
		String workDir = dir + "\\work";
		String rootDirectory = workDir + "\\FtpTestDir";

		ListMount listMount = new ListMount(null);

		//var op  = new Option(kernel, "", "Ftp");
		String homeDir = String.format("%s\\home0", rootDirectory);

		//ディレクトリ変更 と表示テキスト
		CurrentDir currentDir = new CurrentDir(homeDir, listMount); //初期化
		Assert.assertEquals(currentDir.cwd("home0-sub0"), true);
		Assert.assertEquals(currentDir.getPwd(), "/home0-sub0");

		currentDir = new CurrentDir(homeDir, listMount); //初期化
		Assert.assertEquals(currentDir.cwd("home0-sub0/sub0-sub0"), true);
		Assert.assertEquals(currentDir.getPwd(), "/home0-sub0/sub0-sub0");

		currentDir = new CurrentDir(homeDir, listMount); //初期化
		Assert.assertEquals(currentDir.cwd("home0-sub0/sub0-sub0"), true);
		Assert.assertEquals(currentDir.cwd(".."), true);
		Assert.assertEquals(currentDir.getPwd(), "/home0-sub0");

		//ホームディレクトリより階層上へは移動できない
		currentDir = new CurrentDir(homeDir, listMount); //初期化
		Assert.assertEquals(currentDir.cwd("home0-sub0/sub0-sub0"), true);
		Assert.assertEquals(currentDir.cwd(".."), true);
		Assert.assertEquals(currentDir.getPwd(), "/home0-sub0");
		Assert.assertEquals(currentDir.cwd(".."), true);
		Assert.assertEquals(currentDir.getPwd(), "/");
		Assert.assertEquals(currentDir.cwd(".."), false);
		Assert.assertEquals(currentDir.getPwd(), "/");

		//存在しないディレクトリへの変更
		currentDir = new CurrentDir(homeDir, listMount);
		Assert.assertEquals(currentDir.cwd("home0-sub0/sub0"), false);
		Assert.assertEquals(currentDir.getPwd(), "/");

		//初期化文字列の対応
		currentDir = new CurrentDir(homeDir + "\\", listMount);
		Assert.assertEquals(currentDir.cwd("home0-sub0"), true);
		Assert.assertEquals(currentDir.getPwd(), "/home0-sub0");

		//ファイル一覧の取得
		currentDir = new CurrentDir(homeDir, listMount);
		ArrayList<String> ar = new ArrayList<>();
		ar.add("d home0-sub0");
		ar.add("d home0-sub1");
		ar.add("d home0-sub2");
		ar.add("- home0-1.txt");
		ar.add("- home0-2.txt");
		ar.add("- home0-3.txt");
		Assert.assertEquals(confirm(currentDir, "*.*", ar), true);

		//ファイル一覧の取得
		currentDir = new CurrentDir(homeDir, listMount);
		Assert.assertEquals(currentDir.cwd("home0-sub0"), true);
		ar.clear();
		ar.add("- home0-sub0-1.txt");
		ar.add("- home0-sub0-2.txt");
		ar.add("- home0-sub0-3.txt");
		Assert.assertEquals(confirm(currentDir, "*.txt", ar), true);

		//**************************************************
		//仮想フォルダを追加して試験する
		//**************************************************
		String fromFolder = String.format("%s\\home2", rootDirectory);
		String toFolder = String.format("%s\\home0", rootDirectory);
		listMount.add(fromFolder, toFolder);

		//ファイル一覧の取得
		currentDir = new CurrentDir(homeDir, listMount);

		ar.clear();
		ar.add("d home0-sub0");
		ar.add("d home0-sub1");
		ar.add("d home0-sub2");
		ar.add("d home2");
		ar.add("- home0-1.txt");
		ar.add("- home0-2.txt");
		ar.add("- home0-3.txt");
		Assert.assertEquals(confirm(currentDir, "*.*", ar), true);
		Assert.assertEquals(currentDir.cwd("home2"), true);
		Assert.assertEquals(currentDir.getPwd(), "/home2");
		Assert.assertEquals(currentDir.cwd("home2-sub0"), true);
		Assert.assertEquals(currentDir.getPwd(), "/home2/home2-sub0");
		Assert.assertEquals(currentDir.cwd(".."), true);
		Assert.assertEquals(currentDir.getPwd(), "/home2");
		Assert.assertEquals(currentDir.cwd(".."), true);
		Assert.assertEquals(currentDir.getPwd(), "/");

		Assert.assertEquals(currentDir.cwd("home2/home2-sub0"), true);
		Assert.assertEquals(currentDir.getPwd(), "/home2/home2-sub0");
		Assert.assertEquals(currentDir.cwd("../../.."), false);
		Assert.assertEquals(currentDir.getPwd(), "/home2/home2-sub0");
		Assert.assertEquals(currentDir.cwd("../.."), true);
		Assert.assertEquals(currentDir.getPwd(), "/");

		//**************************************************
		////仮想フォルダを追加して試験する
		//**************************************************
		fromFolder = workDir + "\\FtpTestDir2\\tmp";
		toFolder = String.format("%s\\home0", rootDirectory);
		listMount.add(fromFolder, toFolder);

		//ファイル一覧の取得
		currentDir = new CurrentDir(homeDir, listMount);
		ar.clear();
		ar.add("d home0-sub0");
		ar.add("d home0-sub1");
		ar.add("d home0-sub2");
		ar.add("d home2");
		ar.add("d tmp");
		ar.add("- home0-1.txt");
		ar.add("- home0-2.txt");
		ar.add("- home0-3.txt");
		Assert.assertEquals(confirm(currentDir, "*.*", ar), true);
		Assert.assertEquals(currentDir.cwd("tmp"), true);
		Assert.assertEquals(currentDir.getPwd(), "/tmp");
		Assert.assertEquals(currentDir.cwd("sub"), true);
		Assert.assertEquals(currentDir.getPwd(), "/tmp/sub");
		Assert.assertEquals(currentDir.cwd(".."), true);
		Assert.assertEquals(currentDir.getPwd(), "/tmp");
		Assert.assertEquals(currentDir.cwd(".."), true);
		Assert.assertEquals(currentDir.getPwd(), "/");

		Assert.assertEquals(currentDir.cwd("tmp/sub"), true);
		Assert.assertEquals(currentDir.getPwd(), "/tmp/sub");
		Assert.assertEquals(currentDir.cwd("../../.."), false);
		Assert.assertEquals(currentDir.getPwd(), "/tmp/sub");
		Assert.assertEquals(currentDir.cwd("../.."), true);
		Assert.assertEquals(currentDir.getPwd(), "/");

	}

	//CurrentDir.List()の確認用メソッド
	boolean confirm(CurrentDir currentDir, String mask, ArrayList<String> list) {

		//widwMode=trueで試験を実施する
		boolean wideMode = true;
		//一覧取得
		ArrayList<String> ar = currentDir.list(mask, wideMode);
		//件数確認
		if (ar.size() != list.size()) {
			return false;
		}
		//確認用テンポラリ文字列を生成する
		//var tmp = ar.Select(l => l.Split(' ')).Select(a => a[0][0] + " " + a[8]).ToList();
		ArrayList<String> tmp = new ArrayList<>();
		for (String s : ar) {
			String[] t = s.split(" ");
			String l = String.format("%s %s", t[0].charAt(0), t[8]);
			tmp.add(l);

		}
		//指定リストに該当したテンポラリ行を削除していく
		for (String l : list) {
			int index = tmp.indexOf(l);
			if (index < 0) {
				return false;
			}
			tmp.remove(index);
		}
		//テンポラリが０行になったら成功
		if (tmp.size() != 0) {
			return false;
		}

		//widwMode=falseでもう一度同じ要領で試験を実施する
		wideMode = false;
		//一覧取得
		tmp = currentDir.list(mask, wideMode);
		//件数確認
		if (tmp.size() != list.size()) {
			return false;
		}

		//指定レストに該当したテンポラリ行を削除していく
		for (String l : list) {
			int index = tmp.indexOf(l.substring(2));
			if (index < 0) {
				return false;
			}
			tmp.remove(index);
		}
		//テンポラリが０行になったら成功
		if (tmp.size() != 0) {
			return false;
		}
		return true;

	}

}
