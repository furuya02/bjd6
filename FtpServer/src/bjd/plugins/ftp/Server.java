package bjd.plugins.ftp;

import java.io.File;
import bjd.Kernel;
import bjd.ValidObjException;
import bjd.log.LogKind;
import bjd.net.Ip;
import bjd.net.OneBind;
import bjd.net.Ssl;
import bjd.option.Conf;
import bjd.option.Dat;
import bjd.server.Cmd;
import bjd.server.OneServer;
import bjd.sock.SockObj;
import bjd.sock.SockServer;
import bjd.sock.SockState;
import bjd.sock.SockTcp;
import bjd.util.FileSearch;
import bjd.util.Inet;
import bjd.util.Util;
import bjd.util.Util.ExistsKind;

public final class Server extends OneServer {

	private String bannerMessage;
	private ListUser listUser;
	private ListMount listMount;

	public Server(Kernel kernel, Conf conf, OneBind oneBind) {
		super(kernel, "Ftp", conf, oneBind);

		bannerMessage = Kernel.ChangeTag((String) getConf().get("bannerMessage"));
		//ユーザ情報
		listUser = new ListUser((Dat) getConf().get("user"));
		//仮想フォルダ
		listMount = new ListMount((Dat) getConf().get("mountList"));

	}

	@Override
	protected void onStopServer() {

	}

	@Override
	protected boolean onStartServer() {
		return true;
	}

