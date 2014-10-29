package com.yangc.ichat.volley.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

public class BitmapLruMemoryCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {

	public BitmapLruMemoryCache(int maxSize) {
		super(maxSize);
	}

	@Override
	protected int sizeOf(String url, Bitmap bitmap) {
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

	@Override
	public Bitmap getBitmap(String url) {
		return this.get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		this.put(url, bitmap);
	}

}
