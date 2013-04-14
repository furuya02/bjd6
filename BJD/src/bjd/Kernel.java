package bjd;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JMenuBar;

import bjd.ctrl.ListView;
import bjd.log.ILogger;
import bjd.log.LogFile;
import bjd.log.LogKind;
import bjd.log.LogLimit;
import bjd.log.LogView;
import bjd.log.Logger;
import bjd.log.TmpLogger;
import bjd.menu.Menu;
import bjd.net.DnsCache;
import bjd.net.LocalAddress;
import bjd.option.Conf;
import bjd.option.Dat;
import bjd.option.ListOption;
import bjd.option.OneOption;
import bjd.option.OptionIni;
import bjd.plugin.ListPlugin;
import bjd.plugin.OnePlugin;
import bjd.server.ListServer;
import bjd.server.OneServer;
import bjd.util.IDisposable;
import bjd.util.Util;

public final class Kernel implements IDisposable {

	//プロセス起動時に初期化される変数
	private RunMode runMode = RunMode.Normal; //通常起動;
	private boolean editBrowse = false; //「参照」のテキストボックスの編集
	private OneServer remoteServer = null; //クライアントへ接続中のみオブジェクトが存在する
	private TraceDlg traceDlg = null; //トレース表示
	private DnsCache dnsCache;
	//private Ver ver=null;
	private View view = null;
	private LogView logView = null;
	private WindowSize windowSize = null;
	private Menu menu = null;
	private boolean isTest = false; //TEST用のKernelを生成する場合、trueに設定される

	//サーバ起動時に最初期化さえる変数
	private ListOption listOption = null;
	private ListServer listServer = null;
	private LogFile logFile = null;
	//private Lang lang = Lang.JP;
	private boolean isJp = true;
	private Logger logger = null;

	//private MailBox mailBox = null; //実際に必要になった時に生成される(SMTPサーバ若しくはPOP3サーバの起動時)

	public View getView() {
		return view;
	}

	public TraceDlg getTraceDlg() {
		return traceDlg;
	}

	public OneServer getRemoteServer() {
		return remoteServer;
	}

	public ListOption getListOption() {
		return listOption;
	}

	public ListServer getListServer() {
		return listServer;
	}

	//	public ListPlugin getListPlugin() {
	//		return listPlugin;
	//	}

	public boolean isJp() {
		return isJp;
		//return (lang == Lang.JP) ? true : false;
	}

	public RunMode getRunMode() {
		return runMode;
	}

	public boolean getEditBrowse() {
		return editBrowse;
	}

	public DnsCache getDnsCache() {
		return dnsCache;
	}

	public String getServerName() {
		OneOption oneOption = listOption.get("Basic");
		if (oneOption != null) {
			return (String) oneOption.getValue("serverName");
		}
		return "";
	}

	/**
	 * テスト用コンストラクタ
	 */
	public Kernel() {
		isTest = true;
		defaultInitialize(null, null, null);
	}

	/**
	 * 通常使用されるコンストラクタ
	 * @param mainForm メインフォーム
	 * @param listViewLog ログ表示ビュー
	 * @param menuBar メニューバー
	 */
	public Kernel(MainForm mainForm, ListView listViewLog, JMenuBar menuBar) {
		defaultInitialize(mainForm, listViewLog, menuBar);
	}

