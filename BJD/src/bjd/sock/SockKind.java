package bjd.sock;
/**
 * 生成方法によるSockObjの種類
 * @author user1
 *
 */
public enum SockKind {
	/**bindされたサーバから生成されたソケット UDPの場合はクローンなのでclose()しない*/
	ACCEPT,
	/**クライアント側で生成されたソケットオブジェクト*/
	CLIENT
}
