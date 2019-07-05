/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 2017/8/2
 * <p>
 * Description : 用来处理当前Page的缓存，只用于保存无法序列化的对象，只针对本次启动生效，
 * 被干掉，或者杀进程，cache丢失
 * <p>
 * Creation    : 2017/8/2
 * Author      : liwenbo0328@163.com
 * History     : Creation, 2017/8/2, bobo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework;

import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;

import java.util.List;

public interface IPageCacheManager {
    void addPageCache(PageConfig pageConfig, PageCache pageCache);

    void removePageCache(PageConfig rightPageConfig);

    void removePageCache(List<PageConfig> configs);

    PageCache getPageCache(PageConfig config);
}
