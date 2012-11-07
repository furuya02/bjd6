/**
 * 
 */
package bjd.option;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import java.awt.Font;
import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import bjd.RunMode;
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
import bjd.net.BindAddr;
import bjd.net.BindStyle;
import bjd.net.InetKind;
import bjd.net.Ip;
import bjd.util.TestUtil;

@RunWith(Enclosed.class)
public class OneValTest {

	@RunWith(Theories.class)
	public static final class A001 {

		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("デフォルト値をtoReg()で取り出す");
		}

		@DataPoints
		public static Fixture[] datas = {
				//コントロールの種類,デフォルト値,toRegの出力
				new Fixture(CtrlType.CHECKBOX, true, "true"), new Fixture(CtrlType.CHECKBOX, false, "false"), new Fixture(CtrlType.INT, 100, "100"),
				new Fixture(CtrlType.INT, 0, "0"), new Fixture(CtrlType.INT, -100, "-100"), new Fixture(CtrlType.FILE, "c:\\test.txt", "c:\\test.txt"),
				new Fixture(CtrlType.FOLDER, "c:\\test", "c:\\test"),
				new Fixture(CtrlType.TEXTBOX, "abcdefg１２３", "abcdefg１２３"),
				new Fixture(CtrlType.RADIO, 1, "1"),
				new Fixture(CtrlType.RADIO, 5, "5"),
				new Fixture(CtrlType.FONT, new Font("Times New Roman", Font.ITALIC, 15), "Times New Roman,2,15"),
				new Fixture(CtrlType.FONT, new Font("Serif", Font.BOLD, 8), "Serif,1,8"),
				new Fixture(CtrlType.MEMO, "1\r\n2\r\n3\r\n", "1\t2\t3\t"),
				new Fixture(CtrlType.MEMO, "123", "123"),
				new Fixture(CtrlType.HIDDEN, null, "60392a0d922b9077"), //その他はA004でテストする
				new Fixture(CtrlType.ADDRESSV4, "192.168.0.1", "192.168.0.1"),
				new Fixture(CtrlType.DAT, new Dat(new CtrlType[] { CtrlType.TEXTBOX, CtrlType.TEXTBOX }), ""), // CtrlDatはTESTBOX×2で初期化されている
				new Fixture(CtrlType.BINDADDR, new BindAddr(), "V4ONLY,INADDR_ANY,IN6ADDR_ANY_INIT"),
				new Fixture(CtrlType.BINDADDR, new BindAddr(BindStyle.V4ONLY, new Ip(InetKind.V4), new Ip(InetKind.V6)), "V4ONLY,0.0.0.0,::0"),
				new Fixture(CtrlType.COMBOBOX, 0, "0"), new Fixture(CtrlType.COMBOBOX, 1, "1"), };

		static class Fixture {
			private CtrlType ctrlType;
			private Object actual;
			private String expected;

			public Fixture(CtrlType ctrlType, Object actual, String expected) {
				this.ctrlType = ctrlType;
				this.actual = actual;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {

			TestUtil.dispPrompt(this);
			System.out.printf("(%s) default値=%s toReg()=\"%s\"\n", fx.ctrlType, fx.actual, fx.expected);

			OneVal oneVal = Assistance.createOneVal(fx.ctrlType, fx.actual);
			boolean isDebug = false;
			assertThat(oneVal.toReg(isDebug), is(fx.expected));
		}
	}

	@RunWith(Theories.class)
	public static final class A002 {

		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("fromReg()で設定した値をtoReg()で取り出す");
		}

		@DataPoints
		public static Fixture[] datas = {
				//コントロールの種類,fromRegで設定してtoRegで取得する文字列(isDebug=false)
				new Fixture(CtrlType.CHECKBOX, "true"), new Fixture(CtrlType.CHECKBOX, "false"), new Fixture(CtrlType.INT, "100"),
				new Fixture(CtrlType.INT, "0"), new Fixture(CtrlType.FILE, "c:\\test.txt"), new Fixture(CtrlType.FOLDER, "c:\\test"),
				new Fixture(CtrlType.TEXTBOX, "abcdefg１２３"), new Fixture(CtrlType.RADIO, "1"), new Fixture(CtrlType.RADIO, "0"),
				new Fixture(CtrlType.FONT, "Times New Roman,2,15"), new Fixture(CtrlType.FONT, "Serif,1,8"), new Fixture(CtrlType.MEMO, "1\t2\t3\t"),
				new Fixture(CtrlType.HIDDEN, "2d7ee3636680c1f6"),
				new Fixture(CtrlType.HIDDEN, "60392a0d922b9077"),
				new Fixture(CtrlType.ADDRESSV4, "192.168.0.1"), new Fixture(CtrlType.DAT, "\tn1\tn2"), new Fixture(CtrlType.DAT, "\tn1\tn2\b\tn1#\tn2"),
				new Fixture(CtrlType.BINDADDR, "V4ONLY,INADDR_ANY,IN6ADDR_ANY_INIT"), new Fixture(CtrlType.BINDADDR, "V6ONLY,198.168.0.1,ffe0::1"),
				new Fixture(CtrlType.COMBOBOX, "1"),

		};

