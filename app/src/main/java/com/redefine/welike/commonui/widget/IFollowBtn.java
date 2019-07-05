package com.redefine.welike.commonui.widget;

import android.view.View;

/**
 * Created by liwenbo on 2018/4/16.
 */

public interface IFollowBtn {

    void setFollowStatus(IFollowBtn.FollowStatus status);

    void setUnFollowEnable(boolean isCancelFollowEnable);

    IFollowBtn.FollowStatus getFollowStatus();

    void setOnClickFollowBtnListener(OnClickFollowBtnListener onClickFollowBtnListener);

    enum FollowStatus {
        GONE, FOLLOW, FOLLOWING, FRIEND, FOLLOWING_LOADING, UN_FOLLOWING_LOADING
    }

    interface OnClickFollowBtnListener {
        void onClickFollowBtn(View view);
    }

    interface OnFollowBtnClickCallback {
        void onClickFollow(View view);
        void onClickUnfollow(View view);
    }

}
