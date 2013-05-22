package bjd.trace;

import bjd.net.Ip;

final class OneTrace {
	public TraceKind getTraceKind() {
		return traceKind;
	}

	public String getStr() {
		return str;
	}

	public int getThreadId() {
		return threadId;
	}

	public Ip getIp() {
		return ip;
	}

	private TraceKind traceKind;
	private String str;
	private int threadId;
	private Ip ip;

	public OneTrace(TraceKind traceKind, String str, int threadId, Ip ip) {
		this.traceKind = traceKind;
		this.str = str;
		this.threadId = threadId;
		this.ip = ip;
	}
}
