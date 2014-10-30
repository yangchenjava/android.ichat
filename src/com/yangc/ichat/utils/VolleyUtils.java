package com.yangc.ichat.utils;

import java.io.File;

import android.app.ActivityManager;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.yangc.ichat.volley.BitmapLruImageCache;
import com.yangc.ichat.volley.MultiPartStack;
import com.yangc.ichat.volley.Volley;

public class VolleyUtils {

	private static RequestQueue normalRequestQueue;
	private static RequestQueue multiPartRequestQueue;
	private static ImageLoader imageLoader;

	private VolleyUtils() {
	}

	/**
	 * @功能: 初始化RequestQueue
	 * @作者: yangc
	 * @创建日期: 2014年10月22日 下午5:04:15
	 * @param context
	 */
	public static void init(Context context) {
		File cacheDir = AndroidUtils.getCacheDir(context, Constants.CACHE_PORTRAIT);
		int maxCacheSizeInBytes = 10 * 1024 * 1024;
		normalRequestQueue = Volley.newRequestQueue(context, null, cacheDir, maxCacheSizeInBytes);
		multiPartRequestQueue = Volley.newRequestQueue(context, new MultiPartStack(), cacheDir, maxCacheSizeInBytes);

		int memoryClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		int maxSize = 1024 * 1024 * memoryClass / 8;
		imageLoader = new ImageLoader(normalRequestQueue, new BitmapLruImageCache(maxSize));
	}

	public static RequestQueue getNormalRequestQueue() {
		if (normalRequestQueue != null) {
			return normalRequestQueue;
		} else {
			throw new IllegalStateException("RequestQueue not initialized");
		}
	}

	public static RequestQueue getMultiPartRequestQueue() {
		if (multiPartRequestQueue != null) {
			return multiPartRequestQueue;
		} else {
			throw new IllegalStateException("RequestQueue not initialized");
		}
	}

	public static void addNormalRequest(Request<?> request, Object tag) {
		if (tag != null) {
			request.setTag(tag);
		}
		normalRequestQueue.add(request);
	}

	public static void addMultiPartRequest(Request<?> request, Object tag) {
		if (tag != null) {
			request.setTag(tag);
		}
		multiPartRequestQueue.add(request);
	}

	public static void cancelAllRequest(Object tag) {
		normalRequestQueue.cancelAll(tag);
		multiPartRequestQueue.cancelAll(tag);
	}

	public static ImageLoader getImageLoader() {
		if (imageLoader != null) {
			return imageLoader;
		} else {
			throw new IllegalStateException("ImageLoader not initialized");
		}
	}

}