	@Override
	protected void onSubThread(SockObj sockObj) {
		//セッションごとの情報
		Session session = new Session((SockTcp) sockObj);

		//このコネクションの間、１つづつインクメントしながら使用される
		//本来は、切断したポート番号は再利用可能なので、インクリメントの必要は無いが、
		//短時間で再利用しようとするとエラーが発生する場合があるので、これを避ける目的でインクリメントして使用している

		//グリーティングメッセージの送信
		session.stringSend(String.format("220 %s", bannerMessage));

		//コネクションを継続するかどうかのフラグ
		boolean result = true;

		while (isLife() && result) {
			//このループは最初にクライアントからのコマンドを１行受信し、最後に、
			//sockCtrl.LineSend(resStr)でレスポンス処理を行う
			//continueを指定した場合は、レスポンスを返さずに次のコマンド受信に入る（例外処理用）
			//breakを指定した場合は、コネクションの終了を意味する（QUIT ABORT 及びエラーの場合）

			Util.sleep(0);

			Cmd cmd = recvCmd(session.getSockCtrl());
			if (cmd == null) {
				//切断されている
				break;
			}

			if (cmd.getStr().equals("")) {
				//受信待機中
				Util.sleep(100);
				continue;
			}

			//コマンド文字列の解釈
			FtpCmd ftpCmd = FtpCmd.parse(cmd.getCmdStr());
			String param = cmd.getParamStr();

			//SYSTコマンドが有効かどうかの判断
			if (ftpCmd == FtpCmd.Syst) {
				if (!(boolean) getConf().get("useSyst")) {
					ftpCmd = FtpCmd.Unknown;
				}
			}
			//コマンドが無効な場合の処理
			if (ftpCmd == FtpCmd.Unknown) {
				session.stringSend("502 Command not implemented.");
			}

			//QUITはいつでも受け付ける
			if (ftpCmd == FtpCmd.Quit) {
				session.stringSend("221 Goodbye.");
				break;
			}

			if (ftpCmd == FtpCmd.Abor) {
				session.stringSend("250 ABOR command successful.");
				break;
			}

			//これは、ログイン中しか受け付けないコマンドかも？
			//RNFRで指定されたパスの無効化
			if (ftpCmd != FtpCmd.Rnfr) {
				session.setRnfrName("");
			}

			// コマンド組替え
			if (ftpCmd == FtpCmd.Cdup) {
				if (param.equals("..")) {
					ftpCmd = FtpCmd.Cwd;
				}
			}

			// パラメータの確認(パラメータが無い場合はエラーを返す)
			if (param.equals("")) {
				if (ftpCmd == FtpCmd.User || ftpCmd == FtpCmd.Pass || ftpCmd == FtpCmd.Cwd || ftpCmd == FtpCmd.Type || ftpCmd == FtpCmd.Mkd || ftpCmd == FtpCmd.Rmd || ftpCmd == FtpCmd.Dele
						|| ftpCmd == FtpCmd.Port || ftpCmd == FtpCmd.Rnfr
						|| ftpCmd == FtpCmd.Rnto || ftpCmd == FtpCmd.Stor || ftpCmd == FtpCmd.Retr) {
					session.stringSend("500 command not understood:");
					continue;
				}
			}

			//不正アクセス対処 パラメータに極端に長い文字列を送り込まれた場合
			if (param.length() > 128) {
				getLogger().set(LogKind.SECURE, session.getSockCtrl(), 1, String.format("%s Length=%d", ftpCmd, param.length()));
				break;
			}

			// データコネクションが無いとエラーとなるコマンド
			if (ftpCmd == FtpCmd.Nlst || ftpCmd == FtpCmd.List || ftpCmd == FtpCmd.Stor || ftpCmd == FtpCmd.Retr) {
				if (session.getSockData() == null || session.getSockData().getSockState() != SockState.CONNECT) {
					session.stringSend("226 data connection close.");
					continue;
				}
			}

			//デフォルトのレスポンス文字列
			//処理がすべて通過してしまった場合、この文字列が返される
			//String resStr2 = String.format("451 %s error", ftpCmd);

			// ログイン前の処理
			if (session.getCurrentDir() == null) {
				//ftpCmd == FTP_CMD.PASS
				//未実装
				//PASSの前にUSERコマンドを必要とする
				//sockCtrl.LineSend("503 Login with USER first.");

				if (ftpCmd == FtpCmd.User) {
					result = jobUser(session, param);
				} else if (ftpCmd == FtpCmd.Pass) {
					result = jobPass(session, param);
				} else {
					//USER、PASS以外はエラーを返す
					session.stringSend("530 Please login with USER and PASS.");
				}
				// ログイン後の処理
			} else {
				// ユーザのアクセス権にエラーとなるコマンド
				if (session.getOneUser() != null) {
					if (session.getOneUser().getFtpAcl() == FtpAcl.Down) {
						if (ftpCmd == FtpCmd.Stor || ftpCmd == FtpCmd.Dele || ftpCmd == FtpCmd.Rnfr || ftpCmd == FtpCmd.Rnto || ftpCmd == FtpCmd.Rmd || ftpCmd == FtpCmd.Mkd) {
							session.stringSend("550 Permission denied.");
							continue;
						}
					} else if (session.getOneUser().getFtpAcl() == FtpAcl.Up) {
						if (ftpCmd == FtpCmd.Retr || ftpCmd == FtpCmd.Dele || ftpCmd == FtpCmd.Rnfr || ftpCmd == FtpCmd.Rnto || ftpCmd == FtpCmd.Rmd || ftpCmd == FtpCmd.Mkd) {
							session.stringSend("550 Permission denied.");
							continue;
						}
					}
				}

				// ログイン中(認証完了）時は、USER、PASS を受け付けない
				if (ftpCmd == FtpCmd.User || ftpCmd == FtpCmd.Pass) {
					session.stringSend("530 Already logged in.");
					continue;
				}

				if (ftpCmd == FtpCmd.Noop) {
					session.stringSend("200 NOOP command successful");
				} else if (ftpCmd == FtpCmd.Pwd) {
					session.stringSend(String.format("257 \"%s\" is current directory", session.getCurrentDir().getPwd()));
				} else if (ftpCmd == FtpCmd.Cwd) {
					result = jobCwd(session, param);
				} else if (ftpCmd == FtpCmd.Syst) {
					session.stringSend(String.format("215 %s", System.getProperty("os.name")));
				} else if (ftpCmd == FtpCmd.Type) {
					result = jobType(session, param);
				} else if (ftpCmd == FtpCmd.Mkd || ftpCmd == FtpCmd.Rmd || ftpCmd == FtpCmd.Dele) {
					result = jobDir(session, param, ftpCmd);
				} else if (ftpCmd == FtpCmd.Nlst || ftpCmd == FtpCmd.List) {
					result = jobNlist(session, param, ftpCmd);
				} else if (ftpCmd == FtpCmd.Port || ftpCmd == FtpCmd.Eprt) {
					result = jobPort(session, param, ftpCmd);
				} else if (ftpCmd == FtpCmd.Pasv || ftpCmd == FtpCmd.Epsv) {
					result = jobPasv(session, ftpCmd);
				} else if (ftpCmd == FtpCmd.Rnfr) {
					result = jobRnfr(session, param, ftpCmd);
				} else if (ftpCmd == FtpCmd.Rnto) {
					result = jobRnto(session, param, ftpCmd);
				} else if (ftpCmd == FtpCmd.Stor) {
					result = jobStor(session, param, ftpCmd);
				} else if (ftpCmd == FtpCmd.Retr) {
					result = jobRetr(session, param, ftpCmd);
				}
			}
		}
		//ログインしている場合は、ログアウトのログを出力する
		if (session.getCurrentDir() != null) {
			//logout
			getLogger().set(LogKind.NORMAL, session.getSockCtrl(), 13, String.format("%s", session.getOneUser().getUserName()));
		}
		session.getSockCtrl().close();
		if (session.getSockData() != null) {
			session.getSockData().close();
		}
	}

