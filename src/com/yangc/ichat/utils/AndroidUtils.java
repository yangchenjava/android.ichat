package com.yangc.ichat.utils;

import java.io.File;
import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.widget.Toast;

public class AndroidUtils {

	private AndroidUtils() {
	}

	/**
	 * @功能: 获取状态栏高度
	 * @作者: yangc
	 * @创建日期: 2014年10月22日 下午4:08:39
	 * @param context
	 * @return
	 */
	public static int getStatusBarHeight(Context context) {
		int statusBarHeight = 0;
		try {
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object obj = clazz.newInstance();
			Field field = clazz.getField("status_bar_height");
			statusBarHeight = context.getResources().getDimensionPixelSize(Integer.parseInt(field.get(obj).toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusBarHeight;
	}

	/**
	 * @功能: 获取屏幕宽度
	 * @作者: yangc
	 * @创建日期: 2014年10月22日 下午4:09:29
	 * @param activity
	 * @return
	 */
	public static int getScreenWidth(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.widthPixels;
	}

	/**
	 * @功能: 获取屏幕高度
	 * @作者: yangc
	 * @创建日期: 2014年10月22日 下午4:09:47
	 * @param activity
	 * @return
	 */
	public static int getScreenHeight(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.heightPixels;
	}

	/**
	 * @功能: 判断SD卡是否存在
	 * @作者: yangc
	 * @创建日期: 2012-12-10 上午10:47:55
	 * @return
	 */
	public static boolean checkSDCard() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * @功能: 判断网络连接情况
	 * @作者: yangc
	 * @创建日期: 2012-12-3 下午04:52:18
	 * @param context
	 * @return
	 */
	public static boolean checkNetwork(Context context) {
		NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		return info != null && info.isConnectedOrConnecting();
	}

	/**
	 * @功能: 获取缓存文件夹
	 * @作者: yangc
	 * @创建日期: 2014年10月24日 下午9:35:05
	 * @param context
	 * @param dirName
	 * @return
	 */
	public static File getCacheDir(Context context, String dirName) {
		File cacheDir = null;
		if (AndroidUtils.checkSDCard()) {
			cacheDir = new File(context.getExternalCacheDir(), dirName);
		} else {
			cacheDir = new File(context.getCacheDir(), dirName);
		}
		cacheDir.mkdirs();
		return cacheDir;
	}

	/**
	 * @功能: 弹出Toast
	 * @作者: yangc
	 * @创建日期: 2012-12-3 下午04:57:27
	 * @param context
	 * @param resId
	 */
	public static void alertToast(Context context, int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}

	/**
	 * @功能: 弹出Toast
	 * @作者: yangc
	 * @创建日期: 2012-12-3 下午04:57:27
	 * @param context
	 * @param text
	 */
	public static void alertToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

}
