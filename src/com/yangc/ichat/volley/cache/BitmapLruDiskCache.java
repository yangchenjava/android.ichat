package com.yangc.ichat.volley.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.android.volley.toolbox.ImageLoader;
import com.jakewharton.disklrucache.DiskLruCache;

public class BitmapLruDiskCache implements ImageLoader.ImageCache {

	private DiskLruCache diskLruCache;

	public BitmapLruDiskCache(File cacheDir, int appVersion, long maxSize) {
		try {
			this.diskLruCache = DiskLruCache.open(cacheDir, appVersion, 1, maxSize);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Bitmap getBitmap(String url) {
		DiskLruCache.Snapshot snapshot = null;
		try {
			snapshot = this.diskLruCache.get(this.getMD5(url));
			if (snapshot != null) {
				InputStream stream = snapshot.getInputStream(0);
				if (stream != null) {
					return BitmapFactory.decodeStream(new BufferedInputStream(stream));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (snapshot != null) snapshot.close();
		}
		return null;
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		DiskLruCache.Editor editor = null;
		try {
			editor = this.diskLruCache.edit(this.getMD5(url));
			if (editor != null) {
				OutputStream stream = new BufferedOutputStream(editor.newOutputStream(0));
				if (this.writeBitmapToFile(bitmap, stream)) {
					editor.commit();
				} else {
					editor.abort();
				}
			}
			this.diskLruCache.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (editor != null) editor.abort();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean removeBitmap(String url) {
		try {
			return this.diskLruCache.remove(this.getMD5(url));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean containsUrl(String url) {
		DiskLruCache.Snapshot snapshot = null;
		try {
			snapshot = this.diskLruCache.get(this.getMD5(url));
			return snapshot != null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (snapshot != null) snapshot.close();
		}
		return false;
	}

	public void clearCache() {
		try {
			this.diskLruCache.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getMD5(String url) {
		if (!TextUtils.isEmpty(url)) {
			MessageDigest digest = null;
			try {
				digest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}

			byte[] bytes = url.getBytes();
			byte[] results = digest.digest(bytes);
			StringBuilder sb = new StringBuilder();
			for (byte result : results) {
				// 将byte数组转化为16进制字符存入StringBuilder中
				sb.append(String.format("%02x", result));
			}
			return sb.toString();
		}
		return null;
	}

	private boolean writeBitmapToFile(Bitmap bitmap, OutputStream stream) throws IOException {
		boolean result = bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		stream.flush();
		stream.close();
		return result;
	}

}
