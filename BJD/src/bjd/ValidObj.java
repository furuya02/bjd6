package bjd;

import bjd.util.Util;

/**
 * コンストラクタで文字列を受け取って初期化されるようなオブジェクトの「実行時例外」と「チェック例外」を処理する基底クラス<br>
 * <br>
 * このクラスの使用方法<br>
 * コンストラクタ内で初期化に問題が生じたときは、throwException(String paramStr)を記述しておく<br>
 * これにより、コンストラクタはValidObjException(チェック例外)をスローする<br>
 * コンストラクタ内でthrowException()を使用した場合、ValidObjExceptionのthrowsされるので、呼出元でのtry,catch等が必須となる<br>
 * 無効な文字列で初期する可能性がある呼出元は、この例外を適切に処理する必要がある<br>
 * <br>
 * publicなメソッドには、メソッドの頭でcheckInitialise()を記述しておくと初期化に失敗している場合RuntimeException（実行時例外）となる<br>
 * ※初期化に失敗したオブジェクトが使用されるのは、設計上の問題であるため、実行時例外となっている<br>
 * <br>
 * 継承クラス  Ip Mac BindAddr　OneLog　LocalAddress Acl
 * @author SIN
 *
 */
public abstract class ValidObj {
	/**
	 * 初期化に失敗するとtrueに設定される
	 * trueになっている、このオブジェクトを使用すると「実行時例外」が発生する
	 */
	private boolean initialiseFailed = false; //初期化失敗
	
	
	protected abstract void init();

	/**
	 * コンストラクタで初期化に失敗した時に使用する呼び出す<br>
	 * 内部変数が初期化され例外（IllegalArgumentException）がスローされる<br>
	 * @param paramStr 初期化文字列
	 * @throws ValidObjException 
	 * @throws IllegalArgumentException 
	 */
	protected final void throwException(String paramStr) throws ValidObjException {
		initialiseFailed = true; //初期化失敗
		init(); // デフォルト値での初期化
		//throw new Exception(String.format("引数が不正です \"%s\"", ipStr)); 
		//throw new IllegalArgumentException(String.format("[ValidObj] 引数が不正です。 \"%s\"", paramStr));
		throw new ValidObjException(String.format("[ValidObj] 引数が不正です。 \"%s\"", paramStr));
	}

	
	/**
	 * 初期化が失敗している場合は、実行時例外が発生する<br>
	 * 全ての公開メソッドの最初に挿入する<br>
	 */
	protected final void checkInitialise() {
		if (initialiseFailed) {
			Util.runtimeException("[ValidObj] このオブジェクトは、初期化に失敗しているため使用できません");
		}
	}
}

