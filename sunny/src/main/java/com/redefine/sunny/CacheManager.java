package com.redefine.sunny;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.redefine.sunny.api.ApiRequest;
import com.redefine.sunny.api.FileParam;

import java.util.Set;

/**
 * Created by ningdai on 16/8/19.
 */
public class CacheManager {
//    private static CacheManager instance;
//    final int CACHE_DB_VERSION = 1;
//    CacheDatabase db;
//
//    private CacheManager(Context context) {
//        db = new CacheDatabase(context);
//    }
//
//    public static synchronized CacheManager getInstance(Context context) {
//        if (instance == null) {
//            instance = new CacheManager(context);
//        }
//        return instance;
//    }
//
//    public void clean() {
//        db.getWritableDatabase().delete("enginecache", null, null);
//    }
//
//    public void cleanByCategory(String category) {
//        db.getWritableDatabase().delete("enginecache", "category=?", new String[]{category});
//    }
//
//    public void saveContent(Key key, String content) {
//        SQLiteDatabase database = db.getWritableDatabase();
//        String sql = "REPLACE INTO enginecache (key, update_time, content,category) VALUES (?,?," +
//                "?,?);";
//        SQLiteStatement statement = database.compileStatement(sql);
//        statement.bindString(1, key.value);
//        statement.bindLong(2, System.currentTimeMillis());
//        statement.bindString(3, content);
//        statement.bindString(4, key.category);
//        statement.execute();
////        database.close();
//    }
//
//    public Record getContent(Key key) {
//        if (key == null || TextUtils.isEmpty(key.value)) {
//            return null;
//        }
//        Record record = null;
//        SQLiteDatabase rdb = db.getReadableDatabase();
//        Cursor cursor = rdb.query("enginecache", null, "key=?", new String[]{key.value}, null, null,
//                null);
//        if (cursor != null && cursor.moveToFirst()) {
//            record = new Record();
//            record.key = key;
//            record.content = cursor.getString(cursor.getColumnIndex("content"));
//            record.timestamp = cursor.getLong(cursor.getColumnIndex("update_time"));
//        }
//        if (cursor != null) {
//            cursor.close();
//        }
//        return record;
//    }
//
//
//    public static class Record {
//        public Key key;
//        public long timestamp;
//        public String content;
//    }
//
//    /**
//     * Mark which kind of Query
//     */
//    public static class Key {
//        protected String value;
//        protected String category;
//
//        private Key(String category, String value) {
//            this.category = category;
//            this.value = value;
//        }
//
//        public static Key from(ApiRequest request) {
//            StringBuilder builder = new StringBuilder(request.action);
//            Set<String> keys = request.params.keySet();
//            for (String key : keys) {
//                builder.append(key);
//                builder.append(request.params.get(key));
//            }
//            for (FileParam fileParam : request.fileParams) {
//                builder.append(fileParam.key);
//                builder.append(fileParam.file.getName());
//            }
//            return new Key("engine", builder.toString());
//        }
//
//    }
//
//
//    public class CacheDatabase extends SQLiteOpenHelper {
//
//        public CacheDatabase(Context context) {
//            super(context, "enginecache", null, CACHE_DB_VERSION);
//        }
//
//        @Override
//        public void onCreate(SQLiteDatabase db) {
//            String query = "DROP TABLE IF EXISTS enginecache";
//            db.execSQL(query);
//            query = "CREATE TABLE enginecache (local_id integer primary key autoincrement," +
//                    "key text," + "update_time integer," + "category text," +
//                    "content text, u_code text);";
//            db.execSQL(query);
//            query = "CREATE UNIQUE INDEX key_idx ON enginecache(key);";
//            db.execSQL(query);
//        }
//
//        @Override
//        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            onCreate(db);
//        }
//    }

}
