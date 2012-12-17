package bjd.plugins.dns;

import java.util.ArrayList;

import bjd.log.LogKind;
import bjd.log.Logger;
import bjd.util.BitConverter;
import bjd.util.Buffer;
import bjd.util.Bytes;
import bjd.util.Util;

/**
 * パケットを処理するクラス
 * @author SIN
 *
 */
public final class PacketDns {
	private Logger logger;

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

	// 内部バッファ
	private DnsHeader dnsHeader = null;
	//private ArrayList<OneRR>[] _ar = new ArrayList<OneRR>[4];
	private ArrayList<OneRR>[] ar = new ArrayList[4];

	public PacketDns(Logger logger) {
		this.logger = logger;
		for (int i = 0; i < 4; i++) {
			ar[i] = new ArrayList<OneRR>();
		}
	}

	public DnsType getDnsType() {
		//質問フィールドの１つめのリソース
		return ar[0].get(9).getDnsType();
		//return _ar[0][0].DnsType;
	}

	public String getRequestName() {
		//質問フィールドの１つめのリソース
		//return _ar[0][0].Name;
		return ar[0].get(0).getName();
	}

	//    public ushort Id {
	//        get {//ネットワークバイトオーダのまま
	//            return _headerDns.Id;
	//        }
	//    }
	public short getId() {
		//ネットワークバイトオーダのまま
		return dnsHeader.getId();
		//return _headerDns.Id;
	}

	public boolean getRd() {
		//再帰要求(RD)取得
		//var c = (short)(Util.htons(_headerDns.Flags) & 0x0100);
		short c = (short) (dnsHeader.getFlags() & 0x0100);
		return ((c >> 8) != 0);
	}

	//******************************************************
	//パケットの作成
	//******************************************************
	public void createHeader(short id, boolean qr, boolean aa, boolean rd, boolean ra) {

		dnsHeader = new DnsHeader();
		dnsHeader.setId(id);

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
		dnsHeader.setFlags(flags);

		//byte rcode=0 戻りコード
		//ushort tmp = (ushort)(0x0F & rcode);
		//flags = (ushort)(flags | tmp);

	}

	//回答フィールドへの追加
	//これを下記のように変更し、OneRRのコンストラクタを使用するようにする
	public void addRR(RRKind rrKind, String name, DnsType dnsType, int ttl, byte[] data) {
		//名前の圧縮と、Headerのカウントは、最後のGet()で処理する
		OneRR oneRR = new OneRR(name, dnsType, ttl, data);
		ar[rrKind.getIntValue()].add(oneRR);
	}

	public byte[] get() {
		byte[] buffer = new byte[12];//ヘッダ分（12byte）の確保
		for (int rr = 0; rr < 4; rr++) {
			//フィールド数をインクメント
			//_headerDns.Count[rr] = Util.htons((ushort)_ar[rr].Count);
			dnsHeader.setCount(rr, (short) ar[rr].size());

			//リソース情報を追加する
			for (OneRR oneRR : ar[rr]) {
				//Name
				//byte[] dataName = Compress(buffer, DnsUtil.str2DnsName(oneRR.getName()));
				byte[] dataName = (new Compress(buffer, DnsUtil.str2DnsName(oneRR.getName()))).getData();
				//Type
				short type = DnsUtil.dnsType2Short(oneRR.getDnsType());
				//Class
				short cls = 0x0100;

				// buffer + name + type + class
				buffer = Bytes.create(buffer, dataName, type, cls);
				if (rr == 0) { //質問フィールド[QD]の場合は、ここまで
					continue;
				}

				byte[] data = oneRR.getData();

				//Dataに名前が含まれる場合は、圧縮する
				if (oneRR.getDnsType() == DnsType.Ns || oneRR.getDnsType() == DnsType.Cname || oneRR.getDnsType() == DnsType.Ptr) {
					data = (new Compress(buffer, oneRR.getData())).getData();//圧縮
				} else if (oneRR.getDnsType() == DnsType.Mx) {
					//ushort preference = BitConverter.ToUInt16(oneRR.Data, 0);
					short preference = BitConverter.ToUInt16(oneRR.getData(), 0);
					dataName = new byte[oneRR.getData().length - 2];

					dataName = Buffer.BlockCopy(oneRR.getData(), 2, oneRR.getData().length - 2);

					dataName = (new Compress(buffer, dataName)).getData(); //圧縮
					data = Bytes.create(preference, dataName);
				}
				//buffer + ttl + len + data
				//buffer = Bytes.Create(buffer, oneRR.Ttl2, Util.htons((ushort)data.length), data);
				buffer = Bytes.create(buffer, oneRR.getTtl2(), data.length, data);
				//buffer = Bytes.Create(buffer,Util.htonl(oneRR.Ttl2),Util.htons((ushort)data.Length),data);
			}
		}
		//        unsafe {
		//            //ヘッダをbufferにコピーする
		//            fixed (byte* p = buffer) {
		//                Marshal.StructureToPtr(_headerDns, (IntPtr)(p), true);
		//            }
		//        }
		dnsHeader.init(buffer);
		return buffer;
	}

