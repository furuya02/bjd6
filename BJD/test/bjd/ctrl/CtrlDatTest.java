package bjd.ctrl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.junit.Test;

import bjd.RunMode;
import bjd.option.Crlf;
import bjd.option.ListVal;
import bjd.option.OneVal;
import bjd.util.TestUtil;

public class CtrlDatTest {

	@Test
	public final void a001() {

		TestUtil.dispHeader("a001 importDat()及びexportDat()");
		TestUtil.dispPrompt(this);

		//Kernel kernel = new Kernel();
		boolean isJp = true;
		RunMode runMode = RunMode.Normal;
		boolean editBrowse = false;

		ListVal list = new ListVal();
		list.add(new OneVal("combo", 0, Crlf.NEXTLINE, new CtrlComboBox("コンボボックス", new String[] { "DOWN", "PU", "FULL" }, 200)));
		list.add(new OneVal("fileName2", "c:\\work", Crlf.NEXTLINE, new CtrlFolder(isJp, "フォルダ", 30, runMode, editBrowse)));
		list.add(new OneVal("text", "user1", Crlf.NEXTLINE, new CtrlTextBox("テキスト入力", 30)));
		list.add(new OneVal("hidden", "123", Crlf.NEXTLINE, new CtrlHidden("パスワード", 30)));
		CtrlDat ctrlDat = new CtrlDat("help", list, 100, isJp);
		ctrlDat.create(null, 0, 0, null);

		ArrayList<String> in = new ArrayList<>();
		in.add("#	0	c:\\work	user1	c3a5e1369325e2ca");
		in.add(" 	1	c:\\work	user2	b867684066caf9dc");
		in.add(" 	2	c:\\work	user3	4911d0d49c8911ed");
		//		ctrlDat.importDat(in);

		try {
			//リフレクション　importDat及びexportDatはprivateメンバ
			Method exportDat = CtrlDat.class.getDeclaredMethod("exportDat");
			Method importDat = CtrlDat.class.getDeclaredMethod("importDat", ArrayList.class);
			exportDat.setAccessible(true);
			importDat.setAccessible(true);

			importDat.invoke(ctrlDat, in);
			@SuppressWarnings("unchecked")
			ArrayList<String> out = (ArrayList<String>) exportDat.invoke(ctrlDat);

			for (int i = 0; i < in.size(); i++) {
				System.out.println(String.format("import> %s", in.get(i)));
				System.out.println(String.format("export> %s", out.get(i)));
				assertThat(in.get(i), is(out.get(i)));
			}

		} catch (Exception e) {
			System.out.println(e);
			fail(e.toString());
		}

	}
}
