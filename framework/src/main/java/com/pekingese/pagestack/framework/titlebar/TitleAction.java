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

import android.text.TextUtils;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.constant.CommonConstant;


public class TitleAction {

    public static final int ACTION_NONE = 0;
    public static final int ACTION_CLICK = 0x00000001;

    public TitleAction() {

    }

    private int actionId = CommonConstant.INVALID_ID;
    private int actionType = ACTION_NONE;
    private String actionText = "";
    private int actionTextId = CommonConstant.INVALID_ID;
    private int drawableLeftId = CommonConstant.INVALID_ID;
    private int drawableTopId = CommonConstant.INVALID_ID;
    private int drawableRightId = CommonConstant.INVALID_ID;
    private int drawableBottomId = CommonConstant.INVALID_ID;
    private int imageId = CommonConstant.INVALID_ID;
    private int drawablePadding = 0;
    private int width = ViewGroup.LayoutParams.WRAP_CONTENT;  // for dp
    private int height = ViewGroup.LayoutParams.MATCH_PARENT; // for dp

    public int getActionId() {
        return actionId;
    }

    public int getActionType() {
        return actionType;
    }

    public String getActionText() {
        return actionText;
    }

    public int getActionTextId() {
        return actionTextId;
    }

    public int getDrawableLeftId() {
        return drawableLeftId;
    }

    public int getDrawableTopId() {
        return drawableTopId;
    }

    public int getDrawableRightId() {
        return drawableRightId;
    }

    public int getDrawableBottomId() {
        return drawableBottomId;
    }

    public int getDrawablePadding() {
        return drawablePadding;
    }

    public int getImageId() {
        return imageId;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isValid() {
        return actionTextId != CommonConstant.INVALID_ID
                || !TextUtils.isEmpty(actionText)
                || imageId != CommonConstant.INVALID_ID;
    }

    public boolean hasDrawable() {
        return drawableLeftId != CommonConstant.INVALID_ID
                || drawableTopId != CommonConstant.INVALID_ID
                || drawableRightId != CommonConstant.INVALID_ID
                || drawableBottomId != CommonConstant.INVALID_ID;
    }

    public static class Builder {

        private int actionId = CommonConstant.INVALID_ID;
        private int actionType = ACTION_NONE;
        private String actionText = "";
        private int actionTextId = CommonConstant.INVALID_ID;
        private int drawableLeftId = CommonConstant.INVALID_ID;
        private int drawableTopId = CommonConstant.INVALID_ID;
        private int drawableRightId = CommonConstant.INVALID_ID;
        private int drawableBottomId = CommonConstant.INVALID_ID;
        private int imageId = CommonConstant.INVALID_ID;
        private int drawablePadding = 0;
        private int width = 50;  // for dp
        private int height = 50; // for dp

        public Builder(int actionId) {
            this.actionId = actionId;
            this.actionType = ACTION_CLICK;
        }

        public Builder(int actionId, int actionType) {
            this.actionId = actionId;
            this.actionType = actionType;
        }

        public Builder setActionText(String text) {
            this.actionText = text;
            return this;
        }

        public Builder setActionTextId(int textId) {
            this.actionTextId = textId;
            return this;
        }

        public Builder setDrawableLeft(int id) {
            this.drawableLeftId = id;
            return this;
        }

        public Builder setDrawableTop(int id) {
            this.drawableTopId = id;
            return this;
        }

        public Builder setDrawableRight(int id) {
            this.drawableRightId = id;
            return this;
        }

        public Builder setDrawableBottom(int id) {
            this.drawableBottomId = id;
            return this;
        }

        public Builder setDrawablePadding(int drawablePadding) {
            this.drawablePadding = drawablePadding;
            return this;
        }

        public Builder setImage(int id) {
            this.imageId = id;
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public TitleAction build() {
            TitleAction titleAction = new TitleAction();
            titleAction.actionId = actionId;
            titleAction.actionType = actionType;
            titleAction.actionText = actionText;
            titleAction.actionTextId = actionTextId;
            titleAction.drawableLeftId = drawableLeftId;
            titleAction.drawableTopId = drawableTopId;
            titleAction.drawableRightId = drawableRightId;
            titleAction.drawableBottomId = drawableBottomId;
            titleAction.imageId = imageId;
            titleAction.drawablePadding = drawablePadding;
            titleAction.width = width;
            titleAction.height = height;
            return titleAction;
        }
    }


}