		static class Fixture {
			private CtrlType ctrlType;
			private String actual;

			public Fixture(CtrlType ctrlType, String actual) {
				this.ctrlType = ctrlType;
				this.actual = actual;
			}
		}

		@Theory
		public void test(Fixture fx) {

			TestUtil.dispPrompt(this);
			System.out.printf("(%s) fromReg(\"%s\") toReg()=\"%s\"\n", fx.ctrlType, fx.actual, fx.actual);

			OneVal oneVal = Assistance.createOneVal(fx.ctrlType, null);

			boolean isDebug = false;
			oneVal.fromReg(fx.actual);
			assertThat(oneVal.toReg(isDebug), is(fx.actual));
		}
	}

	@RunWith(Theories.class)
	public static final class A003 {

		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("fromReg()の不正パラメータ判定");
		}

		@DataPoints
		public static Fixture[] datas = {
				//コントロールの種類,fromRegに入力する文字列,fromRegの戻り値
				new Fixture(CtrlType.CHECKBOX, "true", true),
				new Fixture(CtrlType.CHECKBOX, "TRUE", true),
				new Fixture(CtrlType.CHECKBOX, "false", true),
				new Fixture(CtrlType.CHECKBOX, "FALSE", true),
				new Fixture(CtrlType.CHECKBOX, "t", false), // 不正入力
				new Fixture(CtrlType.CHECKBOX, "", false), // 不正入力
				new Fixture(CtrlType.INT, "-100", true),
				new Fixture(CtrlType.INT, "0", true),
				new Fixture(CtrlType.INT, "aaa", false), // 不正入力
				new Fixture(CtrlType.FILE, "c:\\test.txt", true), new Fixture(CtrlType.FOLDER, "c:\\test", true),
				new Fixture(CtrlType.TEXTBOX, "abcdefg１２３", true), new Fixture(CtrlType.RADIO, "0", true), new Fixture(CtrlType.RADIO, "5", true),
				new Fixture(CtrlType.RADIO, "-1", false), //不正入力 Radioは0以上
				new Fixture(CtrlType.FONT, "Default,-1,1", false), //不正入力(styleが無効値)
				new Fixture(CtrlType.FONT, "Default,2,-1", false), //不正入力(sizeが0以下)
				new Fixture(CtrlType.FONT, "XXX,1,8", true), //　(Font名ではエラーが発生しない)
				new Fixture(CtrlType.FONT, "Serif,1,8", true), //不正入力
				new Fixture(CtrlType.MEMO, null, false), //不正入力
				new Fixture(CtrlType.HIDDEN, null, false), //不正入力
				new Fixture(CtrlType.ADDRESSV4, null, false), //不正入力
				new Fixture(CtrlType.ADDRESSV4, "xxx", false), //不正入力
				new Fixture(CtrlType.ADDRESSV4, "1", false), //不正入力
				new Fixture(CtrlType.DAT, "", false), //不正入力
				new Fixture(CtrlType.DAT, null, false), //不正入力
				new Fixture(CtrlType.DAT, "\tn1", false), //不正入力(カラム不一致)
				new Fixture(CtrlType.BINDADDR, null, false), //不正入力
				new Fixture(CtrlType.BINDADDR, "XXX", false), //不正入力
				new Fixture(CtrlType.COMBOBOX, "XXX", false), //不正入力
				new Fixture(CtrlType.COMBOBOX, null, false), //不正入力
				new Fixture(CtrlType.COMBOBOX, "2", false), //不正入力 list.size()オーバー
		};

		static class Fixture {
			private CtrlType ctrlType;
			private String actual;
			private boolean expected;

			public Fixture(CtrlType ctrlType, String actual, boolean expected) {
				this.ctrlType = ctrlType;
				this.actual = actual;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {

			TestUtil.dispPrompt(this);
			System.out.printf("(%s) fromReg(\"%s\") = %s\n", fx.ctrlType, fx.actual, fx.expected);

			OneVal oneVal = Assistance.createOneVal(fx.ctrlType, null);

			assertSame(oneVal.fromReg(fx.actual), fx.expected);
		}
	}

	@RunWith(Theories.class)
	public static final class A004 {

		@BeforeClass
		public static void before() {
			TestUtil.dispHeader("isDebug=trueの時のtoReg()出力");
		}

