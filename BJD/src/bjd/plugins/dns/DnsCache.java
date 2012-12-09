package bjd.plugins.dns;

import java.util.ArrayList;
import java.util.Calendar;

import bjd.log.Logger;
import bjd.option.Dat;
import bjd.option.OneDat;
import bjd.option.OneOption;

/**
 * 後でListRRに変更予定<br>
 * ＲＲレコードのデータベース<br>
 * @author SIN
 *
 */
public class DnsCache {
	
	private Object lock = new Object();
	
	
    //コンストラクタでファイルを読み込んで初期化する
    //コンストラクタは、named.ca用と.zone用の２種類がある
    ArrayList<OneRR> _db = new ArrayList<>();

    //プロパティ
    private String domainName;

    int _soaExpire;//終了時間（オプションで指定された有効時間）


    //named.caで初期化する場合
    public DnsCache(OneOption oneOption, String fileName) {
        int ttl = 0;//rootCacheは有効期限なし

        //オプションを読み込んで、ローカルデータを初期化する
        //this.oneOption = oneOption;
        _soaExpire = (int)oneOption.getValue("soaExpire");

        domainName = ".";
        //this.defaultExpire = defaultExpire;
        if (File.Exists(fileName)) {
            using (var sr = new StreamReader(fileName, Encoding.GetEncoding("Shift_JIS"))) {
                String tmpName = "";//全行のNAMEを保持する　NAMEは前行と同じ場合省略が可能

                while (true) {
                    String name = "";
                    //String Class = "IN";
                    DnsType dnsType = DnsType.Unknown;

                    var str = sr.ReadLine();
                    if (str == null)
                        break;
                    //;以降はコメントとして削除する
                    var i = str.indexOf(';');
                    if (i != -1)
                        str = str.SubString(0, i);

                    //空の行は処理しない
                    if (str.Length == 0)
                        continue;

                    //空白・タブを削除して、パラメータをtmp2へ取得する
                    var tmp = str.Split(new[] { ' ', '\t' });
                    var tmp2 = tmp.Where(s => s != "").ToList();

                    //************************************************
                    //タイプだけは省略することができないので、それを基準にサーチする
                    //************************************************
                    var typeCol = 0;
                    for (; typeCol < tmp2.Count; typeCol++) {
                        foreach (DnsType t in Enum.GetValues(typeof(DnsType))) {
                            if (tmp2[typeCol] != t.ToString().ToUpper()) continue;
                            dnsType = t;
                            break;
                        }
                        if (dnsType != DnsType.Unknown)
                            break;
                    }
                    if (dnsType == DnsType.Unknown)
                        goto err;//タイプが見つからない場合は、無効行とする

                    //タイプの次がDATAとなる
                    if (typeCol + 1 >= tmp2.Count)
                        goto err; //タイプの次にカラム（DATA）が存在しない
                    String dataStr = tmp2[typeCol + 1];

                    //************************************************
                    //クラス(IN)が含まれているかどうかをサーチする
                    //************************************************
                    var classCol = 0;
                    for (; classCol < tmp2.Count; classCol++){
                        if (tmp2[classCol] != "IN") continue;
                        goto find;
                    }
                    classCol = -1;
                find:
                    //クラスが含まれた場合、そのカラムはclassColに保存されている
                    //含まれていない場合 classCol=-1

                    if (typeCol == 1) {
                        if (classCol == -1) { //INが無い場合
                            //０番目はNAME若しくはTTLとなる
                            if (str.SubString(0, 1) == " " || str.SubString(0, 1) == "\t") {
                                //名前は省略されているので
                                ttl = Convert.ToUInt32(tmp2[0]);
                                ttl = Util.htonl(ttl);
                            } else {
                                name = tmp2[0];
                            }
                        } else { //INが有る場合
                            if (classCol != 0)
                                goto err;//位置がおかしい
                            //０番目はINであるので、名前もTTLも省略されている
                        }
                    } else if (typeCol == 2) {
                        if (classCol == -1) { //INが無い場合
                            //０番目はNAME、1番目はTTLとなる
                            name = tmp2[0];
                            ttl = Convert.ToUInt32(tmp2[1]);
                            ttl = Util.htonl(ttl);
                        } else { //INが有る場合
                            if (classCol != 1)
                                goto err;//位置がおかしい

                            //０番目はNAME若しくはTTLとなる
                            if (str.SubString(0, 1) == " " || str.SubString(0, 1) == "\t") {
                                //名前は省略されているので
                                ttl = Convert.ToUInt32(tmp2[0]);
                                ttl = Util.htonl(ttl);
                            } else {
                                name = tmp2[0];
                            }
                        }
                    } else if (typeCol == 3) {
                        if (classCol == -1) { //INが無い場合
                            //カラム不足
                            goto err;
                        } 
                        //INが有る場合
                        if (classCol != 2)
                            goto err;//位置がおかしい
                        //０番目はNAME、1番目はTTLとなる
                        name = tmp2[0];
                        ttl = Convert.ToUInt32(tmp2[1]);
                        ttl = Util.htonl(ttl);
                    }

                    //*********************************************
                    //nameの補完
                    //*********************************************
                    if (name == "@") { //@の場合
                        name = domainName;
                    } else if (name.LastIndexOf('.') != name.Length - 1) { //最後に.がついていない場合、ドメイン名を追加する
                        name = name + "." + domainName + ".";
                    } else if (name == "") {
                        name = tmpName;//前行と同じ
                    }
                    tmpName = name;//前行分として記憶する

                    //*********************************************
                    //String sataStr を変換してデータベースに追加
                    //*********************************************
                    if (dnsType == DnsType.A) {
                        var ipV4 = new Ip(dataStr);
                        Add(new OneRR(name, dnsType, ttl, ipV4.NetBytes()));
                    } else if (dnsType == DnsType.Ns) {
                        Add(new OneRR(name, dnsType, ttl, DnsUtil.Str2DnsName(dataStr)));
                    } else if (dnsType == DnsType.Aaaa) {
                        var ipV6 = new Ip(dataStr);
                        Add(new OneRR(name, dnsType, ttl, ipV6.NetBytes()));
                    } else {
                        Msg.Show(MsgKind.Error, "name.caには、タイプA,AAAA及びNS以外は使用できません。");
                        goto err;
                    }
                    continue;
                err://行に矛盾が有る
                    Msg.Show(MsgKind.Error, String.Format("ServerDnsCache() レコード読み込みエラー 矛盾があります。[ {0} {1} ]", fileName, str));
                }
                sr.Close();
            }
        }
        //locaohostレコードの追加
        {
            var ip = new Ip("127.0.0.1");
            Add(new OneRR("localhost.", DnsType.A, ttl, ip.NetBytes()));
            Add(new OneRR("1.0.0.127.in-addr.arpa.", DnsType.Ptr, ttl, DnsUtil.Str2DnsName("localhost")));

            ip = new Ip("::1");
            Add(new OneRR("localhost.", DnsType.Aaaa, ttl, ip.NetBytes()));
            Add(new OneRR("1.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.IP6.ARPA.", DnsType.Ptr, ttl, DnsUtil.Str2DnsName("localhost")));
        }
    }

