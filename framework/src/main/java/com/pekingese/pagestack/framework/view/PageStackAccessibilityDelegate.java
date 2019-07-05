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

import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

class PageStackAccessibilityDelegate extends View.AccessibilityDelegate {

    private final BasePageStack mPageStack;
    private final BasePageStackAdapter mAdapter;

    PageStackAccessibilityDelegate(BasePageStack stack) {
        mPageStack = stack;
        mAdapter = mPageStack.getAdapter();
    }


    @Override
    public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(host, event);
        event.setClassName(BasePageStack.class.getName());
        event.setScrollable(canScroll());
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED
                && mAdapter != null) {
            event.setItemCount(mAdapter.getCount());
            event.setFromIndex(mPageStack.getCurrentItem());
            event.setToIndex(mPageStack.getCurrentItem());
        }
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(host, info);
        info.setClassName(BasePageStack.class.getName());
        info.setScrollable(canScroll());
        if (mPageStack.canScrollHorizontally(1)) {
            info.addAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        }
        if (mPageStack.canScrollHorizontally(-1)) {
            info.addAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
        }
    }

    @Override
    public boolean performAccessibilityAction(View host, int action, Bundle args) {
        if (super.performAccessibilityAction(host, action, args)) {
            return true;
        }
        switch (action) {
            case AccessibilityNodeInfo.ACTION_SCROLL_FORWARD: {
                if (mPageStack.canScrollHorizontally(1)) {
                    mPageStack.setCurrentItem(mPageStack.getCurrentItem() + 1);
                    return true;
                }
            }
            return false;
            case AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD: {
                if (mPageStack.canScrollHorizontally(-1)) {
                    mPageStack.setCurrentItem(mPageStack.getCurrentItem() - 1);
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private boolean canScroll() {
        return (mAdapter != null) && (mAdapter.getCount() > 1);
    }
}
