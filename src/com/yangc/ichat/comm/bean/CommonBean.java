package com.yangc.ichat.comm.bean;

import java.io.Serializable;

public class CommonBean implements Serializable {

	private static final long serialVersionUID = 7843755906977764001L;

	private String uuid;
	private String from;
	private String to;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

}
