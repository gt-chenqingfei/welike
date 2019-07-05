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

import android.database.DataSetObserver;

public class PagerObserver extends DataSetObserver {

    private final BasePageStack mPageStack;

    PagerObserver(BasePageStack stack) {
        mPageStack = stack;
    }

    @Override
    public void onChanged() {
        mPageStack.dataSetChanged();
    }

    @Override
    public void onInvalidated() {
        mPageStack.dataSetChanged();
    }
}
