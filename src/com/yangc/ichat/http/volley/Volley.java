package com.yangc.ichat.http.volley;

import java.io.File;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.os.Environment;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;

public class Volley extends com.android.volley.toolbox.Volley {

	private static final String DEFAULT_CACHE_DIR = "volley";

	public static RequestQueue newRequestQueue(Context context, HttpStack stack) {
		File cacheDir = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			cacheDir = new File(context.getExternalCacheDir(), DEFAULT_CACHE_DIR);
		} else {
			cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);
		}
		return newRequestQueue(context, stack, cacheDir);
	}

	public static RequestQueue newRequestQueue(Context context, HttpStack stack, File cacheDir) {
		return newRequestQueue(context, stack, cacheDir, 0);
	}

	public static RequestQueue newRequestQueue(Context context, HttpStack stack, File cacheDir, int maxCacheSizeInBytes) {
		String userAgent = "volley/0";
		try {
			String packageName = context.getPackageName();
			PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
			userAgent = packageName + "/" + info.versionCode;
		} catch (NameNotFoundException e) {
		}

		if (stack == null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				stack = new HurlStack();
			} else {
				stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
			}
		}

		Cache cache = null;
		if (maxCacheSizeInBytes == 0) {
			cache = new DiskBasedCache(cacheDir);
		} else {
			cache = new DiskBasedCache(cacheDir, maxCacheSizeInBytes);
		}

		Network network = new BasicNetwork(stack);

		RequestQueue queue = new RequestQueue(cache, network);
		queue.start();
		return queue;
	}
}
