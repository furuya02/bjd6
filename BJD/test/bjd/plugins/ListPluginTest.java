package bjd.plugins;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

import bjd.Kernel;
import bjd.ValidObjException;
import bjd.net.Ip;
import bjd.net.OneBind;
import bjd.net.ProtocolKind;
import bjd.option.Conf;
import bjd.option.OneOption;
import bjd.plugin.ListPlugin;
import bjd.plugin.OnePlugin;
import bjd.server.OneServer;
import bjd.util.TestUtil;

public final class ListPluginTest {

	@Test
	public void a001() {

		TestUtil.dispHeader("a001 pluginsフォルダの中のjarファイルを列挙して、Option及びServerインスタンスを生成する");

		Kernel kernel = new Kernel();
		String currentDir = new File(".").getAbsoluteFile().getParent(); // カレントディレクトリ

		String dir = String.format("%s\\bin\\plugins", currentDir);
		TestUtil.dispPrompt(this, String.format("対象フォルダ　plugin = new Plugin(%s)", dir));
		ListPlugin listPlugin = new ListPlugin(dir);
		TestUtil.dispPrompt(this, String.format("列挙数 plugin.length()=%d　件", listPlugin.size()));
		assertThat(listPlugin.size(), is(1));
		for (OnePlugin onePlugin : listPlugin) {
			//Optionインスタンス生成
			OneOption oneOption = onePlugin.createOption(kernel);
			Assert.assertNotNull(oneOption);
			TestUtil.dispPrompt(this, String.format("Optionインスタンス生成 => success!! getNameTag() = %s", oneOption.getNameTag()));
			
			Conf conf = new Conf(oneOption);
			Ip ip = null;
			try {
				ip = new Ip("127.0.0.1");
			} catch (ValidObjException e) {
				Assert.fail(e.getMessage());
			}
			OneBind oneBind = new OneBind(ip, ProtocolKind.Tcp);
			//Serverインスタンス生成
			OneServer oneServer = onePlugin.createServer(kernel, conf, oneBind);
			Assert.assertNotNull(oneServer);
			TestUtil.dispPrompt(this, String.format("Serverインスタンス生成 => success!! count() = %d", oneServer.count()));
			
			
		}
	}

}
