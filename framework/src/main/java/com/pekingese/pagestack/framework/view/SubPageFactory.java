/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 2017/10/4
 * <p>
 * Description : description
 * <p>
 * Creation    : 2017/10/4
 * Author      : liwenbo0328@163.com
 * History     : Creation, 2017/10/4, bobo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.view;

import android.view.ViewGroup;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.BasePage;
import com.pekingese.pagestack.framework.page.BaseSubPage;
import com.pekingese.pagestack.framework.page.NoneSubPage;

import java.lang.reflect.Constructor;

public class SubPageFactory implements IPageFactory {

    private final IPageStackManager mPageStackManager;

    public SubPageFactory(IPageStackManager pageStackManager) {
        mPageStackManager = pageStackManager;
    }

    @Override
    public BasePage instantiatePage(ViewGroup container, PageConfig config, PageCache cache) {
        Class<? extends BasePage> clazz = config.pageClazz;
        BasePage page = null;
        if (clazz != null && BaseSubPage.class.isAssignableFrom(clazz)) {
            try {
                Constructor<? extends BasePage> constructor = clazz.getDeclaredConstructor(IPageStackManager.class,
                        PageConfig.class, PageCache.class);
                page = constructor.newInstance(mPageStackManager, config, cache);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (page == null || !(page instanceof BaseSubPage)) {
            page = new NoneSubPage(mPageStackManager, config, cache);
        }
        page.createAndAttach(container);
        return page;
    }

}
