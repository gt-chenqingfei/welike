/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 17/11/10
 * <p>
 * Description : description
 * <p>
 * Creation    : 17/11/10
 * Author      : liwenbo0328@163.com
 * History     : Creation, 17/11/10, liwenbo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.TypedValue;

public class DrawableUtil {
    private static TypedValue sTempValue;
    private static final Object sLock = new Object();

    public static final Drawable getDrawable(Context context, @DrawableRes int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 21) {
            return context.getDrawable(id);
        } else if (version >= 16) {
            return context.getResources().getDrawable(id);
        } else {
            // Prior to JELLY_BEAN, Resources.getDrawable() would not correctly
            // retrieve the final configuration density when the resource ID
            // is a reference another Drawable resource. As a workaround, try
            // to resolve the drawable reference manually.
            final int resolvedId;
            synchronized (sLock) {
                if (sTempValue == null) {
                    sTempValue = new TypedValue();
                }
                context.getResources().getValue(id, sTempValue, true);
                resolvedId = sTempValue.resourceId;
            }
            return context.getResources().getDrawable(resolvedId);
        }
    }
}
