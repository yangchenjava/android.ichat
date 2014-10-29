package com.yangc.ichat.volley.cache;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.android.volley.toolbox.ImageLoader;

public class LruImageCache implements ImageLoader.ImageCache {

	private LruImageDiskCache lruImageDiskCache;
	private LruImageMemoryCache lruImageMemoryCache;

	public LruImageCache(File cacheDir, int appVersion, long diskMaxSize, int memoryMaxSize) {
		this.lruImageDiskCache = new LruImageDiskCache(cacheDir, appVersion, diskMaxSize);
		this.lruImageMemoryCache = new LruImageMemoryCache(memoryMaxSize);
	}

	@Override
	public Bitmap getBitmap(String url) {
		String key = this.getMD5(url);
		Bitmap bitmap = this.lruImageMemoryCache.get(key);
		if (bitmap == null) {
			bitmap = this.lruImageDiskCache.get(key);
			if (bitmap != null) {
				this.lruImageMemoryCache.put(key, bitmap);
			}
		}
		return null;
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		String key = this.getMD5(url);
		this.lruImageDiskCache.put(key, bitmap);
		this.lruImageMemoryCache.put(key, bitmap);
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

}
