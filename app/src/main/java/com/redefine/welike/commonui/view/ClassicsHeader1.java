package com.redefine.welike.commonui.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.redefine.commonui.widget.progress.CircularProgressBar;
import com.redefine.multimedia.widget.CircleDownloadProgressBar;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.ProgressDrawable;
import com.scwang.smartrefresh.layout.internal.pathview.PathsDrawable;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import java.util.Date;

/**
 * 经典下拉头部
 * Created by hgonlin on 2018/7/6.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ClassicsHeader1 extends RelativeLayout implements RefreshHeader {

    protected Date mLastTime;
    protected CircularProgressBar mLoadingProgressView;
    protected CircleDownloadProgressBar commonProgressbar;

    protected RefreshKernel mRefreshKernel;
    protected PathsDrawable mArrowDrawable;
    protected ProgressDrawable mProgressDrawable;
    protected Drawable mDrawable;
    protected SpinnerStyle mSpinnerStyle = SpinnerStyle.Translate;
    protected Integer mAccentColor;
    protected Integer mPrimaryColor;
    protected int mBackgroundColor;
    protected int mFinishDuration = 500;
    protected int mPaddingTop = 20;
    protected int mPaddingBottom = 20;
    protected ImageView mResultView;

    public ClassicsHeader1(Context context) {
        super(context);
        this.initView(context, null);
    }

    public ClassicsHeader1(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView(context, attrs);
    }

    public ClassicsHeader1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context, attrs);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public ClassicsHeader1(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initView(context, attrs);
    }

    protected void initView(Context context, AttributeSet attrs) {
        DensityUtil density = new DensityUtil();

        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LayoutParams lpProgress = new LayoutParams(density.dip2px(20), density.dip2px(20));
        lpProgress.setMargins(0, density.dip2px(18), 0, 0);
        lpProgress.addRule(CENTER_IN_PARENT);
        View view = LayoutInflater.from(context).inflate(com.redefine.welike.R.layout.refresh_loading, null);
        mLoadingProgressView = view.findViewById(com.redefine.welike.R.id.common_progressbar);
        commonProgressbar = view.findViewById(com.redefine.welike.R.id.common_progressbar_0);
        addView(view, lpProgress);

        LayoutParams lpResult = new LayoutParams(density.dip2px(32), density.dip2px(32));

        lpResult.addRule(CENTER_IN_PARENT);
        mResultView = new ImageView(context);
        addView(mResultView, lpResult);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            setPadding(getPaddingLeft(), 0, getPaddingRight(), 0);
        } else {
            setPadding(getPaddingLeft(), mPaddingTop, getPaddingRight(), mPaddingBottom);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //</editor-fold>

    //<editor-fold desc="RefreshHeader">
    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int extendHeight) {
        mRefreshKernel = kernel;
        mRefreshKernel.requestDrawBackgroundForHeader(mBackgroundColor);
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
    }

    @Override
    public void onPulling(float percent, int offset, int height, int extendHeight) {

        if (percent > 1)//偏移量
            commonProgressbar.setProgress((int) ((percent - 1) * 2 * 100));
        else {
            commonProgressbar.setProgress(1);
        }

    }

    @Override
    public void onReleasing(float percent, int offset, int height, int extendHeight) {

    }

    @Override
    public void onReleased(RefreshLayout layout, int height, int extendHeight) {

    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout layout, int height, int extendHeight) {

    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        mLoadingProgressView.setVisibility(GONE);
        mResultView.setVisibility(VISIBLE);
        if (success) {
            mResultView.setImageResource(com.redefine.welike.R.drawable.feed_refresh_header_success);
        } else {
            mResultView.setImageResource(com.redefine.welike.R.drawable.feed_refresh_header_success);
        }
        showAnimation();
        return mFinishDuration;//延迟500毫秒之后再弹回
    }

    private void showAnimation() {
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(mResultView, "scaleX",
                .5f, .9f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(mResultView, "scaleY",
                .5f, .9f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(anim1).with(anim2);
        animSet.setDuration(250);
        animSet.start();
    }

    @Override
    @Deprecated
    public void setPrimaryColors(@ColorInt int... colors) {
        if (colors.length > 0) {
            if (!(getBackground() instanceof BitmapDrawable) && mPrimaryColor == null) {
                setPrimaryColor(colors[0]);
                mPrimaryColor = null;
            }
            if (mAccentColor == null) {
                if (colors.length > 1) {
                    setAccentColor(colors[1]);
                } else {
                    setAccentColor(colors[0] == 0xffffffff ? 0xff666666 : 0xffffffff);
                }
                mAccentColor = null;
            }
        }
    }

    @NonNull
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return mSpinnerStyle;
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        switch (newState) {
            case None:
                commonProgressbar.setVisibility(VISIBLE);
            case PullDownToRefresh:
                mResultView.setVisibility(GONE);
                commonProgressbar.setVisibility(VISIBLE);
                mLoadingProgressView.setVisibility(GONE);
                break;
            case Refreshing:
            case RefreshReleased:
                commonProgressbar.setVisibility(GONE);
                mLoadingProgressView.setVisibility(VISIBLE);
                mResultView.setVisibility(GONE);
                break;
            case ReleaseToRefresh:
                break;
            case Loading:
                mResultView.setVisibility(GONE);
                commonProgressbar.setVisibility(GONE);
                break;

            case LoadFinish:
                mLoadingProgressView.setVisibility(GONE);
                mResultView.setVisibility(VISIBLE);
                break;

        }
    }


    public ClassicsHeader1 setSpinnerStyle(SpinnerStyle style) {
        this.mSpinnerStyle = style;
        return this;
    }

    public ClassicsHeader1 setPrimaryColor(@ColorInt int primaryColor) {
        mBackgroundColor = mPrimaryColor = primaryColor;
        if (mRefreshKernel != null) {
            mRefreshKernel.requestDrawBackgroundForHeader(mPrimaryColor);
        }
        return this;
    }

    public ClassicsHeader1 setAccentColor(@ColorInt int accentColor) {
        mAccentColor = accentColor;
        if (mArrowDrawable != null) {
            mArrowDrawable.parserColors(accentColor);
        }
        if (mProgressDrawable != null) {
            mProgressDrawable.setColor(accentColor);
        }
        return this;
    }

    public ClassicsHeader1 setPrimaryColorId(@ColorRes int colorId) {
        setPrimaryColor(ContextCompat.getColor(getContext(), colorId));
        return this;
    }

    public ClassicsHeader1 setAccentColorId(@ColorRes int colorId) {
        setAccentColor(ContextCompat.getColor(getContext(), colorId));
        return this;
    }

    public ClassicsHeader1 setFinishDuration(int delay) {
        mFinishDuration = delay;
        return this;
    }


    public ClassicsHeader1 setTextSizeTitle(float size) {
        if (mRefreshKernel != null) {
            mRefreshKernel.requestRemeasureHeightForHeader();
        }
        return this;
    }

    public ClassicsHeader1 setTextSizeTitle(int unit, float size) {
        if (mRefreshKernel != null) {
            mRefreshKernel.requestRemeasureHeightForHeader();
        }
        return this;
    }

    public ClassicsHeader1 setTextSizeTime(float size) {
        if (mRefreshKernel != null) {
            mRefreshKernel.requestRemeasureHeightForHeader();
        }
        return this;
    }

    public ClassicsHeader1 setTextSizeTime(int unit, float size) {
        if (mRefreshKernel != null) {
            mRefreshKernel.requestRemeasureHeightForHeader();
        }
        return this;
    }

    public ClassicsHeader1 setTextTimeMarginTop(float dp) {
        return setTextTimeMarginTopPx(DensityUtil.dp2px(dp));
    }

    public ClassicsHeader1 setTextTimeMarginTopPx(int px) {
        return this;
    }


    public ProgressBar getProgressView() {
        return mLoadingProgressView;
    }

}
