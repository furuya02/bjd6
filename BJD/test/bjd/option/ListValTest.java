package bjd.option;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;

import org.junit.Test;

import bjd.ctrl.CtrlDat;
import bjd.ctrl.CtrlInt;
import bjd.ctrl.CtrlTabPage;
import bjd.ctrl.OnePage;

/**
 * テストでは、リソースの開放（dispose）を省略する
 * @author SIN
 *
 */
public class ListValTest {

	@Test
	public void パターン１で作成したListValをgetListで取得する() throws Exception {
		//setUp
		ListVal sut = createListVal1();
		String expected = "n1,n2,n3,n4,n5,n6,n7,n8,";

		//exercise
		String actual = arrayToString(sut.getList(null));

		//verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void パターン２で作成したListValをgetListで取得する() throws Exception {
		//setUp
		ListVal sut = createListVal2();
		String expected = "n0,n1,n2,";

		//exercise
		String actual = arrayToString(sut.getList(null));

		//verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void 存在するデータを検査するとnull以外が返る() throws Exception {
		//setUp
		ListVal sut = createListVal1();

		//exercise
		Object actual = sut.search("n1");

		//verify
		assertThat(actual, is(notNullValue()));
	}

	@Test
	public void 存在しないデータを検査するとnullが返る() throws Exception {
		//setUp
		ListVal sut = createListVal1();

		//exercise
		Object actual = sut.search("xxx");

		//verify
		assertThat(actual, is(nullValue()));
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

	//listValを名前一覧（文字列）に変換する
	private String arrayToString(ArrayList<OneVal> list) {
		StringBuilder sb = new StringBuilder();
		for (OneVal o : list) {
			sb.append(o.getName());
			sb.append(",");
		}
		return sb.toString();
	}

}
