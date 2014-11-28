package com.yangc.ichat.comm.bean;

import java.io.Serializable;

public class ResultBean implements Serializable {

	private static final long serialVersionUID = 6489707464322017507L;

	private String uuid;
	private boolean success;
	private String data;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
