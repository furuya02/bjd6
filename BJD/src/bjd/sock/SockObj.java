package bjd.sock;

import java.net.InetSocketAddress;

import bjd.log.LogKind;
import bjd.log.Logger;

/**
 * SockTcp 及び SockUdp の基底クラス
 * @author SIN
 *
 */
public abstract class SockObj {

	//****************************************************************
	// LastError関連
	//****************************************************************
	private String lastError = "";
	/**
	 * LastErrorの取得
	 * @return
	 */
	public final String getLastEror() {
		return lastError;
	}

	//****************************************************************
	// SockState関連
	//****************************************************************
	private SockState sockState = SockState.IDLE;
	/**
	 * ステータスの取得
	 * @return
	 */
	public final SockState getSockState() {
		return sockState;
	}

	/**
	 * ステータスの設定<br>
	 * Connect/bindで使用する
	 * @param sockState
	 * @param localAddress
	 * @param remoteAddress
	 */
	protected final void set(SockState sockState, InetSocketAddress localAddress, InetSocketAddress remoteAddress) {
		this.sockState = sockState;
		this.localAddress = localAddress;
		this.remoteAddress = remoteAddress;
	}

	//****************************************************************
	// エラー（切断）発生時にステータスの変更とLastErrorを設定するメソッド
	//****************************************************************
	protected final void setException(Exception ex) {
		ex.printStackTrace();
		lastError = ex.getMessage();
		set(SockState.Error, null, null);
	}

	protected final void setError(String msg) {
		lastError = msg;
		set(SockState.Error, null, null);
	}

	//****************************************************************
	// アドレス関連
	//****************************************************************
	private String remoteHostname = "";
	private InetSocketAddress remoteAddress = null;
	private InetSocketAddress localAddress = null;

	public final String getRemoteHostname() {
		return remoteHostname;
	}

	public final InetSocketAddress getRemoteAddress() {
		return remoteAddress;
	}

	public final InetSocketAddress getLocalAddress() {
		return localAddress;
	}

	//TODO メソッドの配置はここでよいか？
	public final void resolve(boolean useResolve, Logger logger) {
		if (useResolve) {
			remoteHostname = "resolve error!";
			try {
				//remoteHost = kernel.DnsCache.Get(RemoteEndPoint.Address,logger);
			} catch (Exception ex) {
				logger.set(LogKind.ERROR, (SockObj) null, 9000053, ex.getMessage());
			}
		} else {
			remoteHostname = remoteAddress.getAddress().toString();
		}
	}

	public abstract void close();

	//バイナリデータであることが判明している場合は、noEncodeをtrueに設定する
	//これによりテキスト判断ロジックを省略できる
	//	protected void Trace(TraceKind traceKind,byte [] buf,boolean noEncode) {
	//
	//		if (buf == null || buf.length == 0){
	//			return;
	//		}
	//
	//		if (kernel.getRunMode() == RunMode.Remote){
	//			return;//リモートクライアントの場合は、ここから追加されることはない
	//		}
	//
	//		//Ver5.0.0-a22 サービス起動の場合は、このインスタンスは生成されていない
	//		boolean enableDlg = kernel.TraceDlg != null && kernel.TraceDlg.Visible;
	//		if (!enableDlg && kernel.RemoteServer==null) {
	//			//どちらも必要ない場合は処置なし
	//			return;
	//		}
	//
	//		boolean isText = false;//対象がテキストかどうかの判断
	//		Encoding encoding = null;
	//
	//		if(!noEncode) {//エンコード試験が必要な場合
	//			try {
	//				encoding = MLang.GetEncoding(buf);
	//			} catch {
	//				encoding = null;
	//			}
	//			if(encoding != null) {
	//				//int codePage = encoding.CodePage;
	//				if(encoding.CodePage == 20127 || encoding.CodePage == 65001 || encoding.CodePage == 51932 || encoding.CodePage == 1200 || encoding.CodePage == 932 || encoding.CodePage == 50220) {
	//					//"US-ASCII" 20127
	//					//"Unicode (UTF-8)" 65001
	//					//"日本語(EUC)" 51932
	//					//"Unicode" 1200
	//					//"日本語(シフトJIS)" 932
	//					//日本語(JIS) 50220
	//					isText = true;
	//				}
	//			}
	//		}
	//
	//		ArrayList<String> ar = new ArrayList<String>();
	//		if (isText){
	//			var lines = Inet.GetLines(buf);
	//			ar.AddRange(lines.Select(line => encoding.GetString(Inet.TrimCrlf(line))));
	//		}
	//		else {
	//			ar.Add(noEncode
	//					? String.Format("binary {0} byte", buf.length)
	//							: String.Format("Binary {0} byte", buf.length));
	//		}
	//		for (String str : ar) {
	//			Ip ip = new Ip(remoteAddress.getAddress().ToString());
	//
	//			if(enableDlg) {//トレースダイアログが表示されてい場合、データを送る
	//				kernel.TraceDlg.AddTrace(traceKind,str,ip);
	//			}
	//			if(kernel.RemoteServer!=null) {//リモートサーバへもデータを送る（クライアントが接続中の場合は、クライアントへ送信される）
	//				kernel.RemoteServer.AddTrace(traceKind,str,ip);
	//			}
	//		}
	//
	//	}

}
