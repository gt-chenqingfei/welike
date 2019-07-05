package com.redefine.welike.statistical.lifecyclehelper;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.text.format.DateUtils;

import com.redefine.foundation.utils.LogUtil;
import com.redefine.welike.business.assignment.ui.presenter.AssignmentNotifyPresenter;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;

/**
 * Created by nianguowang on 2018/4/27
 */
public class ActivityLifecycleHelper implements Application.ActivityLifecycleCallbacks {
    private long mLastUploadTime = 0;
    private long mLastShowTime;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        LogUtil.d("wng", activity.getClass().getSimpleName() + "onResume");
        mLastShowTime = System.currentTimeMillis();
        EventLog.ShowPage.report1(activity.getClass().getName());
        EventLog1.ShowPage.report1(activity.getClass().getName());
        if(mLastUploadTime == 0 || !DateUtils.isToday(mLastUploadTime)) {
            EventLog.LaunchApp.report2();
            EventLog1.LaunchApp.report1(EventLog1.LaunchApp.FromPush.FROM_OTHER);
            mLastUploadTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        LogUtil.d("wng", activity.getClass().getSimpleName() + "onPause");
        long endTime = System.currentTimeMillis();
        long stayTime = Math.abs(endTime - mLastShowTime);
        EventLog.ShowPage.report2(activity.getClass().getName(), stayTime);
        EventLog1.ShowPage.report2(activity.getClass().getName(), stayTime);
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (activity != null) {
            AssignmentNotifyPresenter.checkNotify(activity.getIntent());
        }
    }
}