	/**
	 * 起動時に、コンストラクタから呼び出される初期化<br>
	 * <br>
	 * @param mainForm メインフォーム
	 * @param listViewLog ログ表示用のビュー
	 * @param menuBar メニューバー
	 * @param isTest テスト用かどうか（テスト用の場合プラグインの初期化は行わない）
	 */
	private void defaultInitialize(MainForm mainForm, ListView listViewLog, JMenuBar menuBar) {

		//loggerが生成されるまでのログを一時的に保管する
		//ArrayList<LogTemporary> tmpLogger = new ArrayList<>();

		//プロセス起動時に初期化される
		view = new View(this, mainForm, listViewLog);
		logView = new LogView(listViewLog);
		traceDlg = new TraceDlg(this, (mainForm != null) ? mainForm.getFrame() : null); //トレース表示
		menu = new Menu(this, menuBar); //ここでは、オブジェクトの生成のみ、menu.Initialize()は、listInitialize()の中で呼び出される
		dnsCache = new DnsCache();
		//ver = new Ver();//バージョン管理

		//RunModeの初期化
		//if (mainForm == null) {
		//	RunMode = RunMode.Service;//サービス起動
		//} else {
		//	if (Environment.GetCommandLineArgs().Length > 1) {
		//  	RunMode = RunMode.Remote;//リモートクライアント
		//	} else {
		//		//サービス登録の状態を取得する
		//      var setupService = new SetupService(this);
		//      if (setupService.IsRegist)
		//  	    RunMode = RunMode.NormalRegist;//サービス登録完了状態
		//      }
		//  }
		//}

		OptionIni.create(this); //インスタンスの初期化

		listInitialize(); //サーバ再起動で、再度実行される初期化 

		//Java fix
		if (this.isTest) {
			return;
		}

		//ウインドサイズの復元
		String path = String.format("%s\\BJD.ini", getProgDir());
		try {
			//ウインドウの外観を保存・復元(Viewより前に初期化する)
			windowSize = new WindowSize(new Conf(listOption.get("Basic")), path);
			view.read(windowSize);
		} catch (IOException e) {
			windowSize = null;
			// 指定されたWindow情報保存ファイル(BJD.ini)にIOエラーが発生している
			logger.set(LogKind.ERROR, null, 9000022, path);
		}

		switch (runMode) {
			case Normal:
				menuOnClick("StartStop_Start"); //メニュー選択イベント
				break;
			case Remote:
				//                RemoteClient = new RemoteClient(this);
				//                RemoteClient.Start();
				break;
			default:
				Util.runtimeException("Kernel.defaultInitialize() not implement (RunMode)");
				break;
		}

		//        switch (RunMode){
		//            case RunMode.Remote:
		//                RemoteClient = new RemoteClient(this);
		//                RemoteClient.Start();
		//                break;
		//            case RunMode.Normal:
		//                Menu.EnqueueMenu("StartStop_Start",true);//synchro
		//                break;
		//        }
	}