    //リソース定義（Dat)で初期化する場合
    public DnsCache(Logger logger, OneOption oneOption, Dat dat, String dName) {
        int ttl = 0; //有効期限なし
        String ns = "";//SOA追加時に使用するため、NSレコードを見つけたときにサーバ名を保存しておく

        //オプションを読み込んで、ローカルデータを初期化する
        //this.oneOption = oneOption;
        _soaExpire = (int)oneOption.getValue("soaExpire");

        domainName = dName;

        for (OneDat o : dat) {
            if (o.isEnable()) {
                var type = Convert.ToInt32(o.StrList[0]);
                var name = o.StrList[1];
                var alias = o.StrList[2];
                var ip = new Ip(o.StrList[3]);
                var n = Convert.ToInt32(o.StrList[4]);

                var dnsType = DnsType.A;
                if (type == 1) {
                    dnsType = DnsType.Ns;
                } else if (type == 2) {
                    dnsType = DnsType.Mx;
                } else if (type == 3) {
                    dnsType = DnsType.Cname;
                } else if (type == 4) {
                    dnsType = DnsType.Aaaa;
                }
                var priority = (ushort)n;
                //uint addr = ip.AddrV4;        //class Ip -> uint;

                //最後に.がついていない場合、ドメイン名を追加する
                if (name.LastIndexOf('.') != name.Length - 1) {
                    name = name + "." + domainName;
                }
                if (alias.LastIndexOf('.') != alias.Length - 1) {
                    alias = alias + "." + domainName;
                }

                //CNAME以外は、PTRレコードを自動的に生成する
                if (dnsType != DnsType.Cname) {
                    //PTR名を作成 [例] 192.168.0.1 -> 1.0.168.192.in-addr.arpa;
                    if (ip.InetKind == InetKind.V4) { //IPv4
                        String ptrName = String.Format("{0}.{1}.{2}.{3}.in-addr.arpa.", ip.IpV4[3], ip.IpV4[2], ip.IpV4[1], ip.IpV4[0]);
                        Add(new OneRR(ptrName, DnsType.Ptr, ttl, DnsUtil.Str2DnsName(name)));
                    } else { //IPv6
                        var sb = new StringBuilder();
                        for (var a : ip.IpV6) {
                            sb.Append(String.Format("{0:x4}", a));
                        }
                        String ipStr = sb.ToString();
                        if (ipStr.Length == 32) {
                            sb = new StringBuilder();
                            for (int e = 31; e >= 0; e--) {
                                sb.Append(ipStr[e]);
                                sb.Append('.');
                            }
                            Add(new OneRR(sb + "ip6.arpa.", DnsType.Ptr, ttl, DnsUtil.Str2DnsName(name)));
                        }
                    }
                }

                //データベースへの追加
                if (dnsType == DnsType.A) {
                    if (ip.InetKind == InetKind.V4) {
                        //ネットワークバイト配列の取得
                        Add(new OneRR(name, DnsType.A, ttl, ip.NetBytes()));
                    } else {
                        logger.Set(LogKind.Error, null, 19, String.Format("address {0}", ip));
                    }
                } else if (dnsType == DnsType.Aaaa) {
                    if (ip.InetKind == InetKind.V6) {
                        Add(new OneRR(name, DnsType.Aaaa, ttl, ip.NetBytes()));
                    } else {
                        logger.Set(LogKind.Error, null, 20, String.Format("address {0}", ip));
                    }
                } else if (dnsType == DnsType.Ns) {
                    ns = name;//SOA追加時に使用するため、ネームサーバの名前を保存する

                    // A or AAAAレコードも追加
                    Add(new OneRR(name, (ip.InetKind == InetKind.V4) ? DnsType.A : DnsType.Aaaa, ttl, ip.NetBytes()));

                    Add(new OneRR(domainName, DnsType.Ns, ttl, DnsUtil.Str2DnsName(name)));
                } else if (dnsType == DnsType.Mx) {
                    // A or AAAAレコードも追加
                    Add(new OneRR(name, DnsType.A, ttl, ip.NetBytes()));

                    //プライオリィ
                    byte[] dataName = DnsUtil.Str2DnsName(name);//DNS名前形式に変換
                    byte[] data = Bytes.Create(Util.htons(priority), dataName);
                    Add(new OneRR(domainName, DnsType.Mx, ttl, data));
                } else if (dnsType == DnsType.Cname) {
                    Add(new OneRR(alias, DnsType.Cname, ttl, DnsUtil.Str2DnsName(name)));
                }
            }

            //SOAレコードの追加
            if (ns != "") { //NSサーバ名が必須
                var soaMail = (String)oneOption.GetValue("soaMail");
                soaMail.Replace('@', '.');//@を.に置き換える
                soaMail = soaMail + ".";//最後に.を追加する
                var soaSerial = (uint)(int)oneOption.GetValue("soaSerial");
                var soaRefresh = (uint)(int)oneOption.GetValue("soaRefresh");
                var soaRetry = (uint)(int)oneOption.GetValue("soaRetry");
                var soaExpire = (uint)(int)oneOption.GetValue("soaExpire");
                var soaMinimum = (uint)(int)oneOption.GetValue("soaMinimum");

                byte[] data = Bytes.Create(
                    DnsUtil.Str2DnsName(ns),
                    DnsUtil.Str2DnsName(soaMail),
                    Util.htonl(soaSerial),
                    Util.htonl(soaRefresh),
                    Util.htonl(soaRetry),
                    Util.htonl(soaExpire),
                    Util.htonl(soaMinimum));

                Add(new OneRR(domainName, DnsType.Soa, ttl, data));
            }
        }
    }

