package com.redefine.im.cache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.redefine.foundation.utils.MD5Helper;
import com.redefine.welike.base.dao.im.DaoMaster;
import com.redefine.welike.base.dao.im.DaoSession;

import org.w3c.dom.Text;

/**
 * 根据 用户ID 管理多db
 */
public class IMMultiAccountDBStore {
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private SQLiteDatabase db;
    private String currentDbName;
    private static final String PRE_SUFFIX = "IM";

    private static class IMDBStoreHolder {
        public static IMMultiAccountDBStore instance = new IMMultiAccountDBStore();
    }

    private IMMultiAccountDBStore() {}

    public static IMMultiAccountDBStore getInstance() { return IMDBStoreHolder.instance; }

    /**
     * 根据aid 创建DB
     * @param aid
     * @param context
     */
    public void init(String aid, Context context) {
//        String dbName = getDbName(aid);
//        if (db == null || TextUtils.isEmpty(currentDbName) || !TextUtils.equals(dbName, currentDbName)) {
//            if (db != null) {
//                close();
//            }
//            currentDbName = getDbName(aid);
////            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, currentDbName, null);
////            db = helper.getWritableDatabase();
//        }
//
//        if (daoMaster == null) {
//            daoMaster = new DaoMaster(db);
//        }
//
//        if (daoSession == null) {
//            daoSession = daoMaster.newSession();
//            ImAccountSettingCache.getInstance().update();
//            IMMessageCache.getInstance().update();
//        }
    }

    private String getDbName(String aid) {
        String dbName = PRE_SUFFIX + aid;
        try {
            return MD5Helper.md5(dbName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dbName;
    }

    public void close() {
//        daoSession = null;
//        daoMaster = null;
//        if (db != null) {
//            db.close();
//            db = null;
//        }
    }

    public DaoSession getDaoSession() { return daoSession; }
}
