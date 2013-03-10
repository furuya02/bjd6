package bjd.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.awt.Font;
import java.io.File;
import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import bjd.Kernel;
import bjd.ValidObjException;
import bjd.ctrl.CtrlAddress;
import bjd.ctrl.CtrlBindAddr;
import bjd.ctrl.CtrlCheckBox;
import bjd.ctrl.CtrlComboBox;
import bjd.ctrl.CtrlDat;
import bjd.ctrl.CtrlFile;
import bjd.ctrl.CtrlFolder;
import bjd.ctrl.CtrlFont;
import bjd.ctrl.CtrlHidden;
import bjd.ctrl.CtrlInt;
import bjd.ctrl.CtrlMemo;
import bjd.ctrl.CtrlRadio;
import bjd.ctrl.CtrlTextBox;
import bjd.ctrl.CtrlType;
import bjd.ctrl.OneCtrl;
import bjd.net.InetKind;
import bjd.net.Ip;
import bjd.net.IpKind;
import bjd.option.Crlf;
import bjd.option.Dat;
import bjd.option.ListVal;
import bjd.option.OneVal;

@RunWith(Enclosed.class)
public class IniDbTest {

	
//	private static IniDb createIniDb(){
//		String fileName = "iniDbTestTmp"; //テンポラリファイル名
//		String progDir = new File(".").getAbsoluteFile().getParent();
//		String path = String.format("%s\\%s.ini", progDir, fileName);
//		return new IniDb(progDir, fileName);
//	}
	
	@RunWith(Theories.class)
	public static final class listVal_add_OneVal_で初期化後saveして当該設定が保存されているかどうか {

		@DataPoints
		public static Fixture[] datas = { 
			new Fixture(CtrlType.INT, 123, "INT=Basic\bname=123"),
			new Fixture(CtrlType.TEXTBOX, "123", "STRING=Basic\bname=123"), 
			new Fixture(CtrlType.COMBOBOX, 1, "LIST=Basic\bname=1"), 
			new Fixture(CtrlType.FILE, "c:\\1.txt", "FILE=Basic\bname=c:\\1.txt"), 
			new Fixture(CtrlType.FOLDER, "c:\\tmp", "FOLDER=Basic\bname=c:\\tmp"), 
			new Fixture(CtrlType.CHECKBOX, true, "BOOL=Basic\bname=true"), 
			new Fixture(CtrlType.HIDDEN, "123", "HIDE_STRING=Basic\bname=2d7ee3636680c1f6"),
			new Fixture(CtrlType.MEMO, "123", "MEMO=Basic\bname=123"), 
			new Fixture(CtrlType.RADIO, 1, "RADIO=Basic\bname=1"),
			new Fixture(CtrlType.ADDRESSV4,	new Ip(InetKind.V4), "ADDRESS_V4=Basic\bname=0.0.0.0"),

			//new Fixture(CtrlType.ADDRESSV4,	new Ip("192.168.0.1"), "ADDRESS_V4=Basic\bname=192.168.0.1"),
		};

		static class Fixture {
			//private OneVal oneVal;
			private CtrlType ctrlType;
			private Object value;
			private String expected;

			public Fixture(CtrlType ctrlType, Object value, String expected) {
				this.ctrlType = ctrlType;
				this.value = value;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) throws Exception {

			//setUp
			String fileName = "iniDbTestTmp"; //テンポラリファイル名
			String progDir = new File(".").getAbsoluteFile().getParent(); //カレントディレクトリ
			String path = String.format("%s\\%s.ini", progDir, fileName);
			IniDb sut = new IniDb(progDir, fileName);
			
			ListVal listVal = new ListVal();
			listVal.add(Assistance.createOneVal(fx.ctrlType, fx.value));
			sut.save("Basic", listVal); // nameTagは"Basic"で決め打ちされている
			
			String expected = fx.expected;
			//exercise
			ArrayList<String> lines = Util.textFileRead(new File(path));
			String actual = lines.get(0);
			//verify
			assertThat(actual, is(expected));
			//tearDown
			sut.deleteIni();
			sut.deleteTxt();
			sut.deleteBak();
		}
	}

	@RunWith(Theories.class)
	public static final class 設定ファイルにテキストでセットしてreadして当該設定が読み込めるかどうか {

		@DataPoints
		public static Fixture[] datas = { 
			new Fixture(CtrlType.INT, "123", "INT=Basic\bname=123"),
			new Fixture(CtrlType.TEXTBOX, "123", "STRING=Basic\bname=123"), 
			new Fixture(CtrlType.COMBOBOX, "1", "LIST=Basic\bname=1"), 
			new Fixture(CtrlType.FILE, "c:\\1.txt", "FILE=Basic\bname=c:\\1.txt"), 
			new Fixture(CtrlType.FOLDER, "c:\\tmp", "FOLDER=Basic\bname=c:\\tmp"), 
			new Fixture(CtrlType.CHECKBOX, "true", "BOOL=Basic\bname=true"), 
			new Fixture(CtrlType.HIDDEN, "2d7ee3636680c1f6", "HIDE_STRING=Basic\bname=2d7ee3636680c1f6"),
			new Fixture(CtrlType.MEMO, "123", "MEMO=Basic\bname=123"), new Fixture(CtrlType.RADIO, "1", "RADIO=Basic\bname=1"),
			new Fixture(CtrlType.ADDRESSV4, "192.168.0.1", "ADDRESS_V4=Basic\bname=192.168.0.1"),		
		};

