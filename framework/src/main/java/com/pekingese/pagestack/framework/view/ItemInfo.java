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

import com.pekingese.pagestack.framework.page.BasePage;

class ItemInfo {
    BasePage object;
    int position;
    boolean scrolling;
    float widthFactor;
    float offset;
    int pageState;

    public boolean isDetached() {
        return pageState == BasePage.PAGE_STATE_DETACH;
    }

    public boolean isShown() {
        return pageState == BasePage.PAGE_STATE_SHOW;
    }

    public boolean isHide() {
        return pageState == BasePage.PAGE_STATE_HIDE;
    }

    public void updatePosition(int position) {
        this.position = position;
        if (object != null) {
            object.getPageConfig().position = position;
        }
    }
}
