package com.redefine.welike.business.user.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.page.PageName;
import com.redefine.welike.business.user.ui.constant.UserConstant;
import com.redefine.welike.business.user.ui.contract.IUserFollowerContract;

/**
 *
 * Created by gongguan on 2018/1/21.
 */
@PageName("UserFollowerFragment")
public class UserFollowerFragment extends Fragment {

    private IUserFollowerContract.IUserFollowerPresenter mPresenter;

    public static UserFollowerFragment create(String uid, boolean show) {
        Bundle bundle = new Bundle();
        bundle.putString(UserConstant.UID, uid);
        bundle.putBoolean(UserConstant.FRAGMENT_SHOW, show);
        UserFollowerFragment fragment = new UserFollowerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mPresenter = IUserFollowerContract.IUserFollowerFactory.createPresenter(null, getArguments());
//        TrackerUtil.getPageTracker().setScreenName(TrackerConstant.PAGE_FOLLOWER);
//        TrackerUtil.getPageTracker().send(new HitBuilders.ScreenViewBuilder().build());
        return mPresenter.createView(getContext(), savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (mPresenter != null) {
                mPresenter.onFragmentShow();
            }
        } else {
            if (mPresenter != null) {
                mPresenter.onFragmentHide();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.onActivityResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPresenter != null) {
            mPresenter.onActivityPause();
        }
    }
}
