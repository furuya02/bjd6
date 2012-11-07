package bjd.log;

import java.util.ArrayList;

import bjd.sock.SockObj;

public final class TmpLogger extends Logger {
	
	private ArrayList<LogTemporary> ar = new ArrayList<>();	
	public TmpLogger() {
		
	}
	
	@Override
	public void set(LogKind logKind, SockObj sockObj, int messageNo, String detailInfomation) {
		ar.add(new LogTemporary(logKind, sockObj, messageNo, detailInfomation));
	}
	
	final class LogTemporary {
		private LogKind logKind;
		private SockObj sockObj;
		private int messageNo;
		private String detailInfomation;
		
		public LogKind getLogKind() {
			return logKind;
		}
		public SockObj getSockObj() {
			return sockObj;
		}
		public int getMessageNo() {
			return messageNo;
		}
		public String getDetailInfomation() {
			return detailInfomation;
		}

		public LogTemporary(LogKind logKind, SockObj sockObj, int messageNo, String detailInfomation) {
			this.logKind = logKind;
			this.sockObj = sockObj;
			this.messageNo = messageNo;
			this.detailInfomation = detailInfomation;
		}
	}
}
