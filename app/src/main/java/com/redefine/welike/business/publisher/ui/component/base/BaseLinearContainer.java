package com.redefine.welike.business.publisher.ui.component.base;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * @author qingfei.chen
 * @date 2018-11-08
 * Copyright (C) 2018 redefine , Inc.
 */
public abstract class BaseLinearContainer extends LinearLayout {

    protected Context mContext;

    abstract public void onCreateView();

    private AttributeSet attrs;

    public BaseLinearContainer(Context context) {
        super(context);
        mContext = context;
        createView(this, false);
    }

    public BaseLinearContainer(Context context, boolean attachToRoot) {
        super(context);
        mContext = context;
        createView(this, attachToRoot);
    }

    public BaseLinearContainer(Context context, ViewGroup vg, boolean attachToRoot) {
        super(context);
        mContext = context;
        createView(vg, attachToRoot);
    }

    public BaseLinearContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;
        mContext = context;
        createView(this, true);
    }

    public BaseLinearContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.attrs = attrs;
        mContext = context;
        createView(this, true);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BaseLinearContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.attrs = attrs;
        mContext = context;
        createView(this, true);
    }

    public void createView(ViewGroup vg, boolean attachToRoot) {
        final Class<?> clazz = getClass();
        final LayoutResource layout = clazz.getAnnotation(LayoutResource.class);
        if (layout != null) {
            ViewGroup contentView = (ViewGroup) LayoutInflater.from(mContext).inflate(layout.layout(), vg, attachToRoot);
            if (!attachToRoot) {
                this.addView(contentView);
            }
        }

        onCreateView();
    }

    public AttributeSet getAttrs() {
        return attrs;
    }

    public AppCompatActivity getActivityContext() {
        return (AppCompatActivity) getContext();
    }
}
