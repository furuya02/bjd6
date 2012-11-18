package bjd.util;

import java.util.Calendar;

public final class Timeout {
	private Calendar endTime;

	public Timeout(int msec) {
		endTime = Calendar.getInstance();
		endTime.add(Calendar.MILLISECOND, msec);
	}

	public boolean isFinish() {
		if (endTime.compareTo(Calendar.getInstance()) < 0) {
			return true;
		}
		return false;
	}

}
