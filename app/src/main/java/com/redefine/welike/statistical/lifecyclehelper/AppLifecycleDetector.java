package com.redefine.welike.statistical.lifecyclehelper;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.redefine.foundation.utils.LogUtil;
import com.redefine.welike.statistical.EventLog1;

/**
 * Created by nianguowang on 2018/12/3
 */
public class AppLifecycleDetector implements Application.ActivityLifecycleCallbacks {

    private int mStartActivityCount;
    private long mStartTime;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) { }

    @Override
    public void onActivityStarted(Activity activity) { }

    @Override
    public void onActivityResumed(Activity activity) {
        if (mStartActivityCount == 0 && mStartTime == 0) {
            mStartTime = System.currentTimeMillis();
        }
        mStartActivityCount++;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        mStartActivityCount--;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (mStartActivityCount == 0 && mStartTime != 0) {
            long duration = System.currentTimeMillis() - mStartTime;
            EventLog1.StayTime.report1(duration);
            mStartTime = 0;
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

}
