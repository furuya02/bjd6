package bjd.option;

import javax.swing.JPanel;

import bjd.ctrl.CtrlBindAddr;
import bjd.ctrl.CtrlCheckBox;
import bjd.ctrl.CtrlComboBox;
import bjd.ctrl.CtrlDat;
import bjd.ctrl.CtrlGroup;
import bjd.ctrl.CtrlInt;
import bjd.ctrl.CtrlRadio;
import bjd.ctrl.CtrlTextBox;
import bjd.ctrl.ICtrlEventListener;
import bjd.ctrl.OneCtrl;
import bjd.ctrl.OnePage;
import bjd.net.BindAddr;
import bjd.net.Ip;
import bjd.net.LocalAddress;
import bjd.net.ProtocolKind;
import bjd.util.IDispose;
import bjd.util.IniDb;
import bjd.util.Util;

/**
 * 各Option(オプションクラス)の既定クラス
 * @author SIN
 *
 */
public abstract class OneOption implements ICtrlEventListener, IDispose {

	private ListVal listVal = new ListVal();
	private boolean isJp;
	private String path; //実態が格納されているモジュール(DLL)のフルパス
	private String nameTag;
	
	/**
	 * OneValのリストを返す
	 * @return ListVal
	 */
	public final ListVal getListVal() {
		return listVal;
	}

	/**
	 * nameTagの取得
	 * @return
	 */
	public final String getNameTag() {
		return nameTag;
	}

	/**
	 * 「サーバを使用する」の状態取得
	 * @return
	 */
	public final boolean getUseServer() {
		OneVal oneVal = listVal.search("useServer");
		if (oneVal == null) {
			return false;
		}
		return (boolean) oneVal.getValue();
	}
	
    public abstract String getJpMenu();
    public abstract String getEnMenu();	
    public abstract char getMnemonic();	

    /**
     * コンストラクタ
     * @param isJp　
     * @param path　モジュールへのパス //TODO　Javaでこれ必要なのかな？
     * @param nameTag　名前
     * @param iniDb Option.ini
     */
	public OneOption(boolean isJp, String path, String nameTag) {
		this.isJp = isJp;
		this.path = path;
		this.nameTag = nameTag;
	}

	protected final boolean isJp() {
		return isJp;
	}
	
	/**
	 * レジストリからの読み込み
	 */
	protected final void read(IniDb iniDb) {
	//protected final void read() {
		iniDb.read(nameTag, listVal);
	}

	protected final OnePage pageAcl() {
		OnePage onePage = new OnePage("ACL", "ACL");
		onePage.add(new OneVal("enableAcl", 0, Crlf.NEXTLINE, new CtrlRadio(isJp ? "指定したアドレスからのアクセスのみを" : "Access of ths user who appoint it", new String[] { isJp ? "許可する" : "Allow", isJp ? "禁止する" : "Deny" }, 550, 2)));

		ListVal list = new ListVal();
		list.add(new OneVal("aclName", "", Crlf.NEXTLINE, new CtrlTextBox(isJp ? "名前（表示名）" : "Name(Display)", 20)));
		list.add(new OneVal("aclAddress", "", Crlf.NEXTLINE, new CtrlTextBox(isJp ? "アドレス" : "Address", 20)));
		onePage.add(new OneVal("acl", null, Crlf.NEXTLINE, new CtrlDat(isJp ? "利用者（アドレス）の指定" : "Access Control List", list, 320, isJp)));

		return onePage;
	}

	/**
	 * ダイアログ作成時の処理
	 * @param mainPanel 表示のベースとなるダイアログのパネル
	 */
	public final void createDlg(JPanel mainPanel) {
		// 表示開始の基準位置
		int x = 0;
		int y = 0;
		listVal.createCtrl(mainPanel, x, y);
		listVal.setListener(this);

		// 基底クラスのセットアップされる「サーバ設定」などのコントロールの状態を初期化するため、このダミーのイベントを発生させる
		onChange(null);
	}

	/**
	 * ダイアログ破棄時の処理
	 */
	public final void deleteDlg() {
		listVal.deleteCtrl();
	}

	/**
	 * ダイアログでOKボタンが押された時の処理
	 * @param isComfirm 確認が必要かどうか？
	 * @return falseの場合、ダイアログを閉じる処理はキャンセルする
	 */
	public final boolean onOk(boolean isComfirm) {
		return listVal.readCtrl(isComfirm);
	}

