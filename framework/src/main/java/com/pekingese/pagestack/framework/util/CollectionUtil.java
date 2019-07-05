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
package com.pekingese.pagestack.framework.util;

import java.util.Collection;

public class CollectionUtil {

    public static boolean isArrayEmpty(Collection list) {
        if (list == null || list.size() == 0) {
            return true;
        }
        return false;
    }

    public static <T>  boolean isArrayEmpty(T[] list) {
        if (list == null || list.length == 0) {
            return true;
        }
        return false;
    }
}
