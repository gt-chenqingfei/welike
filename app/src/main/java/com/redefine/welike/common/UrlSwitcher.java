package com.redefine.welike.common;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.redefine.commonui.share.CommonListener;
import com.redefine.welike.base.URLCenter;

/**
 * Created by daining on 2018/5/3.
 */

public class UrlSwitcher {

    public static int x = 0;

    public static void showSwitcher(Activity activity) {
        createDialog(activity, new CommonListener<Integer>() {
            @Override
            public void onFinish(Integer value) {
                URLCenter.use(value);
            }

            @Override
            public void onError(Integer value) {

            }
        });
    }

    private static void createDialog(Activity activity, final CommonListener<Integer> listener) {
        String[] src = {"DEV", "TEST"};
        new AlertDialog.Builder(activity)
                .setSingleChoiceItems(src, URLCenter.getCurrentType(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        x = which;
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onFinish(x);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setTitle("Pick").create().show();

    }

}
