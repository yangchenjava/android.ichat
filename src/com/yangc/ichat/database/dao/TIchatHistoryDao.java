package com.yangc.ichat.database.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.yangc.ichat.database.DaoSession;

import com.yangc.ichat.database.bean.TIchatHistory;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table T_ICHAT_HISTORY.
*/
public class TIchatHistoryDao extends AbstractDao<TIchatHistory, Long> {

    public static final String TABLENAME = "T_ICHAT_HISTORY";

    /**
     * Properties of entity TIchatHistory.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserId = new Property(1, Long.class, "userId", false, "USER_ID");
        public final static Property Chat = new Property(2, String.class, "chat", false, "CHAT");
        public final static Property ChatStatus = new Property(3, Long.class, "chatStatus", false, "CHAT_STATUS");
        public final static Property TransmitStatus = new Property(4, Long.class, "transmitStatus", false, "TRANSMIT_STATUS");
        public final static Property Date = new Property(5, java.util.Date.class, "date", false, "DATE");
    };


    public TIchatHistoryDao(DaoConfig config) {
        super(config);
    }
    
    public TIchatHistoryDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'T_ICHAT_HISTORY' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'USER_ID' INTEGER," + // 1: userId
                "'CHAT' TEXT," + // 2: chat
                "'CHAT_STATUS' INTEGER," + // 3: chatStatus
                "'TRANSMIT_STATUS' INTEGER," + // 4: transmitStatus
                "'DATE' INTEGER);"); // 5: date
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_T_ICHAT_HISTORY_USER_ID ON T_ICHAT_HISTORY" +
                " (USER_ID);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'T_ICHAT_HISTORY'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, TIchatHistory entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long userId = entity.getUserId();
        if (userId != null) {
            stmt.bindLong(2, userId);
        }
 
        String chat = entity.getChat();
        if (chat != null) {
            stmt.bindString(3, chat);
        }
 
        Long chatStatus = entity.getChatStatus();
        if (chatStatus != null) {
            stmt.bindLong(4, chatStatus);
        }
 
        Long transmitStatus = entity.getTransmitStatus();
        if (transmitStatus != null) {
            stmt.bindLong(5, transmitStatus);
        }
 
        java.util.Date date = entity.getDate();
        if (date != null) {
            stmt.bindLong(6, date.getTime());
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public TIchatHistory readEntity(Cursor cursor, int offset) {
        TIchatHistory entity = new TIchatHistory( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // userId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // chat
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // chatStatus
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4), // transmitStatus
            cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)) // date
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, TIchatHistory entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setChat(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setChatStatus(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setTransmitStatus(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
        entity.setDate(cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(TIchatHistory entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(TIchatHistory entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
