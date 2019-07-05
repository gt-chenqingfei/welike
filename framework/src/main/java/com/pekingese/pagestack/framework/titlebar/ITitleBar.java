/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 17/9/30
 * <p>
 * Description : description
 * <p>
 * Creation    : 17/9/30
 * Author      : liwenbo0328@163.com
 * History     : Creation, 17/9/30, liwenbo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.titlebar;

import android.view.View;

public interface ITitleBar {
    void applyTitleAction(PageTitleActionPack actionPack);
    View getView();
    void setChildrenAlpha(float i);

    void setTitleActionSubject(SingleTitleActionSubject subject);

    void offsetTitle(float positionOffset);
}
