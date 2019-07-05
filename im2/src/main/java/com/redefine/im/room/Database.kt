package com.redefine.im.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.redefine.foundation.utils.MD5Helper
import com.redefine.im.d
import com.redefine.im.w

/**
 * Created by daining on 2018/5/5.
 */
@Database(entities = arrayOf(MESSAGE::class, SESSION::class, AccountSetting::class), version = 6, exportSchema = false)
abstract class MyDatabase : RoomDatabase() {

    abstract fun messageDao(): MessageDao
    abstract fun sessionDao(): SessionDao
    abstract fun accountSettingDao(): ASDao
}

object DatabaseCenter {
    var database: MyDatabase? = null
    var name = ""

    fun init(context: Context, _name: String) {
        val dbName = getDbName(_name)
       // context.deleteDatabase(dbName)
       database.let {
            if (it == null || name != dbName) {
                w("buildDatabase1")
                name = dbName
                database = Room.databaseBuilder(context.applicationContext,
                        MyDatabase::class.java, name)
                        .addMigrations(MIGRATION_5_6)
                        //.addMigrations(MIGRATION_6_7)
                        .build()
            }
        }
    }

    private fun getDbName(aid: String): String {
        val dbName = "IM$aid"
        try {
            return MD5Helper.md5(dbName)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return dbName
    }
}