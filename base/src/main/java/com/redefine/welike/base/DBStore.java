package com.redefine.welike.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.redefine.welike.base.dao.welike.DaoMaster;
import com.redefine.welike.base.dao.welike.DaoSession;

/**
 * Created by liubin on 2018/1/5.
 */

public class DBStore {
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private SQLiteDatabase db;

    private static class DBStoreHolder {
        public static DBStore instance = new DBStore();
    }

    private DBStore() {
    }

    public static DBStore getInstance() {
        return DBStoreHolder.instance;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public void init(String dbName, Context context) {
        if (db == null) {
            try {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, dbName, null);
            db = helper.getWritableDatabase();
            }catch (Exception e){}
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

    public DaoSession getDaoSession() {
        return daoSession;
    }

}
