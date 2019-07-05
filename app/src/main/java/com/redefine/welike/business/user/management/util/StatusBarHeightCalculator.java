package com.redefine.welike.business.user.management.util;

import android.content.res.Resources;

/**
 * Created by nianguowang on 2018/8/21
 */
public class StatusBarHeightCalculator {

    private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";

    public static int getInternalDimensionSize(Resources res) {
        int result = 0;
        int resourceId = res.getIdentifier(STATUS_BAR_HEIGHT_RES_NAME, "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
