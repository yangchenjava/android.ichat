package com.yangc.ichat.utils;

import java.util.List;

import android.content.Context;

import com.yangc.ichat.database.DaoMaster;
import com.yangc.ichat.database.DaoSession;
import com.yangc.ichat.database.bean.TIchatAddressbook;
import com.yangc.ichat.database.bean.TIchatHistory;
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
		getDaoSession(context).getTIchatAddressbookDao().deleteAll();
		for (TIchatAddressbook addressbook : addressbookList) {
			addressbook.setDeleted(0L);
		}
		getDaoSession(context).getTIchatAddressbookDao().insertInTx(addressbookList);
	}

	public static void deleteAddressbook_logic(Context context, Long userId) {
		TIchatAddressbook addressbook = getDaoSession(context).getTIchatAddressbookDao().queryBuilder().where(Properties.UserId.eq(userId)).unique();
		if (addressbook != null) {
			addressbook.setDeleted(1L);
			getDaoSession(context).getTIchatAddressbookDao().update(addressbook);
		}
	}

	public static void deleteAddressbook_physical(Context context, Long userId) {
		TIchatAddressbook addressbook = getDaoSession(context).getTIchatAddressbookDao().queryBuilder().where(Properties.UserId.eq(userId)).unique();
		if (addressbook != null) {
			getDaoSession(context).getTIchatAddressbookDao().delete(addressbook);
		}
	}

	public static TIchatAddressbook getAddressbookByUsername(Context context, String username) {
		return getDaoSession(context).getTIchatAddressbookDao().queryBuilder().where(Properties.Username.eq(username)).unique();
	}

	public static List<TIchatAddressbook> getAddressbookList(Context context) {
		return getDaoSession(context).getTIchatAddressbookDao().queryBuilder().where(Properties.Deleted.eq(0L)).orderAsc(Properties.Spell).list();
	}

	public static List<TIchatAddressbook> getAddressbookListByDelete(Context context) {
		return getDaoSession(context).getTIchatAddressbookDao().queryBuilder().where(Properties.Deleted.eq(1L)).list();
	}

	public static List<TIchatHistory> getHistoryList(Context context) {
		String where = "JOIN (SELECT MAX(_ID) _ID, USERNAME FROM T_ICHAT_HISTORY GROUP BY USERNAME) C ON C._ID = T._ID ORDER BY C._ID DESC";
		return getDaoSession(context).getTIchatHistoryDao().queryRawCreate(where).list();
	}

}
