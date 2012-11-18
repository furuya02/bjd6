package bjd.acl;

import junit.framework.Assert;

import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import bjd.ctrl.CtrlType;
import bjd.log.Logger;
import bjd.net.Ip;
import bjd.option.Dat;
import bjd.test.TestUtil;

@RunWith(Enclosed.class)
public class AclListTest {
	@RunWith(Theories.class)
	public static final class checkによる許可不許可の動作 {

		@DataPoints
		public static Fixture[] datas = {
				//コントロールの種類,デフォルト値,toRegの出力
				new Fixture("192.168.0.1", "192.168.0.1", AclKind.Allow),
				new Fixture("192.168.0.300", "192.168.0.1", AclKind.Deny), //無効リスト
				new Fixture("192.168.0.0/24", "192.168.0.1", AclKind.Allow),
				new Fixture("192.168.1.0/24", "192.168.0.1", AclKind.Deny),
				new Fixture("192.168.1.0/200", "192.168.1.0", AclKind.Deny), //無効リスト
				new Fixture("192.168.0.0-192.168.0.100", "192.168.0.1", AclKind.Allow), new Fixture("192.168.0.2-192.168.0.100", "192.168.0.1", AclKind.Deny),
				new Fixture("192.168.0.0-192.168.2.100", "192.168.0.1", AclKind.Allow),
				new Fixture("192.168.0.1-5", "192.168.0.1", AclKind.Allow), new Fixture("192.168.0.2-5", "192.168.0.1", AclKind.Deny), new Fixture("192.168.0.*", "192.168.0.1", AclKind.Allow),
				new Fixture("192.168.1.*", "192.168.0.1", AclKind.Deny),
				new Fixture("192.168.*.*", "192.168.0.1", AclKind.Allow), new Fixture("192.*.*.*", "192.168.0.1", AclKind.Allow), new Fixture("*.*.*.*", "192.168.0.1", AclKind.Allow),
				new Fixture("*", "192.168.0.1", AclKind.Allow),
				new Fixture("xxx", "192.168.0.1", AclKind.Deny), //無効リスト
				new Fixture("172.*.*.*", "192.168.0.1", AclKind.Deny), };

		static class Fixture {
			private String aclStr;
			private String ip;
			private AclKind expected;

			public Fixture(String aclStr, String ip, AclKind expected) {
				this.aclStr = aclStr;
				this.ip = ip;
				this.expected = expected;
			}

			public String toString() {
				return String.format("new AclV4(%s) => check(%s)=%s", aclStr, ip, expected);
			}

		}

		@Theory
		public void test(Fixture fx) {

			TestUtil.prompt(fx.toString());

			Ip ip = TestUtil.createIp(fx.ip);
			Dat dat = new Dat(new CtrlType[] { CtrlType.TEXTBOX, CtrlType.ADDRESSV4 });
			if (!dat.add(true, String.format("NAME\t%s", fx.aclStr))) {
				Assert.fail("このエラーが発生したら、テストの実装に問題がある");
			}

			int enableNum = 0; //enableNum=0 のみを許可する
			AclList sut = new AclList(dat, enableNum, new Logger());
			Assert.assertEquals(sut.check(ip), fx.expected);

			enableNum = 1; //enableNum=1 のみを禁止する
			sut = new AclList(dat, enableNum, new Logger());
			//ACLは逆転する
			AclKind expected = (fx.expected == AclKind.Allow) ? AclKind.Deny : AclKind.Allow;
			Assert.assertEquals(sut.check(ip), expected);

		}
	}
}
