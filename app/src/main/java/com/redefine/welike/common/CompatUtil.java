package com.redefine.welike.common;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;

/**
 * Created by daining on 2018/4/17.
 */

public class CompatUtil {
    @TargetApi(Build.VERSION_CODES.O)
    public static void disableAutoFill(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
        }
    }
}
