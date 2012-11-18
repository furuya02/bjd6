package bjd.plugins.ftp;

import org.junit.Test;

import bjd.test.TmpOption;
import bjd.util.Util;

public class ServerTest {

	@Test
	public void test() {

		//テスト用の設定ファイル上書き
		TmpOption tmpOption = new TmpOption("user1-full user2-up user3-down.ini");
		//テスト用サーバを起動する
		TmpServer tmpServer = new TmpServer();

		for (int i = 0; i < 10; i++) {
			Util.sleep(1000);
			//TODO Debug Print
			System.out.println(String.format("i=%d", i));

		}

		//テスト用サーバを停止する
		tmpServer.dispose();
		//設定ファイルを元に戻す
		tmpOption.dispose();
	}

}
/*
using NUnit.Framework;

namespace FtpServerTest {
    [TestFixture]
    class ServerTest {

        TsServer _tsServer;

        [SetUp]
        public void SetUp() {

            _tsServer = new TsServer();
            _tsServer.Start(); //サーバ起動
        }
        [TearDown]
        public void TearDown() {
            _tsServer.Dispose();//サーバ停止・オプション書き戻し
        }


        [Test]
        public void StatusTest() {

            var s = string.Format("+ サービス中 \t{0}           \t[{1}\t:{2} {3}]\tThread 0/10", _tsServer.NameTag, _tsServer.BindAddress, _tsServer.ProtocolKind.ToString().ToUpper(), _tsServer.Port);
            Assert.AreEqual(_tsServer.Server.ToString(), s);
        }

        [Test]
        public void ConnectTest() {
            var tcp = _tsServer.TcpClient();
            Assert.AreEqual(tcp.Connected, true);
            tcp.Close();
        }


        [TestCase("user1","user1","230 User user1 logged in.")]
        [TestCase("user1","xxx", "530 Login incorrect.")]//パスワード誤り
        [TestCase("xxx", "xxx", "530 Login incorrect.")]//ユーザ名誤り
        public void LoginTest(string user, string pass, string response) {
            var lineClient = _tsServer.LineCLient();
            lineClient.RecvLines(300);
            lineClient.Send(string.Format("USER {0}\n", user));
            lineClient.RecvLines(300);
            lineClient.Send(string.Format("PASS {0}\n", pass));
            var lines = lineClient.RecvLines(300);
            Assert.AreEqual(lines[0], response);
            lineClient.Dispose();

        }

        

    }
}
 * */
