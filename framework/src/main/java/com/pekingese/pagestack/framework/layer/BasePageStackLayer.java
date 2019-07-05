/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 17/8/1
 * <p>
 * Description : description
 * <p>
 * Creation    : 17/8/1
 * Author      : liwenbo0328@163.com
 * History     : Creation, 17/8/1, liwenbo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.layer;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BasePageStackLayer<T extends IStackLayerPresenter> extends FrameLayout {

    protected Map<Class<T>, T> mPresenters = new HashMap<>();

    public BasePageStackLayer(@NonNull Context context) {
        super(context);
        initPageStackLayer();
    }

    public BasePageStackLayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPageStackLayer();
    }

    public BasePageStackLayer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPageStackLayer();
    }

    protected void initPageStackLayer() {
        setClipChildren(false);
        registerPresenters();
    }

    protected void registerPresenters() {

    }

    public T getStackPresenter(Class<? extends IStackLayerPresenter> clazz) {
        return mPresenters.get(clazz);
    }

    public void remove(View view) {
        removeView(view);
    }

    public void showAtCenter(View view) {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        addView(view, layoutParams);
    }

    public void removeAll() {
        removeAllViews();
    }

    public void onActivityDestroy() {
        Collection<T> collection = mPresenters.values();
        for (T t : collection) {
            t.onActivityDestroy();
        }
    }

    public void onActivityPause() {
        Collection<T> collection = mPresenters.values();
        for (T t : collection) {
            t.onActivityPause();
        }
    }

    public void onActivityStop() {
        Collection<T> collection = mPresenters.values();
        for (T t : collection) {
            t.onActivityStop();
        }
    }

    public void onActivityStart() {
        Collection<T> collection = mPresenters.values();
        for (T t : collection) {
            t.onActivityStart();
        }
    }

    public void onActivityResume() {
        Collection<T> collection = mPresenters.values();
        for (T t : collection) {
            t.onActivityResume();
        }
    }


    /**
     * 获取location相对PageStack的位置
     *
     * @param targetView
     * @param inOutLocation
     */
    public void getLocationInPageStack(View targetView, @Size(2) int[] inOutLocation) {
        if (inOutLocation == null || inOutLocation.length < 2) {
            throw new IllegalArgumentException("inOutLocation must be an array of two integers");
        }
        inOutLocation[0] = inOutLocation[1] = 0;

        float position[] = new float[2];
        position[0] = inOutLocation[0];
        position[1] = inOutLocation[1];

//        if (!hasIdentityMatrix()) {
//            getMatrix().mapPoints(position);
//        }

        position[0] += targetView.getLeft();
        position[1] += targetView.getTop();

        ViewParent viewParent = targetView.getParent();
        while (viewParent instanceof View) {
            final View view = (View) viewParent;

            position[0] -= view.getScrollX();
            position[1] -= view.getScrollY();

//            if (!view.hasIdentityMatrix()) {
//                view.getMatrix().mapPoints(position);
//            }

            position[0] += view.getLeft();
            position[1] += view.getTop();

            viewParent = view.getParent();
            if (viewParent instanceof BasePageEnvironment) {
                break;
            }
        }

        inOutLocation[0] = Math.round(position[0]);
        inOutLocation[1] = Math.round(position[1]);
    }
}
