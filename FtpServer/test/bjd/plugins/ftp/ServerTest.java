package bjd.plugins.ftp;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import bjd.ILife;
import bjd.Kernel;
import bjd.net.Ip;
import bjd.net.IpKind;
import bjd.net.OneBind;
import bjd.net.ProtocolKind;
import bjd.option.Conf;
import bjd.sock.SockServer;
import bjd.sock.SockTcp;
import bjd.test.TestUtil;
import bjd.test.TmpOption;
import bjd.util.Inet;
import bjd.util.Util;

public final class ServerTest implements ILife {

	private static TmpOption op = null; //設定ファイルの上書きと退避
	private static Server sv = null; //サーバ
	private SockTcp cl; //クライアント

	private String bannerStr = "220 FTP ( BlackJumboDog Version TEST ) ready\r\n";

	@BeforeClass
	public static void beforeClass() throws Exception {

		//設定ファイルの退避と上書き
		op = new TmpOption(String.format("%s\\FtpServer\\test\\FtpServerTest.ini",TestUtil.getProhjectDirectory()));
		OneBind oneBind = new OneBind(new Ip(IpKind.V4_LOCALHOST), ProtocolKind.Tcp);
		Kernel kernel = new Kernel();
		Option option = new Option(kernel, "");
		Conf conf = new Conf(option);

		//サーバ起動
		sv = new Server(kernel, conf, oneBind);
		sv.start();
	}

	@AfterClass
	public static void afterClass() {

		//サーバ停止
		sv.stop();
		sv.dispose();

		//設定ファイルのリストア
		op.dispose();

	}

	@Before
	public void setUp() {
		//クライアント起動
		cl = Inet.connect(new Ip(IpKind.V4_LOCALHOST), 21, 10, null, this);
		//クライアントの接続が完了するまで、少し時間がかかる
		//Util.sleep(10);

	}

	@After
	public void tearDown() {
		//クライアント停止
		cl.close();
	}

	//共通処理(ログイン成功)
	void login(String userName) {
		assertThat(cl.stringRecv(1, this), is(bannerStr));
		cl.stringSend(String.format("USER %s", userName));
		assertThat(cl.stringRecv(1, this), is(String.format("331 Password required for %s.\r\n", userName)));
		cl.stringSend(String.format("PASS %s", userName));
		assertThat(cl.stringRecv(1, this), is(String.format("230 User %s logged in.\r\n", userName)));
	}

	@Test
	public void ステータス情報_toString_の出力確認() throws Exception {

		String expected = "+ サービス中 \t                 Ftp\t[127.0.0.1\t:TCP 21]\tThread";

		//exercise
		String actual = sv.toString().substring(0, 56);
		//verify
		assertThat(actual, is(expected));

	}

	@Test
	public void パスワード認証成功() {

		assertThat(cl.stringRecv(1, this), is(bannerStr));
		//cl.stringSend("USER user1");
		cl.stringSend("user user1");
		assertThat(cl.stringRecv(1, this), is("331 Password required for user1.\r\n"));
		cl.stringSend("PASS user1");
		assertThat(cl.stringRecv(1, this), is("230 User user1 logged in.\r\n"));

	}

	@Test
	public void アノニマス認証成功() {

		assertThat(cl.stringRecv(1, this), is(bannerStr));
		cl.stringSend("USER Anonymous");
		assertThat(cl.stringRecv(1, this), is("331 Password required for Anonymous.\r\n"));
		cl.stringSend("PASS user@aaa.com");
		assertThat(cl.stringRecv(1, this), is("230 User Anonymous logged in.\r\n"));

	}

	@Test
	public void アノニマス認証成功2() {

		assertThat(cl.stringRecv(1, this), is(bannerStr));
		cl.stringSend("USER ANONYMOUS");
		assertThat(cl.stringRecv(1, this), is("331 Password required for ANONYMOUS.\r\n"));
		cl.stringSend("PASS xxx");
		assertThat(cl.stringRecv(1, this), is("230 User ANONYMOUS logged in.\r\n"));

	}

	@Test
	public void パスワード認証失敗() {

		assertThat(cl.stringRecv(1, this), is(bannerStr));
		cl.stringSend("USER user1");
		assertThat(cl.stringRecv(1, this), is("331 Password required for user1.\r\n"));
		cl.stringSend("PASS xxxx");
		assertThat(cl.stringRecv(10, this), is("530 Login incorrect.\r\n"));

	}

