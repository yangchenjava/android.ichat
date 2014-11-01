package com.yangc.ichat.utils;

import java.util.List;

import android.content.Context;

import com.yangc.ichat.database.DaoMaster;
import com.yangc.ichat.database.DaoSession;
import com.yangc.ichat.database.bean.TIchatAddressbook;
import com.yangc.ichat.database.bean.TIchatMe;
import com.yangc.ichat.database.dao.TIchatAddressbookDao.Properties;

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
		me.setUsername(username);
		me.setPassword(password);

		getDaoSession(context).getTIchatMeDao().deleteAll();
		getDaoSession(context).getTIchatMeDao().insert(me);
	}

	public static void updateMe(Context context, TIchatMe me) {
		getDaoSession(context).getTIchatMeDao().update(me);
	}

	public static TIchatMe getMe(Context context) {
		return getDaoSession(context).getTIchatMeDao().queryBuilder().unique();
	}

	public static void saveOrUpdateAddressbook(Context context, List<TIchatAddressbook> addressbookList) {
		getDaoSession(context).getTIchatAddressbookDao().insertOrReplaceInTx(addressbookList);
	}

	public static List<TIchatAddressbook> getAddressbookList(Context context) {
		return getDaoSession(context).getTIchatAddressbookDao().queryBuilder().orderAsc(Properties.Spell).list();
	}

}
