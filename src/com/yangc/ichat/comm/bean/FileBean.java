package com.yangc.ichat.comm.bean;

public class FileBean extends ChatBean {

	private String fileName;
	private Long fileSize;
	private String fileMd5;

	private byte contentType;
	private int offset;
	private byte[] data;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileMd5() {
		return fileMd5;
	}

	public void setFileMd5(String fileMd5) {
		this.fileMd5 = fileMd5;
	}

	public byte getContentType() {
		return contentType;
	}

	public void setContentType(byte contentType) {
		this.contentType = contentType;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

}
