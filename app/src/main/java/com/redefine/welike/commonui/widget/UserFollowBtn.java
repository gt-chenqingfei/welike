package com.redefine.welike.commonui.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

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

public class UserFollowBtn extends FrameLayout implements IFollowBtn {

    public IFollowBtn.FollowStatus followStatus = FOLLOW;
    private boolean mIsCancelFollowEnable = true;
    private boolean mUnFollowShowStroke;
    private int mTextSize = ScreenUtils.dip2Px(12);
    private TextView mFollowBtn;
    private ProgressView progress;
    private OnFollowBtnClickCallback mCallback;

    public UserFollowBtn(Context context) {
        super(context);
        initViews();
    }

    public UserFollowBtn(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public UserFollowBtn(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        mFollowBtn = new TextView(getContext());
        LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        mFollowBtn.setEllipsize(TextUtils.TruncateAt.END);
        mFollowBtn.setSingleLine();
//        mFollowBtn.setMaxWidth(DensityUtil.dp2px(56));
        mFollowBtn.setGravity(Gravity.CENTER);
        mFollowBtn.setAllCaps(true);
        mFollowBtn.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mFollowBtn.setBackgroundResource(R.drawable.ripple1);
        addView(mFollowBtn, layoutParams);

        LayoutParams loadingParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        progress = new ProgressView(getContext());
        addView(progress, loadingParams);
    }

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
        setVisibility(VISIBLE);
        progress.setLoadingRenderer(new MaterialLoadingRenderer.Builder(getContext())
                .setStrokeWidth(ScreenUtils.dip2Px(3))
                .setColors(new int[]{ContextCompat.getColor(getContext(), com.redefine.commonui.R.color.main_orange_dark)}).build());
        showLoading(true);
        setBackgroundResource(R.drawable.follow_btn_gray_bound);
        mFollowBtn.setPadding(0, 0, 0, 0);
        mFollowBtn.setCompoundDrawablePadding(ScreenUtils.dip2Px(0));
        mFollowBtn.setTextColor(getResources().getColor(R.color.follow_btn_gray_color));
        mFollowBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mFollowBtn.setText("");
    }

    private void doFollowingLoadingStatus() {

        setVisibility(VISIBLE);
        progress.setLoadingRenderer(new MaterialLoadingRenderer.Builder(getContext())
                .setStrokeWidth(ScreenUtils.dip2Px(3))
                .setColors(new int[]{ContextCompat.getColor(getContext(), R.color.white)}).build());
        showLoading(true);
        setBackgroundResource(R.drawable.common_btn_orange_solid_bg);
        mFollowBtn.setPadding(0, 0, 0, 0);
        mFollowBtn.setCompoundDrawablePadding(ScreenUtils.dip2Px(3));
        mFollowBtn.setTextColor(getResources().getColor(R.color.follow_btn_orange_color));
        mFollowBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mFollowBtn.setText("");
    }

    private void doFriendStatus() {
        if (!mIsCancelFollowEnable) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        showLoading(false);
        if (mUnFollowShowStroke) {
            setBackgroundResource(R.drawable.common_btn_orange_stroke_bg);
        } else {
            setBackgroundResource(android.R.color.transparent);
        }
        mFollowBtn.setPadding(0, 0, 0, 0);
        mFollowBtn.setCompoundDrawablePadding(0);
        mFollowBtn.setCompoundDrawables(null, null, null, null);
        mFollowBtn.setTextColor(getResources().getColor(R.color.main_orange_dark));
        mFollowBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScreenUtils.dip2Px(12));
        mFollowBtn.setText(R.string.friends_btn_text);
    }

    private void doFollowingStatus() {
        if (!mIsCancelFollowEnable) {
            setVisibility(GONE);
            return;
        }
        showLoading(false);
        setVisibility(VISIBLE);
        mFollowBtn.setPadding(0, 0, 0, 0);
        if (mUnFollowShowStroke) {
            setBackgroundResource(R.drawable.common_btn_orange_stroke_bg);
        } else {
            setBackgroundResource(android.R.color.transparent);
        }
        mFollowBtn.setCompoundDrawablePadding(0);
        mFollowBtn.setCompoundDrawables(null, null, null, null);
        mFollowBtn.setTextColor(getResources().getColor(R.color.main_orange_dark));

        mFollowBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScreenUtils.dip2Px(12));
        mFollowBtn.setText(R.string.following_btn_text);
    }

    private void doFollowStatus() {
        setVisibility(VISIBLE);
        showLoading(false);
        setBackgroundResource(R.drawable.common_btn_orange_solid_bg);
        mFollowBtn.setPadding(ScreenUtils.dip2Px(0), 0, 0, 0);
        mFollowBtn.setCompoundDrawablePadding(ScreenUtils.dip2Px(0));
        mFollowBtn.setCompoundDrawables(null, null, null, null);
        mFollowBtn.setTextColor(getResources().getColor(R.color.white));

        mFollowBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScreenUtils.dip2Px(12));
        mFollowBtn.setText(R.string.follow_btn_text);
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progress.setVisibility(VISIBLE);
        } else {
            progress.setVisibility(GONE);
        }

    }


    public void setUnFollowEnable(boolean isCancelFollowEnable) {
        mIsCancelFollowEnable = isCancelFollowEnable;
    }

    public void setUnFollowShowStroke(boolean unFollowShowStroke) {
        mUnFollowShowStroke = unFollowShowStroke;
    }

    public void setTextSize(int textSize) {
        mTextSize = textSize;
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
                    if ((followStatus == FRIEND || followStatus == FOLLOWING) && !mIsCancelFollowEnable)
                        return;
                    onClickFollowBtnListener.onClickFollowBtn(v);
                }
            }
        });
    }

    View.OnClickListener otherlistener;

    public void addOtherListener(View.OnClickListener listener) {
        otherlistener = listener;
    }
}
