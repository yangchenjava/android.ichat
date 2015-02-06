package com.yangc.ichat.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

public class MultipartEntity implements HttpEntity {

	private static final char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

	private String boundary = null;

	private ByteArrayOutputStream out = new ByteArrayOutputStream();
	private boolean isSetLast = false;
	private boolean isSetFirst = false;

	public MultipartEntity() {
		final StringBuilder sb = new StringBuilder();
		final Random rand = new Random();
		for (int i = 0; i < 30; i++) {
			sb.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
		}
		this.boundary = sb.toString();

	}

	public void writeFirstBoundaryIfNeeds() {
		if (!this.isSetFirst) {
			try {
				this.out.write(("--" + this.boundary + "\r\n").getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.isSetFirst = true;
	}

	public void writeLastBoundaryIfNeeds() {
		if (!this.isSetLast) {
			try {
				this.out.write(("\r\n--" + this.boundary + "--\r\n").getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.isSetLast = true;
	}

	public void addPart(String key, String value) {
		this.writeFirstBoundaryIfNeeds();
		try {
			this.out.write(("Content-Disposition: form-data; name=\"" + key + "\"\r\n").getBytes());
			this.out.write("Content-Type: text/plain; charset=UTF-8\r\n".getBytes());
			this.out.write("Content-Transfer-Encoding: 8bit\r\n\r\n".getBytes());
			this.out.write(value.getBytes());
			this.out.write(("\r\n--" + this.boundary + "\r\n").getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addPart(String key, File value) {
		try {
			this.addPart(key, value.getName(), new FileInputStream(value));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void addPart(String key, String fileName, InputStream in) {
		this.addPart(key, fileName, in, "application/octet-stream");
	}

	public void addPart(String key, String fileName, InputStream in, String type) {
		this.writeFirstBoundaryIfNeeds();
		try {
			this.out.write(("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + fileName + "\"\r\n").getBytes());
			this.out.write(("Content-Type: " + type + "\r\n").getBytes());
			this.out.write("Content-Transfer-Encoding: binary\r\n\r\n".getBytes());

			byte[] b = new byte[4096];
			int len = 0;
			while ((len = in.read(b)) != -1) {
				this.out.write(b, 0, len);
			}
			this.out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean isRepeatable() {
		return false;
	}

	@Override
	public boolean isChunked() {
		return false;
	}

	@Override
	public long getContentLength() {
		writeLastBoundaryIfNeeds();
		return out.toByteArray().length;
	}

	@Override
	public Header getContentType() {
		return new BasicHeader("Content-Type", "multipart/form-data; boundary=" + this.boundary);
	}

	@Override
	public Header getContentEncoding() {
		return null;
	}

	@Override
	public InputStream getContent() throws IOException, IllegalStateException {
		return new ByteArrayInputStream(this.out.toByteArray());
	}

	@Override
	public void writeTo(OutputStream paramOutputStream) throws IOException {
		paramOutputStream.write(this.out.toByteArray());
	}

	@Override
	public boolean isStreaming() {
		return false;
	}

	@Override
	public void consumeContent() throws IOException {
		if (isStreaming()) {
			throw new UnsupportedOperationException("Streaming entity does not implement #consumeContent()");
		}
	}

}
