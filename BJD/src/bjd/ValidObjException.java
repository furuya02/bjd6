package bjd;
/**
 * ValidObj用のチェック例外<br>
 * 初期化文字列が不正なため初期化に失敗している<br>
 * 
 * @author SIN
 *
 */
@SuppressWarnings("serial")
public class ValidObjException extends Exception {
	public ValidObjException(String msg) {
		super(msg);
	}
}
