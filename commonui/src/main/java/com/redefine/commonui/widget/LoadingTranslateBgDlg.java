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
 * Created by honlin on 2018/1/5.
 */

public class LoadingTranslateBgDlg extends Dialog {
    private String loadingText;
    private TextView mLoadingText;

    public LoadingTranslateBgDlg(Activity activity, String loadingText) {
        super(activity, R.style.loading_theme1);
        setOwnerActivity(activity);
        this.loadingText = loadingText;
    }

    public LoadingTranslateBgDlg(Activity activity) {
        super(activity, R.style.loading_theme1);
        setOwnerActivity(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (TextUtils.isEmpty(loadingText)) {
            setContentView(R.layout.dlg_loading);
        } else {
            setContentView(R.layout.dlg_loading_layout);
            mLoadingText = findViewById(R.id.dlg_loading_text);
            mLoadingText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_setting_clear_cache_pop_loading_text"));
        }

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.dimAmount = 0f;
        getWindow().setAttributes(lp);
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                loadingText = null;
            }
        });

        setCancelable(false);
        setOnShowListener(new OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
//                ImageView image = LoadingDlg.this.findViewById(R.id.iv_loading);
//                Animation anim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//                anim.setRepeatCount(Animation.INFINITE);
//                anim.setDuration(1000);
//                anim.setInterpolator(new LinearInterpolator());
//                image.startAnimation(anim);
//                LottieAnimationView image = LoadingDlg.this.findViewById(R.id.iv_loading);
//
//                image.setAnimation("loading.json");
//
//                image.setRepeatCount(LottieDrawable.INFINITE);
//                image.playAnimation();

            }

        });

    }

}
