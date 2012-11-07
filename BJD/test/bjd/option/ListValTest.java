package bjd.option;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;

import org.junit.Test;

import bjd.ctrl.CtrlDat;
import bjd.ctrl.CtrlInt;
import bjd.ctrl.CtrlTabPage;
import bjd.ctrl.OnePage;
import bjd.util.TestUtil;

//@RunWith(Enclosed.class)
public class ListValTest {

	@Test
	public final void a001() {
		TestUtil.dispHeader("a001 getList（)で取得した値の確認（パターン１）");
		TestUtil.dispPrompt(this);

		//テスト用のListVal作成(パターン１)
		ListVal listVal = createListVal1();

		//listValを名前覧にする
		String actual = arrayToString(listVal.getList(null));
		String expected = "n1,n2,n3,n4,n5,n6,n7,n8,";
		System.out.printf("listVal.getList(null)=%s expected=%s\n", actual, expected);
		assertThat(actual, is(expected));
	}

	@Test
	public final void a002() {
		TestUtil.dispHeader("a002 getList（)で取得した値の確認（パターン２）");
		TestUtil.dispPrompt(this);

		//テスト用のListVal作成(パターン２)
		ListVal listVal = createListVal2();

		//listValを名前覧にする
		String actual = arrayToString(listVal.getList(null));
		String expected = "n0,n1,n2,";
		System.out.printf("listVal.getList(null)=%s expected=%s\n", actual, expected);
		assertThat(actual, is(expected));
	}

	@Test
	public final void a003() {
		TestUtil.dispHeader("a003 search（)で検索に成功するとオブジェクトが返る");
		TestUtil.dispPrompt(this);
		
		//テスト用のListVal作成(パターン１)
		ListVal listVal = createListVal1();
		System.out.printf("listVal.search(n1)!=null \n");
		assertNotNull(listVal.search("n1"));
	}

	@Test
	public final void a004() {
		TestUtil.dispHeader("a004 search（)で検索に失敗するとnullが返される");
		TestUtil.dispPrompt(this);

		//テスト用のListVal作成(パターン１)
		ListVal listVal = createListVal1();
		OneVal  oneVal = listVal.search("xxx");
		assertNull(oneVal);
		TestUtil.dispPrompt(this, String.format("listVal.search(xxx) = %s ", oneVal));
	}

	//テスト用のListVal作成(パターン１)
	private ListVal createListVal1() {

		ListVal listVal = new ListVal();
		listVal.add(new OneVal("n1", 1, Crlf.NEXTLINE, new CtrlInt("help", 10)));
		listVal.add(new OneVal("n2", 1, Crlf.NEXTLINE, new CtrlInt("help", 10)));

		ListVal datList = new ListVal();
		datList.add(new OneVal("n3", 1, Crlf.NEXTLINE, new CtrlInt("help", 10)));
		datList.add(new OneVal("n4", 1, Crlf.NEXTLINE, new CtrlInt("help", 10)));
		listVal.add(new OneVal("n5", 1, Crlf.NEXTLINE, new CtrlDat("help", datList, 10, true)));

		datList = new ListVal();
		datList.add(new OneVal("n6", 1, Crlf.NEXTLINE, new CtrlInt("help", 10)));
		datList.add(new OneVal("n7", 1, Crlf.NEXTLINE, new CtrlInt("help", 10)));
		listVal.add(new OneVal("n8", 1, Crlf.NEXTLINE, new CtrlDat("help", datList, 10, true)));

		return listVal;
	}

	//テスト用のListVal作成(パターン２)
	private ListVal createListVal2() {

		ListVal listVal = new ListVal();

		ArrayList<OnePage> pageList = new ArrayList<>();

		OnePage onePage = new OnePage("page1", "ページ１");
		onePage.add(new OneVal("n0", 1, Crlf.NEXTLINE, new CtrlInt("help", 10)));
		pageList.add(onePage);

		onePage = new OnePage("page2", "ページ２");
		onePage.add(new OneVal("n1", 1, Crlf.NEXTLINE, new CtrlInt("help", 10)));
		pageList.add(onePage);

		listVal.add(new OneVal("n2", null, Crlf.NEXTLINE, new CtrlTabPage("help", pageList)));
		return listVal;
	}

	//listValを名前覧にする
	private String arrayToString(ArrayList<OneVal> list) {
		StringBuilder sb = new StringBuilder();
		for (OneVal o : list) {
			sb.append(o.getName());
			sb.append(",");
		}
		return sb.toString();
	}

}
