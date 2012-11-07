package bjd.server;

import bjd.option.ListOption;
import bjd.util.IDispose;
import bjd.util.ListBase;
import bjd.util.Util;

public final class ListServer extends ListBase<OneServer> implements IDispose {

	public ListServer(ListOption listOption) {

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
}
