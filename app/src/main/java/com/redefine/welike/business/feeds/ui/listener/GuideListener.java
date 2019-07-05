package com.redefine.welike.business.feeds.ui.listener;

import android.view.View;

import com.redefine.welike.commonui.widget.ArrowTextView;

public interface GuideListener<T> {

    void onShow(T t,ArrowTextView guide);

    void onClick(View view);
}
