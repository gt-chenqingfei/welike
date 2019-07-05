/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 17/7/25
 * <p>
 * Description : description
 * <p>
 * Creation    : 17/7/25
 * Author      : liwenbo0328@163.com
 * History     : Creation, 17/7/25, liwenbo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.view;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.IPageCacheManager;
import com.pekingese.pagestack.framework.IPageConfigManager;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.cache.PageCacheManager;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.config.PageConfigManager;
import com.pekingese.pagestack.framework.config.PageSaveState;
import com.pekingese.pagestack.framework.config.PageStackSaveState;
import com.pekingese.pagestack.framework.page.BasePage;

import java.util.List;

public class PageStackAdapter extends BasePageStackAdapter {

    private final IPageConfigManager mPageConfigs = new PageConfigManager();
    private final IPageCacheManager mPageCacheManager = new PageCacheManager();
    private final IPageFactory mPageFactory;

    public PageStackAdapter(IPageFactory factory) {
        mPageFactory = factory;
    }

    @Override
    public BasePage instantiateItem(ViewGroup container, int position) {
        PageConfig config = mPageConfigs.getPageConfig(position);
        PageCache cache = mPageCacheManager.getPageCache(config);
        return mPageFactory.instantiatePage(container, config, cache);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, BasePage object) {
        object.destroy(container);
    }

    @Override
    public void attachItem(ViewGroup container, int position, BasePage object) {
        object.attach(container);
    }

    @Override
    public void detachItem(ViewGroup container, int position, BasePage object) {
        object.detach(container);
    }

    @Override
    public int getCount() {
        return mPageConfigs.size();
    }

    @Override
    public int getItemPosition(BasePage object) {
        int position = mPageConfigs.getPagePosition(object.getPageConfig());
        if (position == -1) {
            position = POSITION_NONE;
        }
        return position;
    }

    @Override
    public boolean isViewFromObject(View view, BasePage object) {
        return object.getView() == view;
    }

    @Override
    public Parcelable saveState() {
        return new PageStackSaveState(mPageConfigs.getSaveState());
    }

    @Override
    public void restorePageState(Bundle savedInstanceState) {
        super.restorePageState(savedInstanceState);
        if (savedInstanceState == null) {
            return ;
        }
        List<PageConfig> configs = mPageConfigs.getAllPageConfigs();
        Parcelable parcelable = null;
        for (PageConfig config : configs) {
            if (config == null) {
                continue;
            }
            parcelable = savedInstanceState.getParcelable(BasePage.PAGE_SAVE_STATE + config.position);
            if (parcelable != null && parcelable instanceof PageSaveState) {
                mPageCacheManager.addPageCache(config, new PageCache((PageSaveState) parcelable));
            }
        }
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        PageStackSaveState saveState = (PageStackSaveState) state;
        if (saveState != null) {
            mPageConfigs.restoreState(saveState);
        }
    }


    public void addPageConfig(PageConfig pageConfig, PageCache pageCache) {
        mPageConfigs.pushPageConfig(pageConfig);
        mPageCacheManager.addPageCache(pageConfig, pageCache);
    }

    public List<PageConfig> removePositionRight(int curItem) {
        return mPageConfigs.removePositionRight(curItem);
    }

    public PageConfig getPageConfig(int currentItem) {
        return mPageConfigs.getPageConfig(currentItem);
    }

    public void removePageCache(List<PageConfig> configs) {
        mPageCacheManager.removePageCache(configs);
    }

    public List<PageConfig> findPageConfigByType(Class<? extends BasePage> pageClass) {
        return mPageConfigs.findPageConfigByType(pageClass);
    }

    public PageConfig findPageConfigByIndex(int index) {
        return mPageConfigs.findPageConfigByIndex(index);
    }
}