	@Test
	public void USERの前にPASSコマンドを送るとエラーが返る() {

		assertThat(cl.stringRecv(1, this), is(bannerStr));
		cl.stringSend("PASS user1");
		assertThat(cl.stringRecv(1, this), is("503 Login with USER first.\r\n"));

	}

	@Test
	public void パラメータが必要なコマンドにパラメータ指定が無かった場合エラーが返る() {

		assertThat(cl.stringRecv(1, this), is(bannerStr));
		cl.stringSend("USER");
		assertThat(cl.stringRecv(1, this), is("500 USER: command requires a parameter.\r\n"));

	}

	@Test
	public void 無効なコマンドでエラーが返る() {

		assertThat(cl.stringRecv(1, this), is(bannerStr));
		cl.stringSend("xxx");
		assertThat(cl.stringRecv(1, this), is("500 Command not understood.\r\n"));
	}

	@Test
	public void 空行を送るとエラーが返る() {

		assertThat(cl.stringRecv(1, this), is(bannerStr));
		cl.stringSend("");
		assertThat(cl.stringRecv(1, this), is("500 Invalid command: try being more creative.\r\n"));

	}

	@Test
	public void 認証前に無効なコマンド_list_を送るとエラーが返る() {

		assertThat(cl.stringRecv(1, this), is(bannerStr));
		cl.stringSend("LIST");
		assertThat(cl.stringRecv(1, this), is("530 Please login with USER and PASS.\r\n"));

	}

	@Test
	public void 認証前に無効なコマンド_dele_を送るとエラーが返る() {

		assertThat(cl.stringRecv(1, this), is(bannerStr));
		cl.stringSend("DELE");
		assertThat(cl.stringRecv(1, this), is("530 Please login with USER and PASS.\r\n"));

	}

	@Test
	public void 認証後にUSERコマンドを送るとエラーが返る() {

		//共通処理(ログイン成功)
		login("user1");
		
		//user
		cl.stringSend("USER user1");
		assertThat(cl.stringRecv(1, this), is("530 Already logged in.\r\n"));

	}

	@Test
	public void 認証後にPASSコマンドを送るとエラーが返る() {

		//共通処理(ログイン成功)
		login("user1");

		//pass
		cl.stringSend("PASS user1");
		assertThat(cl.stringRecv(1, this), is("530 Already logged in.\r\n"));

	}

	@Test
	public void PWDコマンド() {

		//共通処理(ログイン成功)
		login("user1");

		//pwd
		cl.stringSend("PWD");
		assertThat(cl.stringRecv(1, this), is("257 \"/\" is current directory.\r\n"));

	}

	@Test
	public void SYSTコマンド() {

		//共通処理(ログイン成功)
		login("user1");

		//syst
		cl.stringSend("SYST");
		assertThat(cl.stringRecv(1, this), is("215 Windows 8\r\n"));

	}

	@Test
	public void TYPEコマンド() {

		//共通処理(ログイン成功)
		login("user1");

		//type
		cl.stringSend("TYPE A");
		assertThat(cl.stringRecv(1, this), is("200 Type set 'A'\r\n"));
		cl.stringSend("TYPE I");
		assertThat(cl.stringRecv(1, this), is("200 Type set 'I'\r\n"));
		cl.stringSend("TYPE X");
		assertThat(cl.stringRecv(1, this), is("500 command not understood.\r\n"));

	}

	@Test
	public void PORTコマンド() {

		//共通処理(ログイン成功)
		login("user1");

		int port = 256; //テストの連続のためにPORTコマンドのテストとはポート番号をずらす必要がある
		cl.stringSend("PORT 127,0,0,1,0,256");
		SockTcp dl = SockServer.createConnection(new Ip(IpKind.V4_LOCALHOST), port, this);
		assertThat(cl.stringRecv(1, this), is("200 PORT command successful.\r\n"));

		dl.close();
	}

	@Test
	public void PORTコマンド_パラメータ誤り() {

		//共通処理(ログイン成功)
		login("user1");

		cl.stringSend("PORT 127,3,x,x,1,0,256");
		assertThat(cl.stringRecv(1, this), is("501 Illegal PORT command.\r\n"));

	}

