/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 17/8/1
 * <p>
 * Description : description
 * <p>
 * Creation    : 17/8/1
 * Author      : liwenbo0328@163.com
 * History     : Creation, 17/8/1, liwenbo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.config;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.pekingese.pagestack.framework.constant.CommonConstant;
import com.pekingese.pagestack.framework.page.BasePage;
import com.pekingese.pagestack.framework.page.NonePage;
import com.pekingese.pagestack.framework.transformer.TransitionEffect;

import java.io.Serializable;

public class PageConfig implements Parcelable {

    public int position = CommonConstant.INVALID_POSITION;
    public boolean isPushWithAnimation = true;
    public boolean isPopWithAnimation = true;
    public boolean isUserGestureEnable = true;
    public boolean isCanDragFromEdge = true;
    public boolean isAlwaysRetain = false;
    public TransitionEffect effect = TransitionEffect.Ios_Stack;
    public Class<? extends BasePage> pageClazz;
    public int orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    public Bundle pageBundle;
    public boolean isFitSystemWindow = false;

    private PageConfig() {

    }

    protected PageConfig(Parcel in) {
        position = in.readInt();
        isPushWithAnimation = in.readByte() != 0;
        isPopWithAnimation = in.readByte() != 0;
        isUserGestureEnable = in.readByte() != 0;
        isCanDragFromEdge = in.readByte() != 0;
        isAlwaysRetain = in.readByte() != 0;
        effect = (TransitionEffect) in.readSerializable();
        pageClazz = (Class<? extends BasePage>) in.readSerializable();
        orientation = in.readInt();
        pageBundle = in.readBundle(getClass().getClassLoader());
        isFitSystemWindow = in.readByte() != 0;
    }

    public static final Creator<PageConfig> CREATOR = new Creator<PageConfig>() {
        @Override
        public PageConfig createFromParcel(Parcel in) {
            return new PageConfig(in);
        }

        @Override
        public PageConfig[] newArray(int size) {
            return new PageConfig[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(position);
        dest.writeByte((byte) (isPushWithAnimation ? 1 : 0));
        dest.writeByte((byte) (isPopWithAnimation ? 1 : 0));
        dest.writeByte((byte) (isUserGestureEnable ? 1 : 0));
        dest.writeByte((byte) (isCanDragFromEdge ? 1 : 0));
        dest.writeByte((byte) (isAlwaysRetain ? 1 : 0));
        dest.writeSerializable(effect);
        dest.writeSerializable(pageClazz);
        dest.writeInt(orientation);
        dest.writeBundle(pageBundle);
        dest.writeInt((byte) (isFitSystemWindow ? 1 : 0));
    }

    public static class Builder {
        private boolean isPushWithAnimation = true;
        private boolean isPopWithAnimation = true;
        private boolean isUserGestureEnable = true;
        private boolean isCanDragFromEdge = true;
        private boolean isAlwaysRetain = false;
        private TransitionEffect effect = TransitionEffect.Ios_Stack;
        private Class<? extends BasePage> pageClazz;
        private int orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        private Bundle pageBundle;
        private boolean isFitSystemWindow = false;

        public Builder(Class<? extends BasePage> pageClazz) {
            setPageClass(pageClazz);
        }

        public Builder setPushWithAnimation(boolean isPushWithAnimation) {
            this.isPushWithAnimation = isPushWithAnimation;
            return this;
        }

        public Builder setPopWithAnimation(boolean isPopWithAnimation) {
            this.isPopWithAnimation = isPopWithAnimation;
            return this;
        }

        public Builder setUserGestureEnable(boolean isUserGestureEnable) {
            this.isUserGestureEnable = isUserGestureEnable;
            return this;
        }

        public Builder setCanDragFromEdge(boolean canDragFromEdge) {
            isCanDragFromEdge = canDragFromEdge;
            return this;
        }

        public Builder setAlwaysRetain(boolean isAlwaysRetain) {
            this.isAlwaysRetain = isAlwaysRetain;
            return this;
        }

        public Builder setTransitionEffect(TransitionEffect effect) {
            this.effect = effect;
            return this;
        }

        public Builder setPageClass(Class<? extends BasePage> clazz) {
            this.pageClazz = clazz;
            return this;
        }

        public Builder setOrientation(int orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder setPageBundle(Bundle pageBundle) {
            this.pageBundle = pageBundle;
            return this;
        }

        public Builder setFitSystemWindow(boolean isFitSystemWindow) {
            this.isFitSystemWindow = isFitSystemWindow;
            return this;
        }

        public PageConfig build() {
            PageConfig config = new PageConfig();
            config.position = CommonConstant.INVALID_POSITION;
            config.isPushWithAnimation = isPushWithAnimation;
            config.isPopWithAnimation = isPopWithAnimation;
            config.isUserGestureEnable = isUserGestureEnable;
            config.isCanDragFromEdge = isCanDragFromEdge;
            config.isAlwaysRetain = isAlwaysRetain;
            if (effect == null) {
                effect = TransitionEffect.Ios_Stack;
            }
            config.effect = effect;
            if (pageClazz == null) {
                pageClazz = NonePage.class;
            }
            config.pageClazz = pageClazz;
            config.orientation = orientation;
            config.pageBundle = pageBundle;
            config.isFitSystemWindow = isFitSystemWindow;
            return config;
        }
    }
}