	private boolean jobUser(Session session, String userName) {

		//送信されたユーザ名を記憶する
		//ユーザが存在するかどうかは、PASSコマンドの時点で評価される
		session.setUserName(userName);

		//ユーザ名の有効・無効に関係なくパスワードの入力を促す
		session.stringSend(String.format("331 Password required for %s.", userName));
		return true;

	}

	private boolean jobPass(Session session, String password) {

		//ユーザ情報検索
		session.setOneUser(listUser.get(session.getUserName()));
		if (session.getOneUser() == null) {
			//無効なユーザの場合
			getLogger().set(LogKind.SECURE, session.getSockCtrl(), 14, String.format("USER:%s PASS:%s", session.getUserName(), password));
		} else {
			//パスワード確認
			boolean success = false;
			// *の場合、Anonymous接続として処理する
			if (session.getOneUser().getPassword() == "*") {
				//oneUser.getUserName() = String.format("{0}(ANONYMOUS)",oneUser.getUserName());
				getLogger().set(LogKind.NORMAL, session.getSockCtrl(), 5, String.format("%s(ANONYMOUS) %s", session.getOneUser().getUserName(), password));
				success = true;
			} else if (session.getOneUser().getPassword().equals(password)) {
				getLogger().set(LogKind.SECURE, session.getSockCtrl(), 6, String.format("%s", session.getOneUser().getUserName()));
				success = true;
			}

			if (success) {
				//以下、パスワード認証に成功した場合の処理
				//ホームディレクトリの存在確認
				//サーバ起動（運営）中にディレクトリが削除されている可能性があるので、この時点で確認する
				if (Util.exists(session.getOneUser().getHomeDir()) != ExistsKind.DIR) {
					//ホームディレクトリが存在しません（処理が継続できないため切断しました
					getLogger().set(LogKind.ERROR, session.getSockCtrl(), 2, String.format("userName=%s hoemDir=%s", session.getOneUser().getUserName(), session.getOneUser().getHomeDir()));
					return false;
				}

				//ログイン成功 （カレントディレクトリは、ホームディレクトリで初期化される）
				session.setCurrentDir(new CurrentDir(session.getOneUser().getHomeDir(), listMount));

				session.stringSend(String.format("230 User %s logged in.", session.getUserName()));
				return true;
			}
			//以下認証失敗処理
			getLogger().set(LogKind.SECURE, session.getSockCtrl(), 15, String.format("USER:%s PASS:%s", session.getUserName(), password));
		}

		//ブルートフォース防止のためのウエイト(5秒)
		for (int i = 0; i < 50 && isLife(); i++) {
			Util.sleep(100);
		}
		//認証に失敗した場合の処理
		session.stringSend("530 Login incorrect.");
		return true;

	}

	private boolean jobType(Session session, String param) {
		String resStr = "";
		switch (param.toUpperCase().charAt(0)) {
			case 'A':
				session.setFtpType(FtpType.ASCII);
				resStr = "200 Type set 'A'";
				break;
			case 'I':
				session.setFtpType(FtpType.BINARY);
				resStr = "200 Type set 'I'";
				break;
			default:
				resStr = "500 command not understood:";
				break;
		}
		session.stringSend(resStr);
		return true;
	}

	private boolean jobCwd(Session session, String param) {
		if (session.getCurrentDir().cwd(param)) {
			session.stringSend("250 CWD command successful");
		} else {
			session.stringSend(String.format("550 %s: No such file or directory.", param));
		}
		return true;
	}

