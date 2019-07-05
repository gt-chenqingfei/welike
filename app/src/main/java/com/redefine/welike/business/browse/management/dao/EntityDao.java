package com.redefine.welike.business.browse.management.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.redefine.welike.business.browse.management.bean.FollowUser;
import com.redefine.welike.business.browse.management.bean.LikePost;
import com.redefine.welike.business.browse.management.bean.LikePostCount;

import java.util.List;

/**
 * Created by honglin on 2018/7/19.
 */
@Dao
public interface EntityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FollowUser event);

    @Query("delete  FROM follow_table")
    void delete();

    @Query("SELECT id FROM follow_table")
    List<String> queryUserListEvent();

    @Query("SELECT * FROM follow_table WHERE id = :id")
    FollowUser queryUserEvent(String id);

    @Query("SELECT count(*) FROM follow_table ")
    Integer queryFollowCountEvent();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LikePostCount event);

    @Query("SELECT * FROM like_table ")
    LikePostCount queryCountEvent();

    @Query("DELETE FROM like_table ")
    void deleteByDay();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LikePost event);

    @Query("SELECT count(*) FROM like_feed_table ")
    Integer queryLikeCountEvent();

    @Query("delete FROM like_feed_table")
    void deleteLikeData();

    @Query("SELECT pid FROM like_feed_table ")
    List<String> queryLikeListEvent();

    @Query("SELECT * FROM like_feed_table where pid = :id")
    LikePost queryLikeEvent(String id);

}