		static class Fixture {
			private CtrlType ctrlType;
			private String value;
			private String regStr;

			public Fixture(CtrlType ctrlType, String value, String regStr) {
				this.ctrlType = ctrlType;
				this.value = value;
				this.regStr = regStr;
			}
		}

		@Theory
		public void test(Fixture fx) {

			//setUp
			String fileName = "iniDbTestTmp"; //テンポラリファイル名
			String progDir = new File(".").getAbsoluteFile().getParent();
			String path = String.format("%s\\%s.ini", progDir, fileName);


			IniDb sut = new IniDb(progDir, fileName);
			sut.deleteIni();
			sut.deleteTxt();
			sut.deleteBak();

			String expected = fx.value; 
			//exercise
			ArrayList<String> lines = new ArrayList<>();
			lines.add(fx.regStr);
			Util.textFileSave(new File(path), lines);

			ListVal listVal = new ListVal();
			listVal.add(Assistance.createOneVal(fx.ctrlType, null));
			sut.read("Basic", listVal); // nameTagは"Basic"で決め打ちされている
			OneVal oneVal = listVal.search("name");

			String actual = oneVal.toReg(false);

			//verify
			assertThat(actual, is(expected));

			
			//TearDown
			sut.deleteIni();
			sut.deleteTxt();
			sut.deleteBak();
		}
	}
	
	/**
	 * 共通的に利用されるメソッド
	 */
	private static class Assistance {
		/**
		 * OneValの生成
		 * @param val
		 *            デフォルト値(nullを設定した場合、適切な値を自動でセットする)
		 */
		public static OneVal createOneVal(CtrlType ctrlType, Object val) {
			Kernel kernel = new Kernel();
			final String help = "help";
			OneCtrl oneCtrl = null;
			boolean editBrowse = false;
			switch (ctrlType) {
				case CHECKBOX:
					if (val == null) {
						val = true;
					}
					oneCtrl = new CtrlCheckBox(help);
					break;
				case INT:
					if (val == null) {
						val = 1;
					}
					oneCtrl = new CtrlInt(help, 3); // ３桁で決め打ち
					break;
				case FILE:
					if (val == null) {
						val = "1.txt";
					}
					oneCtrl = new CtrlFile(kernel.isJp(), help, 200, kernel.getRunMode(), editBrowse);
					break;
				case FOLDER:
					if (val == null) {
						val = "c:\temp";
					}
					oneCtrl = new CtrlFolder(kernel.isJp(), help, 200, kernel.getRunMode(), editBrowse);
					break;
				case TEXTBOX:
					if (val == null) {
						val = "abc";
					}
					oneCtrl = new CtrlTextBox(help, 20);
					break;
				case RADIO:
					if (val == null) {
						val = 0;
					}
					oneCtrl = new CtrlRadio(help, new String[] { "1", "2", "3" }, 30, 3);
					break;
				case FONT:
					if (val == null) {
						val = new Font("Default", Font.PLAIN, 9);
					}
					oneCtrl = new CtrlFont(help, true);
					break;
				case MEMO:
					if (val == null) {
						val = "1";
					}
					oneCtrl = new CtrlMemo(help, 10, 10);
					break;
				case HIDDEN:
					if (val == null) {
						val = "";
					}
					oneCtrl = new CtrlHidden(help, 30);
					break;
				case ADDRESSV4:
					if (val == null) {
						val = "";
					}
					oneCtrl = new CtrlAddress(help);
					break;
				case BINDADDR:
					if (val == null) {
						val = "V4ONLY,INADDR_ANY,IN6ADDR_ANY_INIT";
					}
					ArrayList<Ip> list = new ArrayList<>();
					try {
						list.add(new Ip(IpKind.INADDR_ANY));
						list.add(new Ip("192.168.0.1"));
					} catch (ValidObjException e) {
						Assert.fail(e.getMessage());
					}
					oneCtrl = new CtrlBindAddr(help, list.toArray(new Ip[]{}), list.toArray(new Ip[]{}));
					break;
				case COMBOBOX:
					//listを{"1","2"}で決め打ち

					if (val == null) {
						val = 0;
					}
					oneCtrl = new CtrlComboBox(help, new String[] { "1", "2" }, 10);
					break;
				case DAT:
					//カラムはTEXTBOX×2で決め打ち
					ListVal listVal = new ListVal();
					listVal.add(new OneVal("name1", true, Crlf.NEXTLINE, new CtrlCheckBox("help")));
					listVal.add(new OneVal("name2", true, Crlf.NEXTLINE, new CtrlCheckBox("help")));

					if (val == null) {
						val = (Dat) new Dat(new CtrlType[] { CtrlType.CHECKBOX, CtrlType.CHECKBOX });
					}

					oneCtrl = new CtrlDat(help, listVal, 300, true);
					break;
				default:
					// not implement.
					throw new IllegalArgumentException(ctrlType.toString());
			}
			return new OneVal("name", val, Crlf.NEXTLINE, oneCtrl);
		}

	}

}

