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

import android.view.View;

import java.util.Comparator;

class ViewPositionComparator implements Comparator<View> {
    @Override
    public int compare(View lhs, View rhs) {
        final BasePageStack.LayoutParams llp = (BasePageStack.LayoutParams) lhs.getLayoutParams();
        final BasePageStack.LayoutParams rlp = (BasePageStack.LayoutParams) rhs.getLayoutParams();
        if (llp.isDecor != rlp.isDecor) {
            return llp.isDecor ? 1 : -1;
        }
        return llp.position - rlp.position;
    }
}
