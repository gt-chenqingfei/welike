package com.redefine.welike.business.user.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.user.management.FollowUserManager;

/**
 * Created by gongguan on 2018/3/4.
 */

public class UnfollowDialog extends Dialog {
    private TextView confirm, cancel;
    private TextView mTvTitle;

    public UnfollowDialog(@NonNull Context context, String uId, UnFollowCallback callback) {
        super(context, R.style.BaseAppTheme_Dialog);
        setContentView(R.layout.popupwindow_setting_clear_cache);
        setCanceledOnTouchOutside(false);

        mTvTitle = findViewById(R.id.mine_setting_clearCache_pop_title);
        confirm = findViewById(R.id.setting_clearCache_yes);
        cancel = findViewById(R.id.setting_clearCache_no);

        confirm.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_setting_clear_cache_pop_confirm"));
        cancel.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_setting_clear_cache_pop_cancel"));
        mTvTitle.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_follow_dialog_title"));

        setOnclick(uId, callback);
    }

    private void setOnclick(final String uId, final UnFollowCallback callback) {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FollowUserManager.getInstance().unfollow(uId, null);
                callback.CallBack(true);
                dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public interface UnFollowCallback {
        void CallBack(boolean isConfirm);
    }
}
