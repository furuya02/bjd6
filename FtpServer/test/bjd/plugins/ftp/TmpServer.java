package bjd.plugins.ftp;

import bjd.Kernel;
import bjd.net.Ip;
import bjd.net.IpKind;
import bjd.net.OneBind;
import bjd.net.ProtocolKind;
import bjd.option.Conf;
import bjd.util.IDispose;

public class TmpServer implements IDispose {
	private Server server;

	public TmpServer() {
		OneBind oneBind = new OneBind(new Ip(IpKind.V4_LOCALHOST), ProtocolKind.Tcp);
		Kernel kernel = new Kernel();

		Option option = new Option(kernel, "");
		Conf conf = new Conf(option);
		server = new Server(kernel, conf, oneBind);
		server.start();
	}

	public void dispose() {
		server.stop();
		server.dispose();
	}

}
