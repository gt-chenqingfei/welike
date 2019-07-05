/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 2017/10/3
 * <p>
 * Description : description
 * <p>
 * Creation    : 2017/10/3
 * Author      : liwenbo0328@163.com
 * History     : Creation, 2017/10/3, bobo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.view;

import android.content.Context;
import android.view.Gravity;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.constant.CommonConstant;
import com.pekingese.pagestack.framework.page.BaseSubPage;
import com.pekingese.pagestack.framework.statusbar.IPageStatusBarManager;
import com.pekingese.pagestack.framework.statusbar.PageStatusBarManager;
import com.pekingese.pagestack.framework.titlebar.TitleActionObserverDelegate;
import com.pekingese.pagestack.framework.titlestrip.ISubPageTitleManager;
import com.pekingese.pagestack.framework.titlestrip.SubPageTitleStrip;
import com.pekingese.pagestack.framework.util.DeviceUtil;

public class SubPageStack extends PageStack {

    private ISubPageTitleManager mPageTitleStrip;
    private IPageStatusBarManager mStatusBarManager;

    public SubPageStack(Context context, IPageStackManager manager, IPageFactory pageFactory) {
        super(context, manager, pageFactory);
        initTitle();
    }

    private void initTitle() {
        mPageTitleStrip = new SubPageTitleStrip(getContext());
        mStatusBarManager = new PageStatusBarManager(getContext());
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, DeviceUtil.dp2px(getResources(), CommonConstant.DEFAULT_TITLE_ACTION_HEIGHT));
        params.gravity = Gravity.TOP;
        params.isDecor = true;
        addView(mPageTitleStrip.getView(), params);
        TitleActionObserverDelegate observer = new TitleActionObserverDelegate();
        mPageTitleStrip.setTitleActionObserver(observer);
        ((TitlePageStackAdapter) getAdapter()).setTitleActionObserver(observer);
    }

    @Override
    protected TitlePageStackAdapter initPageAdapter() {
        return new TitlePageStackAdapter(new SubPageFactory(mPageStackManager));
    }

    @Override
    public void onPageScrollStart() {
        super.onPageScrollStart();
        ItemInfo lastInfo = infoForPosition(getLastPosition());
        ItemInfo preInfo = infoForPosition(getSecondToLastPosition());

        if (preInfo != null && lastInfo != null && lastInfo.object instanceof BaseSubPage && preInfo.object instanceof BaseSubPage) {
            BaseSubPage lastPage = (BaseSubPage) lastInfo.object;
            BaseSubPage prePage = (BaseSubPage) preInfo.object;
            mPageTitleStrip.applyPageTitleActions(prePage.getTitleAction(), lastPage.getTitleAction());
            mStatusBarManager.applyPageStatusBarConfig(prePage.getStatusBarConfig(), lastPage.getStatusBarConfig());
        }
    }

    @Override
    public void onPageScrolled(float positionOffset, int offsetPixels) {
        super.onPageScrolled(positionOffset, offsetPixels);
        if (isSmall(positionOffset)) {
            return ;
        }
        mPageTitleStrip.applyPageScrollOffset(positionOffset);
        mStatusBarManager.applyPageScrollOffset(positionOffset);
    }

    @Override
    public void onPageScrollEnd(int currentItem) {
        super.onPageScrollEnd(currentItem);
        ItemInfo lastInfo = infoForPosition(getLastPosition());
        if (lastInfo != null &&  lastInfo.object instanceof BaseSubPage) {
            BaseSubPage lastPage = (BaseSubPage) lastInfo.object;
            mPageTitleStrip.applyPageScrollEnd(lastPage.getTitleAction());
            mStatusBarManager.applyPageScrollEnd(lastPage.getStatusBarConfig());
        }
    }

    private boolean isSmall(float position) {
        return Math.abs(position) <= 0.001;
    }
}
