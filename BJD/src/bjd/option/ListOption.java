package bjd.option;

import bjd.Kernel;
import bjd.menu.ListMenu;
import bjd.menu.OneMenu;
import bjd.plugin.OnePlugin;
import bjd.util.ListBase;
import bjd.util.Util;

/**
 * オプションのリストを表現するクラス<br>
 * Kernelの中で使用される<br>
 * @author SIN
 *
 */
public final class ListOption extends ListBase<OneOption> {

	private Kernel kernel;

	public ListOption(Kernel kernel) {
		this.kernel = kernel;
		initialize();
	}

	public OneOption get(String nameTag) {
		for (OneOption o : getAr()) {
			if (o.getNameTag().equals(nameTag)) {
				return o;
			}
		}
		//TODO DEBUGのためにとりあえずnullを返す
		if (nameTag.equals("Basic")) {
			return new OptionBasic(kernel, "");
		}
		Util.runtimeException(String.format("nameTag=%s", nameTag));
		return null; //ランタイム例外が発生するので、このnullが返されることはない
	}

	/**
	 * null追加を回避するために、getAr().add()は、このファンクションを使用する
	 * @param o
	 * @return
	 */
	private boolean add(OneOption o) {
		if (o == null) {
			return false;
		}
		getAr().add(o);
		return true;
	}

	/**
	 * Kernel.Dispose()で、有効なオプションだけを出力するために使用する
	 */
	public void save() {
		for (OneOption o : getAr()) {
			o.save(OptionIni.getInstance());
		}
	}

	/**
	 * オプションリストの初期化
	 */
	private void initialize() {

		getAr().clear();

		//固定的にBasicとLogを生成する
		String executePath = ""; // Application.ExecutablePath
		add(new OptionBasic(kernel, executePath)); //「基本」オプション
		add(new OptionLog(kernel, executePath)); //「ログ」オプション

		for (OnePlugin onePlugin : kernel.getListPlugin()) {
			//TODO Debug Print
			System.out.println(String.format("onePlugin.getName() = %s", onePlugin.getName()));
			OneOption oneOption = onePlugin.createOption(kernel);
			System.out.println(String.format("■oneOption.getNameTag()=%s", oneOption.getNameTag()));
			add(oneOption);
		}

		/*
		//DLLを検索し、各オプションを生成する
		//Ver5.2.4 関係ない*Server.dll以外は、対象外とする
		//var list = Directory.GetFiles(kernel.ProgDir(), "*.dll").ToList();
		var list = Directory.GetFiles(kernel.ProgDir(), "*Server.dll").ToList();
		list.Sort();
		//foreach (var path in Directory.GetFiles(kernel.ProgDir(), "*.dll")) {
		foreach (var path in list) {

		    //テスト時の関連ＤＬＬを読み飛ばす
		    if (path.IndexOf("TestDriven") != -1)
		        continue;

		    string nameTag = Path.GetFileNameWithoutExtension(path);

		    //DLLバージョン確認
		    var vi = FileVersionInfo.GetVersionInfo(path);
		    if (vi.FileVersion != Define.ProductVersion()) {
		        throw new Exception(string.Format("A version of DLL is different [{0} {1}]", nameTag, vi.FileVersion));
		    }
		    
		    if (nameTag == "WebServer") {
		        var op = (OneOption)Util.CreateInstance(kernel, path, "OptionVirtualHost", new object[] { kernel, path, "VirtualHost" });
		        if (Add(op)) {
		            //WebServerの場合は、バーチャルホストごとに１つのオプションを初期化する
		            foreach (var o in (Dat)op.GetValue("hostList")) {
		                if (o.Enable) {
		                    string name = string.Format("Web-{0}:{1}", o.StrList[1], o.StrList[2]);
		                    Add((OneOption)Util.CreateInstance(kernel, path, "Option", new object[] { kernel, path, name }));
		                }
		            }
		        }
		    } else if (nameTag == "TunnelServer") {
		        //TunnelServerの場合は、１トンネルごとに１つのオプションを初期化する
		        var op = (OneOption)Util.CreateInstance(kernel, path, "OptionTunnel", new object[] { kernel, path, "TunnelList" });
		        if (Add(op)) {
		            //トンネルのリスト
		            foreach (var o in (Dat)op.GetValue("tunnelList")) {
		                if (o.Enable) {

		                    //int protocol = (int)o[0].Obj;//プロトコル
		                    //int port = (int)o[1].Obj;//クライアントから見たポート
		                    //string targetServer = (string)o[2].Obj;//接続先サーバ
		                    //int targetPort = (int)o[3].Obj;//接続先ポート
		                    string name = string.Format("{0}:{1}:{2}:{3}", (o.StrList[0] == "0") ? "TCP" : "UDP", o.StrList[1], o.StrList[2], o.StrList[3]);
		                    Add((OneOption)Util.CreateInstance(kernel, path, "Option", new object[] { kernel, path, "Tunnel-" + name }));
		                }
		            }
		        }
		    } else {  //上記以外
		        //DLLにclass Optionが含まれていない場合、Util.CreateInstanceはnulllを返すため、以下の処理はスキップされる
		        if (Add((OneOption)Util.CreateInstance(kernel, path, "Option", new object[] { kernel, path, nameTag }))) {
		            //DnsServerがリストされている場合 ドメインリソースも追加する
		            if (nameTag == "DnsServer") {
		                var o = (OneOption)Util.CreateInstance(kernel, path, "OptionDnsDomain", new object[] { kernel, path, "DnsDomain" });
		                if (Add(o)) {
		                    foreach (var e in (Dat)o.GetValue("domainList")) {
		                        if (e.Enable) {
		                            Add((OneOption)Util.CreateInstance(kernel, path, "OptionDnsResource", new object[] { kernel, path, "Resource-" + e.StrList[0] }));
		                        }
		                    }
		                }
		            }else if (nameTag == "SmtpServer") {
		#if ML_SERVER
		                var o = (OneOption)Util.CreateInstance(kernel,path, "OptionMl", new object[] { kernel, path, "Ml" });
		                if (Add(o)) {
		                    foreach (var e in (Dat)o.GetValue("mlList")) {
		                        if (e.Enable) {
		                            Add((OneOption)Util.CreateInstance(kernel,path, "OptionOneMl", new object[] { kernel, path, "Ml-" + e.StrList[0] }));
		                        }
		                    }
		                }
		#endif
		            }
		        }
		    }
		}
		//SmtpServer若しくはPopServerがリストされている場合、MailBoxを生成する
		if (Get("SmtpServer")!=null || Get("PopServer")!=null){
		    Add(new OptionMailBox(kernel, Application.ExecutablePath, "MailBox"));//メールボックス
		}

		}
		*/
	}

