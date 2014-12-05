package com.yangc.ichat.bean;

public class EmojiBean {

	private int resId;
	private String name;
	private String content;

	public EmojiBean() {
	}

	public EmojiBean(int resId, String name, String content) {
		this.resId = resId;
		this.name = name;
		this.content = content;
	}

	public int getResId() {
		return resId;
	}

	public void setResId(int resId) {
		this.resId = resId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
