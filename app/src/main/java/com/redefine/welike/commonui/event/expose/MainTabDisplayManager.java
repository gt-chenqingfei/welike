package com.redefine.welike.commonui.event.expose;

import android.text.TextUtils;

import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;

/**
 * Created by nianguowang on 2018/12/4
 */
public class MainTabDisplayManager {

    private String mCurrentTab;
    private long mStartShowTime;

    private MainTabDisplayManager() {}

    private static class MainTabDisplayManagerHolder {
        private static MainTabDisplayManager sInstance = new MainTabDisplayManager();
    }

    public static MainTabDisplayManager getInstance() {
        return MainTabDisplayManagerHolder.sInstance;
    }

    public void switchTab(String tabName) {
        if (TextUtils.equals(mCurrentTab, tabName)) {
            return;
        }
        if (mCurrentTab == null) {
            reportShow(tabName);

            mCurrentTab = tabName;
            mStartShowTime = System.currentTimeMillis();
            return;
        }
        reportShow(tabName);

        long duration = System.currentTimeMillis() - mStartShowTime;
        reportStay(mCurrentTab, duration);

        mCurrentTab = tabName;
        mStartShowTime = System.currentTimeMillis();
    }

    public void onMainActivityResume() {
        if (mCurrentTab == null) {
            return;
        }
        reportShow(mCurrentTab);
        mStartShowTime = System.currentTimeMillis();
    }

    public void onMainActivityPause() {
        if (mCurrentTab == null || mStartShowTime == 0) {
            return;
        }
        long duration = System.currentTimeMillis() - mStartShowTime;
        reportStay(mCurrentTab, duration);
    }

    private void reportShow(String pageName) {
        EventLog1.ShowPage.report1(pageName);
        EventLog.ShowPage.report1(pageName);
    }

    private void reportStay(String pageName, long stayTime) {
        EventLog1.ShowPage.report2(pageName, stayTime);
        EventLog.ShowPage.report2(pageName, stayTime);
    }
}