	/**
	 * メニュー取得
	 * @return
	 */
	public ListMenu getListMenu() {

		ListMenu mainMenu = new ListMenu();
		ListMenu webMenu = null;
		ListMenu dnsMenu = null;
		ListMenu mailMenu = null;
		ListMenu proxyMenu = null;
		int countTunnel = 0;

		for (OneOption a : getAr()) {
			ListMenu menu = mainMenu;

			if (a.getNameTag().equals("DnsServer")) {
				OneMenu m = mainMenu.add(new OneMenu("Option_DnsServer0", "DNSサーバ", "DNS Server", 'D', null));
				dnsMenu = new ListMenu();
				m.setSubMenu(dnsMenu);
				menu = dnsMenu;
			} else if (a.getNameTag().equals("DnsDomain") || a.getNameTag().indexOf("Resource-") == 0) {
				if (dnsMenu != null && dnsMenu.size() == 1) {
					dnsMenu.add(new OneMenu()); //セパレータ
				}
				menu = dnsMenu;
			} else if (a.getNameTag().equals("Pop3Server") || a.getNameTag().equals("SmtpServer")) {
				if (mailMenu == null) {
					OneMenu m = mainMenu.add(new OneMenu("Option_MailServer0", "メールサーバ", "Mail Server", 'M', null));
					mailMenu = new ListMenu();
					m.setSubMenu(mailMenu);
				}
				menu = mailMenu;
			} else if (a.getNameTag().indexOf("Ml") == 0) {
				if (mailMenu != null && mailMenu.size() == 3) {
					mailMenu.add(new OneMenu()); // セパレータ
				}
				menu = mailMenu;
			} else if (a.getNameTag().equals("VirtualHost")) {
				OneMenu m = mainMenu.add(new OneMenu("Option_WebServer0", "Webサーバ", "Web Server", 'W', null));
				webMenu = new ListMenu();
				m.setSubMenu(webMenu);
				menu = webMenu;
			} else if (a.getNameTag().indexOf("Web-") == 0) {
				if (webMenu != null && webMenu.size() == 1) {
					webMenu.add(new OneMenu()); // セパレータ
				}
				menu = webMenu;
			} else if (a.getNameTag().indexOf("Tunnel-") == 0) {
				if (countTunnel == 0) {
					if (proxyMenu != null) {
						proxyMenu.add(new OneMenu()); // セパレータ
					}
				}
				countTunnel++;
				menu = proxyMenu;
			} else if (a.getNameTag().indexOf("Proxy") == 0 || a.getNameTag().equals("TunnelList")) {
				if (proxyMenu == null) {
					OneMenu m = mainMenu.add(new OneMenu("Option_Proxy", "プロキシサーバ", "Proxyl Server", 'P', null));
					proxyMenu = new ListMenu();
					m.setSubMenu(proxyMenu);
				}
				menu = proxyMenu;
			}

			String nameTag = String.format("Option_%s", a.getNameTag());

			if (a.getNameTag().equals("MailBox")) {
				if (mailMenu != null) {
					mailMenu.insert(0, new OneMenu()); // セパレータ
					mailMenu.insert(0, new OneMenu(nameTag, a.getJpMenu(), a.getEnMenu(), a.getMnemonic(), null));
				}
			} else {
				if (menu != null) {
					menu.add(new OneMenu(nameTag, a.getJpMenu(), a.getEnMenu(), a.getMnemonic(), null));
				}
			}

		}
		return mainMenu;
	}

}
