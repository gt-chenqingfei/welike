package com.redefine.welike.business.startup.management.dao;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.redefine.foundation.utils.LogUtil;
import com.redefine.welike.business.startup.management.bean.SplashEntity;
import com.redefine.welike.business.startup.management.callback.DatabaseCallback;
import com.redefine.welike.common.WeightRandom;

import java.util.List;

/**
 * Created by honglin on 2018/5/9.
 */

public enum SplashEventStore {

    INSTANCE;

    private SplashEventDao mDao;

    public void init(Context context) {
        SplashEventDatabase db = Room.databaseBuilder(context.getApplicationContext(), SplashEventDatabase.class, "splash_table").build();
        mDao = db.eventDao();
    }

    public synchronized void insert(final SplashEntity event) {
        LogUtil.d("HL", "insert splash_table db : " + event.getId());
        new Thread(new Runnable() {
            @Override
            public void run() {
                mDao.insert(event);
            }
        }).start();

    }

    public synchronized void delete(final SplashEntity event) {
        LogUtil.d("HL", "delete splash_table db : " + event.getId());
        mDao.delete(event);
    }

    public synchronized void getSplashBean(final DatabaseCallback callback) {
        //在io线程进行数据库操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<SplashEntity> splashEntitys = mDao.queryCurrentShowEvent(System.currentTimeMillis() / 1000L);

                if (splashEntitys != null && splashEntitys.size() > 0) {

                    int index = WeightRandom.PercentageRandom(splashEntitys);

                    callback.onLoadEntity(splashEntitys.get(index));
                } else {
                    callback.onLoadEntity(null);
                }
            }
        }).start();


    }

    /**
     * 批量添加
     */
    public synchronized void insert(List<SplashEntity> list) {

        for (SplashEntity event : list) {
            insert(event);
        }
    }

    /**
     * 批量删除
     */
    public synchronized void delete() {
        mDao.deleteExpiredEntry();
    }

}
