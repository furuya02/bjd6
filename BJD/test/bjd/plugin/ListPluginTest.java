package bjd.plugin;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

import bjd.Kernel;
import bjd.net.Ip;
import bjd.net.IpKind;
import bjd.net.OneBind;
import bjd.net.ProtocolKind;
import bjd.option.Conf;
import bjd.option.OneOption;

import bjd.server.OneServer;
import bjd.test.TestUtil;

public final class ListPluginTest {

	@Test
	public void pluginsフォルダの中のjarファイルを列挙してOption及びServerインスタンスを生成する() {

		Kernel kernel = new Kernel();
		String currentDir = new File(".").getAbsoluteFile().getParent(); // カレントディレクトリ

		String dir = String.format("%s\\bin\\plugins", currentDir);
		TestUtil.prompt(String.format("対象フォルダ　plugin = new Plugin(%s)", dir));
		ListPlugin listPlugin = new ListPlugin(dir);
		TestUtil.prompt(String.format("列挙数 plugin.length()=%d　件", listPlugin.size()));
		assertThat(listPlugin.size(), is(2));
		for (OnePlugin onePlugin : listPlugin) {
			//Optionインスタンス生成
			OneOption oneOption = onePlugin.createOption(kernel);
			Assert.assertNotNull(oneOption);
			TestUtil.prompt(String.format("Optionインスタンス生成 => success!! getNameTag() = %s", oneOption.getNameTag()));
			
			Conf conf = new Conf(oneOption);
			Ip ip = new Ip(IpKind.V4_LOCALHOST);
			OneBind oneBind = new OneBind(ip, ProtocolKind.Tcp);
			//Serverインスタンス生成
			OneServer oneServer = onePlugin.createServer(kernel, conf, oneBind);
			Assert.assertNotNull(oneServer);
			TestUtil.prompt(String.format("Serverインスタンス生成 => success!! count() = %d", oneServer.count()));
			
			
		}
	}

}
