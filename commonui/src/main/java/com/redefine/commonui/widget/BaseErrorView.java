package com.redefine.commonui.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.redefine.commonui.R;

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

public abstract class BaseErrorView extends FrameLayout implements View.OnClickListener {

    private IErrorViewClickListener mListener;
    protected TextView mReloadView;
    protected TextView mErrorText;
    protected View mErrorContent;

    public BaseErrorView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public BaseErrorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseErrorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BaseErrorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @CallSuper
    protected void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.common_error_view, this, true);
        mReloadView = findViewById(R.id.common_reload_view);
        mErrorText = findViewById(R.id.common_error_text);
        mErrorContent = findViewById(R.id.common_error_content);
        mReloadView.setOnClickListener(this);
    }

    public void setGravity(int gravity) {
        LayoutParams params = (LayoutParams) mErrorContent.getLayoutParams();
        params.gravity = gravity;
        mErrorContent.setLayoutParams(params);
    }

    public void setErrorText(String text) {
        mErrorText.setText(text);
    }

    public void setOnErrorViewClickListener(IErrorViewClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (v == mReloadView) {
            if (mListener != null) {
                mListener.onErrorViewClick();
            }
        }
    }

    public static interface IErrorViewClickListener {
        void onErrorViewClick();
    }
}
