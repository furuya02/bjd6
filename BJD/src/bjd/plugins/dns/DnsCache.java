package bjd.plugins.dns;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import bjd.ValidObjException;
import bjd.log.LogKind;
import bjd.log.Logger;
import bjd.net.InetKind;
import bjd.net.Ip;
import bjd.net.IpKind;
import bjd.option.Dat;
import bjd.option.OneDat;
import bjd.option.OneOption;
import bjd.util.Bytes;
import bjd.util.Msg;
import bjd.util.MsgKind;
import bjd.util.Util;

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
        File file = new File(fileName);
        
        if (file.exists()) {
            //using (var sr = new StreamReader(fileName, Encoding.GetEncoding("Shift_JIS"))) {
        	ArrayList<String> lines = Util.textFileRead(file);
            String tmpName = "";//全行のNAMEを保持する　NAMEは前行と同じ場合省略が可能
            for(String str : lines){
                String name = "";
                //String Class = "IN";
                DnsType dnsType = DnsType.Unknown;
                //;以降はコメントとして削除する
                int i = str.indexOf(";");
                if (i != -1){
                    str = str.substring(0, i);
                }

                //空の行は処理しない
                if (str.length() == 0){
                    continue;
                }

                //空白・タブを削除して、パラメータをtmp2へ取得する
                //var tmp = str.Split(new[] { ' ', '\t' });
                //var tmp2 = tmp.Where(s => s != "").ToList();
                String [] tmp = str.split("[ \t]");
                ArrayList<String> tmp2 = new ArrayList<>();
                for(String s : tmp){
                	if(!s.equals("")){
                		tmp2.add(s);
                	}
                }

                //************************************************
                //タイプだけは省略することができないので、それを基準にサーチする
                //************************************************
                int typeCol = 0;
                for (; typeCol<tmp2.size() ; typeCol++) {
                    //for (DnsType t : Enum.GetValues(typeof(DnsType))) {
                	for (DnsType t : DnsType.values()) {
                        if (!tmp2.get(typeCol).equals(t.toString().toUpperCase())){
                        	continue;
                        }
                        dnsType = t;
                        break;
                    }
                    if (dnsType != DnsType.Unknown){
                        break;
                    }
                }
                if (dnsType == DnsType.Unknown){
                	throw new IllegalArgumentException(String.format("タイプ名に矛盾があります [file=%s str=%s]", fileName, str));
                }

                //タイプの次がDATAとなる
                if (typeCol + 1 >= tmp2.size()){
                	throw new IllegalArgumentException(String.format("タイプの次にカラム（DATA）が存在しない [file=%s str=%s]", fileName, str));
                }
                String dataStr = tmp2.get(typeCol + 1);

                //************************************************
                //クラス(IN)が含まれているかどうかをサーチする
                //************************************************
                int classCol = 0;
                boolean find = false;
                for (; classCol < tmp2.size(); classCol++){
                    if (!tmp2.get(classCol).equals("")){
                    	continue;
                    }
                    find=true;
                    break;
                }
                if(!find){
                	classCol = -1;
                }
                //クラスが含まれた場合、そのカラムはclassColに保存されている
                //含まれていない場合 classCol=-1

                if (typeCol == 1) {
                    if (classCol == -1) { //INが無い場合
                        //０番目はNAME若しくはTTLとなる
                        if (str.substring(0, 1).equals(" ") || str.substring(0, 1).equals("\t")) {
                            //名前は省略されているので
                            //ttl = Convert.ToUInt32(tmp2[0]);
                            ttl = Integer.valueOf(tmp2.get(0));
                            ttl = Util.htonl(ttl);
                        } else {
                            name = tmp2.get(0);
                        }
                    } else { //INが有る場合
                    	//0番目はINであるので、名前もTTLも省略されている
                        if (classCol != 0){
                        	throw new IllegalArgumentException(String.format("INの位置に矛盾がありま [file=%s str=%s]", fileName, str));
                        }
                    }
                } else if (typeCol == 2) {
                    if (classCol == -1) { //INが無い場合
                        //０番目はNAME、1番目はTTLとなる
                        name = tmp2.get(0);
                        //ttl = Convert.ToUInt32(tmp2[1]);
                        ttl = Integer.valueOf(tmp2.get(1));
                        ttl = Util.htonl(ttl);
                    } else { //INが有る場合
                        if (classCol != 1){
                        	throw new IllegalArgumentException(String.format("INの位置に矛盾がありま [file=%s str=%s]", fileName, str));
                        }
                        //０番目はNAME若しくはTTLとなる
                        if (str.substring(0, 1).equals(" ")|| str.substring(0, 1).equals("\t")) {
                            //名前は省略されているので
                            //ttl = Convert.ToUInt32(tmp2[0]);
                            ttl = Integer.valueOf(tmp2.get(0));
                            ttl = Util.htonl(ttl);
                        } else {
                            name = tmp2.get(0);
                        }
                    }
                } else if (typeCol == 3) {
                    if (classCol == -1) { //INが無い場合
                    	throw new IllegalArgumentException(String.format("カラムが不足している [file=%s str=%s]", fileName, str));
                    } 
                    //INが有る場合
                    if (classCol != 2){
                    	throw new IllegalArgumentException(String.format("INの位置に矛盾がありま [file=%s str=%s]", fileName, str));
                    }
                    //０番目はNAME、1番目はTTLとなる
                    name = tmp2.get(0);
                    //ttl = Convert.ToUInt32(tmp2[1]);
                    ttl = Integer.valueOf(tmp2.get(1));
                    ttl = Util.htonl(ttl);
                }

                //*********************************************
                //nameの補完
                //*********************************************
                if (name == "@") { //@の場合
                    name = domainName;
                } else if (name.lastIndexOf(".") != name.length() - 1) { //最後に.がついていない場合、ドメイン名を追加する
                    name = name + "." + domainName + ".";
                } else if (name == "") {
                    name = tmpName;//前行と同じ
                }
                tmpName = name;//前行分として記憶する

                //*********************************************
                //String sataStr を変換してデータベースに追加
                //*********************************************
                if (dnsType == DnsType.A) {
					try {
						Ip ipV4 = new Ip(dataStr);
	                    Add(new OneRR(name, dnsType, ttl, ipV4.getIpV4()));
					} catch (ValidObjException e) {
	                	throw new IllegalArgumentException(String.format("Ipアドレスに矛盾があります [ip=%s file=%s str=%s]", dataStr,fileName, str));
					}
                } else if (dnsType == DnsType.Ns) {
                    Add(new OneRR(name, dnsType, ttl, DnsUtil.str2DnsName(dataStr)));
                } else if (dnsType == DnsType.Aaaa) {
					try {
						Ip ipV6 = new Ip(dataStr);
	                    Add(new OneRR(name, dnsType, ttl, ipV6.getIpV6()));
					} catch (ValidObjException e) {
	                	throw new IllegalArgumentException(String.format("Ipアドレスに矛盾があります [ip=%s file=%s str=%s]", dataStr,fileName, str));
					}
                } else {
                	throw new IllegalArgumentException(String.format("name.caには、タイプA,AAAA及びNS以外は使用できません [file=%s str=%s]", fileName, str));
                }
            }
        }
        //locaohostレコードの追加
        Ip ip = new Ip(IpKind.V4_LOCALHOST);
        Add(new OneRR("localhost.", DnsType.A, ttl, ip.getIpV4()));
        Add(new OneRR("1.0.0.127.in-addr.arpa.", DnsType.Ptr, ttl, DnsUtil.str2DnsName("localhost")));

        ip = new Ip(IpKind.V6_LOCALHOST);
        Add(new OneRR("localhost.", DnsType.Aaaa, ttl, ip.getIpV6()));
        Add(new OneRR("1.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.IP6.ARPA.", DnsType.Ptr, ttl, DnsUtil.str2DnsName("localhost")));
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
                //var type = Convert.ToInt32(o.StrList[0]);
                int type = Integer.valueOf(o.getStrList().get(0));
                String name = o.getStrList().get(1);
                String alias = o.getStrList().get(2);
                var ip = new Ip(o.StrList[3]);
                var n = Convert.ToInt32(o.StrList[4]);

                Dnstype dnsType = DnsType.A;
                if (type == 1) {
                    dnsType = DnsType.Ns;
                } else if (type == 2) {
                    dnsType = DnsType.Mx;
                } else if (type == 3) {
                    dnsType = DnsType.Cname;
                } else if (type == 4) {
                    dnsType = DnsType.Aaaa;
                }
                short priority = (short)n;
                //uint addr = ip.AddrV4;        //class Ip -> uint;

                //最後に.がついていない場合、ドメイン名を追加する
                if (name.lastIndexOf('.') != name.length() - 1) {
                    name = name + "." + domainName;
                }
                if (alias.lastIndexOf('.') != alias.length() - 1) {
                    alias = alias + "." + domainName;
                }

                //CNAME以外は、PTRレコードを自動的に生成する
                if (dnsType != DnsType.Cname) {
                    //PTR名を作成 [例] 192.168.0.1 -> 1.0.168.192.in-addr.arpa;
                    if (ip.InetKind == InetKind.V4) { //IPv4
                        String ptrName = String.Format("%d.%d.%d.%d.in-addr.arpa.", ip.IpV4[3], ip.IpV4[2], ip.IpV4[1], ip.IpV4[0]);
                        Add(new OneRR(ptrName, DnsType.Ptr, ttl, DnsUtil.str2DnsName(name)));
                    } else { //IPv6
                        StringBuilder sb = new StringBuilder();
                        for (byte a : ip.IpV6) {
                            sb.append(String.format("%4d", a));
                        }
                        String ipStr = sb.toString();
                        if (ipStr.length() == 32) {
                            sb = new StringBuilder();
                            for (int e = 31; e >= 0; e--) {
                                sb.append(ipStr.charAt(e));
                                sb.append('.');
                            }
                            Add(new OneRR(sb + "ip6.arpa.", DnsType.Ptr, ttl, DnsUtil.str2DnsName(name)));
                        }
                    }
                }

                //データベースへの追加
                if (dnsType == DnsType.A) {
                    if (ip.InetKind == InetKind.V4) {
                        //ネットワークバイト配列の取得
                        Add(new OneRR(name, DnsType.A, ttl, ip.NetBytes()));
                    } else {
                        logger.Set(LogKind.ERROR, null, 19, String.Format("address {0}", ip));
                    }
                } else if (dnsType == DnsType.Aaaa) {
                    if (ip.InetKind == InetKind.V6) {
                        Add(new OneRR(name, DnsType.Aaaa, ttl, ip.NetBytes()));
                    } else {
                        logger.Set(LogKind.ERROR, null, 20, String.Format("address {0}", ip));
                    }
                } else if (dnsType == DnsType.Ns) {
                    ns = name;//SOA追加時に使用するため、ネームサーバの名前を保存する

                    // A or AAAAレコードも追加
                    Add(new OneRR(name, (ip.InetKind == InetKind.V4) ? DnsType.A : DnsType.Aaaa, ttl, ip.NetBytes()));

                    Add(new OneRR(domainName, DnsType.Ns, ttl, DnsUtil.str2DnsName(name)));
                } else if (dnsType == DnsType.Mx) {
                    // A or AAAAレコードも追加
                    Add(new OneRR(name, DnsType.A, ttl, ip.NetBytes()));

                    //プライオリィ
                    byte[] dataName = DnsUtil.str2DnsName(name);//DNS名前形式に変換
                    byte[] data = Bytes.create(Util.htons(priority), dataName);
                    Add(new OneRR(domainName, DnsType.Mx, ttl, data));
                } else if (dnsType == DnsType.Cname) {
                    Add(new OneRR(alias, DnsType.Cname, ttl, DnsUtil.str2DnsName(name)));
                }
            }

            //SOAレコードの追加
            if (ns != "") { //NSサーバ名が必須
            	String soaMail = (String)oneOption.getValue("soaMail");
                soaMail = soaMail.replace('@', '.');//@を.に置き換える
                soaMail = soaMail + ".";//最後に.を追加する
                int soaSerial = (int)oneOption.getValue("soaSerial");
                int soaRefresh = (int)oneOption.getValue("soaRefresh");
                int soaRetry = (int)oneOption.getValue("soaRetry");
                int soaExpire = (int)oneOption.getValue("soaExpire");
                int soaMinimum = (int)oneOption.getValue("soaMinimum");

                byte[] data = Bytes.create(
                    DnsUtil.str2DnsName(ns),
                    DnsUtil.str2DnsName(soaMail),
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
    
