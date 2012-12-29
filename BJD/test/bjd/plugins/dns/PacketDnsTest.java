package bjd.plugins.dns;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import bjd.net.Ip;
import bjd.test.TestUtil;

public final class PacketDnsTest {

	//set type=a で www.google.com をリクエストした時のレスポンス
	private String str0 = "0003818000010000000400040377777706676f6f676c6503636f6d00001c0001c01000020001000145c80006036e7334c010c01000020001000145c80006036e7332c010c01000020001000145c80006036e7333c010c01000020001000145c80006036e7331c010c06200010001000146090004d8ef200ac03e000100010001466b0004d8ef220ac05000010001000146090004d8ef240ac02c00010001000146090004d8ef260a";
	//set type=mx で gmail.comをリクエストした時のレスポンス
	private String str1 = "00028180000100050004000705676d61696c03636f6d00000f0001c00c000f000100000d630020000a04616c74310d676d61696c2d736d74702d696e016c06676f6f676c65c012c00c000f000100000d630009001404616c7432c02ec00c000f000100000d630009001e04616c7433c02ec00c000f000100000d630009002804616c7434c02ec00c000f000100000d6300040005c02ec00c000200010000d48d0006036e7334c03ec00c000200010000d48d0006036e7332c03ec00c000200010000d48d0006036e7331c03ec00c000200010000d48d0006036e7333c03ec02e000100010000012700044a7d191bc055000100010000003c00044a7d8c1bc07f000100010000009500044a7d831bc0c6000100010000d5380004d8ef200ac0b4000100010000d5320004d8ef220ac0d8000100010000d5710004d8ef240ac0a2000100010000d4f80004d8ef260a";
	//set type=soa で nifty.comをリクエストした時のレスポンス
	private String str2 = "000481800001000100020002056e6966747903636f6d0000060001c00c00060001000006160033046f6e7330056e69667479026164026a70000a686f73746d6173746572c02c0bfe412800000e10000003840036ee8000000384c00c00020001000006d20002c027c00c00020001000006d20007046f6e7331c02cc02700010001000007120004caf8254dc07400010001000006da0004caf8149c";

	//set type=a で www.google.com をリクエストした時のレスポンス
	private String str3 = "0005818000010000000400040377777706676f6f676c6503636f6d00001c0001c0100002000100000a7d0006036e7333c010c0100002000100000a7d0006036e7331c010c0100002000100000a7d0006036e7334c010c0100002000100000a7d0006036e7332c010c03e0001000100000b5e0004d8ef200ac0620001000100000bde0004d8ef220ac02c0001000100000af50004d8ef240ac0500001000100000ab30004d8ef260a";

	@Test
	public void パケット解釈_str0() throws Exception {
		//exercise
		PacketDns sut = new PacketDns(TestUtil.hexStream2Bytes(str0));
		//verify
		assertThat(sut.getId(), is((short) 0x0003));
		assertThat(sut.getCount(RrKind.QD), is(1));
		assertThat(sut.getCount(RrKind.AN), is(0));
		assertThat(sut.getCount(RrKind.NS), is(4));
		assertThat(sut.getCount(RrKind.AR), is(4));
		assertThat(sut.getRcode(), is((short) 0));
		assertThat(sut.getAA(), is(false));
		assertThat(sut.getRd(), is(true));
		assertThat(sut.getDnsType(), is(DnsType.Aaaa));
		assertThat(sut.getRequestName(), is("www.google.com."));
		assertThat(sut.getRR(RrKind.QD, 0).toString(), is((new RrQuery("www.google.com.", DnsType.Aaaa)).toString()));
		assertThat(sut.getRR(RrKind.NS, 0).toString(), is((new RrNs("google.com.", 83400, "ns4.google.com.")).toString()));
		assertThat(sut.getRR(RrKind.NS, 1).toString(), is((new RrNs("google.com.", 83400, "ns2.google.com.")).toString()));
		assertThat(sut.getRR(RrKind.NS, 2).toString(), is((new RrNs("google.com.", 83400, "ns3.google.com.")).toString()));
		assertThat(sut.getRR(RrKind.NS, 3).toString(), is((new RrNs("google.com.", 83400, "ns1.google.com.")).toString()));
		assertThat(sut.getRR(RrKind.AR, 0).toString(), is((new RrA("ns1.google.com.", 83465, new Ip("216.239.32.10"))).toString()));
		assertThat(sut.getRR(RrKind.AR, 1).toString(), is((new RrA("ns2.google.com.", 83563, new Ip("216.239.34.10"))).toString()));
		assertThat(sut.getRR(RrKind.AR, 2).toString(), is((new RrA("ns3.google.com.", 83465, new Ip("216.239.36.10"))).toString()));
		assertThat(sut.getRR(RrKind.AR, 3).toString(), is((new RrA("ns4.google.com.", 83465, new Ip("216.239.38.10"))).toString()));
	}