	@Test
	public void PASVコマンド() {

		//共通処理(ログイン成功)
		login("user1");

		cl.stringSend("PASV");

		//227 Entering Passive Mode. (127,0,0,1,xx,xx)
		String[] t = cl.stringRecv(1, this).split("[()]");
		String[] tmp = t[1].split(",");
		int n = Integer.valueOf(tmp[4]);
		int m = Integer.valueOf(tmp[5]);
		int port = n * 256 + m;

		Util.sleep(10);
		SockTcp dl = Inet.connect(new Ip(IpKind.V4_LOCALHOST), port, 10, null, this);
		dl.close();
	}

	@Test
	public void EPSVコマンド() {
		//共通処理(ログイン成功)
		login("user1");

		cl.stringSend("EPSV");

		//229 Entering Extended Passive Mode. (|||xxxx|)
		String[] tmp = cl.stringRecv(1, this).split("[|]");
		int port = Integer.valueOf(tmp[3]);
		SockTcp dl = Inet.connect(new Ip(IpKind.V6_LOCALHOST), port, 10, null, this);
	}

	@Test
	public void EPRTコマンド() {

		//共通処理(ログイン成功)
		login("user1");

		int port = 252; //テストの連続のためにPORTコマンドのテストとはポート番号をずらす必要がある
		cl.stringSend("EPRT |2|::1|252|");
		SockTcp dl = SockServer.createConnection(new Ip(IpKind.V6_LOCALHOST), port, this);
		assertThat(cl.stringRecv(1, this), is("200 EPRT command successful.\r\n"));

		dl.close();
	}

	@Test
	public void EPORTコマンド_パラメータ誤り() {

		//共通処理(ログイン成功)
		login("user1");

		cl.stringSend("EPRT |x|");
		assertThat(cl.stringRecv(1, this), is("501 Illegal EPRT command.\r\n"));

	}

	@Test
	public void MKD_RMDコマンド() {

		//共通処理(ログイン成功)
		login("user1");

		cl.stringSend("MKD test");
		assertThat(cl.stringRecv(1, this), is("257 Mkd command successful.\r\n"));

		cl.stringSend("RMD test");
		assertThat(cl.stringRecv(1, this), is("250 Rmd command successful.\r\n"));
	}

	@Test
	public void MKDコマンド_既存の名前を指定するとエラーとなる() {

		//共通処理(ログイン成功)
		login("user1");

		cl.stringSend("MKD home0");
		assertThat(cl.stringRecv(1, this), is("451 Mkd error.\r\n"));

	}

	@Test
	public void RMDコマンド_存在しない名前を指定するとエラーとなる() {

		//共通処理(ログイン成功)
		login("user1");

		cl.stringSend("RMD test");
		assertThat(cl.stringRecv(1, this), is("451 Rmd error.\r\n"));

	}

	@Test
	public void RETRコマンド() {

		//共通処理(ログイン成功)
		login("user1");

		//port
		int port = 250;
		cl.stringSend("PORT 127,0,0,1,0,250");
		SockTcp dl = SockServer.createConnection(new Ip(IpKind.V4_LOCALHOST), port, this);
		assertThat(cl.stringRecv(1, this), is("200 PORT command successful.\r\n"));

		//retr
		cl.stringSend("RETR 3.txt");
		assertThat(cl.stringRecv(1, this), is("150 Opening ASCII mode data connection for 3.txt (24 bytes).\r\n"));
		Util.sleep(10);
		assertThat(dl.length(), is(24));

		dl.close();
	}

	@Test
	public void STOR_DELEマンド() {

		//共通処理(ログイン成功)
		login("user1");

		//port
		int port = 249;
		cl.stringSend("PORT 127,0,0,1,0,249");
		SockTcp dl = SockServer.createConnection(new Ip(IpKind.V4_LOCALHOST), port, this);
		assertThat(cl.stringRecv(1, this), is("200 PORT command successful.\r\n"));

		//stor
		cl.stringSend("STOR 0.txt");
		assertThat(cl.stringRecv(1, this), is("150 Opening ASCII mode data connection for 0.txt.\r\n"));

		dl.send(new byte[3]);
		dl.close();

		assertThat(cl.stringRecv(1, this), is("226 Transfer complete.\r\n"));

		//dele
		cl.stringSend("DELE 0.txt");
		assertThat(cl.stringRecv(1, this), is("250 Dele command successful.\r\n"));

	}

