package com.redefine.welike.business.mysetting.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;

/**
 * Created by gongguan on 2018/3/4.
 */

public class ClearCacheDialog extends Dialog {
    private TextView confirm, cancel;
    private ClearCache clearCache;
    private TextView mTvTitle;
    private String confirmType;

    public ClearCacheDialog(@NonNull Context context, ClearCache clearCache, String confirmType) {
        super(context, R.style.BaseAppTheme_Dialog);
        setContentView(R.layout.popupwindow_setting_clear_cache);
        setCanceledOnTouchOutside(false);

        this.clearCache = clearCache;
        this.confirmType = confirmType;

        mTvTitle = findViewById(R.id.mine_setting_clearCache_pop_title);
        confirm = findViewById(R.id.setting_clearCache_yes);
        cancel = findViewById(R.id.setting_clearCache_no);

        confirm.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_setting_clear_cache_pop_confirm"));
        cancel.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_setting_clear_cache_pop_cancel"));
        mTvTitle.setText(confirmType);

        setOnclick();
    }

    private void setOnclick() {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCache.setOnClearCache(confirmType);
                confirmType = null;
                clearCache = null;
                dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmType = null;
                clearCache = null;
                dismiss();
            }
        });

    }
}
