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

import com.pekingese.pagestack.framework.config.PageSaveState;

public class PageCache {
    // Activity被杀保存的用于page恢复的状态
    public PageSaveState savedInstanceState;

    public PageCache(PageSaveState parcelabl) {
        savedInstanceState = parcelabl;
    }
}
