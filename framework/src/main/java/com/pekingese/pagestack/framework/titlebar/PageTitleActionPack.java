/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 17/9/29
 * <p>
 * Description : description
 * <p>
 * Creation    : 17/9/29
 * Author      : liwenbo0328@163.com
 * History     : Creation, 17/9/29, liwenbo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.titlebar;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.pekingese.pagestack.framework.util.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

public class PageTitleActionPack {
    private Drawable background = null;
    private List<TitleAction> leftActions = new ArrayList<TitleAction>();
    private List<TitleAction> centerActions = new ArrayList<TitleAction>();
    private List<TitleAction> rightActions = new ArrayList<TitleAction>();

    public Drawable getBackgroundDrawable() {
        return background;
    }

    public List<TitleAction> getLeftActions() {
        return leftActions;
    }

    public List<TitleAction> getCenterActions() {
        return centerActions;
    }

    public List<TitleAction> getRightActions() {
        return rightActions;
    }

    public boolean isValid() {
        return !CollectionUtil.isArrayEmpty(leftActions) || !CollectionUtil.isArrayEmpty(centerActions) || !CollectionUtil.isArrayEmpty(rightActions);
    }

    public static class Builder {
        private Drawable backgroundDrawable = null;
        private int backgroundColor = Color.BLACK;
        private List<TitleAction> leftActions = new ArrayList<TitleAction>();
        private List<TitleAction> centerActions = new ArrayList<TitleAction>();
        private List<TitleAction> rightActions = new ArrayList<TitleAction>();

        public Builder addLeftAction(TitleAction action) {
            if (action != null) {
                leftActions.add(action);
            }
            return this;
        }

        public Builder addCenterAction(TitleAction action) {
            if (action != null) {
                centerActions.add(action);
            }
            return this;
        }

        public Builder addRightAction(TitleAction action) {
            if (action != null) {
                rightActions.add(action);
            }
            return this;
        }

        public Builder setBackgroundDrawable(Drawable drawable) {
            backgroundDrawable = drawable;
            return this;
        }

        public Builder setBackgroundColor(int color) {
            backgroundColor = color;
            return this;
        }

        public PageTitleActionPack build() {
            PageTitleActionPack pack = new PageTitleActionPack();
            if (!CollectionUtil.isArrayEmpty(leftActions)) {
                pack.leftActions.addAll(leftActions);
                leftActions.clear();
            }
            if (!CollectionUtil.isArrayEmpty(centerActions)) {
                pack.centerActions.addAll(centerActions);
                centerActions.clear();
            }
            if (!CollectionUtil.isArrayEmpty(rightActions)) {
                pack.rightActions.addAll(rightActions);
                rightActions.clear();
            }
            if (backgroundDrawable != null) {
                pack.background = backgroundDrawable;
                backgroundDrawable = null;
            } else {
                pack.background = new ColorDrawable(backgroundColor).mutate();
                backgroundColor = Color.BLACK;
            }
            return pack;
        }
    }

}
