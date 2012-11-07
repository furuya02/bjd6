package bjd.net;

/**
 * bind情報を表現するクラス 
 * 
 * @author SIN
 *
 */
public final class OneBind {
	private Ip addr;
	private ProtocolKind protocol;

	/**
	 * Ipアドレスの取得
	 * 
	 * @return Ip
	 */
	public Ip getAddr() {
		return addr;
	}

	/**
	 * プロトコルの取得
	 * 
	 * @return TCP/UDP
	 */
	public ProtocolKind getProtocol() {
		return protocol;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param addr　Ipアドレス
	 * @param protocol　プロトコル(TCP/UDP)
	 */
	public OneBind(Ip addr, ProtocolKind protocol) {
		this.addr = addr;
		this.protocol = protocol;
	}

	/**
	 * 文字列化
	 */
	@Override
	public String toString() {
		return String.format("%s-%s", addr.toString(), protocol.toString());
	}
}
