package com.yangc.ichat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

//模式: Context.MODE_PRIVATE只有本应用可以使用, Context.MODE_WORLD_READABLE其他应用可以读, Context.MODE_WORLD_WRITEABLE其他应用可以写
public class PreferenceUtils {

	private PreferenceUtils() {
	}

	public static String getString(Context context, String key, String defValue) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPreferences.getString(key, defValue);
	}

	public static void setString(Context context, String key, String value) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPreferences.edit().putString(key, value).commit();
	}

	public static int getInt(Context context, String key, int defValue) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPreferences.getInt(key, defValue);
	}

	public static void setInt(Context context, String key, int value) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPreferences.edit().putInt(key, value).commit();
	}

	public static long getLong(Context context, String key, long defValue) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPreferences.getLong(key, defValue);
	}

	public static void setLong(Context context, String key, long value) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPreferences.edit().putLong(key, value).commit();
	}

	public static float getFloat(Context context, String key, float defValue) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPreferences.getFloat(key, defValue);
	}

	public static void setFloat(Context context, String key, float value) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPreferences.edit().putFloat(key, value).commit();
	}

	public static boolean getBoolean(Context context, String key, boolean defValue) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPreferences.getBoolean(key, defValue);
	}

	public static void setBoolean(Context context, String key, boolean value) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPreferences.edit().putBoolean(key, value).commit();
	}

	public static boolean contains(Context context, String key) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPreferences.contains(key);
	}

	public static void remove(Context context, String key) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPreferences.edit().remove(key).commit();
	}

	public static void clear(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPreferences.edit().clear();
	}

}