	@Test
	public void UPユーザはRETRに失敗する() {

		//共通処理(ログイン成功)
		login("user2");

		//port
		int port = 250;
		cl.stringSend("PORT 127,0,0,1,0,250");
		SockTcp dl = SockServer.createConnection(new Ip(IpKind.V4_LOCALHOST), port, this);
		assertThat(cl.stringRecv(1, this), is("200 PORT command successful.\r\n"));

		//retr
		cl.stringSend("RETR 3.txt");
		assertThat(cl.stringRecv(1, this), is("550 Permission denied.\r\n"));
		//		Util.sleep(10);
		//		assertThat(dl.length(), is(24));

		dl.close();
	}

	@Test
	public void UPユーザはDELEに失敗する() {

		//共通処理(ログイン成功)
		login("user2");

		//dele
		cl.stringSend("DELE 1.txt");
		assertThat(cl.stringRecv(1, this), is("550 Permission denied.\r\n"));

	}

	@Test
	public void UPユーザはRNFR_RNTO_ファイル名変更_に失敗する() {

		//共通処理(ログイン成功)
		login("user2");

		cl.stringSend("RNFR 1.txt");
		assertThat(cl.stringRecv(1, this), is("550 Permission denied.\r\n"));

		cl.stringSend("RNTO $$$.1.txt");
		assertThat(cl.stringRecv(1, this), is("550 Permission denied.\r\n"));

	}

	@Test
	public void DOWNユーザはSTORに失敗する() {

		//共通処理(ログイン成功)
		login("user3");

		//port
		int port = 249;
		cl.stringSend("PORT 127,0,0,1,0,249");
		SockTcp dl = SockServer.createConnection(new Ip(IpKind.V4_LOCALHOST), port, this);
		assertThat(cl.stringRecv(1, this), is("200 PORT command successful.\r\n"));

		//stor
		cl.stringSend("STOR 0.txt");
		assertThat(cl.stringRecv(1, this), is("550 Permission denied.\r\n"));

	}

	@Test
	public void DOWNユーザはDELEに失敗する() {

		//共通処理(ログイン成功)
		login("user3");

		//dele
		cl.stringSend("DELE 1.txt");
		assertThat(cl.stringRecv(1, this), is("550 Permission denied.\r\n"));

	}

	@Test
	public void DOWNユーザはRETRコに成功する() {

		//共通処理(ログイン成功)
		login("user3");

		//port
		int port = 250;
		cl.stringSend("PORT 127,0,0,1,0,250");
		SockTcp dl = SockServer.createConnection(new Ip(IpKind.V4_LOCALHOST), port, this);
		assertThat(cl.stringRecv(1, this), is("200 PORT command successful.\r\n"));

		//retr
		cl.stringSend("RETR 3.txt");
		assertThat(cl.stringRecv(1, this), is("150 Opening ASCII mode data connection for 3.txt (24 bytes).\r\n"));
		Util.sleep(10);
		assertThat(dl.length(), is(24));

		dl.close();
	}

	@Test
	public void DOWNユーザはRNFR_RNTO_ファイル名変更_に失敗する() {

		//共通処理(ログイン成功)
		login("user3");

		cl.stringSend("RNFR 1.txt");
		assertThat(cl.stringRecv(1, this), is("550 Permission denied.\r\n"));

		cl.stringSend("RNTO $$$.1.txt");
		assertThat(cl.stringRecv(1, this), is("550 Permission denied.\r\n"));

	}

	@Test
	public void DELEマンド_存在しない名前を指定するとエラーとなる() {

		//共通処理(ログイン成功)
		login("user1");

		//dele
		cl.stringSend("DELE 0.txt");
		assertThat(cl.stringRecv(1, this), is("451 Dele error.\r\n"));

	}

