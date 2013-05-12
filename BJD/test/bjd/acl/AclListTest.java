package bjd.acl;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
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
				new Fixture("192.168.0.0-192.168.0.100", "192.168.0.1", AclKind.Allow),
				new Fixture("192.168.0.2-192.168.0.100", "192.168.0.1", AclKind.Deny),
				new Fixture("192.168.0.0-192.168.2.100", "192.168.0.1", AclKind.Allow),
				new Fixture("192.168.0.1-5", "192.168.0.1", AclKind.Allow),
				new Fixture("192.168.0.2-5", "192.168.0.1", AclKind.Deny),
				new Fixture("192.168.0.*", "192.168.0.1", AclKind.Allow),
				new Fixture("192.168.1.*", "192.168.0.1", AclKind.Deny),
				new Fixture("192.168.*.*", "192.168.0.1", AclKind.Allow),
				new Fixture("192.*.*.*", "192.168.0.1", AclKind.Allow),
				new Fixture("*.*.*.*", "192.168.0.1", AclKind.Allow),
				new Fixture("*", "192.168.0.1", AclKind.Allow),
				new Fixture("xxx", "192.168.0.1", AclKind.Deny), //無効リスト
				new Fixture("172.*.*.*", "192.168.0.1", AclKind.Deny), };

		static class Fixture {
			private AclKind expected;
			private Ip ip;
			private Dat dat;

			public Fixture(String aclStr, String ipStr, AclKind expected) {
				this.expected = expected;
				ip = TestUtil.createIp(ipStr);
				dat = new Dat(new CtrlType[] { CtrlType.TEXTBOX, CtrlType.ADDRESSV4 });
				if (!dat.add(true, String.format("NAME\t%s", aclStr))) {
					Assert.fail("このエラーが発生したら、テストの実装に問題がある");
				}
			}
		}

		@Theory
		public void enableNum_0で_のみを許可する_を検証する(Fixture fx) throws Exception {
			//setUp
			AclKind expected = fx.expected;
			int enableNum = 0; //enableNum=0 のみを許可する
			AclList sut = new AclList(fx.dat, enableNum, new Logger());

			//exercise
			AclKind actual = sut.check(fx.ip);
			//verify
			assertThat(actual, is(expected));
		}

		@Theory
		public void enableNum_1で_のみを禁止する_を検証する(Fixture fx) throws Exception {
			//setUp
			//ACLは逆転する
			AclKind expected = (fx.expected == AclKind.Allow) ? AclKind.Deny : AclKind.Allow;
			int enableNum = 1; //enableNum=1 のみを禁止する
			AclList sut = new AclList(fx.dat, enableNum, new Logger());

			//exercise
			AclKind actual = sut.check(fx.ip);
			//verify
			assertThat(actual, is(expected));
		}
	}
}
