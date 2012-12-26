package bjd.plugins.dns;

import java.io.IOException;
import java.util.ArrayList;

import bjd.packet.Conv;
import bjd.util.Bytes;
import bjd.util.Util;

/**
 * パケットを処理するクラス
 * @author SIN
 *
 */
public final class PacketDns {
	//	private Logger logger;

	// [受信に使用する場合]
	// PacketDns p = new PacketDns(Kernel kernel,String nameTag);
	// p.Read(byte[] buffer)
	// p.RequestName;
	// p.DnsType
	// p.Id （識別子）
	// p.Rd  (再帰問い合わせ)
	// p.GetRR(RR_KIND rrKind)

	// [送信に使用する場合]
	// PacketDns p = new PacketDns(Kernel kernel,String nameTag);
	// p.CreateHeader(uint id,boolean qr,boolean aa,boolean rd,boolean ra);//ヘッダの作成
	// p.AddRR(RR_KIND rrKind,OneRR oneRR);//フィールドの追加
	// byte [] p.Get();//内部バッファの取得（HeaderDnsとar(List<OneRR>)を結合(名前の圧縮及びHeaderのCountの修正)して返す）

	// 内部バッファ  DNSパケットの内容は、以下の２つのバッファで保持される
	private PacketDnsHeader dnsHeader = null;
	//private ArrayList<OneRR>[] _ar = new ArrayList<OneRR>[4];
	private ArrayList<OneRr>[] ar = new ArrayList[4];

	//デフォルトコンストラクタの隠蔽
	private PacketDns() {
		for (int i = 0; i < 4; i++) {
			ar[i] = new ArrayList<OneRr>();
		}
	}

	/**
	 * パケット生成のためのコンストラクタ
	 */
	public PacketDns(short id, boolean qr, boolean aa, boolean rd, boolean ra) {
		this(); //デフォルトコンストラクタ

		//ヘッダの生成
		dnsHeader = new PacketDnsHeader();
		try {
			dnsHeader.setId(id);
		} catch (IOException e) {
			//DnsHeaderは12バイトのサイズで初期化されているはずなので、ここで例外が発生するのは設計上の問題
			Util.runtimeException("PacketDns.createHeader() dnsHeader.serId()");
		}

		// 各種フラグ
		short flags = 0;
		if (aa) { //権威応答 権威有り(true)
			flags = (short) (flags | 0x0400);
		}
		if (ra) { //再帰有効 有効(true)
			flags = (short) (flags | 0x0080);
		}
		if (rd) { //再帰要求 有り(true)
			flags = (short) (flags | 0x0100);
		}
		if (qr) { //要求(false)・応答(true)
			flags = (short) (flags | 0x8000);
		}
		//_headerDns.Flags = Util.htons(flags);
		try {
			dnsHeader.setFlags(flags);
		} catch (IOException e) {
			//DnsHeaderは12バイトのサイズで初期化されているはずなので、ここで例外が発生するのは設計上の問題
			Util.runtimeException("PacketDns.createHeader() dnsHeader.setFlags(flags)");
		}

		//byte rcode=0 戻りコード
		//ushort tmp = (ushort)(0x0F & rcode);
		//flags = (ushort)(flags | tmp);
	}

	/**
	 * パケット解釈のためのコンストラクタ
	 * @param data
	 * @throws IOException 
	 */
	public PacketDns(byte[] buffer) throws IOException {
		this(); //デフォルトコンストラクタ

		//ヘッダの解釈
		dnsHeader = new PacketDnsHeader(buffer, 0);
		int offset = dnsHeader.length();

		// オペコード　0:標準 1:逆 2:サーバ状態
		//var c = (short)(Util.htons(_headerDns.Flags) & 0x7800);
		//var opcode = (ushort)(c >> 11);
		short flags = dnsHeader.getFlags();
		int opcode = flags & 0x7800;

		if (opcode != 0) { // 標準問い合せ以外は対応していない
			throw new IOException(String.format("OPCODE not 0 [CPCODE=%d]", opcode));
		}

		//****************************************
		//質問/回答/権限/追加フィールド取得
		//****************************************
		for (int rr = 0; rr < 4; rr++) {
			//ushort max = Util.htons(_headerDns.Count[rr]);//対象フィールドのデータ数
			short max = dnsHeader.getCount(rr);
			if (rr == 0 && max != 1) {
				//質問エントリーが１でないパケットは処理できません。
				throw new IOException(String.format("QD Entry !=0  [count=%d]", max));
			}
			for (int n = 0; n < max; n++) {
				//名前の取得
				//offsetの移動  名前のサイズが一定ではないので、そのサイズ分だけ進める
				UnCompress u0 = new UnCompress(buffer, offset);
				offset = u0.getOffSet();
				String name = u0.getHostName();

				//名前以降のリソースレコードを取得
				PacketRr packetRr = new PacketRr(buffer, offset);

				DnsType dnsType = packetRr.getType();
				int ttl = packetRr.getTtl();
				short dlen = packetRr.getDLen();
				byte[] data = packetRr.getData();

				if (rr == 0) { //質問フィールド[QD]の場合は、TTL, DLEN , DATAは無い
					ar[rr].add(new RrQuery(name, dnsType));
					//offsetの移動  名前以降の分だけ進める
					offset += 4;
					continue;
				}

				//TypeによってはNameが含まれている場合があるが、Nameは圧縮されている可能性があるので、
				//いったん、String 戻してから、改めてリソース用に組み直したDataを作成する
				OneRr oneRr = null;
				if (dnsType == DnsType.A) {
					oneRr = new RrA(name, ttl, data);
				} else if (dnsType == DnsType.Aaaa) {
					oneRr = new RrAaaa(name, ttl, data);
				} else if (dnsType == DnsType.Cname || dnsType == DnsType.Ptr || dnsType == DnsType.Ns) {
					UnCompress u1 = new UnCompress(buffer, offset + 10);
					switch (dnsType) {
						case Cname:
							oneRr = new RrCname(name, ttl, u1.getHostName());
							break;
						case Ptr:
							oneRr = new RrPtr(name, ttl, u1.getHostName());
							break;
						case Ns:
							oneRr = new RrNs(name, ttl, u1.getHostName());
							break;
						default:
							Util.runtimeException(String.format("DnsPacket() not implement dnsType=%s", dnsType));
							break;
					}
				} else if (dnsType == DnsType.Mx) {
					short preference = Conv.getShort(buffer, offset + 10);
					UnCompress u2 = new UnCompress(buffer, offset + 12);
					oneRr = new RrMx(name, ttl, preference, u2.getHostName());
				} else if (dnsType == DnsType.Soa) {
					UnCompress u3 = new UnCompress(buffer, offset + 10);
					UnCompress u4 = new UnCompress(buffer, u3.getOffSet());
					int p = u4.getOffSet();
					int serial = Conv.getInt(buffer, p += 4);
					int refresh = Conv.getInt(buffer, p += 4);
					int retry = Conv.getInt(buffer, p += 4);
					int expire = Conv.getInt(buffer, p += 4);
					int minimum = Conv.getInt(buffer, p += 4);
					oneRr = new RrSoa(name, ttl, u3.getHostName(), u4.getHostName(), serial, refresh, retry, expire, minimum);
				}
				if (oneRr != null) { //A NS MX SOA PTR CNAMEの6種類以外は、処理(追加)しない
					ar[rr].add(oneRr);
				}
				offset += 10 + dlen;
			}
			//ヘッダ内のRRレコードのエントリー数を設定する
			dnsHeader.setCount(rr, (short) ar[rr].size());
		}

	}