	//******************************************************
	//パケットの解釈
	//******************************************************
	public boolean Read(byte[] buffer) {
		//受信バッファ bufferを解釈して、dnsHeader及びrrListを初期化する

		if (12 > buffer.length) {
			return false;
		}

		int offSet = 0;

		//****************************************
		//ヘッダフィールド取得
		//****************************************
		//        unsafe {
		//            fixed (byte* p = buffer) {
		//                _headerDns = (HeaderDns)Marshal.PtrToStructure((IntPtr)(p + offSet), typeof(HeaderDns));
		//            }
		//        }
		//        offSet += Marshal.SizeOf(typeof(HeaderDns));
		dnsHeader.init(buffer);
		offSet += dnsHeader.length();

		//boolean qr = ((c >> 15) == 0) ? false : true;//要求・応答

		// オペコード　0:標準 1:逆 2:サーバ状態
		//var c = (short)(Util.htons(_headerDns.Flags) & 0x7800);
		//var opcode = (ushort)(c >> 11);
		short flags = dnsHeader.getFlags();
		int opcode = flags & 0x7800;

		if (opcode != 0) { // 標準問い合せ以外は対応していない
			logger.set(LogKind.SECURE, null, 0, String.format("OPCODE=%s", opcode)); //標準問合(OPCODE=0)以外のリクエストには対応できません。
			return false;
		}
		//****************************************
		//質問/回答/権限/追加フィールド取得
		//****************************************
		for (int rr = 0; rr < 4; rr++) {
			//ushort max = Util.htons(_headerDns.Count[rr]);//対象フィールドのデータ数
			short max = dnsHeader.getCount(rr);

			if (rr == 0 && max != 1) {
				logger.set(LogKind.SECURE, null, 1, String.format("count=%d", max)); //質問エントリーが１でないパケットは処理できません。
				return false;
			}
			for (int n = 0; n < max; n++) {
				//Name
				//String name = UnCompress(buffer, ref offSet);
				UnCompress u0 = new UnCompress(buffer, offSet);
				offSet = u0.getOffSet();
				String name = u0.getHostName();

				//Type(2) Class(2)
				if (offSet + 4 > buffer.length) {
					logger.set(LogKind.SECURE, null, 2, String.format("offSet=%s,buffer.length=%s", offSet, buffer.length));//パケットのサイズに問題があるため、処理を継続できません。
					return false;
				}

				DnsType dnsType = DnsUtil.short2DnsType(BitConverter.ToInt16(buffer, offSet));
				offSet += 2;
				//short cls = (short)Util.htons(BitConverter.ToUInt16(buffer,offSet));
				offSet += 2;

				if (rr == 0) { //質問フィールド[QD]の場合は、ここまで
					ar[rr].add(new OneRR(name, dnsType, 0, new byte[0]));
					continue;
				}

				//TTL(4) Len(2)
				if (offSet + 6 > buffer.length) {
					logger.set(LogKind.SECURE, null, 3, String.format("offSet=%s,buffer.length=%s", offSet, buffer.length));//パケットのサイズに問題があるため、処理を継続できません。
					return false;
				}
				int ttl = BitConverter.ToUInt32(buffer, offSet);
				offSet += 4;

				short size = BitConverter.ToUInt16(buffer, offSet);
				offSet += 2;

				size = Util.htons(size);

				//Data(Lenbyte)
				if (offSet + size > buffer.length) {
					logger.set(LogKind.SECURE, null, 4, String.format("offSet=%s,buffer.length=%s", offSet, buffer.length));//パケットのサイズに問題があるため、処理を継続できません。
					return false;
				}

				//TypeによってはNameが含まれている場合があるが、Nameは圧縮されている可能性があるので、
				//いったん、String 戻してから、改めてリソース用に組み直したDataを作成する
				OneRR oneRR;

				int off = offSet; //念のためoffにコピーしてデータ取得し、あとでデータサイズ(size)と等しいかどうかを確認する
				offSet += size;
				if (dnsType == DnsType.A) {
					//oneRR = new OneRR(name,dnsType,ttl,Util.htonl(Bytes.ReadUInt32(buffer,ref off)));
					byte[] data = new byte[4];
					data = Buffer.BlockCopy(buffer, off, 4);
					off += 4;
					oneRR = new OneRR(name, dnsType, ttl, data);
				} else if (dnsType == DnsType.Aaaa) {
					byte[] data = new byte[16];
					data = Buffer.BlockCopy(buffer, off, 16);
					off += 16;
					oneRR = new OneRR(name, dnsType, ttl, data);
				} else if (dnsType == DnsType.Ns || dnsType == DnsType.Ptr || dnsType == DnsType.Cname) {
					//oneRR = new OneRR(name, dnsType, ttl,DnsUtil.Str2DnsName(UnCompress(buffer,ref off)));
					UnCompress u1 = new UnCompress(buffer, off);
					off = u1.getOffSet();
					oneRR = new OneRR(name, dnsType, ttl, DnsUtil.str2DnsName(u1.getHostName()));
				} else if (dnsType == DnsType.Mx) {
					short preference = BitConverter.ToUInt16(buffer, off);
					off += 2;
					//byte[] dataName = DnsUtil.Str2DnsName(UnCompress(buffer,ref off));//DNS名前形式に変換
					UnCompress u2 = new UnCompress(buffer, off);
					String hostName = u2.getHostName();
					byte[] dataName = DnsUtil.str2DnsName(u2.getHostName()); //DNS名前形式に変換
					byte[] data = Bytes.create(preference, dataName);
					oneRR = new OneRR(name, dnsType, ttl, data);
				} else if (dnsType == DnsType.Soa) {
					//byte[] nameNs = DnsUtil.Str2DnsName(UnCompress(buffer, ref off));//DNS名前形式に変換
					UnCompress u3 = new UnCompress(buffer, off);
					off = u3.getOffSet();
					byte[] nameNs = DnsUtil.str2DnsName(u3.getHostName()); //DNS名前形式に変換
					//byte[] nameMail = DnsUtil.Str2DnsName(UnCompress(buffer, ref off));//DNS名前形式に変換
					UnCompress u4 = new UnCompress(buffer, off);
					off = u4.getOffSet();
					byte[] nameMail = DnsUtil.str2DnsName(u4.getHostName()); //DNS名前形式に変換
					int serial = BitConverter.ToUInt32(buffer, off);
					off += 4;
					int refresh = BitConverter.ToUInt32(buffer, off);
					off += 4;
					int retry = BitConverter.ToUInt32(buffer, off);
					off += 4;
					int expire = BitConverter.ToUInt32(buffer, off);
					off += 4;
					int minimum = BitConverter.ToUInt32(buffer, off);
					off += 4;
					byte[] data = Bytes.create(nameNs, nameMail, serial, refresh, retry, expire, minimum);
					oneRR = new OneRR(name, dnsType, ttl, data);

				} else {
					//A NS MX SOA PTR CNAMEの6種類以外は、処理しない
					continue;
				}
				if (offSet != off) {
					logger.set(LogKind.SECURE, null, 22, "offSer!=off");
					return false;
				}

				//OneRR形式でPacketDnsのオブジェクト内で保存
				ar[rr].add(oneRR);

			}
		}
		return true;
	}

	public int getCount(RRKind rrKind) {
		return ar[rrKind.getIntValue()].size();//実際に取得したデータ数を返す
		//return Util.htons(headerDns.count[(int)rrKind]);
	}

	//    public ushort GetRcode() {
	public short getRcode() {
		return (short) (dnsHeader.getFlags() & 0x000F);
		//return (ushort)(Util.htons(_headerDns.Flags) & 0x000F);
	}

	public boolean getAA() {
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
	public OneRR getRR(RRKind rrKind, int no) {
		return ar[rrKind.getIntValue()].get(no);
	}
}