	@Test
	public void LISTコマンド() {

		//共通処理(ログイン成功)
		login("user1");

		//port
		int port = 251;
		cl.stringSend("PORT 127,0,0,1,0,251");
		SockTcp dl = SockServer.createConnection(new Ip(IpKind.V4_LOCALHOST), port, this);
		assertThat(cl.stringRecv(1, this), is("200 PORT command successful.\r\n"));

		//list
		cl.stringSend("LIST -la");
		assertThat(cl.stringRecv(1, this), is("150 Opening ASCII mode data connection for ls.\r\n"));

		assertThat(listMask(dl.stringRecv(3, this)), is("drwxrwxrwx 1 nobody nogroup nnnn mon dd hh:mm home0\r\n"));
		assertThat(listMask(dl.stringRecv(3, this)), is("drwxrwxrwx 1 nobody nogroup nnnn mon dd hh:mm home1\r\n"));
		assertThat(listMask(dl.stringRecv(3, this)), is("drwxrwxrwx 1 nobody nogroup nnnn mon dd hh:mm home2\r\n"));
		assertThat(listMask(dl.stringRecv(3, this)), is("-rwxrwxrwx 1 nobody nogroup nnnn mon dd hh:mm 1.txt\r\n"));
		assertThat(listMask(dl.stringRecv(3, this)), is("-rwxrwxrwx 1 nobody nogroup nnnn mon dd hh:mm 2.txt\r\n"));
		assertThat(listMask(dl.stringRecv(3, this)), is("-rwxrwxrwx 1 nobody nogroup nnnn mon dd hh:mm 3.txt\r\n"));

		dl.close();
	}

	private String listMask(String str) {
		String[] tmp = str.split(" ");
		return String.format("%s %s %s %s nnnn mon dd hh:mm %s", tmp[0], tmp[1], tmp[2], tmp[3], tmp[8]);
	}

	@Test
	public void CWDコマンドで有効なディレクトリに移動() {
		//共通処理(ログイン成功)
		login("user1");

		//cwd
		cl.stringSend("CWD home0");
		assertThat(cl.stringRecv(1, this), is("250 CWD command successful.\r\n"));

	}

	@Test
	public void CWDコマンドで無効なディレクトリに移動しようとするとエラーが返る() {

		//共通処理(ログイン成功)
		login("user1");

		//cwd
		cl.stringSend("CWD xxx");
		assertThat(cl.stringRecv(1, this), is("550 xxx: No such file or directory.\r\n"));
		cl.stringSend("PWD");

	}

	@Test
	public void CWDコマンドでルートより上に移動しようとするとエラーが返る() {

		//共通処理(ログイン成功)
		login("user1");

		//cwd
		cl.stringSend("CWD home0");
		assertThat(cl.stringRecv(1, this), is("250 CWD command successful.\r\n"));
		cl.stringSend("CWD ..\\..");
		assertThat(cl.stringRecv(1, this), is("550 ..\\..: No such file or directory.\r\n"));

	}

	@Test
	public void CDUPコマンド() {

		//共通処理(ログイン成功)
		login("user1");

		//cwd
		cl.stringSend("CWD home0");
		assertThat(cl.stringRecv(1, this), is("250 CWD command successful.\r\n"));
		//cdup
		cl.stringSend("CDUP");
		assertThat(cl.stringRecv(1, this), is("250 CWD command successful.\r\n"));
		//pwd ルートに戻っていることを確認する
		cl.stringSend("PWD");
		assertThat(cl.stringRecv(1, this), is("257 \"/\" is current directory.\r\n"));

	}

	@Test
	public void RNFR_RNTOコマンド_ファイル名変更() {

		//共通処理(ログイン成功)
		login("user1");

		cl.stringSend("RNFR 1.txt");
		assertThat(cl.stringRecv(1, this), is("350 File exists, ready for destination name.\r\n"));

		cl.stringSend("RNTO $$$.1.txt");
		assertThat(cl.stringRecv(1, this), is("250 RNTO command successful.\r\n"));

		cl.stringSend("RNFR $$$.1.txt");
		assertThat(cl.stringRecv(1, this), is("350 File exists, ready for destination name.\r\n"));

		cl.stringSend("RNTO 1.txt");
		assertThat(cl.stringRecv(1, this), is("250 RNTO command successful.\r\n"));
	}

	@Test
	public void RNFR_RNTOコマンド_ディレクトリ名変更() {

		//共通処理(ログイン成功)
		login("user1");

		cl.stringSend("RNFR home0");
		assertThat(cl.stringRecv(1, this), is("350 File exists, ready for destination name.\r\n"));

		cl.stringSend("RNTO $$$.home0");
		assertThat(cl.stringRecv(1, this), is("250 RNTO command successful.\r\n"));

		cl.stringSend("RNFR $$$.home0");
		assertThat(cl.stringRecv(1, this), is("350 File exists, ready for destination name.\r\n"));

		cl.stringSend("RNTO home0");
		assertThat(cl.stringRecv(1, this), is("250 RNTO command successful.\r\n"));
	}

	@Override
	public boolean isLife() {
		return true;
	}

}