	private boolean jobDir(Session session, String param, FtpCmd ftpCmd) {
		boolean isDir = !(ftpCmd == FtpCmd.Dele);
		int retCode = -1;
		//パラメータから新しいパス名を生成する
		String path = session.getCurrentDir().createPath(null, param, isDir);
		if (path == null) {
			//TODO エラーログ取得力が必要
		} else {
			File file = new File(path);
			if (ftpCmd == FtpCmd.Mkd) {
				//ディレクトリは無いか?
				if (!file.exists()) {
					if (file.mkdir()) {
						retCode = 257;
					}
				}
			} else if (ftpCmd == FtpCmd.Rmd || ftpCmd == FtpCmd.Dele) {
				//ディレクトリは有るか?
				if (file.exists()) {
					if (file.delete()) {
						retCode = 250;
					}
				}
			}
			if (retCode != -1) {
				//成功
				getLogger().set(LogKind.NORMAL, session.getSockCtrl(), 7, String.format("User:%s Cmd:%s Path:%s", session.getOneUser().getUserName(), ftpCmd, path));
				session.stringSend(String.format("%s %s command successful", retCode, ftpCmd));
				return true;
			} else { //失敗
				//コマンド処理でエラーが発生しました
				getLogger().set(LogKind.ERROR, session.getSockCtrl(), 3, String.format("User:%s Cmd:%s Path:%s", session.getOneUser().getUserName(), ftpCmd, path));
			}
		}
		session.stringSend(String.format("451 %s error", ftpCmd));
		return true;

	}

	private boolean jobNlist(Session session, String param, FtpCmd ftpCmd) {
		// 短縮リストかどうか
		boolean wideMode = (ftpCmd == FtpCmd.List);
		String mask = "*.*";

		//パラメータが指定されている場合、マスクを取得する
		if (!param.equals("")) {
			for (String p : param.split(" ")) {
				if (p.equals("")) {
					continue;
				}
				if (p.toUpperCase().indexOf("-L") == 0) {
					wideMode = true;
				} else {
					//ワイルドカード指定
					if (p.indexOf('*') != -1 || p.indexOf('?') != -1) {
						mask = param;
					} else { //フォルダ指定
						ExistsKind existsKind = Util.exists(session.getCurrentDir().createPath(null, param, false));
						switch (existsKind) {
							case DIR:
								mask = param + "\\*.*";
								break;
							case FILE:
								mask = param;
								break;
							default:
								Util.runtimeException(String.format("ExistsKind=%s", existsKind));
								break;
						}
					}
				}
			}
		}
		session.stringSend(String.format("150 Opening %s mode data connection for ls.", session.getFtpType()));
		//ファイル一覧取得
		for (String s : session.getCurrentDir().list(mask, wideMode)) {
			session.getSockData().stringSend(s, "Shift-Jis");
		}
		session.stringSend("226 Transfer complete.");

		session.getSockData().close();
		session.setSockData(null);
		return true;
	}

	private boolean jobPort(Session session, String param, FtpCmd ftpCmd) {
		String resStr = "500 command not understood:";

		Ip ip = null;
		int port = 0;

		if (ftpCmd == FtpCmd.Eprt) {
			String[] tmpBuf = param.split("\\|");
			if (tmpBuf.length == 4) {
				try {
					ip = new Ip(tmpBuf[2]);
				} catch (ValidObjException e) {
					//ここは、エラー表示で継続するように変更が必要
					Util.runtimeException(this, e);
				}
				port = Integer.parseInt(tmpBuf[3]);
			}
		} else {
			String[] tmpBuf = param.split(",");
			if (tmpBuf.length == 6) {
				try {
					ip = new Ip(tmpBuf[0] + "." + tmpBuf[1] + "." + tmpBuf[2] + "." + tmpBuf[3]);
				} catch (ValidObjException e) {
					//ここは、エラー表示で継続するように変更が必要
					Util.runtimeException(this, e);
				}
				port = Integer.parseInt(tmpBuf[4]) * 256 + Integer.parseInt(tmpBuf[5]);
			}
		}
		if (ip != null) {
			Ssl ssl = null;
			SockTcp sockData = Inet.connect(ip, port, getTimeout(), ssl, this);
			if (sockData != null) {
				resStr = "200 PORT command successful.";
			}
			session.setSockData(sockData);
		}
		session.stringSend(resStr);
		return true;

	}

	private boolean jobPasv(Session session, FtpCmd ftpCmd) {
		int port = session.getPort();
		// データストリームのソケットの作成
		for (int i = 0; i < 100; i++) {

			port++;
			if (port >= 9999) {
				port = 2000;
			}
			//指定したアドレス・ポートで待ち受ける
			SockTcp sockData = SockServer.createConnection(new Ip(session.getSockCtrl().getLocalAddress().getAddress()), port, this);
			if (sockData == null) {
				//接続失敗
				return false;
			}
			if (session.getSockData().getSockState() != SockState.Error) {
				//成功
				if (ftpCmd == FtpCmd.Epsv) {
					session.stringSend(String.format("229 Entering Extended Passive Mode (|||%d|)", port));
				} else {
					String ipStr = session.getSockCtrl().getLocalAddress().getAddress().toString();
					session.stringSend(String.format("227 Entering Passive Mode (%s,%d,%d)", ipStr.replace(".", ","), port / 256, port % 256));
				}
				//セッション情報の保存
				session.setPort(port);
				session.setSockData(sockData);
				return true;
			}
		}
		session.stringSend("500 command not understood:");
		return true;
	}

