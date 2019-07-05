package com.redefine.welike.business.feeds.ui.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.redefine.welike.R;

/**
 * Created by nianguowang on 2019/1/16
 */
public class CustomTabView extends FrameLayout {

    private TextView mTabTitle;
    private OnePlusFlipperView mBadgeView;
    public CustomTabView(@NonNull Context context) {
        super(context);
        init();
    }

    public CustomTabView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTabView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomTabView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.custom_tab_view, this, true);
        mTabTitle = findViewById(R.id.tab_title);
        mBadgeView = findViewById(R.id.tab_badge_view);
    }

    public void onTabSelect() {
        mTabTitle.setTextColor(getContext().getResources().getColor(R.color.common_text_color_31));
    }

    public void onTabUnSelect() {
        mTabTitle.setTextColor(getContext().getResources().getColor(R.color.common_text_color_afb0b1));
    }

    public void setText(String text) {
        mTabTitle.setText(text);
    }

    public OnePlusFlipperView getBadgeView() {
        return mBadgeView;
    }
}
