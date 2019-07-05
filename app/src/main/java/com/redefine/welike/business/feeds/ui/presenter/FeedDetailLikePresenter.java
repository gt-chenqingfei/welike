package com.redefine.welike.business.feeds.ui.presenter;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.loadmore.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.redefine.commonui.loadmore.adapter.OnClickRetryListener;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.frameworkmvp.presenter.MvpFragmentPagePresenter;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.adapter.FeedDetailLikeAdapter;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.business.feeds.ui.contract.IFeedDetailLikeContract;
import com.redefine.welike.business.feeds.ui.fragment.IRefreshDelegate;
import com.redefine.welike.business.user.management.PostLikeUsersManager;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.ui.page.UserHostPage;

import java.util.List;

/**
 * Created by MR on 2018/1/20.
 */

public class FeedDetailLikePresenter extends MvpFragmentPagePresenter<IFeedDetailLikeContract.IFeedDetailLikeView> implements IFeedDetailLikeContract.IFeedDetailLikePresenter, PostLikeUsersManager.PostLikeUsersCallback, OnClickRetryListener {
    private final FeedDetailLikeAdapter mAdapter;
    private final IRefreshDelegate mRefreshDelegate;
    private final PostLikeUsersManager mModel;
    private PostBase mPostBase;

    public FeedDetailLikePresenter(IPageStackManager pageStackManager, IRefreshDelegate refreshDelegate, Bundle pageBundle) {
        super(pageStackManager, pageBundle);
        if (pageBundle != null) {
            mPostBase = (PostBase) pageBundle.getSerializable(FeedConstant.KEY_FEED);
        }
        mView.setPresenter(this);
        mAdapter = new FeedDetailLikeAdapter();
        mAdapter.setOnItemClickListener(new HeaderAndFooterRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder, Object t) {
                if (t instanceof User) {
                    if (!AccountManager.getInstance().isLogin()) {
                        onBrowseFeedDetailLikeClick(BrowseConstant.TYPE_UNKOWN);
                        UserHostPage.launch(true, ((User) t).getUid());
                        return;
                    }
                    UserHostPage.launch(true, ((User) t).getUid());
                }
            }
        });
        mAdapter.setRetryLoadMoreListener(this);
        mModel = new PostLikeUsersManager();
        mModel.setListener(this);
        mRefreshDelegate = refreshDelegate;
    }

    @Override
    protected IFeedDetailLikeContract.IFeedDetailLikeView createFragmentPageView() {
        return IFeedDetailLikeContract.FeedDetailLikeFactory.createView();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if (mPostBase == null && savedInstanceState != null) {
            mPostBase = (PostBase) savedInstanceState.getSerializable(FeedConstant.KEY_FEED);
        }
        mView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mView.showLoading();
        mModel.tryRefresh(mPostBase.getPid(), !AccountManager.getInstance().isLogin());
    }

    @Override
    public boolean canLoadMore() {
        return mAdapter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        mRefreshDelegate.setRefreshEnable(false);
        mAdapter.onLoadMore();
        mModel.tryHis(mPostBase.getPid(), !AccountManager.getInstance().isLogin());
    }

    @Override
    public void onRefresh() {
        if (mAdapter.getRealItemCount() == 0) {
            mView.showLoading();
        }
        mAdapter.hideFooter();
        mModel.tryRefresh(mPostBase.getPid(), !AccountManager.getInstance().isLogin());
    }

    @Override
    public void onNewLike(User user) {
        mAdapter.addNewData(user);
        mView.showContent();
    }

    @Override
    public void refreshPostBase(PostBase postBase) {
        mPostBase = postBase;
    }

    @Override
    public void onRefreshPostLikeUsers(PostLikeUsersManager manager, List<User> users, String fid, int errCode) {
        if (mModel == manager) {
            boolean isSuccess = errCode == ErrorCode.ERROR_SUCCESS;
            if (isSuccess) {
                mAdapter.addNewData(users);
                mAdapter.clearFinishFlag();
            }

            if (!CollectionUtil.isEmpty(users) || mAdapter.getRealItemCount() > 0) {
                mView.showContent();
            } else if (isSuccess) {
                mView.showEmptyView();
            } else {
                mView.showErrorView();
            }
            if (mRefreshDelegate != null) {
                mRefreshDelegate.stopRefresh();
            }
        }
        mAdapter.showFooter();
    }

    @Override
    public void onReceiveHisPostLikeUsers(PostLikeUsersManager manager, List<User> users, String fid, boolean last, int errCode) {
        if (mModel == manager) {
            if (errCode == ErrorCode.ERROR_SUCCESS) {
                mAdapter.addHisData(users);
                if (last) {
                    mAdapter.noMore();
                } else {
                    mAdapter.finishLoadMore();
                }
            } else {
                mAdapter.loadError();
            }
        }
        mRefreshDelegate.setRefreshEnable(true);
    }

    @Override
    public void onRetryLoadMore() {
        if (canLoadMore()) {
            onLoadMore();
        }
    }


    public void onBrowseFeedDetailLikeClick(int tye) {


    }
}
