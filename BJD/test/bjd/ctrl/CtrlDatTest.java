package bjd.ctrl;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Test;

import bjd.Kernel;
import bjd.option.Crlf;
import bjd.option.ListVal;
import bjd.option.OneVal;

public class CtrlDatTest {

	@Test
	public final void privateメンバのimportDatとexportDatの整合性を確認する() {

		boolean isJp = true;
		Kernel kernel = new Kernel();

		ListVal list = new ListVal();
		list.add(new OneVal("combo", 0, Crlf.NEXTLINE, new CtrlComboBox("コンボボックス", new String[] { "DOWN", "PU", "FULL" }, 200)));
		list.add(new OneVal("fileName2", "c:\\work", Crlf.NEXTLINE, new CtrlFolder("フォルダ", 30, kernel)));
		list.add(new OneVal("text", "user1", Crlf.NEXTLINE, new CtrlTextBox("テキスト入力", 30)));
		list.add(new OneVal("hidden", "123", Crlf.NEXTLINE, new CtrlHidden("パスワード", 30)));
		CtrlDat sut = new CtrlDat("help", list, 100, isJp);
		sut.create(null, 0, 0, null);

		ArrayList<String> in = new ArrayList<>();
		in.add("#	0	c:\\work	user1	c3a5e1369325e2ca");
		in.add(" 	1	c:\\work	user2	b867684066caf9dc");
		in.add(" 	2	c:\\work	user3	4911d0d49c8911ed");

		try {
			//リフレクションによるprivateメンバへのアクセス
			Method exportDat = CtrlDat.class.getDeclaredMethod("exportDat");
			Method importDat = CtrlDat.class.getDeclaredMethod("importDat", ArrayList.class);
			exportDat.setAccessible(true);
			importDat.setAccessible(true);
			importDat.invoke(sut, in);
			@SuppressWarnings("unchecked")
			ArrayList<String> out = (ArrayList<String>) exportDat.invoke(sut);

			for (int i = 0; i < in.size(); i++) {
				//TestUtil.prompt(String.format("import> %s", in.get(i)));
				//TestUtil.prompt(String.format("export> %s", out.get(i)));
				
				assertThat(in.get(i), is(out.get(i)));
			}

		} catch (Exception e) {
			Assert.fail(e.toString());
		}

	}
}
