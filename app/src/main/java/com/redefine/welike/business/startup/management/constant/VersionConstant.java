package com.redefine.welike.business.startup.management.constant;

import android.text.TextUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by honglin on 2018/7/3.
 * <p>
 * app introduction config
 * <p>
 * app version in whiteList can be show
 */

public class VersionConstant {

    private static final List<String> whiteList = new LinkedList<>();

    static {
        whiteList.add("2.0.1");
        whiteList.add("2.0.2");
    }

    public static boolean isInWhiteList(String version) {

        if (TextUtils.isEmpty(version)) return false;

        boolean isInWhite = false;

        for (String whiteVersion : whiteList) {

            if (TextUtils.equals(version, whiteVersion))

                isInWhite = true;

        }

        return isInWhite;

    }

}
