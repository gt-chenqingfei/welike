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

import android.view.ViewGroup;

import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.BasePage;

public interface IPageFactory<T extends BasePage> {
    T instantiatePage(ViewGroup container, PageConfig config, PageCache cache);
}
