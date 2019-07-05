/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 17/11/3
 * <p>
 * Description : description
 * <p>
 * Creation    : 17/11/3
 * Author      : liwenbo0328@163.com
 * History     : Creation, 17/11/3, liwenbo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.config;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class StatusBarConfig implements Serializable {

    private static final long serialVersionUID = -2480004243239434223L;

    public static int sDefaultColor = Color.BLACK;

    private boolean isUseImmersiveStateBar = true;
    private boolean isUseTransientEffect = true;
    private Drawable tintDrawable = null;

    private StatusBarConfig() {

    }

    public boolean isUseImmersiveStateBar() {
        return isUseImmersiveStateBar;
    }

    public boolean isUseTransientEffect() {
        return isUseTransientEffect;
    }

    public Drawable getDrawable() {
        return tintDrawable;
    }

    public static class Builder {
        private boolean isUseImmersiveStateBar = true;
        private boolean isUseTransientEffect = true;
        private int tintColor = sDefaultColor;
        private Drawable tintDrawable = null;

        public Builder(boolean isUseImmersiveStateBar, boolean isUseTransientEffect) {
            this.isUseImmersiveStateBar = isUseImmersiveStateBar;
            this.isUseTransientEffect = isUseTransientEffect;
        }

        public Builder setTintColor(int color) {
            this.tintColor = color;
            return this;
        }

        public Builder setTintDrawable(Drawable drawable) {
            this.tintDrawable = drawable;
            return this;
        }

        public StatusBarConfig build() {
            StatusBarConfig config = new StatusBarConfig();
            config.isUseTransientEffect = isUseTransientEffect;
            config.isUseImmersiveStateBar = isUseImmersiveStateBar;
            if (tintDrawable == null) {
                config.tintDrawable = new ColorDrawable(tintColor).mutate();
            } else {
                config.tintDrawable = tintDrawable;
            }
            return config;
        }
    }
}
