package com.yangc.ichat.volley.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class LruImageMemoryCache extends LruCache<String, Bitmap> {

	public LruImageMemoryCache(int maxSize) {
		super(maxSize);
	}

	@Override
	protected int sizeOf(String key, Bitmap bitmap) {
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

}
