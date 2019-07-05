package com.redefine.welike.base.dao;

import com.redefine.welike.base.dao.im.SessionDao;
import com.redefine.welike.base.dao.welike.DaoMaster;
import com.redefine.welike.base.dao.welike.DraftDao;
import com.redefine.welike.base.dao.welike.ProfileDao;
import com.redefine.welike.base.dao.welike.SearchHistoryDao;
import com.redefine.welike.base.dao.welike.TopicSearchHistoryDao;
import com.redefine.welike.base.dao.welike.UserDao;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.internal.DaoConfig;

public class DaoUpgrade {

    public static void upgradeWelikeDatabase(Database db, int oldVersion, int newVersion) {
        try {
            if (oldVersion < 6) {
                upgradeWelikeDatabaseLow6(db);
                oldVersion++;
            }
            if (oldVersion < 7) {
                upgradeWelikeDatabaseLow7(db);
                oldVersion++;
            }
            if (oldVersion < 8) {
                TopicSearchHistoryDao.createTable(db, true);
                oldVersion++;
            }
            if (oldVersion < 9) {
                DaoConfig profileDaoConfig = new DaoConfig(db, ProfileDao.class);
                String profileTableName = profileDaoConfig.tablename;
                String sql1 = "ALTER TABLE " + profileTableName + " ADD COLUMN '" + ProfileDao.Properties.Vip.columnName + "' INTEGER DEFAULT '0';";
                db.execSQL(sql1);

                DaoConfig userDaoConfig = new DaoConfig(db, UserDao.class);
                String userTableName = userDaoConfig.tablename;
                String sql2 = "ALTER TABLE " + userTableName + " ADD COLUMN '" + UserDao.Properties.Vip.columnName + "' INTEGER DEFAULT '0';";
                db.execSQL(sql2);
                oldVersion++;
            }
            if (oldVersion < 10) {
                DaoConfig daoConfig = new DaoConfig(db, DraftDao.class);
                String tableName = daoConfig.tablename;
                String sql = "ALTER TABLE " + tableName + " ADD COLUMN '" + DraftDao.Properties.FText.columnName + "' TEXT;";
                db.execSQL(sql);
                sql = "ALTER TABLE " + tableName + " ADD COLUMN '" + DraftDao.Properties.FSummary.columnName + "' TEXT;";
                db.execSQL(sql);
                sql = "ALTER TABLE " + tableName + " ADD COLUMN '" + DraftDao.Properties.FRich.columnName + "' TEXT;";
                db.execSQL(sql);
                sql = "ALTER TABLE " + tableName + " ADD COLUMN '" + DraftDao.Properties.CommentUid.columnName + "' TEXT;";
                db.execSQL(sql);
                sql = "ALTER TABLE " + tableName + " ADD COLUMN '" + DraftDao.Properties.CommentNick.columnName + "' TEXT;";
                db.execSQL(sql);
                sql = "ALTER TABLE " + tableName + " ADD COLUMN '" + DraftDao.Properties.CText.columnName + "' TEXT;";
                db.execSQL(sql);
                sql = "ALTER TABLE " + tableName + " ADD COLUMN '" + DraftDao.Properties.CSummary.columnName + "' TEXT;";
                db.execSQL(sql);
                sql = "ALTER TABLE " + tableName + " ADD COLUMN '" + DraftDao.Properties.CRich.columnName + "' TEXT;";
                db.execSQL(sql);
                sql = "ALTER TABLE " + tableName + " ADD COLUMN '" + DraftDao.Properties.RText.columnName + "' TEXT;";
                db.execSQL(sql);
                sql = "ALTER TABLE " + tableName + " ADD COLUMN '" + DraftDao.Properties.RSummary.columnName + "' TEXT;";
                db.execSQL(sql);
                sql = "ALTER TABLE " + tableName + " ADD COLUMN '" + DraftDao.Properties.RRich.columnName + "' TEXT;";
                db.execSQL(sql);
                oldVersion++;
            }
            if (oldVersion < 11) {
                DaoConfig daoConfig = new DaoConfig(db, ProfileDao.class);
                String tableName = daoConfig.tablename;
                String sql1 = "ALTER TABLE " + tableName + " ADD COLUMN '" + ProfileDao.Properties.Status.columnName + "' INTEGER DEFAULT '0';";
                db.execSQL(sql1);
                oldVersion++;
            }
            if (oldVersion < 12) {
                DaoConfig daoConfig = new DaoConfig(db, DraftDao.class);
                String tableName = daoConfig.tablename;
                String sql = "ALTER TABLE " + tableName + " ADD COLUMN '" + DraftDao.Properties.VideoWidth.columnName + "' INTEGER DEFAULT '0';";
                db.execSQL(sql);
                sql = "ALTER TABLE " + tableName + " ADD COLUMN '" + DraftDao.Properties.VideoHeight.columnName + "' INTEGER DEFAULT '0';";
                db.execSQL(sql);
                oldVersion++;
            }
        } catch (Throwable e) {
            DaoMaster.dropAllTables(db, true);
            DaoMaster.createAllTables(db, false);
        }
    }

    private static void upgradeWelikeDatabaseLow7(Database db) {
        String deleteHomeCacheTable = "DROP TABLE IF EXISTS HOME_POST";
        String deleteHotCacheTable = "DROP TABLE IF EXISTS HOT_POST";
        db.execSQL(deleteHomeCacheTable);
        db.execSQL(deleteHotCacheTable);
        DraftDao.dropTable(db, true);

        DraftDao.createTable(db, false);
    }

    private static void upgradeWelikeDatabaseLow6(Database db) {
        String deleteHomeCacheTable = "DROP TABLE IF EXISTS HOME_POST";
        String deleteHotCacheTable = "DROP TABLE IF EXISTS HOT_POST";
        db.execSQL(deleteHomeCacheTable);
        db.execSQL(deleteHotCacheTable);
        ProfileDao.dropTable(db, true);
        SearchHistoryDao.dropTable(db, true);
        UserDao.dropTable(db, true);
        DraftDao.dropTable(db, true);

        ProfileDao.createTable(db, false);
        SearchHistoryDao.createTable(db, false);
        UserDao.createTable(db, false);
        DraftDao.createTable(db, false);
    }

    public static void upgradeIMDatabase(Database db, int oldVersion, int newVersion) {
        try {
            if (oldVersion < 4) {
                upgradeIMDatabaseLow4(db);
                oldVersion++;
            }
            if (oldVersion < 5) {
                DaoMaster.dropAllTables(db, true);
                DaoMaster.createAllTables(db, false);
            }
        } catch (Exception e) {
            DaoMaster.dropAllTables(db, true);
            DaoMaster.createAllTables(db, false);
        }
    }

    private static void upgradeIMDatabaseLow4(Database db) {
        DaoConfig daoConfig = new DaoConfig(db, SessionDao.class);
        String tableName = daoConfig.tablename;
        String sql = "ALTER TABLE " + tableName + " ADD COLUMN '" + SessionDao.Properties.IsGreet.columnName + "' INTEGER DEFAULT '0';";
        db.execSQL(sql);
    }

}
