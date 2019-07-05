package com.redefine.welike.business.publisher.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

/**
 * Created by daining on 2018/5/5.
 */
@Database(entities = arrayOf(Draft::class), version = 1, exportSchema = false)
abstract class DraftDatabase : RoomDatabase() {
    abstract fun draftDao(): DraftDao
}

object DatabaseCenter {
    var database: DraftDatabase? = null

    fun init(context: Context) {
        database = Room.databaseBuilder(context.applicationContext,
                DraftDatabase::class.java, "draft.db")
                .build()
    }


}