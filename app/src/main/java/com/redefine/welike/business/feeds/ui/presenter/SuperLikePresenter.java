package com.redefine.welike.business.feeds.ui.presenter;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.airbnb.lottie.Cancellable;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.airbnb.lottie.utils.LottieValueAnimator;
import com.redefine.welike.business.feeds.ui.contract.ISuperLikeContract;
import com.redefine.welike.business.feeds.ui.util.OnSuperLikeExpCallback;
import com.redefine.welike.commonui.framework.PageStackLayer;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by liwenbo on 2018/4/2.
 */

public class SuperLikePresenter implements ISuperLikeContract.ISuperLikePresenter {

    private final PageStackLayer mParent;
    private final LottieAnimationView mLottieView;
    private final FrameLayout mLottieContainer;
    private final int mTouchSlop;
    private Cancellable mCancelable;
    private LottieComposition mCompostion;
    private long mExp;
    private boolean mIsLongTouch;
    private Disposable mDispose;
    private boolean mIsTrigger = false;
    private float mLastY;
    private float mLastX;
    private OnSuperLikeExpCallback mCallback;


    public SuperLikePresenter(PageStackLayer viewGroup) {
        mParent = viewGroup;
        mLottieView = new LottieAnimationView(viewGroup.getContext());
        mCancelable = LottieComposition.Factory.fromAssetFileName(viewGroup.getContext(), "super_like/data.json", new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(@Nullable LottieComposition composition) {
                mCancelable = null;
                if (composition == null) {
                    return;
                }
                mCompostion = composition;
                mLottieView.setComposition(mCompostion);
                mLottieView.setImageAssetsFolder("super_like/images");
            }
        });

        mLottieContainer = new FrameLayout(viewGroup.getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        mLottieContainer.addView(mLottieView, params);
        final ViewConfiguration configuration = ViewConfiguration.get(viewGroup.getContext());
        mTouchSlop = configuration.getScaledPagingTouchSlop();
    }

    @Override
    public void onActivityResume() {

    }

    @Override
    public void onActivityStart() {

    }

    @Override
    public void onActivityStop() {

    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityDestroy() {

    }

    @Override
    public boolean onTouch(View v, final int[] positions,MotionEvent event, OnSuperLikeExpCallback callback, long exp) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCallback = callback;
                mExp = exp;
                mIsLongTouch = false;
                mLastX = event.getX();
                mLastY = event.getY();
                mDispose = AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        mIsLongTouch = true;
                        fireSuperLike();
                    }
                }, 500, TimeUnit.MILLISECONDS);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsTrigger) {
                    requestParentDisallowInterceptTouchEvent(v, true);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mIsLongTouch) {
                    // 触发了长按
                    stopSuperLike();
                } else {
                    if (mDispose != null && !mDispose.isDisposed()) {
                        mDispose.dispose();
                    }
                    if (event.getX() - mLastX < mTouchSlop && event.getY() - mLastY < mTouchSlop) {
                        fireSuperLikeOnce();
                    }
                }
                break;
        }
        return true;
    }


    @SuppressLint("RestrictedApi")
    private void fireSuperLikeOnce() {
        if (mIsTrigger || mCompostion == null) {
            return;
        }
        mIsTrigger = true;

        mLottieView.removeUpdateListener(mLongClickAnimatiorListener);
        mParent.addView(mLottieContainer);
        mLottieView.playAnimation();
        int maxFrame = (int) Math.min(mCompostion.getEndFrame(), mExp + 1);
        mLottieView.setMinAndMaxFrame(maxFrame, maxFrame);
        mLottieView.setFrame(maxFrame);
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                stopSuperLike();
            }
        }, 100, TimeUnit.MILLISECONDS);
    }
    /**
     * 触发超级赞
     *
     */
    @SuppressLint("RestrictedApi")
    private void fireSuperLike() {
        if (mIsTrigger || mCompostion == null) {
            return;
        }
        mIsTrigger = true;

        mLottieView.addAnimatorUpdateListener(mLongClickAnimatiorListener);
        mParent.showAtCenter(mLottieContainer);
        mLottieView.playAnimation();
        @SuppressLint("RestrictedApi")
        int minFrame = (int) Math.min(mCompostion.getEndFrame(), mExp);
        mLottieView.setMinAndMaxFrame(minFrame, (int) mCompostion.getEndFrame());
        mLottieView.setFrame(minFrame);
    }

    /**
     * 取消超级赞
     */
    private void stopSuperLike() {
        /**
         * 倘若手指弹起，还没加载完lottie资源，那么直接cancel
         */
        if (mIsTrigger) {
            mParent.remove(mLottieContainer);
            mIsTrigger = false;
            mLottieView.cancelAnimation();
            int mCurrentFrame = mLottieView.getFrame();
            mExp = mCurrentFrame;
            if (mCallback != null) {
                mCallback.onSuperLikeExpCallback(true, mCurrentFrame);
                mCallback = null;
            }
        }
    }



    private void requestParentDisallowInterceptTouchEvent(View triggerView, boolean disallowIntercept) {
        final ViewParent parent = triggerView.getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    private ValueAnimator.AnimatorUpdateListener mLongClickAnimatiorListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            if (animation instanceof LottieValueAnimator) {
                @SuppressLint("RestrictedApi")
                int mCurrentFrame = (int) ((LottieValueAnimator) animation).getFrame();
                if (mCallback != null) {
                    mCallback.onSuperLikeExpCallback(false, mCurrentFrame);
                }
            }
        }
    };

}