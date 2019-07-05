package com.redefine.welike.business.browse.management.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.redefine.welike.business.browse.management.bean.Interest;

import java.util.List;

/**
 * Created by honglin on 2018/7/19.
 */
@Dao
public  interface InterestEntityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Interest event);

    @Delete
    void delete(Interest event);


    @Query("SELECT * FROM interest_table")
    List<Interest> queryInterestListEvent();

}