	public int getCount(RrKind rrKind) {
		try {
			return dnsHeader.getCount(rrKind.getIntValue());
		} catch (IOException e) {
			//ここで例外が派生するのは、 設計上の問題
			Util.runtimeException(this, e);
			return 0;
		}
	}

	//    public ushort GetRcode() {
	public short getRcode() throws IOException {
		return (short) (dnsHeader.getFlags() & 0x000F);
		//return (ushort)(Util.htons(_headerDns.Flags) & 0x000F);
	}

	public boolean getAA() throws IOException {
		//        if ((_headerDns.Flags & 0004) != 0)
		//            return true;
		//        return false;
		if ((dnsHeader.getFlags() & 0x0004) != 0) {
			return true;
		}
		return false;
	}

	//フィールドの読み込み
	//RR_TYPEフィールドのno番目のデータを取得する
	public OneRr getRR(RrKind rrKind, int no) {
		return ar[rrKind.getIntValue()].get(no);
	}

	/**
	 * 質問フィールドのDNSタイプ取得
	 * @return
	 */
	public DnsType getDnsType() {
		//質問フィールドの１つめのリソース
		return ar[0].get(0).getDnsType();
	}

	/**
	 * 質問フィールドの名前取得
	 * @return
	 */
	public String getRequestName() {
		//質問フィールドの１つめのリソース
		return ar[0].get(0).getName();
	}

	/**
	 * 識別子取得
	 * @return
	 */
	public short getId() {
		//ネットワークバイトオーダのまま
		try {
			return dnsHeader.getId();
		} catch (IOException e) {
			Util.runtimeException(this, e);
		}
		return 0; // これが返されることは無い
	}

	public boolean getRd() {
		//再帰要求(RD)取得
		//var c = (short)(Util.htons(_headerDns.Flags) & 0x0100);
		try {
			short c = (short) (dnsHeader.getFlags() & 0x0100);
			return ((c >> 8) != 0);
		} catch (IOException e) {
			return false; // これが実行されることは無い
		}
	}

	//回答フィールドへの追加
	//これを下記のように変更し、OneRRのコンストラクタを使用するようにする
	public void addRR(RrKind rrKind, OneRr oneRr) {
		//名前の圧縮と、Headerのカウントは、最後のGet()で処理する
		ar[rrKind.getIntValue()].add(oneRr);
	}

	/**
	 * バイトイメージの取得
	 * @return
	 * @throws IOException 
	 */
	public byte[] getBytes() {
		byte[] buffer = dnsHeader.getBytes();
		for (ArrayList<OneRr> a : ar) {
			for (OneRr o : a) {

				byte[] dataName = (new Compress(buffer, DnsUtil.str2DnsName(o.getName()))).getData();
				byte[] data = o.getData();
				DnsType dnsType = o.getDnsType();

				if (dnsType == DnsType.Ns || dnsType == DnsType.Cname || dnsType == DnsType.Ptr) {
					data = (new Compress(buffer, o.getData())).getData(); //圧縮
				} else if (dnsType == DnsType.Mx) {
					short preference = Conv.getShort(o.getData(), 0);
					dataName = new byte[o.getData().length - 2];
					//dataName = Buffer.BlockCopy(o.getData(), 2, o.getData().length - 2);
					System.arraycopy(o.getData(), 2, dataName, 0, o.getData().length - 2);

					dataName = (new Compress(buffer, dataName)).getData(); //圧縮
					data = Bytes.create(preference, dataName);
				}

				PacketRr packetRr = new PacketRr(data.length);
				try {
					packetRr.setCls((short) 1);
					packetRr.setType(dnsType);
					packetRr.setTtl(o.getTtl());
					packetRr.setData(data);
				} catch (IOException e) {
					//設計上の問題
					Util.runtimeException(this, e);
				}

				buffer = Bytes.create(buffer, dataName, packetRr.getBytes());
			}
		}
		return buffer;
	}
}
