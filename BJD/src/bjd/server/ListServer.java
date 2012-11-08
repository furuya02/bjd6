package bjd.server;

import bjd.Kernel;
import bjd.net.BindAddr;
import bjd.net.BindStyle;
import bjd.net.OneBind;
import bjd.net.ProtocolKind;
import bjd.option.ListOption;
import bjd.option.OneOption;
import bjd.util.IDispose;
import bjd.util.ListBase;
import bjd.util.Util;

public final class ListServer extends ListBase<OneServer> implements IDispose {

	private Kernel kernel;
	
	public ListServer(Kernel kernel, ListOption listOption) {
		this.kernel = kernel;
				
		initialize(listOption);

	}

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
	private void initialize(ListOption listOption) {
		getAr().clear();

		for (OneOption op : listOption) {
			if (!op.getUseServer()) { //サーバオプション以外は対象外にする
				continue;
			}
			if (op.getNameTag().indexOf("Web-") == 0) {

				//既に同一ポートで仮想サーバがリストされている場合はサーバの生成は行わない
				boolean find = false;
				int port = (int) op.getValue("port");
				BindAddr bindAddr = (BindAddr) op.getValue("bindAddress2");
				for (OneServer sv : getAr()) {
					if (sv.getNameTag().indexOf("Web-") == 0) {
						OneOption o = listOption.get(sv.getNameTag());
						//同一ポートの設定が既にリストされているかどうか
						if (port == (int) o.getValue("port")) {
							//Ver5.5.3 バインドアドレスが競合しているかどうか
							if (bindAddr.checkCompetition((BindAddr) o.getValue("bindAddress2"))) {
								find = true;
								break;
							}
						}
					}
				}
				if (!find) {
					addServer(op); //サーバ（OneServer）生成
				}
			} else {
				addServer(op); //サーバ（OneServer）生成
			}
		}
	}

	/**
	 * サーバ（OneServer）の生成
	 * @param kernel
	 * @param op
	 */
	private void addServer(OneOption op) {
		ProtocolKind protocol = (ProtocolKind) op.getValue("protocolKind");
		BindAddr bindAddr = (BindAddr) op.getValue("bindAddress2");

		if (bindAddr.getBindStyle() != BindStyle.V4ONLY) {
			OneBind oneBind = new OneBind(bindAddr.getIpV6(), protocol);
			
			
			
			OneServer o = Util.CreateInstance(kernel, op.getPath(), "Server", new Object[] { kernel, op.getNameTag(), oneBind });
			if (o != null) {
				getAr().add((OneServer) o);
			}
		}
		if (bindAddr.getBindStyle() != BindStyle.V6ONLY) {
			OneBind oneBind = new OneBind(bindAddr.getIpV4(), protocol);
			OneServer o = Util.CreateInstance(kernel, op.getPath(), "Server", new Object[] { kernel, op.getNameTag(), oneBind });
			if (o != null) {
				getAr().add((OneServer) o);
			}
		}
	}
}
