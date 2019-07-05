/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 17/7/25
 * <p>
 * Description : Page堆栈接口，用于处理Page的入栈和出栈
 * <p>
 * Creation    : 17/7/25
 * Author      : liwenbo0328@163.com
 * History     : Creation, 17/7/25, liwenbo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework;

import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.BasePage;

import java.util.List;

public interface IPageStack {
    void pushPage(PageConfig pageConfig);

    void pushPage(PageConfig pageConfig, PageCache pageCache);

    boolean popPage();

    boolean popToRootPage();

    List<PageConfig> findPageConfigByType(Class<? extends BasePage> pageClass);
    
    PageConfig findPageConfigByIndex(int index);
}
