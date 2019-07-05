package com.redefine.welike.commonui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.redefine.commonui.widget.loading.ProgressView;
import com.redefine.commonui.widget.loading.render.circle.rotate.MaterialLoadingRenderer;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;

import static com.redefine.welike.commonui.widget.IFollowBtn.FollowStatus.FOLLOW;
import static com.redefine.welike.commonui.widget.IFollowBtn.FollowStatus.FOLLOWING;
import static com.redefine.welike.commonui.widget.IFollowBtn.FollowStatus.FOLLOWING_LOADING;
import static com.redefine.welike.commonui.widget.IFollowBtn.FollowStatus.FRIEND;

/**
 * Created by liwenbo on 2018/4/12.
 */

public class SmallUserFollowBtn extends FrameLayout implements IFollowBtn {

    public FollowStatus followStatus = FOLLOW;
    private boolean mIsCancelFollowEnable;
    private AppCompatImageView mFollowBtn;
    private ProgressView circularProgressBar;
    private OnFollowBtnClickCallback mCallback;
    private boolean mUnFollowShowStroke;

    public SmallUserFollowBtn(Context context) {
        super(context);
        initViews();
    }

    public SmallUserFollowBtn(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public SmallUserFollowBtn(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        mFollowBtn = new AppCompatImageView(getContext());
        LayoutParams layoutParams = new LayoutParams(ScreenUtils.dip2Px(24), ScreenUtils.dip2Px(24));
        layoutParams.gravity = Gravity.CENTER;
        mFollowBtn.setBackgroundResource(R.drawable.ripple1);
        addView(mFollowBtn, layoutParams);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.common_follow_progressbar, this, false);
        addView(view);
        circularProgressBar = this.findViewById(R.id.common_progressbar);
        circularProgressBar.setLoadingRenderer(new MaterialLoadingRenderer.Builder(getContext())
                .setStrokeWidth(ScreenUtils.dip2Px(3))
                .setColors(new int[]{ContextCompat.getColor(getContext(), com.redefine.commonui.R.color.main_orange_dark)}).build());
    }

    public void setFollowStatus(FollowStatus status) {
        switch (status) {
            case GONE:
                doGoneStatus();
                break;
            case FOLLOW:
                doFollowStatus();
                break;
            case FOLLOWING:
                doFollowingStatus();
                break;
            case FRIEND:
                doFriendStatus();
                break;
            case FOLLOWING_LOADING:
                doFollowingLoadingStatus();
                break;
            case UN_FOLLOWING_LOADING:
                doUnFollowingLoadingStatus();
                break;
            default:
                doFollowStatus();
                break;
        }
        followStatus = status;
    }

    private void doGoneStatus() {
        setVisibility(GONE);
    }

    private void doUnFollowingLoadingStatus() {
        if (!mIsCancelFollowEnable) {
            setVisibility(GONE);
            return;
        }
        mFollowBtn.setImageResource(R.drawable.white_circle_bg);
        setVisibility(VISIBLE);
        showLoading(true);
    }

    private void doFollowingLoadingStatus() {
        setVisibility(VISIBLE);
        mFollowBtn.setImageResource(R.drawable.white_circle_bg);
        showLoading(true);
    }

    private void doFriendStatus() {
        if (!mIsCancelFollowEnable) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        showLoading(false);
        mFollowBtn.setImageResource(R.drawable.small_follow_btn_friend);
    }

    private void doFollowingStatus() {
        if (!mIsCancelFollowEnable) {
            setVisibility(GONE);
            return;
        }
        showLoading(false);
        setVisibility(VISIBLE);
        mFollowBtn.setImageResource(R.drawable.small_follow_btn_followed);
    }

    private void doFollowStatus() {
        setVisibility(VISIBLE);
        showLoading(false);
        mFollowBtn.setImageResource(R.drawable.small_follow_btn_follow);
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            circularProgressBar.setVisibility(VISIBLE);
        } else {
            circularProgressBar.setVisibility(GONE);
        }

    }


    public void setUnFollowEnable(boolean isCancelFollowEnable) {
        mIsCancelFollowEnable = isCancelFollowEnable;
    }

    public void setUnFollowShowStroke(boolean unFollowShowStroke) {
        mUnFollowShowStroke = unFollowShowStroke;
    }

    public void setFollowBtnClickCallback(OnFollowBtnClickCallback callback) {
        mCallback = callback;
    }

    @Override
    public FollowStatus getFollowStatus() {
        return followStatus;
    }

    @Override
    public void setOnClickFollowBtnListener(final OnClickFollowBtnListener onClickFollowBtnListener) {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otherlistener != null) {
                    otherlistener.onClick(v);
                }
                if (mCallback != null) {
                    if (followStatus == FOLLOW || followStatus == FOLLOWING_LOADING) {
                        mCallback.onClickFollow(v);
                    } else if (followStatus == FRIEND || followStatus == FOLLOWING) {
                        mCallback.onClickUnfollow(v);
                    }
                }
                if (onClickFollowBtnListener != null) {
                    onClickFollowBtnListener.onClickFollowBtn(v);
                }
            }
        });
    }

    OnClickListener otherlistener;

    public void addOtherListener(OnClickListener listener) {
        otherlistener = listener;
    }
}
