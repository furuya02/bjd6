package bjd.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import org.junit.Test;

import bjd.ctrl.CtrlDat;
import bjd.ctrl.CtrlTextBox;
import bjd.option.Crlf;
import bjd.option.ListVal;
import bjd.option.OneVal;

public class IniDbTest2 {
	@Test
	public void データの無いDATの保存() throws Exception {

		//setUp
		String fileName = "iniDbTestTmp"; //テンポラリファイル名
		String progDir = new File(".").getAbsoluteFile().getParent(); //カレントディレクトリ
		String path = String.format("%s\\%s.ini", progDir, fileName);
		IniDb sut = new IniDb(progDir, fileName);

		ListVal listVal = new ListVal();
		ListVal l = new ListVal();
		l.add(new OneVal("mimeExtension", "", Crlf.NEXTLINE, new CtrlTextBox("Extension", 10)));
		l.add(new OneVal("mimeType", "", Crlf.NEXTLINE, new CtrlTextBox("MIME Type", 50)));
		OneVal oneVal = new OneVal("mime", null, Crlf.NEXTLINE, new CtrlDat("comment", l, 350, true));
		listVal.add(oneVal);

		sut.save("Basic", listVal); // nameTagは"Basic"で決め打ちされている

		//exercise
		ArrayList<String> lines = Util.textFileRead(new File(path));
		String actual = lines.get(0);
		//verify
		assertThat(actual, is("DAT=Basic\bmime="));
		//tearDown
		sut.delete();
		
	}
}