	@Test
	public void パケット解釈_str1() throws Exception {
		//exercise
		PacketDns sut = new PacketDns(TestUtil.hexStream2Bytes(str1));
		//verify
		assertThat(sut.getId(), is((short) 0x0002));
		assertThat(sut.getCount(RrKind.QD), is(1));
		assertThat(sut.getCount(RrKind.AN), is(5));
		assertThat(sut.getCount(RrKind.NS), is(4));
		assertThat(sut.getCount(RrKind.AR), is(7));
		assertThat(sut.getRcode(), is((short) 0));
		assertThat(sut.getAA(), is(false));
		assertThat(sut.getRd(), is(true));
		assertThat(sut.getDnsType(), is(DnsType.Mx));
		assertThat(sut.getRequestName(), is("gmail.com."));
		assertThat(sut.getRR(RrKind.QD, 0).toString(), is((new RrQuery("gmail.com.", DnsType.Mx)).toString()));
		assertThat(sut.getRR(RrKind.AN, 0).toString(), is((new RrMx("gmail.com.", 3427, (short) 10, "alt1.gmail-smtp-in.l.google.com.")).toString()));
		assertThat(sut.getRR(RrKind.AN, 1).toString(), is((new RrMx("gmail.com.", 3427, (short) 20, "alt2.gmail-smtp-in.l.google.com.")).toString()));
		assertThat(sut.getRR(RrKind.AN, 2).toString(), is((new RrMx("gmail.com.", 3427, (short) 30, "alt3.gmail-smtp-in.l.google.com.")).toString()));
		assertThat(sut.getRR(RrKind.AN, 3).toString(), is((new RrMx("gmail.com.", 3427, (short) 40, "alt4.gmail-smtp-in.l.google.com.")).toString()));
		assertThat(sut.getRR(RrKind.AN, 4).toString(), is((new RrMx("gmail.com.", 3427, (short) 5, "gmail-smtp-in.l.google.com.")).toString()));
		assertThat(sut.getRR(RrKind.NS, 0).toString(), is((new RrNs("gmail.com.", 54413, "ns4.google.com.")).toString()));
		assertThat(sut.getRR(RrKind.NS, 1).toString(), is((new RrNs("gmail.com.", 54413, "ns2.google.com.")).toString()));
		assertThat(sut.getRR(RrKind.NS, 2).toString(), is((new RrNs("gmail.com.", 54413, "ns1.google.com.")).toString()));
		assertThat(sut.getRR(RrKind.NS, 3).toString(), is((new RrNs("gmail.com.", 54413, "ns3.google.com.")).toString()));
		assertThat(sut.getRR(RrKind.AR, 0).toString(), is((new RrA("gmail-smtp-in.l.google.com.", 295, new Ip("74.125.25.27"))).toString()));
		assertThat(sut.getRR(RrKind.AR, 1).toString(), is((new RrA("alt2.gmail-smtp-in.l.google.com.", 60, new Ip("74.125.140.27"))).toString()));
		assertThat(sut.getRR(RrKind.AR, 2).toString(), is((new RrA("alt4.gmail-smtp-in.l.google.com.", 149, new Ip("74.125.131.27"))).toString()));
		assertThat(sut.getRR(RrKind.AR, 3).toString(), is((new RrA("ns1.google.com.", 54584, new Ip("216.239.32.10"))).toString()));
		assertThat(sut.getRR(RrKind.AR, 4).toString(), is((new RrA("ns2.google.com.", 54578, new Ip("216.239.34.10"))).toString()));
		assertThat(sut.getRR(RrKind.AR, 5).toString(), is((new RrA("ns3.google.com.", 54641, new Ip("216.239.36.10"))).toString()));
		assertThat(sut.getRR(RrKind.AR, 6).toString(), is((new RrA("ns4.google.com.", 54520, new Ip("216.239.38.10"))).toString()));
	}

