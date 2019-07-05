package com.redefine.welike.business.feeds.ui.presenter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.business.common.LikeManager;
import com.redefine.welike.business.feeds.ui.contract.ISuperLikeContract;
import com.redefine.welike.business.feeds.ui.util.OnSuperLikeExpCallback;
import com.redefine.welike.commonui.framework.PageStackLayer;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by honlin on 2018/5/3.
 */

public class SuperLike1Presenter implements ISuperLikeContract.ISuperLikePresenter {

    private final PageStackLayer mParent;
    private final LottieAnimationView mLottieView;
    private final FrameLayout mLottieContainer;
    private final int mTouchSlop;
    private FrameLayout.LayoutParams params;
    private int padding;
    private long mExp = 0;
    private int oldTen = 0;
    private int oldLevel = 0;
    private boolean mIsLongTouch;
    private Disposable mDispose;
    private boolean mIsTrigger = false;
    private float mLastY;
    private float mLastX;
    private LinearLayout numberlayout, infoLayout;
    private ImageView likeLevelImageView;
    private ImageView bitImageView, tenImageView;

    private int offsetHeight = 0;
    private int realWidth = 0;


    private RelativeLayout layout;//lottieView的父布局 使lottie动画不缩小

    private OnSuperLikeExpCallback mCallback;
    private RelativeLayout.LayoutParams infoParams;
    private int[] mPositions;


    public SuperLike1Presenter(PageStackLayer viewGroup) {
        mParent = viewGroup;

        mLottieView = new LottieAnimationView(viewGroup.getContext());

        layout = new RelativeLayout(viewGroup.getContext());
        mLottieView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mLottieView.setRepeatCount(LottieDrawable.INFINITE);

        mLottieContainer = new FrameLayout(viewGroup.getContext());
        mLottieContainer.setOnTouchListener(mOntouchlistener);


        //适配宽高比不同的手机
        int ScreenHeight = ScreenUtils.getScreenHeight(viewGroup.getContext());
        int ScreenWidth = ScreenUtils.getSreenWidth(viewGroup.getContext());
        int width;
        int height;
        int direction = 9 * ScreenHeight - 16 * ScreenWidth;
        if (direction == 0) {
            width = ScreenWidth;
            height = ScreenHeight;
        } else if (direction > 0) {
            width = ScreenWidth;
            height = ScreenWidth * 16 / 9;
            offsetHeight = ScreenHeight - height;
        } else {
            height = ScreenHeight;
            width = ScreenHeight * 9 / 16;
        }
        realWidth = width;
        params = new FrameLayout.LayoutParams(width, height);//在使用lottie时 设置match_parent不能显示view

        mLottieContainer.addView(layout, params);

        final ViewConfiguration configuration = ViewConfiguration.get(viewGroup.getContext());
        mTouchSlop = configuration.getScaledPagingTouchSlop();
    }

