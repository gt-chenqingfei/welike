package com.redefine.commonui.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

import com.redefine.commonui.R;
import com.redefine.welike.base.resource.ResourceTool;

/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 18/1/8
 * <p>
 * Description : TODO
 * <p>
 * Creation    : 18/1/8
 * Author      : liwenbo0328@163.com
 * History     : Creation, 18/1/8, liwenbo, Create the file
 * ****************************************************************************
 */

public class ErrorView extends BaseErrorView {

    public ErrorView(@NonNull Context context) {
        super(context);
    }

    public ErrorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ErrorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ErrorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        setClickable(true);
        mReloadView.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_reload_text"));
        mErrorText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_error_text"));
        if (this.getBackground() == null) {
            this.setBackgroundResource(R.color.common_color_f8f8f8);
        }
    }
}