	@Test
	public void パケット解釈_str2() throws Exception {
		//exercise
		PacketDns sut = new PacketDns(TestUtil.hexStream2Bytes(str2));
		//verify
		assertThat(sut.getId(), is((short) 0x0004));
		assertThat(sut.getCount(RrKind.QD), is(1));
		assertThat(sut.getCount(RrKind.AN), is(1));
		assertThat(sut.getCount(RrKind.NS), is(2));
		assertThat(sut.getCount(RrKind.AR), is(2));
		assertThat(sut.getRcode(), is((short) 0));
		assertThat(sut.getAA(), is(false));
		assertThat(sut.getRd(), is(true));
		assertThat(sut.getDnsType(), is(DnsType.Soa));
		assertThat(sut.getRequestName(), is("nifty.com."));
		assertThat(sut.getRR(RrKind.QD, 0).toString(), is((new RrQuery("nifty.com.", DnsType.Soa)).toString()));
		assertThat(sut.getRR(RrKind.AN, 0).toString(), is((new RrSoa("nifty.com.", 0x616, "ons0.nifty.ad.jp", "hostmaster.nifty.ad.jp", 0x0bfe4128, 0xe10, 0x384, 0x36ee80, 0x384)).toString()));
		assertThat(sut.getRR(RrKind.NS, 0).toString(), is((new RrNs("nifty.com.", 0x6d2, "ons0.nifty.ad.jp.")).toString()));
		assertThat(sut.getRR(RrKind.NS, 1).toString(), is((new RrNs("nifty.com.", 0x6d2, "ons1.nifty.ad.jp.")).toString()));
		assertThat(sut.getRR(RrKind.AR, 0).toString(), is((new RrA("ons0.nifty.ad.jp.", 0x712, new Ip("202.248.37.77"))).toString()));
		assertThat(sut.getRR(RrKind.AR, 1).toString(), is((new RrA("ons1.nifty.ad.jp.", 0x6da, new Ip("202.248.20.156"))).toString()));
	}

	@Test
	public void パケット生成_A_NS() throws Exception {
		//setUp

		//exercise
		//パケットの生成
		short id = 0x0005;
		boolean qr = true; //応答
		boolean rd = true; //再帰要求(有効)
		boolean aa = false; //権限(なし)
		boolean ra = true;
		PacketDns sut = new PacketDns(id, qr, aa, rd, ra);
		sut.addRR(RrKind.QD, new RrQuery("www.google.com.", DnsType.Aaaa));
		sut.addRR(RrKind.NS, new RrNs("google.com.", 0xa7d, "ns3.google.com."));
		sut.addRR(RrKind.NS, new RrNs("google.com.", 0xa7d, "ns1.google.com."));
		sut.addRR(RrKind.NS, new RrNs("google.com.", 0xa7d, "ns4.google.com."));
		sut.addRR(RrKind.NS, new RrNs("google.com.", 0xa7d, "ns2.google.com."));
		sut.addRR(RrKind.AR, new RrA("ns1.google.com.", 0xb5e, new Ip("216.239.32.10")));
		sut.addRR(RrKind.AR, new RrA("ns2.google.com.", 0xbde, new Ip("216.239.34.10")));
		sut.addRR(RrKind.AR, new RrA("ns3.google.com.", 0xaf5, new Ip("216.239.36.10")));
		sut.addRR(RrKind.AR, new RrA("ns4.google.com.", 0xab3, new Ip("216.239.38.10")));
		//生成したパケットのバイト配列で、再度パケットクラスを生成する
		PacketDns p = new PacketDns(sut.getBytes());

		//verify
		assertThat(p.getAA(), is(false));
		assertThat(p.getId(), is((short) 0x0005));
		assertThat(p.getDnsType(), is(DnsType.Aaaa));
		assertThat(p.getCount(RrKind.QD), is(1));
		assertThat(p.getCount(RrKind.AN), is(0));
		assertThat(p.getCount(RrKind.NS), is(4));
		assertThat(p.getCount(RrKind.AR), is(4));
		assertThat(p.getRcode(), is((short) 0));
		assertThat(p.getRR(RrKind.NS, 0).toString(), is(new RrNs("google.com.", 0xa7d, "ns3.google.com.").toString()));
		assertThat(p.getRR(RrKind.NS, 1).toString(), is(new RrNs("google.com.", 0xa7d, "ns1.google.com.").toString()));
		assertThat(p.getRR(RrKind.NS, 2).toString(), is(new RrNs("google.com.", 0xa7d, "ns4.google.com.").toString()));
		assertThat(p.getRR(RrKind.NS, 3).toString(), is(new RrNs("google.com.", 0xa7d, "ns2.google.com.").toString()));

		assertThat(p.getRR(RrKind.AR, 0).toString(), is(new RrA("ns1.google.com.", 0xb5e, new Ip("216.239.32.10")).toString()));
		assertThat(p.getRR(RrKind.AR, 1).toString(), is(new RrA("ns2.google.com.", 0xbde, new Ip("216.239.34.10")).toString()));
		assertThat(p.getRR(RrKind.AR, 2).toString(), is(new RrA("ns3.google.com.", 0xaf5, new Ip("216.239.36.10")).toString()));
		assertThat(p.getRR(RrKind.AR, 3).toString(), is(new RrA("ns4.google.com.", 0xab3, new Ip("216.239.38.10")).toString()));
	}

