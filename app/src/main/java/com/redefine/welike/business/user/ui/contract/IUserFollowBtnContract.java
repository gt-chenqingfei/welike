package com.redefine.welike.business.user.ui.contract;

import android.content.Context;

import com.redefine.foundation.mvp.IBasePresenter;
import com.redefine.foundation.mvp.IBaseView;
import com.redefine.welike.business.user.management.FollowUserManager;
import com.redefine.welike.business.user.ui.presenter.UserFollowBtnPresenter;
import com.redefine.welike.business.user.ui.view.UserFollowBtnView;
import com.redefine.welike.commonui.event.expose.model.FollowEventModel;
import com.redefine.welike.commonui.widget.IFollowBtn;
import com.redefine.welike.commonui.widget.UserFollowBtn;

/**
 * Created by liwenbo on 2018/4/13.
 */

public interface IUserFollowBtnContract {

    interface IUserFollowBtnPresenter extends IBasePresenter {

        void onClickFollowBtn();

        void onClickFollowingBtn(Context context);

        void onClickFriendBtn(Context context);

        void bindView(String uid, boolean following, boolean follower, FollowEventModel eventModel);

        void setFollowCallback(FollowUserManager.FollowUserCallback callback);

        void setFollowBtnClickCallback(IFollowBtnClickCallback callback);

        IUserFollowBtnView getView();
    }

    interface IUserFollowBtnView extends IBaseView {

        void setPresenter(IUserFollowBtnPresenter userFollowBtnPresenter);

        void setFollowStatus(UserFollowBtn.FollowStatus status);
    }

    public interface IFollowBtnClickCallback {

        void onClickFollowBtn();
    }


    class UserFollowBtnFactory {
        public static IUserFollowBtnPresenter createPresenter(IFollowBtn userFollowBtn, boolean isCancelFollowEnable) {
            return new UserFollowBtnPresenter(userFollowBtn, isCancelFollowEnable);
        }

        public static IUserFollowBtnPresenter createPresenter(IFollowBtn userFollowBtn, boolean isCancelFollowEnable, boolean isFullScreen) {
            return new UserFollowBtnPresenter(userFollowBtn, isCancelFollowEnable, isFullScreen);
        }

        public static IUserFollowBtnView createView(IFollowBtn userFollowBtn, boolean isCancelFollowEnable) {
            return new UserFollowBtnView(userFollowBtn, isCancelFollowEnable);
        }
    }
}
