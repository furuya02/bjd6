package bjd;

/**
 * アプリケーションの動作モード<br>
 * @author SIN
 *
 */
public enum RunMode {
	/**通常起動  (ウインドあり)*/
    Normal,
    /**通常起動（サ-ビス登録済み）(ウインドあり)*/
    NormalRegist, 
    /**リモート（ウインドあり）*/
    Remote,
    /**サービス起動　(ウインドなし)*/
    Service, 
}
