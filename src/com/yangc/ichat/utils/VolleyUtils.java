package com.yangc.ichat.utils;

import java.io.File;

import android.app.ActivityManager;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.yangc.ichat.volley.BitmapLruCache;
import com.yangc.ichat.volley.Volley;

public class VolleyUtils {

	private static final String DEFAULT_CACHE_DIR = "portrait";

	private static RequestQueue requestQueue;
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
		File cachePath = null;
		if (AndroidUtils.checkSDCard()) {
			cachePath = new File(context.getExternalCacheDir(), DEFAULT_CACHE_DIR);
		} else {
			cachePath = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);
		}
		requestQueue = Volley.newRequestQueue(context, cachePath);

		int memoryClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		int maxSize = 1024 * 1024 * memoryClass / 8;
		imageLoader = new ImageLoader(requestQueue, new BitmapLruCache(maxSize));
	}

	public static RequestQueue getRequestQueue() {
		if (requestQueue != null) {
			return requestQueue;
		} else {
			throw new IllegalStateException("RequestQueue not initialized");
		}
	}

	public static void addRequest(Request<?> request, Object tag) {
		if (tag != null) {
			request.setTag(tag);
		}
		requestQueue.add(request);
	}

	public static void cancelAllRequest(Object tag) {
		requestQueue.cancelAll(tag);
	}

	public static ImageLoader getImageLoader() {
		if (imageLoader != null) {
			return imageLoader;
		} else {
			throw new IllegalStateException("ImageLoader not initialized");
		}
	}

}
