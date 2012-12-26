package bjd.plugins.dns;

import bjd.packet.Conv;
import bjd.util.Bytes;

/**
 * DNS形式名前(byte[])を圧縮する<br>
 * byte [] bufferは、現在、パケットの先頭からのバイト列<br>
 * 結果は、getData()で取得する<br>
 * 
 * @author SIN
 *
 */
public final class Compress {

	private byte[] data = new byte[0];

	public Compress(byte[] buffer, byte[] dataName) {

		//多い目にバッファを確保する
		byte[] buf = new byte[dataName.length];

		//コピー
		int dst = 0;
		for (int src = 0; src < dataName.length;) {
			int index = -1;
			if (dataName[src] != 0) {
				// 圧縮可能な同一パターンを検索する
				int len = dataName.length - src; //残りの文字列数
				byte[] target = new byte[len];
				//target = Buffer.BlockCopy(dataName, src, len);
				System.arraycopy(dataName, src, target, 0, len);

				//パケットのヘッダ以降が検索対象になる（bufferは、ヘッダの後ろに位置しているので先頭は0となる）
				int off = 12; // 検索開始位置(ヘッダ以降)
				index = Bytes.indexOf(buffer, off, target);
			}
			if (0 <= index) { // 圧縮可能な場合
				int c = 0xC000 | index;
				byte[] cc = Conv.getBytes(c);
				buf[dst] = cc[2];
				buf[dst + 1] = cc[3];

				dst += 2;
				break;
			}
			// 圧縮不可能な場合は、.までをコピーする
			int n = dataName[src] + 1;
			//Buffer.BlockCopy(dataName, src, buf, dst, n);
			for (int i = 0; i < n; i++) {
				dataName[src + i] = buf[dst + i];
			}
			dst += n;
			src += n;
		}
		//有効文字数分のみコピーする
		data = new byte[dst];
		//data = Buffer.BlockCopy(buf, 0, dst);
		System.arraycopy(buf, 0, data, 0, dst);
	}

	public byte[] getData() {
		return data;
	}
}
