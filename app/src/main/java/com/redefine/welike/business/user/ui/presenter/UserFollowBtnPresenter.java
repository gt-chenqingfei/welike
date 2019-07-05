package com.redefine.welike.business.user.ui.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.redefine.commonui.dialog.CommonConfirmDialog;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.user.management.FollowUserManager;
import com.redefine.welike.business.user.ui.contract.IUserFollowBtnContract;
import com.redefine.welike.commonui.event.expose.model.FollowEventModel;
import com.redefine.welike.commonui.widget.IFollowBtn;
import com.redefine.welike.commonui.widget.UserFollowBtn;

/**
 * Created by liwenbo on 2018/4/13.
 */

public class UserFollowBtnPresenter implements IUserFollowBtnContract.IUserFollowBtnPresenter {

    private final IUserFollowBtnContract.IUserFollowBtnView mView;
    private boolean mFullScreen = false;
    private String mUid;
    private FollowEventModel mEventModel;
    private IUserFollowBtnContract.IFollowBtnClickCallback followBtnClickCallback;

    public UserFollowBtnPresenter(IFollowBtn userFollowBtn, boolean isCancelFollowEnable) {
        mView = IUserFollowBtnContract.UserFollowBtnFactory.createView(userFollowBtn, isCancelFollowEnable);
        mView.setPresenter(this);
    }

    public UserFollowBtnPresenter(IFollowBtn userFollowBtn, boolean isCancelFollowEnable, boolean isFullScreen) {
        this(userFollowBtn, isCancelFollowEnable);
        mFullScreen = isFullScreen;
    }

    @Override
    public void setFollowCallback(FollowUserManager.FollowUserCallback callback) {
//        mCallback = callback;
    }

    @Override
    public void setFollowBtnClickCallback(IUserFollowBtnContract.IFollowBtnClickCallback callback) {
        this.followBtnClickCallback = callback;
    }

    @Override
    public void bindView(String uid, boolean following, boolean follower, FollowEventModel eventModel) {
        mUid = uid;
        mEventModel = eventModel;
        if (AccountManager.getInstance().isSelf(uid) && AccountManager.getInstance().isLoginComplete()) {
            mView.setFollowStatus(UserFollowBtn.FollowStatus.GONE);
        } else {
            if (FollowUserManager.getInstance().isFollowRequesting(uid)) {
                // 正在请求follow
                mView.setFollowStatus(UserFollowBtn.FollowStatus.FOLLOWING);
            } else if (FollowUserManager.getInstance().isUnfollowRequesting(uid)) {
                mView.setFollowStatus(UserFollowBtn.FollowStatus.UN_FOLLOWING_LOADING);
            } else {
                if (following && follower) {
                    mView.setFollowStatus(UserFollowBtn.FollowStatus.FRIEND);
                } else if (follower) {
                    mView.setFollowStatus(UserFollowBtn.FollowStatus.FOLLOW);
                } else if (following) {
                    mView.setFollowStatus(UserFollowBtn.FollowStatus.FOLLOWING);
                } else {
                    mView.setFollowStatus(UserFollowBtn.FollowStatus.FOLLOW);
                }
            }
        }
    }

    @Override
    public IUserFollowBtnContract.IUserFollowBtnView getView() {
        return mView;
    }

    @Override
    public void onClickFollowBtn() {
        if (followBtnClickCallback != null) {
            followBtnClickCallback.onClickFollowBtn();
        }
        if (!TextUtils.isEmpty(mUid)) {
            mView.setFollowStatus(UserFollowBtn.FollowStatus.FOLLOWING_LOADING);
            FollowUserManager.getInstance().follow(mUid, mEventModel);
        }
    }

    @Override
    public void onClickFollowingBtn(Context context) {
        if (followBtnClickCallback != null) {
            followBtnClickCallback.onClickFollowBtn();
        }
        CommonConfirmDialog.IConfirmDialogListener callback = new CommonConfirmDialog.IConfirmDialogListener() {
            @Override
            public void onClickCancel() {

            }

            @Override
            public void onClickConfirm() {
                if (!TextUtils.isEmpty(mUid)) {
                    mView.setFollowStatus(UserFollowBtn.FollowStatus.UN_FOLLOWING_LOADING);
                    FollowUserManager.getInstance().unfollow(mUid, mEventModel);
                }
            }
        };
        if (mFullScreen) {
            CommonConfirmDialog.showFullScreenConfirmDialog(context,
                    context.getString(R.string.user_follow_dialog_title), callback).setFullScreen(mFullScreen);
        } else {
            CommonConfirmDialog.showConfirmDialog(context,
                    context.getString(R.string.user_follow_dialog_title), callback).setFullScreen(mFullScreen);
        }
    }


    @Override
    public void onClickFriendBtn(Context context) {
        if (followBtnClickCallback != null) {
            followBtnClickCallback.onClickFollowBtn();
        }

        if (followBtnClickCallback != null) {
            followBtnClickCallback.onClickFollowBtn();
        }
        CommonConfirmDialog.IConfirmDialogListener callback = new CommonConfirmDialog.IConfirmDialogListener() {
            @Override
            public void onClickCancel() {

            }

            @Override
            public void onClickConfirm() {
                if (!TextUtils.isEmpty(mUid)) {
                    mView.setFollowStatus(UserFollowBtn.FollowStatus.UN_FOLLOWING_LOADING);
                    FollowUserManager.getInstance().unfollow(mUid, mEventModel);
                }
            }
        };
        if (mFullScreen) {
            CommonConfirmDialog.showFullScreenConfirmDialog(context,
                    context.getString(R.string.user_follow_dialog_title), callback);
        } else {
            CommonConfirmDialog.showConfirmDialog(context,
                    context.getString(R.string.user_follow_dialog_title), callback);
        }
    }


}
