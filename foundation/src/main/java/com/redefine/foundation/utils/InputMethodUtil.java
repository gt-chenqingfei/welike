package com.redefine.foundation.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by MR on 2018/1/27.
 */

public class InputMethodUtil {

    public static void showInputMethod(View view) {
        if (view == null) {
            return ;
        }
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view,InputMethodManager.SHOW_FORCED);
        }
    }

    public static void hideInputMethod(View view) {
        if (view == null) {
            return ;
        }
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        view.clearFocus();
    }

    public static void hideInputMethod(Context context) {
        if (context == null) {
            return ;
        }
        if (context instanceof Activity) {
            hideInputMethod(((Activity) context).getCurrentFocus());
        }
    }
}
