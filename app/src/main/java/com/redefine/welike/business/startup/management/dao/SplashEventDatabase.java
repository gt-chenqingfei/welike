package com.redefine.welike.business.startup.management.dao;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.redefine.welike.business.startup.management.bean.SplashEntity;

/**
 * Created by honglin on 2018/5/9.
 */

@Database(entities = {SplashEntity.class}, version = 1, exportSchema = false)
public abstract class SplashEventDatabase extends RoomDatabase {
    public abstract SplashEventDao eventDao();
}