package com.redefine.welike.business.user.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.upgraded.UpdateHelper;

/**
 * Created by gongguan on 2018/3/6.
 */

public class UpdateDialog extends Dialog {
    private ImageView mBack;
    private TextView mTitle, mVersion, mUpdate, firstUpdate;

    public UpdateDialog(@NonNull Context context, boolean isNecessary) {
        super(context, R.style.BaseAppTheme_Dialog);
        setContentView(R.layout.mine_check_update);
        setCanceledOnTouchOutside(false);

        mTitle = findViewById(R.id.mine_check_update_dailog_title);
        mBack = findViewById(R.id.mine_check_update_dailog_back_iv);
        mVersion = findViewById(R.id.mine_check_update_dailog_version_tv);
        mUpdate = findViewById(R.id.mine_check_update_dailog_update_tv);
        firstUpdate = findViewById(R.id.first_upadte_text);

        if (isNecessary) {
            setCancelable(false);
            mBack.setVisibility(View.INVISIBLE);
        }

        mTitle.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_check_the_update_title"));
        mUpdate.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_check_the_update_update"));
        mVersion.setText(UpdateHelper.getInstance().getVersion());
        firstUpdate.setText(UpdateHelper.getInstance().getUpdateContent());

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(UpdateHelper.getInstance().getUrl())) {
                    Uri uri = Uri.parse(UpdateHelper.getInstance().getUrl());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    getContext().startActivity(intent);
                }
            }
        });
    }

}
