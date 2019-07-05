package com.redefine.commonui.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.redefine.commonui.R;
import com.redefine.commonui.widget.progress.CircularProgressBar;
import com.redefine.foundation.utils.ScreenUtils;

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

public class LoadingView extends FrameLayout {


    public LoadingView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        setClickable(true);
        View view = LayoutInflater.from(context).inflate(R.layout.common_loading_layout, null);
        int width = ScreenUtils.dip2Px(35);
        int height = ScreenUtils.dip2Px(35);
        LayoutParams params = new LayoutParams(width, height);
        params.gravity = Gravity.CENTER;
        addView(view, params);
        if (getBackground() == null) {
            setBackgroundResource(R.color.common_color_f8f8f8);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }
}
