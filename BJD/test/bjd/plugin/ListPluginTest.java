package bjd.plugin;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

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

public final class ListPluginTest {
	
	@Test
	public void pluginsフォルダの中のjarファイルを列挙() throws Exception {
		//setUp
		String currentDir = new File(".").getAbsoluteFile().getParent(); // カレントディレクトリ
		ListPlugin sut = new ListPlugin(String.format("%s\\bin\\plugins", currentDir));
		int expected = 3; 
		//exercise
		int actual = sut.size(); 
		//verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void Option及びServerインスタンスの生成() throws Exception {

		Kernel kernel = new Kernel();
		String currentDir = new File(".").getAbsoluteFile().getParent(); // カレントディレクトリ
		ListPlugin listPlugin = new ListPlugin(String.format("%s\\bin\\plugins", currentDir));
		for (OnePlugin onePlugin : listPlugin) {
			//Optionインスタンス生成
			OneOption oneOption = onePlugin.createOption(kernel);
			Assert.assertNotNull(oneOption);
			
			//Serverインスタンス生成
			Conf conf = new Conf(oneOption);
			OneBind oneBind = new OneBind(new Ip(IpKind.V4_LOCALHOST), ProtocolKind.Tcp);
			OneServer oneServer = onePlugin.createServer(kernel, conf, oneBind);
			Assert.assertNotNull(oneServer);
		}
	}

}