		@DataPoints
		public static Fixture[] datas = {
				// コントロールの種類,isDebug,デフォルト値,toRegの出力
				new Fixture(CtrlType.HIDDEN, true, "123", "***"),
				new Fixture(CtrlType.HIDDEN, false, "123", "2d7ee3636680c1f6"),
				new Fixture(CtrlType.HIDDEN, false, "", "60392a0d922b9077"),
		//new Fixture(CtrlType.HIDDEN, false, null, "60392a0d922b9077"),
		//new Fixture(CtrlType.HIDDEN, false, "本日は晴天なり", "35c9f14ba7b574f21d70ddaa6e9277658992ffef4868a5be"), 
		};

		static class Fixture {
			private CtrlType ctrlType;
			private boolean isDebug;
			private String actual;
			private String expected;

			public Fixture(CtrlType ctrlType, boolean isDebug, String actual, String expected) {
				this.ctrlType = ctrlType;
				this.isDebug = isDebug;
				this.actual = actual;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {

			TestUtil.dispPrompt(this);
			System.out.printf("(%s) Default=\"%s\" toReg(%s) = %s\n", fx.ctrlType, fx.actual, fx.isDebug, fx.expected);

			OneVal oneVal = Assistance.createOneVal(fx.ctrlType, fx.actual);
			//String s = oneVal.toReg(fx.isDebug);

			assertThat(oneVal.toReg(fx.isDebug), is(fx.expected));
		}
	}
	
	@RunWith(Theories.class)
	public static final class A005 {

	    @BeforeClass
	    public static void before() {
	        TestUtil.dispHeader("createCtrl()->readCtrl(false)して、デフォルトの値に戻るかどうかのテスト");
	    }

	    
	    @DataPoints
	    public static Fixture[] datas = {
	        new Fixture(CtrlType.CHECKBOX, true), 
	        new Fixture(CtrlType.HIDDEN, "123"), 
	        new Fixture(CtrlType.TEXTBOX, "123"), 
	        new Fixture(CtrlType.MEMO, "123\n123"), 
			new Fixture(CtrlType.CHECKBOX, true),
			new Fixture(CtrlType.INT, 0),
			new Fixture(CtrlType.FOLDER, "c:\\test"),
			new Fixture(CtrlType.TEXTBOX, "abcdefg１２３"),
			new Fixture(CtrlType.RADIO, 1),
			new Fixture(CtrlType.FONT, new Font("Times New Roman", Font.ITALIC, 15)),
			new Fixture(CtrlType.MEMO, "1\r\n2\r\n3\r\n"),
			new Fixture(CtrlType.ADDRESSV4, new Ip(InetKind.V4)),
			//new Fixture(CtrlType.ADDRESSV4, new Ip("192.168.0.1")),
			//new Fixture(CtrlType.DAT, new Dat(new CtrlType[] { CtrlType.TEXTBOX, CtrlType.TEXTBOX })),
			new Fixture(CtrlType.BINDADDR, new BindAddr()),
			new Fixture(CtrlType.COMBOBOX, 0),
	    };

	    static class Fixture {
	        private CtrlType ctrlType;
	        private Object value;

			public Fixture(CtrlType ctrlType, Object value) {
	            this.ctrlType = ctrlType;
	            this.value = value;
	        }
	    }

	    @Theory
	    public void test(Fixture fx) {

	        TestUtil.dispPrompt(this);

	        OneVal oneVal = Assistance.createOneVal(fx.ctrlType, fx.value);
			oneVal.createCtrl(null, 0, 0);
			boolean b = oneVal.readCtrl(false); //isConfirm = false; 確認のみではなく、実際に読み込む
	        
	        Assert.assertTrue(b); // readCtrl()の戻り値がfalseの場合、読み込みに失敗している
	        
	        Object expected = fx.value;
	        Object actual = oneVal.getValue();
			System.out.printf("(%s) new oneVal()->createCtrl()->readCtrl() expected=%s actual=%s\n",
							fx.ctrlType, expected, actual);

			assertThat(expected, is(actual));
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
			//Kernel kernel = new Kernel();
			final String help = "help";
			OneCtrl oneCtrl = null;
			boolean isJp = true;
			RunMode runMode = RunMode.Normal;
			boolean editBrowse = true;
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
					oneCtrl = new CtrlFile(isJp, help, 200, runMode, editBrowse);
					break;
				case FOLDER:
					if (val == null) {
						val = "c:\temp";
					}
					oneCtrl = new CtrlFolder(isJp, help, 200, runMode, editBrowse);
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
						list.add(new Ip("INADDR_ANY"));
						list.add(new Ip("192.168.0.1"));
					} catch (ValidObjException ex) {
						Assert.fail(ex.getMessage());

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
