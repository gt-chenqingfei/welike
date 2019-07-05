package com.redefine.welike.business.browse.management.dao

import com.redefine.welike.business.browse.management.bean.FollowUser

/**
 * Created by honglin on 2018/7/20.
 */
interface FollowUsersCallBack {
    fun onLoadEntity(users: List<String>)
}


interface FollowUserCallBack {
    fun onLoadEntity(user: FollowUser)
}

interface FollowUserCountCallBack {
    fun onLoadEntity(inserted: Boolean,count: Int)
}

interface InsertLikeCallBack {
    fun onLoadEntity(inserted: Boolean,count: Int)
}
interface LikeListCallBack {
    fun onLoadEntity(pid: List<String>)
}

interface CountCallBack {
    fun onLoadEntity(count: Int)
}