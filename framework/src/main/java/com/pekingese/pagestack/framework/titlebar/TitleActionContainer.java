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

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class TitleActionContainer extends LinearLayout {
    public TitleActionContainer(Context context) {
        super(context);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        if (getOrientation() == HORIZONTAL) {
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            return params;
        } else if (getOrientation() == VERTICAL) {
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            return params;
        }
        return null;
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (lp == null) {
            return generateDefaultLayoutParams();
        }
        return super.generateLayoutParams(lp);
    }

    @Override
    public boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params,
                                      boolean preventRequestLayout) {
        return super.addViewInLayout(child, index, params, preventRequestLayout);
    }
}
