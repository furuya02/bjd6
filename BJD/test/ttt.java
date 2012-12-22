import java.util.HashSet;

import org.junit.Test;

public final class ttt {

	@Test
	public void test() {

		System.out.println(String.format("%s", func("12本じてゃ本34567890")));
	}

	boolean func(String str) {
		HashSet set = new HashSet();
		for (int i = 0; i < str.length(); i++) {
			if (!set.add(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

}
