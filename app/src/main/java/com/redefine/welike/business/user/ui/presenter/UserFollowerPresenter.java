package com.redefine.welike.business.user.ui.presenter;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.loadmore.adapter.OnClickRetryListener;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.frameworkmvp.presenter.MvpFragmentPagePresenter;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.commonui.event.expose.ItemExposeManager;
import com.redefine.welike.business.user.management.FollowUserManager;
import com.redefine.welike.business.user.management.UsersManager;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.management.provider.FollowedUsersProvider;
import com.redefine.welike.business.user.ui.adapter.UserFollowRecyclerAdapter;
import com.redefine.welike.business.user.ui.constant.UserConstant;
import com.redefine.welike.business.user.ui.contract.IUserFollowerContract;
import com.redefine.welike.commonui.event.expose.impl.ItemExposeCallbackFactory;
import com.redefine.welike.statistical.EventConstants;

import java.util.List;

/**
 * Created by gongguan on 2018/1/22.
 */

public class UserFollowerPresenter extends MvpFragmentPagePresenter<IUserFollowerContract.IUserFollowerView> implements IUserFollowerContract.IUserFollowerPresenter,
        UsersManager.UsersCallback, OnClickRetryListener, FollowUserManager.FollowUserCallback {
    private final String mUId;
    private final UsersManager mUsersManager;
    private UserFollowRecyclerAdapter mAdapter;
    private boolean isFirstCreate = true;
    private ItemExposeManager mPostViewTimeManager = new ItemExposeManager(ItemExposeCallbackFactory.Companion.createFollowUserBtnCallback());

    public UserFollowerPresenter(IPageStackManager mPageStackManager, Bundle pageBundle) {
        super(mPageStackManager, pageBundle);
        mUId = pageBundle.getString(UserConstant.UID);
        mUsersManager = new UsersManager();
        FollowedUsersProvider followedProvider = new FollowedUsersProvider();
        mUsersManager.setDataSourceProvider(followedProvider);
        mAdapter = new UserFollowRecyclerAdapter(EventConstants.FEED_PAGE_FOLLOWER);
        mAdapter.setRetryLoadMoreListener(this);
        mView.setPresenter(this);

        boolean show = pageBundle.getBoolean(UserConstant.FRAGMENT_SHOW);
        if (show) {
            onFragmentShow();
        }
    }

    @Override
    protected IUserFollowerContract.IUserFollowerView createFragmentPageView() {
        return IUserFollowerContract.IUserFollowerFactory.createView();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mView.setAdapter(mAdapter, mPostViewTimeManager);
        if (isFirstCreate) {
            isFirstCreate = false;
            mView.autoRefresh();
            mView.showLoading();
        }
        FollowUserManager.getInstance().register(this);
        mUsersManager.setListener(this);
    }

    @Override
    public void onRefresh() {
        mAdapter.hideFooter();
        mUsersManager.tryRefreshContacts(mUId);
    }

    @Override
    public void destroy() {
        super.destroy();
        mUsersManager.setListener(null);
        FollowUserManager.getInstance().unregister(this);
        mPostViewTimeManager.onDestroy();
    }

    @Override
    public boolean canLoadMore() {
        return mAdapter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        mView.setRefreshEnable(false);
        mAdapter.onLoadMore();
        mUsersManager.tryHisContacts(mUId);
    }

    @Override
    public void onActivityResume() {
        mPostViewTimeManager.onResume();
    }

    @Override
    public void onActivityPause() {
        mPostViewTimeManager.onPause();
    }

    @Override
    public void onFragmentShow() {
        mPostViewTimeManager.onAttach();
        mPostViewTimeManager.onShow();
    }

    @Override
    public void onFragmentHide() {
        mPostViewTimeManager.onDetach();
        mPostViewTimeManager.onHide();
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onRefreshUsers(UsersManager manager, List<User> users, String uid, int newCount, int errCode) {
        if (mUsersManager == manager) {
            if (errCode == ErrorCode.ERROR_SUCCESS) {
                mAdapter.refreshData(users);
                mAdapter.clearFinishFlag();
                int size = CollectionUtil.getCount(users);
                // 提醒用户刷新成功条数
                mView.setRefreshCount(newCount);
                if (size == 0 && mAdapter.getRealItemCount() == 0) {
                    mView.showEmptyView();
                } else {
                    mView.showContent();
                }
                mPostViewTimeManager.onDataLoaded();
            } else {
                if (mAdapter.getRealItemCount() == 0) {
                    mView.showErrorView();
                } else {
                    mView.showContent();
                }
                mView.setRefreshCount(-1);
                // 网络失败给用户提醒
            }
            mView.finishRefresh();
        }
        mAdapter.showFooter();

    }

    @Override
    public void onReceiveHisUsers(UsersManager manager, List<User> users, String uid, boolean last, int errCode) {
        if (mUsersManager == manager) {
            if (errCode == ErrorCode.ERROR_SUCCESS) {
                mAdapter.addNewData(users);
                if (last) {
                    mAdapter.noMore();
                } else {
                    mAdapter.finishLoadMore();
                }
            } else {
                mAdapter.loadError();
            }
        }
        mView.setRefreshEnable(true);
    }

    @Override
    public void onRetryLoadMore() {
        if (canLoadMore()) {
            onLoadMore();
        }
    }

    @Override
    public void onFollowCompleted(String uid, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            mAdapter.refreshOnFollow(uid);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onUnfollowCompleted(String uid, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            mAdapter.refreshOnUnFollow(uid);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }
}
