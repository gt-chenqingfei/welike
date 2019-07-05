/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 17/9/30
 * <p>
 * Description : description
 * <p>
 * Creation    : 17/9/30
 * Author      : liwenbo0328@163.com
 * History     : Creation, 17/9/30, liwenbo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.util;

import android.content.res.Resources;

public class DeviceUtil {

    public static int dp2px(Resources resources, float dp) {
        return (int) (resources.getDisplayMetrics().density * dp);
    }
}
