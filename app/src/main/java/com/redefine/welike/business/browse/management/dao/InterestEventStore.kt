package com.redefine.welike.business.browse.management.dao

import android.arch.persistence.room.Room
import com.redefine.welike.MyApplication
import com.redefine.welike.business.browse.management.bean.Interest

/**
 * Created by honglin on 2018/7/19.
 */
enum class InterestEventStore {

    INSTANCE;

    private var mDao: InterestEntityDao? = null

    init {
        val db = Room.databaseBuilder(MyApplication.getAppContext(), InterestEntityDatabase::class.java, "interest_table").build()
        mDao = db.eventDao()
    }

    fun insert(event: Interest) {
        Thread(Runnable { mDao!!.insert(event) }).start()
    }

    @Synchronized
    fun delete(event: Interest) {
        mDao!!.delete(event)
    }

    @Synchronized
    fun getInterestList(callback: InterestCallBack) {
        //在io线程进行数据库操作
        Thread(Runnable {
            val entitys = mDao!!.queryInterestListEvent()

            if (entitys != null && entitys.isNotEmpty()) {
                callback.onLoadEntity(entitys)
            } else {
                callback.onLoadEntity(ArrayList())
            }
        }).start()
    }

    /**
     * 批量添加
     */
    @Synchronized
    fun insert(list: List<Interest>) {

        for (event in list) {
            insert(event)
        }
    }

}
