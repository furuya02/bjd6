package bjd.option;

import bjd.ValidObjException;
import bjd.ctrl.CtrlType;
import bjd.util.ListBase;

/**
 * オリジナルデータ型<br>
 * OneDatのリストを管理する<br>
 * コンストラクタで指定する CtrlType [] によってOneDatの型が及びシークレートカラムが決定する<br>
 * 
 * @author SIN
 *
 */
public final class Dat extends ListBase<OneDat> {
	
	/**
	 * シークレット化が必要なカラム
	 */
	private boolean[] isSecretList;
	private int colMax;
	
	/**
	 * コンストラクタ<br>
	 * 
	 * @param ctrlTypeList 扱うOneDatの型指定
	 */
	public Dat(CtrlType [] ctrlTypeList) {
		//カラム数の初期化
		colMax = ctrlTypeList.length;
		//isSecretListの生成
		isSecretList = new boolean[colMax];
		for (int i = 0; i < colMax; i++) {
			isSecretList[i] = false;
			if (ctrlTypeList[i] == CtrlType.HIDDEN) {
				isSecretList[i] = true;
			}
		}
	}
	
	
	/**
	 * 文字列によるOneDatの追加<br>
	 * 内部で、OneDatの型がチェックされる<br>
	 * 
	 * @param enable 有効/無効
	 * @param str OneDatを生成する文字列
	 * @return 成功・失敗
	 */
	public boolean add(boolean enable, String str) {
		if (str == null) {
			return false; //引数にnullが渡されました
		}
		String[] list = str.split("\t");
		if (list.length != colMax) {
			return false; //カラム数が一致しません
		}
		OneDat oneDat;
		try {
			oneDat = new OneDat(enable, list, isSecretList);
		} catch (ValidObjException e) {
			return false; // 初期化文字列が不正
		}
		if (getAr().add(oneDat)) {
			return true;
		}
		return false;
	}
	/**
	 * 文字列化<br>
	 * @param isSecret 秘匿が必要なカラムを***に変換して出力する
	 * @return 出力文字列
	 */
    public String toReg(boolean isSecret) {
    	StringBuilder sb = new StringBuilder();
        for (OneDat o : getAr()) {
            if (sb.length() != 0) {
                sb.append("\b");
            }
            sb.append(o.toReg(isSecret));
        }
        return sb.toString();
    }

    /**
     * 文字列による初期化
     * @param str　初期化文字列
     * @return　成否
     */
	public boolean fromReg(String str) {
 		getAr().clear();
		if (str == null || str.equals("")) {
			return false;
		}
		// 各行処理
		String [] lines = str.split("\b");
		if (lines.length <= 0) {
			return false; //"lines.length <= 0"
		}
		
		for (String l : lines) { 
			//OneDatの生成
			OneDat oneDat;
			try {
				oneDat = new OneDat(true, new String[colMax], isSecretList);
			} catch (ValidObjException e) {
				return false;
			}
			if (l.split("\t").length != isSecretList.length + 1) { // +1はenableカラムの分
				//カラム数の不一致
				return false;
			}
			
			//fromRegによる初期化
			if (oneDat.fromReg(l)) {
				if (getAr().add(oneDat)) {
					continue; // 処理成功
				}
			}
			//処理失敗
			getAr().clear();
			return false;
		}
		return true;
	}
}