    //指定したname及びDNS_TYPEにヒットするデータを取得する
    public List<OneRR> Search(String name, DnsType dnsType) {
        var rrList = new List<OneRR>();
        long now = DateTime.Now.Ticks;

        // 排他制御
        synchronized (lock) {
            for (OneRR oneRR : _db) {
                if (oneRR.DnsType != dnsType) continue;
                if (!oneRR.IsEffective(now))
                    continue;//生存時間超過データは使用しない
                if (oneRR.Name.ToUpper() != name.ToUpper()) continue; //大文字で比較される
                boolean find = rrList.Any(o => o.Data == oneRR.Data);//データが重複していない場合だけ、リストに追加する
                if (find) continue;
                var ttl = Util.htonl(_soaExpire);
                rrList.Add(new OneRR(oneRR.Name, oneRR.DnsType, ttl, oneRR.Data));
            }
        }// 排他制御
        return rrList;
    }

    //データが存在するかどうかだけの確認
    public boolean Find(String name, DnsType dnsType) {
        long now = Calendar.getInstance().getTimeInMillis();
        boolean ret = false;
        // 排他制御
        synchronized (lock) {
            for (OneRR oneRR : _db) {
                if (oneRR.getDnsType() != dnsType) continue;
                if (!oneRR.isEffective(now))
                    continue;//生存時間超過データは使用しない
                if (!oneRR.getName().toUpperCase().equals(name.toUpperCase())){
                	continue; //大文字で比較される
                }
                ret = true;//存在する
                break;
            }
        }// 排他制御
        return ret;
    }

    //リソースの追加
    public boolean Add(OneRR oneRR) {
        var ret = true;
        // 排他制御
        synchronized (lock) {
            for (var t : _db){
                if (t.DnsType != oneRR.DnsType) continue;
                if (t.Name != oneRR.Name) continue;
                //TTL=0のデータは普遍であるため、書き換えはしない
                if (t.Ttl2 == 0) {
                    ret = false;
                    goto end;
                }
                //まったく同じデータが既に有る場合、書き換えはしない
                if (t.Name != oneRR.Name) continue;
                if (t.DnsType != oneRR.DnsType) continue;
                boolean flg = !oneRR.Data.Where((t1, n) => t.Data[n] != t1).Any();
                if (!flg) continue;
                ret = false;
                goto end;
            }
            _db.Add(oneRR);
        end:
            ;
        }
        return ret;
    }

    public void TtlClear() {
        long now = Calendar.getInstance().getTimeInMillis();
        // 排他制御
        synchronized (lock) {
            for (int i = _db.size() - 1; i > 0; i--) {
                if (!_db.get(i).isEffective(now)) {
                    _db.remove(i);
                }
            }
        }// 排他制御
    }
}
    
