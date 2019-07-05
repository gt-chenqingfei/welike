package com.redefine.commonui.widget.loading;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.redefine.commonui.R;
import com.redefine.commonui.widget.loading.render.LoadingDrawable;
import com.redefine.commonui.widget.loading.render.LoadingRenderer;
import com.redefine.commonui.widget.loading.render.circle.rotate.MaterialLoadingRenderer;
import com.redefine.foundation.utils.ScreenUtils;

public class ProgressView extends View {
    private LoadingDrawable mLoadingDrawable;

    public ProgressView(Context context) {
        super(context);
        initView();
    }

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        setLoadingRenderer(new MaterialLoadingRenderer.Builder(getContext())
                .setStrokeWidth(ScreenUtils.dip2Px(3))
                .setColors(new int[]{ContextCompat.getColor(getContext(), R.color.main_orange_dark)}).build());
    }


    public void setLoadingRenderer(LoadingRenderer loadingRenderer) {
        stopAnimation();
        mLoadingDrawable = new LoadingDrawable(loadingRenderer);
        setBackground(mLoadingDrawable);
    }

    @Override
    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        stopAnimation();
    }

    @Override
    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        startAnimation();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        final boolean visible = visibility == VISIBLE && getVisibility() == VISIBLE;
        if (visible) {
            startAnimation();
        } else {
            stopAnimation();
        }
    }

    private void startAnimation() {
        if (mLoadingDrawable != null && !mLoadingDrawable.isRunning()) {
            mLoadingDrawable.start();
        }
    }

    private void stopAnimation() {
        if (mLoadingDrawable != null) {
            mLoadingDrawable.stop();
        }
    }
}
