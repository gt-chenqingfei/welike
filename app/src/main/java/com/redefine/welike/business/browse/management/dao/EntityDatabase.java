package com.redefine.welike.business.browse.management.dao;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.redefine.welike.business.browse.management.bean.FollowUser;
import com.redefine.welike.business.browse.management.bean.LikePost;
import com.redefine.welike.business.browse.management.bean.LikePostCount;

/**
 * Created by honglin on 2018/7/19.
 */

@Database(entities = {FollowUser.class, LikePostCount.class, LikePost.class}, version = 3, exportSchema = false)
public abstract class EntityDatabase extends RoomDatabase {
    public abstract EntityDao eventDao();
}