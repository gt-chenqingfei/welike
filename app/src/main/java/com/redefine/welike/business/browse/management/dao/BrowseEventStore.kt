package com.redefine.welike.business.browse.management.dao

import android.arch.persistence.room.Room
import com.redefine.welike.MyApplication
import com.redefine.welike.base.util.TimeUtil
import com.redefine.welike.business.browse.management.bean.FollowUser
import com.redefine.welike.business.browse.management.bean.LikePost
import com.redefine.welike.business.browse.management.bean.LikePostCount
import com.redefine.welike.business.feeds.management.bean.PostBase

/**
 * Created by honglin on 2018/7/19.
 *
 *
 *
 */
enum class BrowseEventStore {


    INSTANCE;

    private var mDao: EntityDao? = null

    init {
        val db = Room.databaseBuilder(MyApplication.getAppContext(), EntityDatabase::class.java, "browse.db")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build()
        mDao = db.eventDao()
    }

    companion object {
        val maxCount = 10
    }

    fun insert(event: FollowUser) {
        Thread(Runnable { mDao!!.insert(event) }).start()
    }

    @Synchronized
    fun delete() {
        mDao!!.delete()
    }

    @Synchronized
    fun getFollowUsersList(callback: FollowUsersCallBack) {
        Thread(Runnable {
            val entitys = mDao!!.queryUserListEvent()

            if (entitys != null && entitys.isNotEmpty()) {
                callback.onLoadEntity(entitys)
            } else {
                callback.onLoadEntity(ArrayList())
            }
        }).start()
    }

    @Synchronized
    fun getFollowUser(id: String, callback: FollowUserCallBack) {
        Thread(Runnable {
            if (id.isEmpty()) return@Runnable
            val entity = mDao!!.queryUserEvent(id)
            if (entity != null)
                callback.onLoadEntity(entity)

        }).start()
    }

    @Synchronized
    fun getFollowUserCount(event: FollowUser, callback: FollowUserCountCallBack) {
        Thread(Runnable {
            val entity = mDao!!.queryFollowCountEvent()
            val entity1 = mDao!!.queryLikeCountEvent()

            if (entity < maxCount) {

                mDao!!.insert(event)

                callback.onLoadEntity(true, entity + 1 + entity1)

            } else {
                callback.onLoadEntity(false, maxCount)
            }

        }).start()
    }

    @Synchronized
    fun updateLikeCount(mPostBase: PostBase, callBack: InsertLikeCallBack) {
        Thread(Runnable {
            var entity = mDao!!.queryLikeCountEvent()
            val entity1 = mDao!!.queryFollowCountEvent()
            val post = LikePost(entity, mPostBase.pid)

            if (entity < maxCount) {

                val likePost = mDao!!.queryLikeEvent(mPostBase.pid)

                if (likePost != null) {

                    return@Runnable

                }
                mDao!!.insert(post)

                callBack.onLoadEntity(true, entity + 1 + entity1)

            } else {
                callBack.onLoadEntity(false, maxCount)
            }

        }).start()
    }

    @Synchronized
    fun getLostLikeList(callback: LikeListCallBack) {
        Thread(Runnable {
            val entitys = mDao!!.queryLikeListEvent()

            if (entitys != null && entitys.isNotEmpty()) {
                callback.onLoadEntity(entitys)
            } else {
                callback.onLoadEntity(ArrayList())
            }
        }).start()
    }

    @Synchronized
    fun deleteLikeData() {
        mDao!!.deleteLikeData()
    }

    @Synchronized
    fun clearClickCount() {
        Thread(Runnable {
            mDao!!.deleteByDay()
        }).start()
    }


    @Synchronized
    fun updateClickCount(callback: CountCallBack) {
        Thread(Runnable {
            var entitys = mDao!!.queryCountEvent()

            if (entitys == null) {
                entitys = LikePostCount(0, 0, 0)
            }

            if (!TimeUtil.isSameDay(entitys.time!!, System.currentTimeMillis())) {
                mDao!!.deleteByDay()
                entitys.count = 0
            }

            mDao!!.insert(LikePostCount(0, entitys.count + 1, System.currentTimeMillis()))

            callback.onLoadEntity(entitys.count + 1)

        }).start()
    }

}
