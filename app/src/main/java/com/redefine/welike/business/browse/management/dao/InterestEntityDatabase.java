package com.redefine.welike.business.browse.management.dao;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.redefine.welike.business.browse.management.bean.Interest;

/**
 * Created by honglin on 2018/7/19.
 */

@Database(entities = {Interest.class}, version = 1, exportSchema = false)
public abstract class InterestEntityDatabase extends RoomDatabase {
    public abstract InterestEntityDao eventDao();
}