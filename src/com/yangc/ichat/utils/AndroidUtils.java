package com.yangc.ichat.utils;

import java.io.File;
import java.lang.reflect.Field;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yangc.ichat.R;

public class AndroidUtils {

	private AndroidUtils() {
	}

	/**
	 * @功能: 获取应用程序版本号
	 * @作者: yangc
	 * @创建日期: 2014年10月29日 下午6:02:45
	 * @param context
	 * @return
	 */
	public static int getAppVersion(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}

	/**
	 * @功能: 获取当前显示的activity的名称(权限)
	 * @作者: yangc
	 * @创建日期: 2014年11月29日 下午8:27:40
	 * @param context
	 * @return
	 */
	public static String getRunningActivityName(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		return activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
	}

	/**
	 * @功能: dp转px
	 * @作者: yangc
	 * @创建日期: 2014年11月9日 上午2:19:33
	 * @param context
	 * @param dp
	 * @return
	 */
	public static int dp2px(Context context, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
	}

	/**
	 * @功能: sp转px
	 * @作者: yangc
	 * @创建日期: 2014年11月9日 上午2:19:33
	 * @param context
	 * @param sp
	 * @return
	 */
	public static int sp2px(Context context, int sp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
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
		return info != null && info.isAvailable();
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
		if (!cacheDir.exists()) cacheDir.mkdirs();
		return cacheDir;
	}

	/**
	 * @功能: 获取存储文件夹
	 * @作者: yangc
	 * @创建日期: 2014年10月24日 下午9:35:05
	 * @param context
	 * @param dirName
	 * @return
	 */
	public static File getStorageDir(Context context, String dirName) {
		File storageDir = null;
		if (AndroidUtils.checkSDCard()) {
			storageDir = new File(Environment.getExternalStorageDirectory(), dirName);
		} else {
			storageDir = new File(context.getFilesDir(), dirName);
		}
		if (!storageDir.exists()) storageDir.mkdirs();
		return storageDir;
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

	/**
	 * @功能: 进度对话框
	 * @作者: yangc
	 * @创建日期: 2014年11月4日 下午11:24:03
	 * @param context
	 * @param message
	 * @param cancelable
	 * @param canceledOnTouchOutside
	 * @return
	 */
	public static Dialog showProgressDialog(Context context, String message, boolean cancelable, boolean canceledOnTouchOutside) {
		View view = View.inflate(context, R.layout.dialog_progress, null);
		LinearLayout llDialogProgress = (LinearLayout) view.findViewById(R.id.ll_dialog_progress);
		((ImageView) view.findViewById(R.id.iv_dialog_progress_image)).startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate_loading));
		((TextView) view.findViewById(R.id.tv_dialog_progress_text)).setText(message);

		Dialog progressDialog = new Dialog(context, R.style.CustomProgressDialog);
		progressDialog.setCancelable(cancelable);
		progressDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
		progressDialog.setContentView(llDialogProgress, new LinearLayout.LayoutParams(dp2px(context, 300), dp2px(context, 45)));
		progressDialog.show();
		return progressDialog;
	}

	/**
	 * @功能: 隐藏软键盘
	 * @作者: yangc
	 * @创建日期: 2014年12月5日 下午1:31:29
	 * @param activity
	 */
	public static void hideSoftInput(Activity activity) {
		View currentFocus = activity.getCurrentFocus();
		if (currentFocus != null) {
			InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

}
