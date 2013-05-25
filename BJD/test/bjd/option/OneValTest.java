/**
 * 
 */
package bjd.option;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.awt.Font;
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
import bjd.net.BindAddr;
import bjd.net.BindStyle;
import bjd.net.InetKind;
import bjd.net.Ip;
import bjd.net.IpKind;

@RunWith(Enclosed.class)
public class OneValTest {

	@RunWith(Theories.class)
	public static final class デフォルト値をtoRegで取り出す {

		@DataPoints
		public static Fixture[] datas = {
				//コントロールの種類,デフォルト値,toRegの出力
				new Fixture(CtrlType.CHECKBOX, true, "true"),
				new Fixture(CtrlType.CHECKBOX, false, "false"),
				new Fixture(CtrlType.INT, 100, "100"),
				new Fixture(CtrlType.INT, 0, "0"),
				new Fixture(CtrlType.INT, -100, "-100"),
				new Fixture(CtrlType.FILE, "c:\\test.txt", "c:\\test.txt"),
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
			private Object val;
			private String expected;

			public Fixture(CtrlType ctrlType, Object val, String expected) {
				this.ctrlType = ctrlType;
				this.val = val;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) throws Exception {
			//setUp
			boolean isSecret = false;
			OneVal sut = Assistance.createOneVal(fx.ctrlType, fx.val);
			String expected = fx.expected;
			//exercise
			String actual = sut.toReg(isSecret);
			//verify
			assertThat(actual, is(expected));
		}
	}

	@RunWith(Theories.class)
	public static final class fromRegで設定した値をtoRegで取り出す {

		@DataPoints
		public static Fixture[] datas = {
				//コントロールの種類,fromRegで設定してtoRegで取得する文字列(isDebug=false)
				new Fixture(CtrlType.CHECKBOX, "true"),
				new Fixture(CtrlType.CHECKBOX, "false"),
				new Fixture(CtrlType.INT, "100"),
				new Fixture(CtrlType.INT, "0"),
				new Fixture(CtrlType.FILE, "c:\\test.txt"),
				new Fixture(CtrlType.FOLDER, "c:\\test"),
				new Fixture(CtrlType.TEXTBOX, "abcdefg１２３"),
				new Fixture(CtrlType.RADIO, "1"),
				new Fixture(CtrlType.RADIO, "0"),
				new Fixture(CtrlType.FONT, "Times New Roman,2,15"),
				new Fixture(CtrlType.FONT, "Serif,1,8"),
				new Fixture(CtrlType.MEMO, "1\t2\t3\t"),
				new Fixture(CtrlType.HIDDEN, "2d7ee3636680c1f6"),
				new Fixture(CtrlType.HIDDEN, "60392a0d922b9077"),
				new Fixture(CtrlType.ADDRESSV4, "192.168.0.1"),
				new Fixture(CtrlType.DAT, "\tn1\tn2"),
				new Fixture(CtrlType.DAT, "\tn1\tn2\b\tn1#\tn2"),
				new Fixture(CtrlType.BINDADDR, "V4ONLY,INADDR_ANY,IN6ADDR_ANY_INIT"),
				new Fixture(CtrlType.BINDADDR, "V6ONLY,198.168.0.1,ffe0::1"),
				new Fixture(CtrlType.COMBOBOX, "1"),

		};

		static class Fixture {
			private CtrlType ctrlType;
			private String str;

			public Fixture(CtrlType ctrlType, String str) {
				this.ctrlType = ctrlType;
				this.str = str;
			}
		}

		@Theory
		public void test(Fixture fx) {
			//setUp
			boolean isSecret = false;
			OneVal sut = Assistance.createOneVal(fx.ctrlType, null);
			sut.fromReg(fx.str);
			String expected = fx.str;
			//exercise
			String actual = sut.toReg(isSecret);
			//verify
			assertThat(actual, is(expected));
		}
	}

	@RunWith(Theories.class)
	public static final class fromRegの不正パラメータ判定 {

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
				new Fixture(CtrlType.FILE, "c:\\test.txt", true),
				new Fixture(CtrlType.FOLDER, "c:\\test", true),
				new Fixture(CtrlType.TEXTBOX, "abcdefg１２３", true),
				new Fixture(CtrlType.RADIO, "0", true), new Fixture(CtrlType.RADIO, "5", true),
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
			private String str;
			private boolean expected;

			public Fixture(CtrlType ctrlType, String str, boolean expected) {
				this.ctrlType = ctrlType;
				this.str = str;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {
			//setUp
			OneVal sut = Assistance.createOneVal(fx.ctrlType, null);
			boolean expected = fx.expected;
			//exercise
			boolean actual = sut.fromReg(fx.str);
			//verify
			assertThat(actual, is(expected));
		}
	}

	@RunWith(Theories.class)
	public static final class isDebug_trueの時のtoReg出力 {

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
			private String str;
			private String expected;

			public Fixture(CtrlType ctrlType, boolean isDebug, String str, String expected) {
				this.ctrlType = ctrlType;
				this.isDebug = isDebug;
				this.str = str;
				this.expected = expected;
			}
		}

		@Theory
		public void test(Fixture fx) {
			//setUp
			OneVal sut = Assistance.createOneVal(fx.ctrlType, fx.str);
			String expected = fx.expected;
			//exercise
			String actual = sut.toReg(fx.isDebug);
			//verify
			assertThat(actual, is(expected));
		}
	}

	@RunWith(Theories.class)
	public static final class readCtrl_false_でデフォルトの値に戻るかどうかのテスト {

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
				new Fixture(CtrlType.ADDRESSV4, new Ip(IpKind.V6_LOCALHOST)), //追加
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
			//setUp
			OneVal sut = Assistance.createOneVal(fx.ctrlType, fx.value);
			sut.createCtrl(null, 0, 0);
			boolean b = sut.readCtrl(false); //isConfirm = false; 確認のみではなく、実際に読み込む
			Assert.assertTrue(b); // readCtrl()の戻り値がfalseの場合、読み込みに失敗している
			Object expected = fx.value;
			//exercise
			Object actual = sut.getValue();
			//verify
			assertThat(actual, is(expected));
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
			boolean isJp = true;
			//RunMode runMode = RunMode.Normal;
			//boolean editBrowse = true;
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
					oneCtrl = new CtrlFile(help, 200, kernel);
					break;
				case FOLDER:
					if (val == null) {
						val = "c:\temp";
					}
					oneCtrl = new CtrlFolder(help, 200, kernel);
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
					} catch (ValidObjException ex) {
						Assert.fail(ex.getMessage());

					}
					oneCtrl = new CtrlBindAddr(help, list.toArray(new Ip[] {}), list.toArray(new Ip[] {}));
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
