package com.redefine.welike.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.redefine.welike.base.dao.storage.DaoMaster;
import com.redefine.welike.base.dao.storage.DaoSession;

/**
 * Created by liubin on 2018/1/22.
 */

public class StorageDBStore {
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private SQLiteDatabase db;

    private static class StorageDBStoreHolder {
        public static StorageDBStore instance = new StorageDBStore();
    }

    private StorageDBStore() {}

    public static StorageDBStore getInstance() { return StorageDBStoreHolder.instance; }

    public void init(String dbName, Context context) {
        if (db == null) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, dbName, null);
            db = helper.getWritableDatabase();
        }

        if (daoMaster == null) {
            daoMaster = new DaoMaster(db);
        }

        if (daoSession == null) {
            daoSession = daoMaster.newSession();
        }
    }

    public void close() {
        daoSession = null;
        daoMaster = null;
        if (db != null) {
            db.close();
            db = null;
        }
    }

    public DaoSession getDaoSession() { return daoSession; }
}
