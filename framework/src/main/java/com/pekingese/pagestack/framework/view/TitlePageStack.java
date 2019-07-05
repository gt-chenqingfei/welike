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

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.page.BaseSubPage;
import com.pekingese.pagestack.framework.page.BaseTitlePage;
import com.pekingese.pagestack.framework.statusbar.IPageStatusBarManager;
import com.pekingese.pagestack.framework.statusbar.PageStatusBarManager;

public class TitlePageStack extends PageStack {

//    private IPageStatusBarManager mStatusBarManager;

    public TitlePageStack(Context context, IPageStackManager manager, IPageFactory pageFactory) {
        super(context, manager, pageFactory);
        initTitle();
    }

    private void initTitle() {
//        mStatusBarManager = new PageStatusBarManager(getContext());
    }

    @Override
    protected TitlePageStackAdapter initPageAdapter() {
        return new TitlePageStackAdapter(mPageFactory);
    }

    @Override
    public void onPageScrollStart() {
        super.onPageScrollStart();
//        ItemInfo lastInfo = infoForPosition(getLastPosition());
//        ItemInfo preInfo = infoForPosition(getSecondToLastPosition());
//
//        if (preInfo != null && lastInfo != null && lastInfo.object instanceof BaseTitlePage && preInfo.object instanceof BaseTitlePage) {
//            BaseTitlePage lastPage = (BaseTitlePage) lastInfo.object;
//            BaseTitlePage prePage = (BaseTitlePage) preInfo.object;
//            mStatusBarManager.applyPageStatusBarConfig(prePage.getStatusBarConfig(), lastPage.getStatusBarConfig());
//        }
    }

    @Override
    public void onPageScrolled(float positionOffset, int offsetPixels) {
        super.onPageScrolled(positionOffset, offsetPixels);
        if (isSmall(positionOffset)) {
            return ;
        }
//        mStatusBarManager.applyPageScrollOffset(positionOffset);
    }

    @Override
    public void onPageScrollEnd(int currentItem) {
        super.onPageScrollEnd(currentItem);
//        ItemInfo lastInfo = infoForPosition(getLastPosition());
//        if (lastInfo != null &&  lastInfo.object instanceof BaseSubPage) {
//            BaseSubPage lastPage = (BaseSubPage) lastInfo.object;
//            mStatusBarManager.applyPageScrollEnd(lastPage.getStatusBarConfig());
//        }
    }

    private boolean isSmall(float position) {
        return Math.abs(position) <= 0.001;
    }
}
