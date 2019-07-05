package com.redefine.commonui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.TextView;

import com.redefine.commonui.R;
import com.redefine.welike.base.resource.ResourceTool;

/**
 * Created by honlin on 2018/9/20.
 */

public class LoadingTranslateBgDlg1 extends Dialog {

    public LoadingTranslateBgDlg1(Activity activity) {
        super(activity, R.style.loading_theme2);
        setOwnerActivity(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dlg_loading);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.dimAmount = .5f;
        getWindow().setAttributes(lp);

        setCancelable(false);
    }

}
