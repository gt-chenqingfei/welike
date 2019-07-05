package com.redefine.welike.business.publisher.ui.component.base;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * @author qingfei.chen
 * @date 2018-11-08
 * Copyright (C) 2018 redefine , Inc.
 */
public abstract class BaseRelativeContainer extends RelativeLayout {

    protected Context mContext;

    abstract public void onCreateView();

    private AttributeSet attrs;

    public BaseRelativeContainer(Context context) {
        super(context);
        mContext = context;
        createView(this, false);
    }

    public BaseRelativeContainer(Context context, boolean attachToRoot) {
        super(context);
        mContext = context;
        createView(this, attachToRoot);
    }

    public BaseRelativeContainer(Context context, ViewGroup vg, boolean attachToRoot) {
        super(context);
        mContext = context;
        createView(vg, attachToRoot);
    }

    public BaseRelativeContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;
        mContext = context;
        createView(this, true);
    }

    public BaseRelativeContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.attrs = attrs;
        mContext = context;
        createView(this, true);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BaseRelativeContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
}
