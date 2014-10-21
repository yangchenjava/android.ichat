package com.yangc.ichat.utils;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class AndroidUtils {

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

	public static int getScreenWidth(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.widthPixels;
	}

	public static int getScreenHeight(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.heightPixels;
	}

}
