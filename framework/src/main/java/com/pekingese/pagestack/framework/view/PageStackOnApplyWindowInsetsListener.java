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

import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

@RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
class PageStackOnApplyWindowInsetsListener implements View.OnApplyWindowInsetsListener {
    private final Rect mTempRect = new Rect();
    private final ViewGroup mPageStack;

    PageStackOnApplyWindowInsetsListener(BasePageStack stack) {
        mPageStack = stack;
    }

    @Override
    public WindowInsets onApplyWindowInsets(final View v,
                                                  final WindowInsets originalInsets) {
        // First let the ViewPager itself try and consume them...
        final WindowInsets applied = v.onApplyWindowInsets(originalInsets);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && applied.isConsumed()) {
            // If the ViewPager consumed all insets, return now
            return applied;
        }

        // Now we'll manually dispatch the insets to our children. Since ViewPager
        // children are always full-height, we do not want to use the standard
        // ViewGroup dispatchApplyWindowInsets since if child 0 consumes them,
        // the rest of the children will not receive any insets. To workaround this
        // we manually dispatch the applied insets, not allowing children to
        // consume them from each other. We do however keep track of any insets
        // which are consumed, returning the union of our children's consumption
        final Rect res = mTempRect;
        res.left = applied.getSystemWindowInsetLeft();
        res.top = applied.getSystemWindowInsetTop();
        res.right = applied.getSystemWindowInsetRight();
        res.bottom = applied.getSystemWindowInsetBottom();

        for (int i = 0, count = mPageStack.getChildCount(); i < count; i++) {
            final WindowInsets childInsets = mPageStack.getChildAt(i)
                    .dispatchApplyWindowInsets(applied);
            // Now keep track of any consumed by tracking each dimension's min
            // value
            res.left = Math.min(childInsets.getSystemWindowInsetLeft(),
                    res.left);
            res.top = Math.min(childInsets.getSystemWindowInsetTop(),
                    res.top);
            res.right = Math.min(childInsets.getSystemWindowInsetRight(),
                    res.right);
            res.bottom = Math.min(childInsets.getSystemWindowInsetBottom(),
                    res.bottom);
        }

        // Now return a new WindowInsets, using the consumed window insets
        return applied.replaceSystemWindowInsets(
                res.left, res.top, res.right, res.bottom);
    }

}
