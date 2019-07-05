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
package com.pekingese.pagestack.framework.titlestrip;

import android.view.View;

import com.pekingese.pagestack.framework.titlebar.PageTitleActionPack;
import com.pekingese.pagestack.framework.titlebar.TitleActionObserverDelegate;

public interface ISubPageTitleManager {
    View getView();

    void applyPageScrollOffset(float positionOffset);

    void applyPageScrollEnd(PageTitleActionPack titleAction);

    void applyPageTitleActions(PageTitleActionPack titleAction, PageTitleActionPack titleAction1);

    void setTitleActionObserver(TitleActionObserverDelegate observer);
}
