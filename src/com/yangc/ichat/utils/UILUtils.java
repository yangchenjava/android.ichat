package com.yangc.ichat.utils;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.yangc.ichat.R;

public class UILUtils {

	private UILUtils() {
	}

	public static void init(Context context) {
		File cacheDir = AndroidUtils.getStorageDir(context, Constants.APP + "/" + Constants.CACHE_PORTRAIT);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2).tasksProcessingOrder(QueueProcessingType.LIFO)
				.denyCacheImageMultipleSizesInMemory().diskCache(new UnlimitedDiscCache(cacheDir)).diskCacheSize(50 * 1024 * 1024).diskCacheFileCount(100)
				.diskCacheFileNameGenerator(new Md5FileNameGenerator()).build();
		ImageLoader.getInstance().init(config);
	}

	public static DisplayImageOptions getDisplayImageOptions() {
		DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.me_info).showImageForEmptyUri(R.drawable.me_info).showImageOnFail(R.drawable.me_info)
				.cacheInMemory(true).cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.ARGB_8888).displayer(new RoundedBitmapDisplayer(8)).build();
		return options;
	}

}