	@Test
	public void パケット生成_MX() throws Exception {
		//setUp

		//exercise
		//パケットの生成
		short id = (short) 0xf00f;
		boolean qr = true; //応答
		boolean rd = false; //再帰要求(有効)
		boolean aa = true; //権限(あり)
		boolean ra = true;
		PacketDns sut = new PacketDns(id, qr, aa, rd, ra);
		sut.addRR(RrKind.QD, new RrQuery("google.com.", DnsType.Mx));
		sut.addRR(RrKind.AN, new RrMx("google.com.", 0xa7d, (short) 10, "smtp.google.com."));
		sut.addRR(RrKind.NS, new RrNs("google.com.", 0xa7d, "ns3.google.com."));
		//生成したパケットのバイト配列で、再度パケットクラスを生成する
		byte[] b = sut.getBytes();
		PacketDns p = new PacketDns(b);

		//verify
		assertThat(p.getAA(), is(true));
		assertThat(p.getId(), is((short) 0xf00f));
		assertThat(p.getDnsType(), is(DnsType.Mx));
		assertThat(p.getCount(RrKind.QD), is(1));
		assertThat(p.getCount(RrKind.AN), is(1));
		assertThat(p.getCount(RrKind.NS), is(1));
		assertThat(p.getCount(RrKind.AR), is(0));
		assertThat(p.getRcode(), is((short) 0));
		assertThat(p.getRR(RrKind.AN, 0).toString(), is(new RrMx("google.com.", 0xa7d, (short) 10, "smtp.google.com.").toString()));
		assertThat(p.getRR(RrKind.NS, 0).toString(), is(new RrNs("google.com.", 0xa7d, "ns3.google.com.").toString()));
		//		assertThat(p.getRR(RrKind.NS, 1).toString(), is(new RrNs("google.com.", 0xa7d, "ns1.google.com.").toString()));
		//		assertThat(p.getRR(RrKind.NS, 2).toString(), is(new RrNs("google.com.", 0xa7d, "ns4.google.com.").toString()));
		//		assertThat(p.getRR(RrKind.NS, 3).toString(), is(new RrNs("google.com.", 0xa7d, "ns2.google.com.").toString()));
		//
		//		assertThat(p.getRR(RrKind.AR, 0).toString(), is(new RrA("ns1.google.com.", 0xb5e, new Ip("216.239.32.10")).toString()));
		//		assertThat(p.getRR(RrKind.AR, 1).toString(), is(new RrA("ns2.google.com.", 0xbde, new Ip("216.239.34.10")).toString()));
		//		assertThat(p.getRR(RrKind.AR, 2).toString(), is(new RrA("ns3.google.com.", 0xaf5, new Ip("216.239.36.10")).toString()));
		//		assertThat(p.getRR(RrKind.AR, 3).toString(), is(new RrA("ns4.google.com.", 0xab3, new Ip("216.239.38.10")).toString()));
	}

}
