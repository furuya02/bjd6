//package bjd.option;
//

//import org.junit.Test;
//import static org.junit.Assert.*;
//import static org.hamcrest.CoreMatchers.is;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertNull;
//import static org.junit.Assert.assertThat;
//
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.experimental.runners.Enclosed;
//import org.junit.experimental.theories.DataPoints;
//import org.junit.experimental.theories.Theories;
//import org.junit.experimental.theories.Theory;
//import org.junit.runner.RunWith;
//

class Example {

}
//
//@RunWith(Enclosed.class)
//public class ListValTest {
//	@RunWith(Theories.class)
//	public static class A001 {
//		@BeforeClass
//		public static void before() {
//			TestUtil.dispHeader("デフォルト値をtoReg()で取り出す"); //TESTヘッダ
//		}
//
//		@DataPoints
//		public static Fixture[] datas = {
//				//コントロールの種類,デフォルト値,toRegの出力
//				new Fixture(CtrlType.CHECKBOX, true, "true"), new Fixture(CtrlType.CHECKBOX, false, "false"), new Fixture(CtrlType.INT, 100, "100"),
//		};
//		static class Fixture {
//			private Object actual;
//			private String expected;
//
//			public Fixture(CObject actual, String expected) {
//				this.actual = actual;
//				this.expected = expected;
//			}
//		}
//
//		@Theory
//		public void test(Fixture fx) {
//
//			TestUtil.dispPrompt(this); //TESTプロンプト
//
//			System.out.printf("(%s) default値=%s toReg()=\"%s\"\n", fx.ctrlType, fx.actual, fx.expected);
//
//			OneVal oneVal = Util.createOneVal(fx.ctrlType, fx.actual);
//			boolean isDebug = false;
//			assertThat(oneVal.toReg(isDebug), is(fx.expected));
//		}
//	}

//TestUtil.dispHeader("TODO TEST example");
//fail("まだ実装されていません");
//}