	/**
	 * サーバ再起動で、再度実行される初期化
	 */
	void listInitialize() {
		//Loggerが使用できない間のログは、こちらに保存して、後でLoggerに送る
		TmpLogger tmpLogger = new TmpLogger();

		//************************************************************
		// 破棄
		//************************************************************
		if (listOption != null) {
			listOption.dispose();
			listOption = null;
		}
		//		if(listTool!=null){
		//			listTool.Dispose();
		//			listTool = null;
		//		}
		if (listServer != null) {
			listServer.dispose();
			listServer = null;
		}
		//		if(mailBox!=null){
		//			mailBox = null;
		//		}
		//		if (listPlugin != null) {
		//			listPlugin.dispose();
		//			listPlugin = null;
		//		}
		if (logFile != null) {
			logFile.dispose();
			logFile = null;
		}

		//************************************************************
		// 初期化
		//************************************************************
		//ListPlugin は。ListOptionとListServerを初期化する間だけ生存する
		//isTest=trueの場合、パスを""にして、プラグイン0個で初期化さあせる

		//ListPlugin listPlugin = new ListPlugin((isTest) ? "" : String.format("%s\\plugins", getProgDir()));
		ListPlugin listPlugin = new ListPlugin(String.format("%s\\plugins", getProgDir()));
		for (OnePlugin o : listPlugin) {
			//リモートクライアントの場合、このログは、ややこしいので表示しない
			if (getRunMode() == RunMode.Normal) {
				tmpLogger.set(LogKind.DETAIL, null, 9000008, String.format("%sServer", o.getName()));
			}
		}

		//listOptionで各オプションを初期化する前に、isJpだけは初期化しておく必要があるので
		//最初にOptionBasicのlangだけを読み出す
		isJp = OptionIni.getInstance().isJp();

		listOption = new ListOption(this, listPlugin);

		//OptionBasic
		Conf confBasic = new Conf(listOption.get("Basic"));
		editBrowse = (boolean) confBasic.get("editBrowse");

		//OptionLog
		Conf confOption = new Conf(listOption.get("Log"));
		if (confOption != null) {

			logView.setFont((Font) confOption.get("font"));

			if (runMode == RunMode.Normal || runMode == RunMode.Service) {
				//LogFileの初期化
				String saveDirectory = (String) confOption.get("saveDirectory");
				saveDirectory = replaceOptionEnv(saveDirectory);
				int normalLogKind = (int) confOption.get("normalLogKind");
				int secureLogKind = (int) confOption.get("secureLogKind");
				int saveDays = (int) confOption.get("saveDays");
				boolean useLogClear = (boolean) confOption.get("useLogClear");
				if (!useLogClear) {
					saveDays = 0; //ログの自動削除が無効な場合、saveDaysに0をセットする
				}
				if (saveDirectory.equals("")) {
					tmpLogger.set(LogKind.ERROR, null, 9000045, "It is not appointed");
				} else {
					tmpLogger.set(LogKind.DETAIL, null, 9000032, saveDirectory);
					try {
						logFile = new LogFile(saveDirectory, normalLogKind, secureLogKind, saveDays);
					} catch (IOException e) {
						logFile = null;
						tmpLogger.set(LogKind.ERROR, null, 9000031, e.getMessage());
					}
				}
			}
		}
		logger = createLogger("kernel", true, null);
		tmpLogger.release(logger);

		//mailBox初期化
		//        foreach (var o in ListOption) {
		//            //SmtpServer若しくは、Pop3Serverが使用される場合のみメールボックスを初期化する                
		//            if (o.NameTag == "SmtpServer" || o.NameTag == "Pop3Server") {
		//                if (o.UseServer) {
		//                    MailBox = new MailBox(this, ListOption.Get("MailBox"));
		//                    break;
		//                }
		//            }
		//        }

		listServer = new ListServer(this, listPlugin);

		//listTool = new ListTool(this);

		remoteServer = listServer.get("RemoteServer");

		view.setColumnText(); //Logビューのカラムテキストの初期化
		menu.initialize(); //メニュー構築（内部テーブルの初期化）

	}

	/**
	 * Confの生成<br>
	 * 事前にlistOptionが初期化されている必要がある<br>
	 * 
	 * @param nameTag
	 * @return
	 */
	public Conf createConf(String nameTag) {
		if (listOption == null) {
			Util.runtimeException("createConf() listOption==null");
		}
		OneOption oneOption = listOption.get(nameTag);
		if (oneOption != null) {
			return new Conf(oneOption);
		}
		return null;
	}

	/**
	 * Loggerの生成<br>
	 * 事前にlistOptionが初期化されている必要がある
	 * 
	 * @param nameTag 名前
	 * @param useDetailsLog　詳細ログを表示するかどうか
	 * @param logger　メソッドMsg()を保持するILoggerクラス
	 * @return Loggerオブジェクト
	 */
	public Logger createLogger(String nameTag, boolean useDetailsLog, ILogger logger) {
		if (listOption == null) {
			Util.runtimeException("createLogger() listOption==null || logFile==null");
		}
		Conf conf = createConf("Log");
		if (conf == null) {
			//createLoggerを使用する際に、OptionLogが検索できないのは、設計上の問題がある
			Util.runtimeException("createLogger() conf==null");
		}
		Dat dat = (Dat) conf.get("limitString");
		boolean isDisplay = ((int) conf.get("isDisplay")) == 0 ? true : false;
		LogLimit logLimit = new LogLimit(dat, isDisplay);

		boolean useLimitString = (boolean) conf.get("useLimitString");
		return new Logger(logLimit, logFile, logView, isJp(), nameTag, useDetailsLog, useLimitString, logger);
	}

