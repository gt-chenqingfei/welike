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
import com.redefine.welike.business.user.management.provider.FollowingUsersProvider;
import com.redefine.welike.business.user.ui.adapter.UserFollowingRecyclerAdapter;
import com.redefine.welike.business.user.ui.constant.UserConstant;
import com.redefine.welike.business.user.ui.contract.IUserFollowingContract;
import com.redefine.welike.commonui.event.expose.impl.ItemExposeCallbackFactory;
import com.redefine.welike.statistical.EventConstants;

import java.util.List;

/**
 *
 * Created by gongguan on 2018/1/12.
 */

public class UserFollowingPresenter extends MvpFragmentPagePresenter<IUserFollowingContract.IUserFollowingView> implements IUserFollowingContract.IUserFollowingPresenter,
        UsersManager.UsersCallback, OnClickRetryListener, FollowUserManager.FollowUserCallback {
    private final UsersManager mUsersManager;
    private UserFollowingRecyclerAdapter mAdapter;
    private String mUId;
    private boolean isFirstCreate = true;
    private ItemExposeManager mPostViewTimeManager = new ItemExposeManager(ItemExposeCallbackFactory.Companion.createFollowUserBtnCallback());

    public UserFollowingPresenter(IPageStackManager mPageStackManager, Bundle pageBundle) {
        super(mPageStackManager, pageBundle);
        mUId = pageBundle.getString(UserConstant.UID);
        mUsersManager = new UsersManager();
        mUsersManager.setDataSourceProvider(new FollowingUsersProvider());
        mAdapter = new UserFollowingRecyclerAdapter(EventConstants.FEED_PAGE_FOLLOWING);
        mAdapter.setRetryLoadMoreListener(this);
        mView.setPresenter(this);

        boolean show = pageBundle.getBoolean(UserConstant.FRAGMENT_SHOW);
        if (show) {
            onFragmentShow();
        }
    }

    @Override
    protected IUserFollowingContract.IUserFollowingView createFragmentPageView() {
        return IUserFollowingContract.IUserFollowingFactory.createView();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mView.setAdapter(mAdapter, mPostViewTimeManager);

        if (isFirstCreate) {
            isFirstCreate = false;
            mView.autoRefresh();
            mView.showLoading();
        }
        mUsersManager.setListener(this);
        FollowUserManager.getInstance().register(this);
    }

    @Override
    public void destroy() {
        super.destroy();
        mUsersManager.setListener(null);
        FollowUserManager.getInstance().unregister(this);
        mPostViewTimeManager.onDestroy();
    }

    @Override
    public void onRefresh() {
        mAdapter.hideFooter();
        mUsersManager.tryRefreshContacts(mUId);
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
                // 网络失败给用户提醒
                if (mAdapter.getRealItemCount() == 0) {
                    mView.showErrorView();
                } else {
                    mView.showContent();
                }
                mView.setRefreshCount(-1);
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
