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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface OnAdapterChangeListener {
    /**
     * Called when the adapter for the given view pager has changed.
     *
     * @param viewPager  ViewPager where the adapter change has happened
     * @param oldAdapter the previously set adapter
     * @param newAdapter the newly set adapter
     */
    void onAdapterChanged(@NonNull BasePageStack viewPager,
                          @Nullable BasePageStackAdapter oldAdapter, @Nullable BasePageStackAdapter newAdapter);
}
