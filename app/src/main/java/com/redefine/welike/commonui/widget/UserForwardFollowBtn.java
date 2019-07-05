package com.redefine.welike.commonui.widget;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;

/**
 * Created by liwenbo on 2018/4/12.
 */

public class UserForwardFollowBtn extends AppCompatImageView implements IFollowBtn {

    private IFollowBtn.FollowStatus mFollowStatus = IFollowBtn.FollowStatus.FOLLOW;
    private boolean mIsCancelFollowEnable;

    public UserForwardFollowBtn(Context context) {
        super(context);
        initViews();
    }

    public UserForwardFollowBtn(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public UserForwardFollowBtn(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        setScaleType(ScaleType.CENTER);
    }

    @Override
    public void setFollowStatus(IFollowBtn.FollowStatus status) {
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
        mFollowStatus = status;
    }

    public IFollowBtn.FollowStatus getFollowStatus() {
        return mFollowStatus;
    }

    @Override
    public void setOnClickFollowBtnListener(final OnClickFollowBtnListener onClickFollowBtnListener) {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickFollowBtnListener != null) {
                    onClickFollowBtnListener.onClickFollowBtn(v);
                }
            }
        });
    }

    private void doGoneStatus() {
        setVisibility(GONE);
    }

    private void doUnFollowingLoadingStatus() {
        if (!mIsCancelFollowEnable) {
            setVisibility(GONE);
            return ;
        }
        setVisibility(VISIBLE);
        setBackgroundResource(R.drawable.forward_follow_btn_gray_bound);

        Drawable drawable = ResourceTool.getBoundDrawable(getResources(), R.drawable.following_btn_loading);
        setImageResource(R.drawable.following_btn_loading);

        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }

    private void doFollowingLoadingStatus() {
        setVisibility(VISIBLE);
        setBackgroundResource(R.drawable.forward_follow_btn_orange_bound);

        Drawable drawable = ResourceTool.getBoundDrawable(getResources(), R.drawable.following_btn_loading);
        setImageResource(R.drawable.following_btn_loading);
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }

    private void doFriendStatus() {
        if (!mIsCancelFollowEnable) {
            setVisibility(GONE);
            return ;
        }
        setVisibility(VISIBLE);
        setBackgroundResource(R.drawable.forward_follow_btn_gray_bound);
        setImageResource(R.drawable.forward_follow_firend);
    }

    private void doFollowingStatus() {
        if (!mIsCancelFollowEnable) {
            setVisibility(GONE);
            return ;
        }
        setVisibility(VISIBLE);
        setBackgroundResource(R.drawable.forward_follow_btn_gray_bound);
        setImageResource(R.drawable.forward_following_btn);
    }

    private void doFollowStatus() {
        setVisibility(VISIBLE);
        setBackgroundResource(R.drawable.forward_follow_btn_orange_bound);
        setImageResource(R.drawable.follow_btn_icon);
    }

    @Override
    public void setUnFollowEnable(boolean isCancelFollowEnable) {
        mIsCancelFollowEnable = isCancelFollowEnable;
    }
}
