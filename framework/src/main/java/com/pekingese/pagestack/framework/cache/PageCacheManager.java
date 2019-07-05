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
package com.pekingese.pagestack.framework.cache;

import com.pekingese.pagestack.framework.IPageCacheManager;
import com.pekingese.pagestack.framework.config.PageConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PageCacheManager implements IPageCacheManager {

    private Map<PageConfig, PageCache> mPageCaches = new HashMap<PageConfig, PageCache>();

    @Override
    public void addPageCache(PageConfig pageConfig, PageCache pageCache) {
        mPageCaches.put(pageConfig, pageCache);
    }

    @Override
    public void removePageCache(PageConfig pageConfig) {
        mPageCaches.remove(pageConfig);
    }

    @Override
    public void removePageCache(List<PageConfig> configs) {
         if (configs == null) {
             return ;
         }
         for (PageConfig config : configs) {
             mPageCaches.remove(config);
         }
    }

    @Override
    public PageCache getPageCache(PageConfig config) {
        return mPageCaches.get(config);
    }
}
