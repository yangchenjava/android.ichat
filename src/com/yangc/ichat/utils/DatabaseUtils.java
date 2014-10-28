package com.yangc.ichat.utils;

import android.content.Context;
import android.text.TextUtils;

import com.yangc.ichat.database.DaoMaster;
import com.yangc.ichat.database.DaoSession;
import com.yangc.ichat.database.bean.TIchatMe;

public class DatabaseUtils {

	private static final String DB_NAME = "ichat-db";

	private static DaoMaster daoMaster;
	private static DaoSession daoSession;

	private DatabaseUtils() {
	}

	public static DaoMaster getDaoMaster(Context context) {
		if (daoMaster == null) {
			DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
			daoMaster = new DaoMaster(helper.getWritableDatabase());
		}
		return daoMaster;
	}

	public static DaoSession getDaoSession(Context context) {
		if (daoSession == null) {
			if (daoMaster == null) {
				daoMaster = getDaoMaster(context);
			}
			daoSession = daoMaster.newSession();
		}
		return daoSession;
	}

	public static void saveMe(Context context, TIchatMe me, String username, String password) {
		if (!TextUtils.isEmpty(me.getPhoto())) {
			me.setPhotoName(me.getPhoto().substring(me.getPhoto().lastIndexOf("/") + 1));
		}
		me.setUsername(username);
		me.setPassword(password);

		getDaoSession(context).getTIchatMeDao().deleteAll();
		getDaoSession(context).getTIchatMeDao().insert(me);
	}

	public static TIchatMe getMe(Context context) {
		return getDaoSession(context).getTIchatMeDao().queryBuilder().unique();
	}

}
