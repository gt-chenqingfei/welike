package com.redefine.foundation.utils;

import android.text.TextUtils;

import java.util.Collection;

/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 18/1/8
 * <p>
 * Description : TODO
 * <p>
 * Creation    : 18/1/8
 * Author      : liwenbo0328@163.com
 * History     : Creation, 18/1/8, liwenbo, Create the file
 * ****************************************************************************
 */

public class CollectionUtil {

    public static boolean isEmpty(Collection collection) {
        if (collection == null || collection.isEmpty()) {
            return true;
        }
        return false;
    }

    public static int getCount(Collection collection) {
        if (collection == null || collection.isEmpty()) {
            return 0;
        }
        return collection.size();
    }

    public static boolean isEmpty(Object[] span) {
        if (span == null || span.length == 0) {
            return true;
        }
        return false;
    }

    public static int getCount(String sug) {
        if (TextUtils.isEmpty(sug)) {
            return 0;
        }
        return sug.length();
    }
}
