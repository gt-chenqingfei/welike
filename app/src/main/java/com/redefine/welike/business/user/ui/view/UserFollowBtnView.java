package com.redefine.welike.business.user.ui.view;

import android.view.View;

import com.redefine.welike.business.user.ui.contract.IUserFollowBtnContract;
import com.redefine.welike.commonui.widget.IFollowBtn;
import com.redefine.welike.commonui.widget.UserFollowBtn;

/**
 * Created by liwenbo on 2018/4/13.
 */

public class UserFollowBtnView implements IUserFollowBtnContract.IUserFollowBtnView {

    private final IFollowBtn mUserFollowBtn;
    private final boolean mIsCancelFollowEnable;
    private IUserFollowBtnContract.IUserFollowBtnPresenter mPresenter;
    private UserFollowBtn.FollowStatus mFollowStatus;

    public UserFollowBtnView(IFollowBtn userFollowBtn, boolean isUnFollowEnable) {
        mUserFollowBtn = userFollowBtn;
        mIsCancelFollowEnable = isUnFollowEnable;
        userFollowBtn.setUnFollowEnable(mIsCancelFollowEnable);
        initView();
    }

    private void initView() {
        mUserFollowBtn.setOnClickFollowBtnListener(new IFollowBtn.OnClickFollowBtnListener() {

            @Override
            public void onClickFollowBtn(View view) {
                onClickUserFollowBtn(view);
            }
        });
    }

    private void onClickUserFollowBtn(View v) {
        switch (mFollowStatus) {
            case GONE:
                break;
            case FOLLOW:
                mPresenter.onClickFollowBtn();
                break;
            case FOLLOWING:
                mPresenter.onClickFollowingBtn(v.getContext());
                break;
            case FOLLOWING_LOADING:
                break;
            case FRIEND:
                mPresenter.onClickFriendBtn(v.getContext());
                break;
            case UN_FOLLOWING_LOADING:
                break;
        }
    }

    @Override
    public void setPresenter(IUserFollowBtnContract.IUserFollowBtnPresenter userFollowBtnPresenter) {
        mPresenter = userFollowBtnPresenter;
    }

    @Override
    public void setFollowStatus(UserFollowBtn.FollowStatus status) {
        mFollowStatus = status;
        mUserFollowBtn.setFollowStatus(status);
    }
}
