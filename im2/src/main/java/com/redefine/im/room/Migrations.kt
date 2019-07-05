package com.redefine.im.room
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration


val MIGRATION_5_6: Migration = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE MESSAGE "
                + " ADD COLUMN IS_GREET INTEGER")
    }
}

val MIGRATION_6_7: Migration = object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE ACCOUNT_SETTING "
                + " ADD COLUMN PUSH_MSG_UN_READ_COUNT INTEGER")
    }
}