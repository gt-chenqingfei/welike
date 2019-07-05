package com.redefine.welike.statistical.lifecyclehelper;

import com.pekingese.pagestack.framework.page.BasePage;
import com.pekingese.pagestack.framework.util.PageNameUtil;
import com.pekingese.pagestack.framework.view.BasePageStack;
import com.redefine.welike.statistical.EventLog;

/**
 * Created by nianguowang on 2018/4/27
 */
public class PageLifecycleHelper implements BasePageStack.IPageStateChangeListener {

    private long mStartTime;
    private BasePage mCurrentPage;

    @Override
    public void onPageStateChange(BasePage basePage, int oldPageState, int newPageState) {
        if (newPageState == BasePage.PAGE_STATE_SHOW) {
            mCurrentPage = basePage;
            mStartTime = System.currentTimeMillis();
            EventLog.ShowPage.report1(new PageNameUtil().getPageName(basePage.getClass()));
        }

        if ((newPageState == BasePage.PAGE_STATE_HIDE || newPageState == BasePage.PAGE_STATE_DESTROY)
                && oldPageState == BasePage.PAGE_STATE_SHOW) {
            long endTime = System.currentTimeMillis();
            long stayTime = Math.abs(endTime - mStartTime);
            EventLog.ShowPage.report2(new PageNameUtil().getPageName(basePage.getClass()), stayTime);
            mStartTime = endTime;
        }
    }

    public void onActivityResume(BasePage basePage) {
        if (basePage == null) {
            return;
        }
        if (basePage == mCurrentPage) {
            mStartTime = System.currentTimeMillis();
        }
    }

    public void onActivityPause(BasePage basePage) {
        if (basePage == null) {
            return;
        }
        if (basePage == mCurrentPage) {
            long endTime = System.currentTimeMillis();
            long stayTime = Math.abs(endTime - mStartTime);
            mStartTime = endTime;
            EventLog.ShowPage.report2(new PageNameUtil().getPageName(basePage.getClass()), stayTime);
        }
    }

}
