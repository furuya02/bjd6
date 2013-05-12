package bjd.sock;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;

import bjd.Kernel;
import bjd.RunMode;
import bjd.TraceKind;
import bjd.ValidObjException;
import bjd.log.LogKind;
import bjd.log.Logger;
import bjd.net.Ip;
import bjd.net.IpKind;
import bjd.util.Inet;
import bjd.util.MLang;

/**
 * SockTcp 及び SockUdp の基底クラス
 * @author SIN
 *
 */
public abstract class SockObj {

	//このKernelはTrace()のためだけに使用されているので、Traceしない場合は削除することができる
	private Kernel kernel;

	public Kernel getKernel() {
		return kernel;
	}

	public SockObj(Kernel kernel) {
		this.kernel = kernel;
	}

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
		lastError = String.format("[%s] %s", ex.getClass().getSimpleName(), ex.getMessage());
		this.sockState = SockState.Error;
	}

	protected final void setError(String msg) {
		lastError = msg;
		this.sockState = SockState.Error;
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

	public final Ip getRemoteIp() {
		String strIp = "0.0.0.0";
		if (remoteAddress != null) {
			strIp = remoteAddress.getAddress().toString();
		}
		try {
			return new Ip(strIp);
		} catch (ValidObjException e) {
		}
		return new Ip(IpKind.V4_0);
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
			String ipStr = remoteAddress.getAddress().toString();
			if (ipStr.charAt(0) == '/') {
				ipStr = ipStr.substring(1);
			}
			remoteHostname = ipStr;
		}
	}

	public abstract void close();

	//バイナリデータであることが判明している場合は、noEncodeをtrueに設定する
	//これによりテキスト判断ロジックを省略できる
	protected final void trace(TraceKind traceKind, byte[] buf, boolean noEncode) {

		if (buf == null || buf.length == 0) {
			return;
		}

		if (kernel.getRunMode() == RunMode.Remote) {
			return; //リモートクライアントの場合は、ここから追加されることはない
		}

		//Ver5.0.0-a22 サービス起動の場合は、このインスタンスは生成されていない
		boolean enableDlg = kernel.getTraceDlg() != null && kernel.getTraceDlg().isVisible();
		if (!enableDlg && kernel.getRemoteServer() == null) {
			//どちらも必要ない場合は処置なし
			return;
		}

		boolean isText = false; //対象がテキストかどうかの判断
		Charset charset = null;

		if (!noEncode) {
			//エンコード試験が必要な場合
			try {
				charset = MLang.getEncoding(buf);
			} catch (Exception ex) {
				charset = null;
			}
			if (charset != null) {
				//int codePage = encoding.CodePage;
				//if (encoding.CodePage == 20127 || encoding.CodePage == 65001 || encoding.CodePage == 51932 || encoding.CodePage == 1200 || encoding.CodePage == 932 || encoding.CodePage == 50220) {
				//"US-ASCII" 20127
				//"Unicode (UTF-8)" 65001
				//"日本語(EUC)" 51932
				//"Unicode" 1200
				//"日本語(シフトJIS)" 932
				//日本語(JIS) 50220
				//	isText = true;
				//}
			}
		}

		ArrayList<String> ar = new ArrayList<String>();
		if (isText) {
			ArrayList<byte[]> lines = Inet.getLines(buf);
			for (byte[] bytes : lines) {
				String str = new String(bytes, charset);
				str = Inet.trimCrlf(str);
				ar.add(str);
			}
			//ar.addRange(lines.Select(line => encoding.GetString(Inet.TrimCrlf(line))));
		} else {
			ar.add(noEncode ? String.format("binary %d byte", buf.length) : String.format("Binary %d byte", buf.length));
		}
		for (String str : ar) {
			Ip ip = this.getRemoteIp();

			if (enableDlg) {
				//トレースダイアログが表示されてい場合、データを送る
				kernel.getTraceDlg().addTrace(traceKind, str, ip);
			}
			if (kernel.getRemoteServer() != null) {
				//リモートサーバへもデータを送る（クライアントが接続中の場合は、クライアントへ送信される）
				//Fix
				//kernel.getRemoteServer().addTrace(traceKind, str, ip);
			}
		}

	}

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