	private boolean jobRnto(Session session, String param, FtpCmd ftpCmd) {
		if (!session.getRnfrName().equals("")) {
			String path = session.getCurrentDir().createPath(null, param, false);
			ExistsKind existsKind = Util.exists(path);
			if (existsKind == ExistsKind.DIR) {
				session.stringSend("550 rename: Is a derectory name");
				return true;
			} else {

				//??? なんか変な感じ/////

				if (existsKind == ExistsKind.FILE) {
					(new File(path)).delete();
				}

				//if (Util.exists(rnfrName)==ExistsKind.DIR) {//変更の対象がディレクトリである場合
				(new File(session.getRnfrName())).renameTo(new File(path));
				//Directory.Move(rnfrName, path);
				//} else {//変更の対象がファイルである場合
				//	File.Move(rnfrName, path);
				//}
				//"RENAME"
				this.getLogger().set(LogKind.NORMAL, session.getSockCtrl(), 8, String.format("%s %s -> %s", session.getOneUser().getUserName(), session.getRnfrName(), path));
				session.stringSend("250 RNTO command successful");
				return true;
			}
		}
		session.stringSend(String.format("451 %s error", ftpCmd));
		return true;
	}

	private boolean jobRnfr(Session session, String param, FtpCmd ftpCmd) {
		String path = session.getCurrentDir().createPath(null, param, false);
		if (Util.exists(path) != ExistsKind.NONE) {
			session.setRnfrName(path);
			session.stringSend("350 File exists, ready for destination name");
			return true;
		}
		session.stringSend(String.format("451 %s error", ftpCmd));
		return true;
	}

	private boolean jobStor(Session session, String param, FtpCmd ftpCmd) {
		String path = session.getCurrentDir().createPath(null, param, false);
		if (path != null) {
			File file = new File(path);

			// アップロードユーザは、既存のファイルを上書きできない
			if (session.getOneUser().getFtpAcl() == FtpAcl.Up && file.exists()) {
				session.stringSend("550 Permission denied.");
				return true;
			} else {

				String str = String.format("150 Opening %s mode data connection for %s.", session.getFtpType(), param);
				session.getSockCtrl().stringSend(str, "ASCII");

				//Up start
				getLogger().set(LogKind.NORMAL, session.getSockCtrl(), 9, String.format("%s %s", session.getOneUser().getUserName(), param));
				boolean result = recvBinary(session.getSockData(), path);
				//Up end
				getLogger().set(LogKind.NORMAL, session.getSockCtrl(), 10, String.format("%s %s", session.getOneUser().getUserName(), param));
				session.getSockData().close();
				session.setSockData(null);

				session.stringSend(result ? "226 Transfer complete." : "426 Transfer abort.");
				return true;
			}
		}
		session.stringSend(String.format("451 %s error", ftpCmd));
		return true;
	}

	private boolean jobRetr(Session session, String param, FtpCmd ftpCmd) {
		String path = session.getCurrentDir().createPath(null, param, false);
		if (Util.exists(path) == ExistsKind.FILE) {
			FileSearch fileSearch = new FileSearch(path);
			File[] files = fileSearch.listFiles("*");
			if (files.length == 1) {
				String str = String.format("150 Opening %s mode data connection for %s (%d bytes).", session.getFtpType(), param, files[0].length());
				session.stringSend(str); //Shift-jisである必要がある？

				//DOWN start
				getLogger().set(LogKind.NORMAL, session.getSockCtrl(), 11, String.format("%s %s", session.getOneUser().getUserName(), param));
				boolean result = sendBinary(session.getSockData(), path);
				//DOWN end
				getLogger().set(LogKind.NORMAL, session.getSockCtrl(), 12, String.format("%s %s", session.getOneUser().getUserName(), param));

				session.getSockData().close();
				session.setSockData(null);

				session.stringSend(result ? "226 Transfer complete." : "426 Transfer abort.");
				return true;
			}
		}
		session.stringSend(String.format("550 %s: No such file or directory.", param));
		return true;
	}

