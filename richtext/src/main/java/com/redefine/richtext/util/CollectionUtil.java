package com.redefine.richtext.util;

import java.util.Collection;
import java.util.List;

/**
 * Name: CollectionUtil
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-09 19:07
 */
public class CollectionUtil {
    public static boolean isEmpty(Object[] spans) {
        if (spans == null || spans.length == 0) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(Collection collection) {
        if (collection == null || collection.isEmpty()) {
            return true;
        }
        return false;
    }

}
