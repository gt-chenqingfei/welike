/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 17/11/3
 * <p>
 * Description : description
 * <p>
 * Creation    : 17/11/3
 * Author      : liwenbo0328@163.com
 * History     : Creation, 17/11/3, liwenbo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.statusbar;

import com.pekingese.pagestack.framework.config.StatusBarConfig;

public interface IPageStatusBarManager {
    void applyPageStatusBarConfig(StatusBarConfig preConfig, StatusBarConfig currentConfig);

    void applyPageScrollOffset(float positionOffset);

    void applyPageScrollEnd(StatusBarConfig statusBarConfig);
}
