package bjd;

/**
 * 例外(RegException）の種類<br>
 * 
 * @author SIN
 *
 */
enum RegExceptionKind {
	/**key==null 若しくは key==""*/
	InvalidKey,
	/**keyでの検索に失敗した*/
	ValueNotFound,
	/**取得した値がint型では無い*/
	NotNumberFormat,
}

/**
 * 
 * クラスReg用の例外クラス<br>
 * @author SIN
 *
 */
@SuppressWarnings("serial")
public final class RegException extends Exception {
	private RegExceptionKind kind;

	/**
	 * @param msg　詳細メッセージ
	 * @param kind 例外の種類 
	 */
	public RegException(String msg, RegExceptionKind kind) {
		super(msg);
		this.kind = kind;
	}

	/**
	 * 例外の種類を取得する
	 * @return　例外の種類
	 */
	public RegExceptionKind getKind() {
		return kind;
	}

}