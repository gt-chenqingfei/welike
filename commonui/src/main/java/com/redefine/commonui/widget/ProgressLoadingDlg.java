package com.redefine.commonui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.TextView;

import com.redefine.commonui.R;
import com.redefine.commonui.widget.progress.CircleProgressBar;
import com.redefine.welike.base.resource.ResourceTool;

/**
 * Created by liubin on 2018/1/5.
 */

public class ProgressLoadingDlg extends Dialog {
    private String loadingText;
    private TextView mLoadingText;
    private CircleProgressBar mProgressBar;

    public ProgressLoadingDlg(Activity activity, String loadingText) {
        super(activity, R.style.loading_theme);
        setOwnerActivity(activity);
        this.loadingText = loadingText;
    }

    public ProgressLoadingDlg(Activity activity) {
        super(activity, R.style.loading_theme);
        setOwnerActivity(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dlg_progress_loading_layout);
        mLoadingText = findViewById(R.id.dlg_loading_text);
        mLoadingText.setText(loadingText);
        mProgressBar = findViewById(R.id.common_progressbar);
        mProgressBar.setProgress(0);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.dimAmount = 0f;
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(false);
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                loadingText = null;
            }
        });
    }

    public void updateProgress(int progress) {
        mProgressBar.setProgress(progress);
    }

}
