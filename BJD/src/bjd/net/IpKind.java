package bjd.net;

/**
 * 例外が発生しないＩｐクラスののコンストラクタに指定する引数
 * @author SIN
 *
 */
public enum IpKind {
	/*INADDR_ANY*/
	INADDR_ANY, 
	/*IN6ADDR_ANY_INIT*/
	IN6ADDR_ANY_INIT,
	/*0.0.0.0*/
	V4_0,  
	/*255.255.255.255*/
	V4_255,
	/*::*/
	V6_0,
	/*ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff*/
	V6_FF,
	/*127.0.0.1*/
	V4_LOCALHOST,
	/*::1*/
	V6_LOCALHOST,
}
