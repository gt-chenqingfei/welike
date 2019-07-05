package com.redefine.commonui.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

public class EmptyView extends FrameLayout {

    private TextView mEmptyBtn;
    private ImageView mEmptyImg;
    private TextView mEmptyText;
    private View mRootView;

    public EmptyView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public EmptyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public EmptyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public EmptyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.common_empty_view, this, true);
        setClickable(true);
        mEmptyBtn = findViewById(R.id.common_empty_btn);
        mRootView = findViewById(R.id.common_empty_root_view);
        mEmptyImg = findViewById(R.id.common_empty_img);
        mEmptyText = findViewById(R.id.common_empty_text);
        if (this.getBackground() == null) {
            this.setBackgroundResource(R.color.common_color_f8f8f8);
        }
    }

    public void showEmptyBtn(int imageRes, String emptyBtnText, final IEmptyBtnClickListener listener) {
        mEmptyText.setVisibility(GONE);
        mEmptyImg.setVisibility(VISIBLE);
        mEmptyBtn.setVisibility(VISIBLE);
        mEmptyBtn.setText(emptyBtnText);
        mEmptyImg.setImageResource(imageRes);
        mEmptyBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickEmptyBtn();
                }
            }
        });
    }

    public void showEmptyBtn(int imageRes, String emptyText) {
        mEmptyText.setVisibility(VISIBLE);
        mEmptyImg.setVisibility(VISIBLE);
        mEmptyBtn.setVisibility(GONE);
        mEmptyText.setText(emptyText);
        mEmptyImg.setImageResource(imageRes);

    }

    public void showEmptyBtn(int imageRes, String emptyBtnText, String emptyText, final IEmptyBtnClickListener listener) {
        mEmptyText.setVisibility(VISIBLE);
        mEmptyImg.setVisibility(VISIBLE);
        mEmptyBtn.setVisibility(VISIBLE);
        mEmptyText.setText(emptyText);
        mEmptyBtn.setText(emptyBtnText);
        mEmptyImg.setImageResource(imageRes);
        mEmptyBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickEmptyBtn();
                }
            }
        });
    }

    public void showEmptyText(String text) {
        mEmptyImg.setVisibility(GONE);
        mEmptyBtn.setVisibility(GONE);
        mEmptyText.setVisibility(VISIBLE);
        mEmptyText.setText(text);
        mEmptyBtn.setOnClickListener(null);
    }

    public void showEmptyImg(int img) {
        mEmptyImg.setVisibility(VISIBLE);
        mEmptyBtn.setVisibility(GONE);
        mEmptyText.setVisibility(GONE);
        mEmptyImg.setImageResource(img);
        mEmptyBtn.setOnClickListener(null);
    }

    public void showEmptyImageText(int imageRes, String text) {
        mEmptyBtn.setVisibility(GONE);
        mEmptyText.setVisibility(VISIBLE);
        mEmptyImg.setVisibility(VISIBLE);
        mEmptyImg.setImageResource(imageRes);
        mEmptyText.setText(text);
        mEmptyBtn.setOnClickListener(null);
    }

    public void setBgResource(int resource) {
        mRootView.setBackgroundResource(resource);
    }

    public static interface IEmptyBtnClickListener {
        void onClickEmptyBtn();
    }
}
