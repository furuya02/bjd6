package bjd.plugins.dns;

import bjd.packet.Conv;

/**
 * 開始位置 buffer[offSet]から、ホスト名(String)を取り出す<br>
 * 結果は、getHostName()及びgetOffSet()で取得する
 * @author SIN
 *
 */
public final class UnCompress {
	private String hostname = "";
	private int offSet = 0;

	public UnCompress(byte[] buffer, int offset) {
		this.offSet = offset;
		int compressOffSet = 0; // 圧縮ラベルを使用した場合のリターン用ポインタ
		boolean compress = false; // 圧縮ラベルを使用したかどうかのフラグ
		char[] tmp = new char[buffer.length];
		int d = 0;
		while (true) {
			//byte c = buffer[offSet];
			int c = buffer[offSet];
			offSet++;
			if (c == 0) {
				//最後の.は残す
				if (d == 0) {
					tmp[d] = '.';
					d++;
				}
				hostname = new String(tmp, 0, d);
				if (compress) { // 圧縮ラベルを使用した場合は、２バイトだけ進めたポインタを返す
					offSet = compressOffSet;
				}
				return;
			}
			if ((c & 0xC0) == 0xC0) { // 圧縮ラベル
				//ushort off1 = Util.htons(BitConverter.ToUInt16(buffer, offSet - 1));
				short off1 = Conv.getShort(buffer, offSet - 1);
				// 圧縮ラベルを使用した直後は、s+2を返す
				// 圧縮ラベルの再帰の場合は、ポインタを保存しない
				if (!compress) {
					compressOffSet = offSet + 1;
					compress = true;
				}
				//ushort off = (ushort)(off1 & 0x3FFF);
				short off = (short) (off1 & 0x3FFF);
				offSet = off;
			} else {
				if (c >= 255) {
				//if (c >= (byte) 255) {
					hostname = "";
					return;
				}
				for (int i = 0; i < c; i++) {
					tmp[d++] = (char) buffer[offSet++];
				}
				tmp[d++] = '.';
			}
		}
	}

	public String getHostName() {
		return hostname;
	}

	public int getOffSet() {
		return offSet;
	}
}
