package com.redefine.welike.business.browse.management.dao
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration


val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS like_feed_table (id INTEGER NOT NULL, pid TEXT NOT NULL, PRIMARY KEY(id)) ")
    }
}

val MIGRATION_2_3: Migration = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS like_feed_table (id INTEGER NOT NULL, pid TEXT NOT NULL, PRIMARY KEY(id)) ")
    }
}
