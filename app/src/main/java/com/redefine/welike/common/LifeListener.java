package com.redefine.welike.common;

import android.support.annotation.WorkerThread;

/**
 * Created by daining on 2018/4/17.
 */

public interface LifeListener {

    @WorkerThread
    void onFire(@Life.LifeEvent int event);
}
