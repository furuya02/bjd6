package bjd.plugins.ftp;

/**
 * FTPの転送モード<br>
 * Windows上でのFTPサーバは、改行コードが\r\nあるため、アスキーモードもバイナリ-モードも操作は同じになる<br>
 * 従って、単純に表示用の変数でしかない
 * @author SIN
 *
 */
enum FtpType {
    ASCII,
    BINARY
}