	/**
	 * ファイル受信（バイナリ）
	 * @param fileName　ファイル名
	 * @return boolean　成否
	 */
	private boolean recvBinary(SockTcp sockTcp, String fileName) {
		//int max = 65535; //処理するブロックサイズ
		boolean result = false;

		//トレース表示
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("RecvBinaryFile(%s) ", fileName));

		//		var fs = new FileStream(fileName, FileMode.Create);
		//		var bw = new BinaryWriter(fs);
		//		fs.Seek(0, SeekOrigin.Begin);
		//
		//		while (isLife()) {
		//			Util.sleep(0);
		//			// キューから取得する
		//			var buffer = TcpQueue.Dequeue(max);
		//			if (buffer == null) {
		//				if (state != SocketObjState.Connect) {
		//					result = true;
		//					break;
		//				}
		//				Util.sleep(1);
		//			} else {
		//				bw.Write(buffer, 0, buffer.Length);
		//				//トレース表示
		//				sb.Append(String.Format("Binary=%dbyte ", buffer.Length));
		//			}
		//		}
		//		bw.Flush();
		//		bw.Close();
		//		fs.Close();

		//noEncode = true バイナリである事が分かっている
		//		Trace(TraceKind.Recv, Encoding.ASCII.GetBytes(sb.ToString()), true);//トレース表示
		return result;
	}

	boolean sendBinary(SockTcp sockTcp, String fileName) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("SendBinaryFile(%s) ", fileName));

		//byte[] buffer = new byte[3000000];
		boolean result = false;
		//		if (File.Exists(fileName)) {
		//				using (var fs = new FileStream(fileName, FileMode.Open, FileAccess.Read, FileShare.Read)) {
		//					using (var br = new BinaryReader(fs)) {
		//						fs.Seek(0, SeekOrigin.Begin);
		//						var offset = 0L;
		//						while (life) {
		//							var len = fs.Length - offset;
		//							if (len == 0) {
		//								result = true;
		//								break;
		//							}
		//							if (len > buffer.Length) {
		//								len = buffer.Length;
		//							}
		//							len = br.Read(buffer, 0, (int)len);
		//
		//							//トレース表示
		//							sb.Append(string.Format("Binary={0}byte ", len));
		//
		//							try {
		//								if (_oneSsl != null) {
		//									_oneSsl.Write(buffer, (int)len);
		//								} else {
		//									Socket.Send(buffer, 0, (int)len, SocketFlags.None);
		//								}
		//							} catch (Exception e) {
		//								//Ver5.0.0-a8
		//								string detailInfomation = Util.SwapStr("\n", "", Util.SwapStr("\r", "", e.Message));
		//								Logger.Set(LogKind.Error, null, 9000014, detailInfomation);//"SendBinaryFile(string fileName) socket.Send()"
		//								break;
		//							}
		//
		//							offset += len;
		//							fs.Seek(offset, SeekOrigin.Begin);
		//						}
		//						br.Close();
		//					}
		//					fs.Close();
		//				}
		//			} catch (Exception ex) {
		//				Logger.Set(LogKind.Error, null, 9000050, ex.Message);
		//			}
		//noEncode = true;//バイナリである事が分かっている
		//		Trace(TraceKind.Send, Encoding.ASCII.GetBytes(sb.ToString()), true);//トレース表示
		return result;
	}

	@Override
	public String getMsg(int messageNo) {
		switch (messageNo) {
			case 1:
				return isJp() ? "パラメータが長すぎます（不正なリクエストの可能性があるため切断しました)" : "A parameter is too long (I cut it off so that there was possibility of an unjust request in it)";
			case 2:
				return isJp() ? "ホームディレクトリが存在しません（処理が継続できないため切断しました)" : "There is not a home directory (because I cannot continue processing, I cut it off)";
			case 3:
				return isJp() ? "コマンド処理でエラーが発生しました" : "An error occurred by command processing";
			case 5:
				return "login";
			case 6:
				return "login";
			case 7:
				return "success";
			case 8:
				return "RENAME";
			case 9:
				return "UP start";
			case 10:
				return "UP end";
			case 11:
				return "DOWN start";
			case 12:
				return "DOWN end";
			case 13:
				return "logout";
			case 14:
				return isJp() ? "ユーザ名が無効です" : "A user name is null and void";
			case 15:
				return isJp() ? "パスワードが違います" : "password is different";
			default:
				break;
		}
		return null;
	}

}
