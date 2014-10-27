package com.yangc.ichat.utils;

import android.content.Context;

import com.yangc.ichat.database.DaoMaster;
import com.yangc.ichat.database.DaoSession;

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

}
