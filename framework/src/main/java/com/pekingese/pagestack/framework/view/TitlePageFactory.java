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
import com.pekingese.pagestack.framework.page.BaseTitlePage;
import com.pekingese.pagestack.framework.page.TitleNonePage;

import java.lang.reflect.Constructor;

public class TitlePageFactory implements IPageFactory<BaseTitlePage> {

    private final IPageStackManager mPageStackManager;

    public TitlePageFactory(IPageStackManager pageStackManager) {
        mPageStackManager = pageStackManager;
    }

    @Override
    public BaseTitlePage instantiatePage(ViewGroup container, PageConfig config, PageCache cache) {
        Class<? extends BasePage> clazz = config.pageClazz;
        BaseTitlePage page = null;
        if (clazz != null && BaseTitlePage.class.isAssignableFrom(clazz)) {
            try {
                Constructor<? extends BasePage> constructor = clazz.getDeclaredConstructor(IPageStackManager.class,
                        PageConfig.class, PageCache.class);
                page = (BaseTitlePage) constructor.newInstance(mPageStackManager, config, cache);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (page == null) {
            page = new TitleNonePage(mPageStackManager, config, cache);
        }
        page.createAndAttach(container);
        return page;
    }

}
