package com.redefine.welike.business.startup.management.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.redefine.welike.business.startup.management.bean.SplashEntity;

import java.util.List;


/**
 * Created by honglin on 2018/5/9.
 */
@Dao
public interface SplashEventDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SplashEntity event);

    @Delete
    void delete(SplashEntity event);


    /**
     * 获取要展示的闪屏
     **/
    @Query("SELECT * FROM splash_table")
    List<SplashEntity> queryCurrentShowEvent();

    @Query("SELECT * FROM splash_table WHERE  startTime < :time and endTime > :time and playTime > 0")
    List<SplashEntity> queryCurrentShowEvent(long time);

    @Query("delete FROM splash_table  WHERE endTime < :time")
    void deleteExpiredEntry(long time);

    @Query("delete FROM splash_table")
    void deleteExpiredEntry();

}
