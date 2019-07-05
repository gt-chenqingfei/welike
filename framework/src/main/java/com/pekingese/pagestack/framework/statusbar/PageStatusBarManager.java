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
package com.pekingese.pagestack.framework.statusbar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.pekingese.pagestack.framework.config.StatusBarConfig;
import com.pekingese.pagestack.framework.drawable.ArrayDrawable;
import com.pekingese.systembartint.SystemBarTintManager;

public class PageStatusBarManager implements IPageStatusBarManager {

    private final boolean mEnable;
    private StatusBarConfig mPreConfig;
    private StatusBarConfig mCurrentConfig;
    private SystemBarTintManager mTintManager;

    public PageStatusBarManager(Context context) {
        mEnable = context instanceof Activity;
        if (mEnable) {
            mTintManager = new SystemBarTintManager((Activity) context);
            mTintManager.setStatusBarTintEnabled(true);
        }
    }

    @Override
    public void applyPageStatusBarConfig(StatusBarConfig preConfig, StatusBarConfig currentConfig) {
        if (!mEnable) {
            return ;
        }
        if (preConfig == null || currentConfig == null || !preConfig.isUseImmersiveStateBar()
                || !currentConfig.isUseImmersiveStateBar()) {
            return ;
        }
        mPreConfig = preConfig;
        mCurrentConfig = currentConfig;
        mTintManager.setStatusBarTintDrawable(getStatusBarDrawable(preConfig, currentConfig));
    }

    private Drawable getStatusBarDrawable(StatusBarConfig preConfig, StatusBarConfig currentConfig) {
        Drawable[] drawables = new Drawable[2];
        drawables[0] = preConfig.getDrawable();
        drawables[1] = currentConfig.getDrawable();
        return new ArrayDrawable(drawables);
    }

    @Override
    public void applyPageScrollOffset(float positionOffset) {
        if (!mEnable) {
            return ;
        }
        if (mCurrentConfig == null || mPreConfig == null || !mCurrentConfig.isUseImmersiveStateBar()
                || !mPreConfig.isUseImmersiveStateBar() || !mPreConfig.isUseTransientEffect() || !mCurrentConfig.isUseTransientEffect()) {
            return ;
        }
        applyStatusBarScrollOffset(positionOffset);
    }

    @Override
    public void applyPageScrollEnd(StatusBarConfig statusBarConfig) {
        if (!mEnable) {
            return ;
        }
        if (statusBarConfig == null) {
            mTintManager.setStatusBarTintDrawable(getDefaultDrawable());
            return ;
        }
        if (!statusBarConfig.isUseImmersiveStateBar()) {
            return ;
        }
        statusBarConfig.getDrawable().setAlpha(255);
        mTintManager.setStatusBarTintDrawable(statusBarConfig.getDrawable());
    }

    private Drawable getDefaultDrawable() {
        return new ColorDrawable(Color.BLACK).mutate();
    }

    private void applyStatusBarScrollOffset(float positionOffset) {
        Drawable drawable = mTintManager.getStatusBarBackground();
        if (drawable instanceof ArrayDrawable) {
            ArrayDrawable arrayDrawable = (ArrayDrawable) drawable;
            if (((ArrayDrawable) drawable).getNumberOfLayers() < 2) {
                return ;
            }
            arrayDrawable.getDrawable(0).setAlpha((int) (255 - positionOffset * 255));
            arrayDrawable.getDrawable(1).setAlpha((int) (positionOffset * 255));
            mTintManager.onUpdateStatusBarBg();
        }
    }

    public static void setEnableStatusBar(Activity context, boolean b) {
        SystemBarTintManager.setEnableStatusBar(context, b);
    }
}
