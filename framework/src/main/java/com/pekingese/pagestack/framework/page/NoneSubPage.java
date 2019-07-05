/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 2017/8/6
 * <p>
 * Description : description
 * <p>
 * Creation    : 2017/8/6
 * Author      : liwenbo0328@163.com
 * History     : Creation, 2017/8/6, bobo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.page;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.config.StatusBarConfig;
import com.pekingese.pagestack.framework.constant.CommonConstant;
import com.pekingese.pagestack.framework.titlebar.PageTitleActionPack;
import com.pekingese.pagestack.framework.titlebar.TitleAction;

public class NoneSubPage extends BaseSubPage {

    public NoneSubPage(IPageStackManager manager, PageConfig config, PageCache cache) {
        super(manager, config, cache);
    }

    @Override
    protected void initView(ViewGroup container, Bundle saveState) {
        getView().setBackgroundColor(Color.WHITE);
    }

    @NonNull
    @Override
    protected View createView(ViewGroup container, Bundle saveState) {
        FrameLayout frameLayout = new FrameLayout(container.getContext());
        frameLayout.setWillNotDraw(true);
        return frameLayout;
    }

    @Override
    protected StatusBarConfig initStatusBarConfig() {
        return new StatusBarConfig.Builder(true, true).build();
    }

    @Override
    protected PageTitleActionPack initTitleActions() {
        return new PageTitleActionPack.Builder().addCenterAction(new TitleAction.Builder(CommonConstant.INVALID_ID).build()).build();
    }
}
