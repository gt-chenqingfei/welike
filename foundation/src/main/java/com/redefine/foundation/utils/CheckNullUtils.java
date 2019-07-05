package com.redefine.foundation.utils;

import java.util.List;
import java.util.Map;

/**
 * 检验空值工具类
 * Created by gongguan on 2018/1/5.
 */

public class CheckNullUtils {

    public CheckNullUtils() {
    }

    public static Boolean isNotNull(Object o) {
        return o != null;
    }

    /**
     * String类型判空
     *
     * @param str
     * @return
     */
    public static Boolean isNotNullString(String str) {
        return str != null && !str.equals("");
    }

    /**
     * String类型判等
     *
     * @param one
     * @param another
     * @return
     */
    public static Boolean isEqual(String one, String another) {
        return one != null && another != null && one.equals(another);
    }

    /**
     * List集合判空
     *
     * @param list
     * @return
     */
    public static Boolean isNotNullList(List list) {
        return list != null && list.size() > 0;
    }

    /**
     * map集合判空
     *
     * @param map
     * @return
     */
    public static Boolean isNotNullMap(Map map) {
        return map != null && map.size() > 0;
    }


}
