/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 17/8/1
 * <p>
 * Description : description
 * <p>
 * Creation    : 17/8/1
 * Author      : liwenbo0328@163.com
 * History     : Creation, 17/8/1, liwenbo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.config;

import android.os.Parcelable;

import com.pekingese.pagestack.framework.IPageConfigManager;
import com.pekingese.pagestack.framework.page.BasePage;
import com.pekingese.pagestack.framework.util.CollectionUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class PageConfigManager implements IPageConfigManager {
    private final Stack<PageConfig> mPageConfigs = new Stack<PageConfig>();

    @Override
    public int size() {
        return mPageConfigs.size();
    }

    @Override
    public PageConfig getPageConfig(int position) {
        if (position < 0 || position >= mPageConfigs.size()) {
            return null;
        }
        return mPageConfigs.get(position);
    }

    @Override
    public void pushPageConfig(PageConfig pageConfig) {
        mPageConfigs.add(pageConfig);
    }

    @Override
    public List<PageConfig> removePositionRight(int curItem) {
        List<PageConfig> configs = new ArrayList<PageConfig>();
        while (size() - 1 > curItem && size() > 0) {
            configs.add(mPageConfigs.pop());
        }
        return configs;
    }

    @Override
    public int getPagePosition(PageConfig pageConfig) {
        return mPageConfigs.indexOf(pageConfig);
    }

    @Override
    public Stack<PageConfig> getSaveState() {
        return mPageConfigs;
    }

    @Override
    public void restoreState(PageStackSaveState saveState) {
        if (!CollectionUtil.isArrayEmpty(saveState.mSaveState)) {
            for (Parcelable parcelable : saveState.mSaveState) {
                mPageConfigs.push((PageConfig) parcelable);
            }
        }
    }

    @Override
    public Stack<PageConfig> getAllPageConfigs() {
        return mPageConfigs;
    }

    @Override
    public List<PageConfig> findPageConfigByType(Class<? extends BasePage> pageClass) {
        List<PageConfig> pageConfigs = new ArrayList<>();
        for (PageConfig config : mPageConfigs) {
            if (config.pageClazz == pageClass) {
                pageConfigs.add(config);
            }
        }
        return pageConfigs;
    }

    @Override
    public PageConfig findPageConfigByIndex(int index) {
        return mPageConfigs.get(index);
    }
}
