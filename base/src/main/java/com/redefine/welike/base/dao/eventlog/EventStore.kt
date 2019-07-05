package com.redefine.welike.base.dao.eventlog

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.*
import android.arch.persistence.room.migration.Migration
import android.content.Context
import android.util.Log

object EventStore {

    lateinit var mDao: EventDao
    fun init(context: Context) {
        mDao = Room.databaseBuilder(context, EventDatabase::class.java, "event_log")
                .addMigrations(EVENT_MIGRATION_1_2)
                .build().eventDao()
    }

    fun insert(event: EventEntity) {
        mDao.insert(event)
    }

    fun delete(event: EventEntity) {
        mDao.delete(event)
    }

    fun deleteAll(vararg event: EventEntity) {
        mDao.deleteAll(*event)
    }

    fun queryAmountEvent(server: Int, limit: Int): List<EventEntity> {
        return mDao.queryAmountEvent(server, limit)
    }

    fun queryCacheEvent(limit: Int): List<EventEntity> {
        return mDao.queryCacheEvent(limit)
    }
}

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg event: EventEntity)

    @Delete
    fun delete(event: EventEntity)

    @Delete
    fun deleteAll(vararg event: EventEntity)

    @Query("SELECT * FROM event_log WHERE type = 1 LIMIT :limit")
    fun queryCacheEvent(limit: Int): List<EventEntity>

    @Query("SELECT * FROM event_log WHERE type = 2 AND server = :server LIMIT :limit")
    fun queryAmountEvent(server: Int, limit: Int): List<EventEntity>
}

@Database(entities = arrayOf(EventEntity::class), version = 2, exportSchema = false)
abstract class EventDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}

@Entity(tableName = "event_log")
class EventEntity(
        @PrimaryKey
        @ColumnInfo(name = "id")
        var id: String,

        @ColumnInfo(name = "data")
        var eventInfo: String?,

        @ColumnInfo(name = "type")
        var type: Int
        ,

        @ColumnInfo(name = "priority")
        var priority: Int?,

        @ColumnInfo(name = "server")
        var server: Int?      //which server receive it. 0 is default. 1 is  AWS

)

val EVENT_MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        try {
            Log.w("DDAI", "EVENT_MIGRATION")
            database.execSQL("ALTER TABLE event_log ADD COLUMN priority INTEGER DEFAULT 0")
            database.execSQL("ALTER TABLE event_log ADD COLUMN server INTEGER DEFAULT 0")
            Log.w("DDAI", "EVENT_MIGRATION 2")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}

