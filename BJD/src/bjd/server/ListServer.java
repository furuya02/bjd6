package bjd.server;

import bjd.Kernel;
import bjd.net.BindAddr;
import bjd.net.BindStyle;
import bjd.net.OneBind;
import bjd.net.ProtocolKind;
import bjd.option.Conf;
import bjd.option.OneOption;
import bjd.plugin.ListPlugin;
import bjd.plugin.OnePlugin;
import bjd.util.IDispose;
import bjd.util.ListBase;
import bjd.util.Util;

public final class ListServer extends ListBase<OneServer> implements IDispose {

	private Kernel kernel;

	public ListServer(Kernel kernel, ListPlugin listPlugin) {
		this.kernel = kernel;

		initialize(listPlugin);
	}

	/**
	 * 名前によるサーバオブジェクト(OneServer)の検索<br>
	 * <font color=red>一覧に存在しない名前で検索を行った場合、設計上の問題として処理される</font>
	 * @param nameTag 名前
	 * @return サーバオブジェクト 
	 */
	public OneServer get(String nameTag) {
		for (OneServer oneServer : getAr()) {
			if (oneServer.getNameTag().equals(nameTag)) {
				return oneServer;
			}
		}
		//TODO DEBUG RemoteServerを検索されたら、とりあえずnullを返しておく
		if (nameTag.equals("RemoteServer")) {
			return null;
		}
		Util.runtimeException(String.format("nameTag=%s", nameTag));
		return null;
	}

	/**
	 * 初期化
	 * @param kernel
	 * @param listOption
	 */
	private void initialize(ListPlugin listPlugin) {
		getAr().clear();


		
		for (OneOption op : kernel.getListOption()) {


			if (!op.getUseServer()) { //サーバオプション以外は対象外にする
				continue;
			}


			//プラグイン情報の検索
			OnePlugin onePlugin = listPlugin.get(op.getNameTag());
			//			if (onePlugin == null) {
			//				//設計上の問題
			//				Util.runtimeException(String.format("ListServer.initialize() listPlugin.get(%s)==null", op.getNameTag()));
			//			}

			if (op.getNameTag().indexOf("Web-") == 0) {

				//既に同一ポートで仮想サーバがリストされている場合はサーバの生成は行わない
				boolean find = false;
				int port = (int) op.getValue("port");
				BindAddr bindAddr = (BindAddr) op.getValue("bindAddress2");
				for (OneServer sv : getAr()) {
					if (sv.getNameTag().indexOf("Web-") == 0) {
						OneOption o = kernel.getListOption().get(sv.getNameTag());
						//同一ポートの設定が既にリストされているかどうか
						if (port == (int) o.getValue("port")) {
							// バインドアドレスが競合しているかどうか
							if (bindAddr.checkCompetition((BindAddr) o.getValue("bindAddress2"))) {
								find = true;
								break;
							}
						}
					}
				}
				if (!find) {
					addServer(new Conf(op), onePlugin); //サーバ（OneServer）生成
				}
			} else {
				addServer(new Conf(op), onePlugin); //サーバ（OneServer）生成
			}
		}
	}

	/**
	 * サーバ（OneServer）の生成
	 * @param kernel
	 * @param op
	 */
	private void addServer(Conf conf, OnePlugin onePlugin) {

		ProtocolKind protocol = ProtocolKind.valueOf((int) conf.get("protocolKind"));
		BindAddr bindAddr = (BindAddr) conf.get("bindAddress2");

		if (bindAddr.getBindStyle() != BindStyle.V4ONLY) {
			OneBind oneBind = new OneBind(bindAddr.getIpV6(), protocol);
			OneServer o = onePlugin.createServer(kernel, conf, oneBind);
			if (o != null) {
				getAr().add((OneServer) o);
			}
		}
		if (bindAddr.getBindStyle() != BindStyle.V6ONLY) {
			OneBind oneBind = new OneBind(bindAddr.getIpV4(), protocol);
			OneServer o = onePlugin.createServer(kernel, conf, oneBind);
			if (o != null) {
				getAr().add((OneServer) o);
			}
		}
	}

	//１つでも起動中かどうか
	/**
	 * サーバが起動中かどうか
	 */
	public boolean isRunnig() {
		//全スレッドの状態確認
		for (OneServer sv : getAr()) {
			if (sv.isRunnig()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * サーバ停止処理
	 */
	public void stop() {
		//全スレッドスタート
		for (OneServer sv : getAr()) {
			sv.stop();
		}
	}

	//開始処理
	/**
	 * サーバ開始処理
	 */
	public void start() {
		if (isRunnig()) {
			return;
		}
		//全スレッド停止 
		for (OneServer sv : getAr()) {
			sv.start();
		}
	}

}