    private View.OnTouchListener mOntouchlistener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return mIsLongTouch;
        }
    };

    private void createInfoLayout() {
        infoLayout = new LinearLayout(mParent.getContext());
        infoParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        infoParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        infoParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        infoParams.bottomMargin = ScreenUtils.dip2Px(mParent.getContext(), 270);
        infoParams.rightMargin = ScreenUtils.dip2Px(mParent.getContext(), 36);
        infoLayout.setLayoutParams(infoParams);
        layout.addView(infoLayout);
    }

    private void initInfoView() {
        layout.removeAllViews();

        layout.addView(mLottieView);

        createInfoLayout();

        padding = ScreenUtils.dip2Px(mParent.getContext(), 17);
        likeLevelImageView = new ImageView(mParent.getContext());
        likeLevelImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        numberlayout = new LinearLayout(mParent.getContext());
        numberlayout.setPadding(padding, 0, padding, 0);
        numberlayout.setGravity(Gravity.BOTTOM);
        numberlayout.setOrientation(LinearLayout.HORIZONTAL);
        infoLayout.setOrientation(LinearLayout.VERTICAL);
        bitImageView = new ImageView(mParent.getContext());
        bitImageView.setLayoutParams(new LinearLayout.LayoutParams(ScreenUtils.dip2Px(mParent.getContext(), 12)
                , ScreenUtils.dip2Px(mParent.getContext(), 14)));
        LinearLayout.LayoutParams likeparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ScreenUtils.dip2Px(mParent.getContext(), 42));
        likeparams.topMargin = -ScreenUtils.dip2Px(mParent.getContext(), 7);
        likeparams.bottomMargin = ScreenUtils.dip2Px(mParent.getContext(), 7);
        likeparams.rightMargin = ScreenUtils.dip2Px(mParent.getContext(), 30);
        likeLevelImageView.setLayoutParams(likeparams);
        tenImageView = new ImageView(mParent.getContext());
        numberlayout.addView(tenImageView);
        numberlayout.addView(bitImageView);
        infoLayout.addView(numberlayout);
        infoLayout.addView(likeLevelImageView);

        if (!mIsLongTouch) {
            likeLevelImageView.setVisibility(View.GONE);
            numberlayout.setPadding(padding, 0, padding, padding * 2);
            numberlayout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        }
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
    public boolean onTouch(View v, final int[] positions, MotionEvent event, OnSuperLikeExpCallback callback, final long exp) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCallback = callback;
                oldLevel = 0;
                mPositions = positions;
                if (!mIsTrigger)
                    mExp = exp;
                mIsLongTouch = false;
                mLastX = event.getX();
                mLastY = event.getY();
                mDispose = AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        mIsLongTouch = true;
                        mExp = exp;
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


    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            showOnceLike();
            handler.postDelayed(this, 80);
        }
    };

    private void showOnceLike() {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {

                AudioManager mAudioMgr = (AudioManager) mParent.getContext().getSystemService(Context.AUDIO_SERVICE);
                if (mAudioMgr != null) {
                    mAudioMgr.playSoundEffect(SoundEffectConstants.NAVIGATION_DOWN, 1);//设置volume值才能正常播放声音
                }
                ++mExp;
                oldTen = (int) mExp / 10;
                int level = LikeManager.convertSuperLikeLevel(mExp);
                if (level == LikeManager.SUPPER_LIKE) {
                    tenImageView.setImageResource(LikeManager.getLikeNumver(-1));
                    bitImageView.setVisibility(View.GONE);
                    if (mIsLongTouch)
                        numberlayout.setPadding(ScreenUtils.dip2Px(mParent.getContext(), 5), 0, 0, 0);
                    else {
                        ((LinearLayout.LayoutParams) numberlayout.getLayoutParams()).rightMargin = -ScreenUtils.dip2Px(mParent.getContext(), 0);
                        numberlayout.setLayoutParams(numberlayout.getLayoutParams());
                    }
                } else {
                    if (oldTen > 0) {
                        tenImageView.setImageResource(LikeManager.getLikeNumver(oldTen));
                        bitImageView.setImageResource(LikeManager.getLikeNumver((int) mExp % 10));
                    } else {
                        tenImageView.setImageResource(LikeManager.getLikeNumver((int) mExp));
                    }
                }
                if (level != oldLevel) {
                    oldLevel = level;
                    likeLevelImageView.setLayoutParams(getLayoutParams(likeLevelImageView.getLayoutParams(), level));
                    likeLevelImageView.setImageResource(LikeManager.getLikelevelImage(oldLevel));
                    if (mIsLongTouch)
                        showLevelChangeAnimation();
                    else {
                        if (mExp > 9)
                            numberlayout.setPadding(0, 0, padding + ScreenUtils.dip2Px(mParent.getContext(), 4), padding * 2);

                        showClickChangeAnimation();
                    }
                }

                if (mCallback != null)
                    mCallback.onSuperLikeExpCallback(false, (int) mExp);

            }
        }, 0, TimeUnit.MILLISECONDS);
    }


    @SuppressLint("RestrictedApi")
    private void fireSuperLikeOnce() {

        if (mIsTrigger) {
            return;
        }
        setASuperLikeLayoutPosition();
        mIsTrigger = true;

        initInfoView();
        showOnceLike();
        mParent.addView(mLottieContainer);
        mLottieView.setAnimation("super_like_sigle.json");
        mLottieView.playAnimation();
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                stopSuperLike();
            }
        }, 800, TimeUnit.MILLISECONDS);
    }

    /**
     * 触发超级赞
     */
    @SuppressLint("RestrictedApi")
    private void fireSuperLike() {
        if (mIsTrigger) {
            return;
        }

        mIsTrigger = true;

        setASuperLikeLayoutPosition();
        mLottieView.setAnimation("super_like.json", LottieAnimationView.CacheStrategy.Weak);
        initInfoView();
        mParent.addView(mLottieContainer);
        mLottieView.playAnimation();

        handler.postDelayed(runnable, 0);
    }

    /**
     * 取消超级赞
     */
    private void stopSuperLike() {
        /**
         * 倘若手指弹起，还没加载完lottie资源，那么直接cancel
         */

        handler.removeCallbacks(runnable);

        if (mIsTrigger) {
            mParent.remove(mLottieContainer);

            mLottieView.cancelAnimation();
            if (mCallback != null) {
                mCallback.onSuperLikeExpCallback(true, (int) mExp);
//                mCallback = null;
            }
            mIsTrigger = false;
        }
    }


    private void requestParentDisallowInterceptTouchEvent(View triggerView, boolean disallowIntercept) {
        final ViewParent parent = triggerView.getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    private void setASuperLikeLayoutPosition() {
        params.topMargin = mPositions[1] - (offsetHeight <= 0 ? (ScreenUtils.getScreenHeight(mParent.getContext()) * 373 / 615) :
                (ScreenUtils.getScreenHeight(mParent.getContext()) * 398 / 640 - offsetHeight));
        params.leftMargin = mPositions[0] - realWidth * 7 / 9;
        layout.setLayoutParams(params);
    }

    private ViewGroup.LayoutParams getLayoutParams(ViewGroup.LayoutParams params, int level) {

        switch (level) {
            case 2:
                params.width = ScreenUtils.dip2Px(mParent.getContext(), 84);
                break;
            case 3:
                params.width = ScreenUtils.dip2Px(mParent.getContext(), 114);
                break;
            case 4:
                params.width = ScreenUtils.dip2Px(mParent.getContext(), 114);
                break;
            case 5:
                params.width = ScreenUtils.dip2Px(mParent.getContext(), 141);
                break;
        }
        return params;

    }

    private void showLevelChangeAnimation() {
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(infoLayout, "scaleX",
                .8f, 1.5f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(infoLayout, "scaleY",
                .8f, 1.5f);
        ObjectAnimator anim3 = ObjectAnimator.ofFloat(infoLayout, "scaleX",
                1.5f, 1f);
        ObjectAnimator anim4 = ObjectAnimator.ofFloat(infoLayout, "scaleY",
                1.5f, 1f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(anim1).with(anim2);
        animSet.play(anim4).with(anim3).after(anim1);
        animSet.setDuration(50);
        animSet.start();
    }

    private void showClickChangeAnimation() {

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(infoLayout, "translationY",
                80, 0f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(infoLayout, "translationY",
                0f, 80f);
        anim1.setInterpolator(new DecelerateInterpolator());
        anim2.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator anim5 = ObjectAnimator.ofFloat(infoLayout, "alpha",
                0f, 1f);
        ObjectAnimator anim6 = ObjectAnimator.ofFloat(infoLayout, "alpha",
                1f, 0f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(anim1).with(anim5);
        animSet.play(anim2).with(anim6).after(anim1);

        animSet.setDuration(400);
        animSet.start();
    }
}