	/**
	 * 終了処理
	 */
	@Override
	public void dispose() {
		//	        if (RunMode != RunMode.Service && RunMode != RunMode.Remote) {
		//	            //**********************************************
		//	            // 一旦ファイルを削除して現在有効なものだけを書き戻す
		//	            //**********************************************
		//	            var iniDb = new IniDb(ProgDir(),"Option");
		//	            iniDb.DeleteIni();
		listOption.save();
		//	            //Ver5.5.1 設定ファイルの保存に成功した時は、bakファイルを削除する
		//	            iniDb.DeleteBak();
		//
		//**********************************************
		// 破棄
		//**********************************************
		listServer.dispose(); //各サーバは停止される
		listOption.dispose();
		//	            ListTool.Dispose();
		//	            MailBox = null;
		//	        }
		//	        if (RemoteClient != null)
		//	            RemoteClient.Dispose();
		//
		view.dispose();
		if (traceDlg != null) {
			// traceDlg.Dispose();
		}
		if (menu != null) {
			menu.dispose();
		}
		if (windowSize != null) {
			view.save(windowSize);
			windowSize.dispose(); //DisposeしないとReg.Dispose(保存)されない
		}
	}

	/**
	 * プログラム本体のパス取得<br>
	 * jarファイルの場合と、classファイルの場合に対応している<br>
	 * 
	 * @return 起動ディレクトリ
	 */
	public String getProgDir() {
		java.net.URL url = this.getClass().getResource("Kernel.class");
		if (url.getProtocol().equals("file")) { //Jar化されていない場合
			try {
				File classFile = new File(new URI(url.toString()));
				Package pack = this.getClass().getPackage();
				if (pack == null) { //無名パッケージ
					return classFile.getParentFile().toString();
				} else { //パッケージ名がある場合、階層の分だけ上に上がる
					String packName = pack.getName();
					String[] words = packName.split("\\.");
					File dir = classFile.getParentFile();
					for (int i = 0; i < words.length; i++) {
						dir = dir.getParentFile();
					}
					return dir.toString();
				}
			} catch (URISyntaxException ex) {
				ex.printStackTrace();
				return "";
			}
		}
		//jarファイルの場合
		File f = new File(System.getProperty("java.class.path"));
		String path = "";
		try {
			path = f.getCanonicalPath(); //正規化したパスを取得する
		} catch (IOException e) {
			e.printStackTrace();
			//正規化に失敗した場合は、正規化されないパスを取得する
			path = f.getAbsolutePath();
		}
		return new File(path).getParent();
	}

	/**
	 * オプションで指定される変数を置き変える
	 * @param str 元の値
	 * @return 変換後の値
	 */
	private String replaceOptionEnv(String str) {
		String executablePath = getProgDir();
		executablePath = executablePath.replaceAll("\\\\", "\\\\\\\\");
		str = str.replaceAll("%ExecutablePath%", executablePath);
		return str;
	}

	private void start() {

		//サービス登録されている場合の処理
		if (runMode == RunMode.NormalRegist) {
			//            var setupService = new SetupService(this);
			//            if (setupService.Status != ServiceControllerStatus.Running) {
			//                setupService.Job(ServiceCmd.Start);
			//            }
		} else {
			if (listServer.size() == 0) {
				logger.set(LogKind.ERROR, null, 9000030, "");
			} else {
				listServer.start();
			}
		}
	}

	private void stop() {

		//サービス登録されている場合の処理
		if (runMode == RunMode.NormalRegist) {
			//            var setupService = new SetupService(this);
			//            if (setupService.Status == ServiceControllerStatus.Running) {
			//                setupService.Job(ServiceCmd.Stop);
			//            }
		} else {
			listServer.stop();
		}
	}