	/**
	 * OneValの追加
	 * @param oneVal
	 */
	public final void add(OneVal oneVal) {
		listVal.add(oneVal);
	}

	/**
	 * OneValとしてサーバ基本設定を作成する
	 * @param protocolKind
	 * @param port
	 * @param timeout
	 * @param multiple
	 * @return
	 */
	protected final OneVal createServerOption(ProtocolKind protocolKind, int port, int timeout, int multiple) {
		ListVal list = new ListVal();
		list.add(new OneVal("protocolKind", (protocolKind == protocolKind.Tcp) ? 0 : 1, Crlf.CONTINUE, new CtrlComboBox(isJp ? "プロトコル" : "Protocol", new String[] { "TCP", "UDP" }, 80)));
		list.add(new OneVal("port", port, Crlf.NEXTLINE, new CtrlInt(isJp ? "クライアントから見たポート" : "Port (from client side)", 5)));
		LocalAddress localAddress = LocalAddress.getInstance();
		Ip[] v4 = localAddress.getV4();
		Ip[] v6 = localAddress.getV6();
		
		list.add(new OneVal("bindAddress2", new BindAddr(), Crlf.NEXTLINE, new CtrlBindAddr(isJp ? "待ち受けるネットワーク" : "Bind Address", v4, v6)));
		list.add(new OneVal("useResolve", false, Crlf.NEXTLINE, new CtrlCheckBox((isJp ? "クライアントのホスト名を逆引きする" : "Reverse pull of host name from IP address"))));
		list.add(new OneVal("useDetailsLog", true, Crlf.CONTINUE, new CtrlCheckBox(isJp ? "詳細ログを出力する" : "Use Details Log")));
		list.add(new OneVal("multiple", multiple, Crlf.CONTINUE, new CtrlInt(isJp ? "同時接続数" : "A repetition thread", 5)));
		list.add(new OneVal("timeOut", timeout, Crlf.NEXTLINE, new CtrlInt(isJp ? "タイムアウト(秒)" : "Timeout", 6)));
		return new OneVal("GroupServer", null, Crlf.NEXTLINE, new CtrlGroup(isJp ? "サーバ基本設定" : "Server Basic Option", list));
	}

	/**
	 * 値の設定
	 * @param name
	 * @param value
	 */
	public final void setValue(String name, Object value) {
		OneVal oneVal = listVal.search(name);
		if (oneVal == null) {
			Util.runtimeException(String.format("名前が見つかりません name=%s", name));
		}
		//コントロールの値を変更
		oneVal.getOneCtrl().write(value);
		//レジストリへ保存
		save(OptionIni.getInstance());
	}

	/**
	 * 値の取得
	 * @param name
	 * @return
	 */
	public final Object getValue(String name) {

		//DEBUG
		if (name.equals("editBrowse")) {
			return false;
		}

		OneVal oneVal = listVal.search(name);
		if (oneVal == null) {
			Util.runtimeException(String.format("名前が見つかりません name=%s", name));
			return null;
		}
		return oneVal.getValue();
	}

	/**
	 * 名前からコントロールを詮索する<br>
	 * 処理だと処理が重くなるので、該当が無い場合nullを返す<br>
	 * @param name
	 * @return コントロールオブジェクト若しくはnull
	 */
	protected final OneCtrl getCtrl(String name) {
		OneVal oneVal = listVal.search(name);
		if (oneVal == null) {
			return null;
		}
		return oneVal.getOneCtrl();
//		try {
//			OneVal oneVal = listVal.search(name);
//			return oneVal.getOneCtrl();
//		} catch (Exception e) {
//			Util.runtimeException(String.format("getCtrl(%s)", name));//実行時例外
//		}
//		return null;
	}

	protected abstract void abstractOnChange(OneCtrl oneCtrl);

	/**
	 * コントロールの変化時のイベント処理
	 */
	@Override
	public final void onChange(OneCtrl oneCtrl) {
		
		try {

			OneCtrl o = getCtrl("protocolKind");
			if (o != null) {
				o.setEnable(false); // プロトコル 変更不可
			}
			abstractOnChange(oneCtrl);
		} catch (NullPointerException e) {
			// コントロールの破棄後に、このイベントが発生した場合（この例外は無視する）
		}
	}

	/**
	 * 終了処理
	 */
	@Override
	public void dispose() {
	}

	/**
	 * レジストリへ保存
	 */
	public final void save(IniDb iniDb) {
	//public final void save() {
		iniDb.save(nameTag, listVal);
	}
}
