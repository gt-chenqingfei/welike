package com.redefine.foundation.utils;

import android.content.Context;
import android.net.Uri;

/**
 * Created by MR on 2018/1/17.
 */

public class ResourceUtil {
    public static final String ANDROID_RESOURCE = "android.resource://";
    public static final String FOREWARD_SLASH = "/";

    public static Uri convertToUri(Context context, int resId) {
        return Uri.parse(ANDROID_RESOURCE + context.getPackageName() + FOREWARD_SLASH + resId);
    }
}