	/**
	 * メニュー選択時の処理<br>
	 * RemoteClientの場合は、このファンクションはフックされない<br>
	 * 
	 * @param cmd コマンド
	 */
	public void menuOnClick(String cmd) {

		if (cmd.indexOf("Option_") == 0) {
			OneOption oneOption = listOption.get(cmd.substring(7));
			if (oneOption != null) {
				OptionDlg dlg = new OptionDlg(view.getMainForm().getFrame(), oneOption);
				if (dlg.showDialog()) {
					oneOption.save(OptionIni.getInstance());
					menuOnClick("StartStop_Reload");
				}
			}
		} else if (cmd.indexOf("Tool_") == 0) {
			//        	OneTool oneTool = listTool.Get(cmd.substring(5));
			//            if (oneTool == null)
			//                return;
			//            OneServer oneServer = ListServer.Get(cmd.substring(5));
			//            //BJD.EXE以外の場合、サーバオブジェクトへのポインタが必要になる
			//            if(oneTool.NameTag != "BJD" && oneServer==null)
			//                return;
			//            ToolDlg dlg = oneTool.CreateDlg(oneServer);
			//            dlg.ShowDialog();
		} else if (cmd.indexOf("StartStop_") == 0) {
			switch (cmd) {
				case "StartStop_Start":
					start();
					break;
				case "StartStop_Stop":
					stop();
					break;
				case "StartStop_Restart":
					stop();
					Util.sleep(300);
					start();
					break;
				case "StartStop_Reload":
					stop();
					listInitialize();
					start();
					break;
				case "StartStop_Service":
					//SetupService(); //サービスの設定
					break;
				default:
					Util.runtimeException(String.format("cmd=%s", cmd));
					break;

			}
			view.setColor(); //ウインドのカラー初期化
			menu.setEnable(); //状態に応じた有効・無効
		} else {
			switch (cmd) {
				case "File_LogClear":
					logView.clear();
					break;
				case "File_LogCopy":
					logView.setClipboard();
					break;
				case "File_Trace":
					traceDlg.open();
					break;
				case "File_Exit":
					view.getMainForm().exit();
					break;
				case "Help_Version":

					view.getMainForm().test();
					break;
				case "Help_Homepage":
					view.getMainForm().test2();
					//Process.Start(Define.WebHome());
					break;
				case "Help_Document":
					//Process.Start(Define.WebDocument());
					break;
				case "Help_Support":
					//Process.Start(Define.WebSupport());
					break;
				default:
					Util.runtimeException(String.format("cmd=%s", cmd));
					break;
			}
		}

	}

	public String changeTag(String src) {
		String[] tagList = new String[] { "$h", "$v", "$p", "$d", "$a", "$s" };

		for (String tag : tagList) {
			while (true) {
				int index = src.indexOf(tag);
				if (index == -1) {
					break;
				}
				String tmp1 = src.substring(0, index);
				String tmp2 = "";
				switch (tag) {
					case "$h":
						String serverName = getServerName();
						if (serverName.equals("")) {
							tmp2 = "localhost"; //Define.HostName();
						} else {
							tmp2 = serverName;
						}
						break;
					case "$v":
						tmp2 = Ver.getVersion();
						break;
					case "$p":
						tmp2 = Define.getApplicationName();
						break;
					case "$d":
						tmp2 = Define.getDate();
						break;
					case "$a":
						LocalAddress localAddress = LocalAddress.getInstance();
						tmp2 = localAddress.remoteStr();
						//tmp2 = Define.ServerAddress();
						break;
					case "$s":
						tmp2 = getServerName();
						break;
					default:
						Util.runtimeException(String.format("undefind tag = %s", tag));
						break;
				}
				String tmp3 = src.substring(index + 2);
				src = tmp1 + tmp2 + tmp3;
			}
		}
		return src;
	}
}
