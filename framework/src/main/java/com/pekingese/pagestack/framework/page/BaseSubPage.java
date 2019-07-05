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
package com.pekingese.pagestack.framework.page;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.config.StatusBarConfig;
import com.pekingese.pagestack.framework.titlebar.ITitleActionObserver;
import com.pekingese.pagestack.framework.titlebar.PageTitleActionPack;
import com.pekingese.pagestack.framework.titlebar.TitleAction;

public abstract class BaseSubPage<T extends IPageStackManager> extends BasePage<T> implements ITitleActionObserver {
    protected final PageTitleActionPack mTitleActions;
    protected final StatusBarConfig mStatusConfig;

    public BaseSubPage(T manager, PageConfig config, PageCache cache) {
        super(manager, config, cache);
        initPage();
        mTitleActions = initTitleActions();
        mStatusConfig = initStatusBarConfig();
    }

    protected void initPage() {

    }

    protected abstract StatusBarConfig initStatusBarConfig();

    protected abstract PageTitleActionPack initTitleActions();

    public PageTitleActionPack getTitleAction() {
        return mTitleActions;
    }

    public StatusBarConfig getStatusBarConfig() {
        return mStatusConfig;
    }

    @Override
    public void onTitleActionFired(TitleAction action) {

    }
}
