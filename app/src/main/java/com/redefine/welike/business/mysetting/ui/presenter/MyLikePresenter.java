package com.redefine.welike.business.mysetting.ui.presenter;

import android.app.Activity;
import android.os.Bundle;

import com.redefine.commonui.loadmore.adapter.OnClickRetryListener;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.frameworkmvp.presenter.MvpTitlePagePresenter1;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.business.common.mission.OnPostButtonClickListener;
import com.redefine.welike.business.feeds.management.FeedsStatusObserver;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.util.FeedHelper;
import com.redefine.welike.commonui.event.expose.ItemExposeManager;
import com.redefine.welike.business.mysetting.ui.contract.IMyLikeContract;
import com.redefine.welike.business.user.management.UserLikePostsManager;
import com.redefine.welike.commonui.event.expose.impl.ItemExposeCallbackFactory;
import com.redefine.welike.commonui.framework.PageStackManager;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.manager.PostEventManager;
import com.redefine.welike.statistical.manager.ShareEventManager;

import java.util.List;

/**
 * Created by gongguan on 2018/2/23.
 */

public class MyLikePresenter extends MvpTitlePagePresenter1<IMyLikeContract.IMyLikeView> implements IMyLikeContract.IMyLikePresenter,
        UserLikePostsManager.UserLikePostsCallback,
        OnClickRetryListener,
        FeedsStatusObserver.FeedsStatusObserverCallback, OnPostButtonClickListener {

    private final UserLikePostsManager userLikePostsManager;
    private final MyLikeFeedAdapter mAdapter;
    private boolean isFirstCreate = true;

    private ItemExposeManager mPostViewTimeManager = new ItemExposeManager(ItemExposeCallbackFactory.Companion.createPostExposeCallback(),
            ItemExposeCallbackFactory.Companion.createPostViewCallback(),
            ItemExposeCallbackFactory.Companion.createPostFollowBtnCallback());

    public MyLikePresenter(Activity activity, Bundle pageBundle) {
        super(activity, pageBundle);
        userLikePostsManager = new UserLikePostsManager();
        mView.setPresenter(this);
        mAdapter = new MyLikeFeedAdapter(new PageStackManager(activity));
        mAdapter.setRetryLoadMoreListener(this);
        mAdapter.setOnPostButtonClickListener(this);
        userLikePostsManager.register(this);

    }

    @Override
    protected IMyLikeContract.IMyLikeView createPageView() {
        return IMyLikeContract.IMyLikeFactory.createView();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mView.setAdapter(mAdapter);
        mView.setRecyclerViewManager(mPostViewTimeManager);
        if (isFirstCreate) {
            isFirstCreate = false;
            mView.showLoading();
            mView.autoRefresh();
        }
        FeedsStatusObserver.getInstance().register(this);
    }

    @Override
    public void destroy() {
        super.destroy();
        mAdapter.destroy();
        FeedsStatusObserver.getInstance().unregister(this);
        userLikePostsManager.register(null);
        mView.destroy();
        mPostViewTimeManager.onDestroy();
    }

    @Override
    public void onRefresh() {
        mAdapter.hideFooter();
        Account account = AccountManager.getInstance().getAccount();
        if (account != null) {
            userLikePostsManager.tryRefreshPosts(account.getUid(), false);
        } else {
            userLikePostsManager.tryRefreshPosts("", false);
        }
    }

    @Override
    public void onActivityResume() {
        mView.onActivityResume();
        mPostViewTimeManager.onResume();
        mPostViewTimeManager.onShow();
    }

    @Override
    public void onActivityPause() {
        mView.onActivityPause();
        mPostViewTimeManager.onPause();
        mPostViewTimeManager.onHide();
    }

    @Override
    public boolean canLoadMore() {
        return mAdapter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        mView.setRefreshEnable(false);
        mAdapter.onLoadMore();
        Account account = AccountManager.getInstance().getAccount();
        userLikePostsManager.tryHisPosts(account.getUid(), false);
    }

    @Override
    public void onRetryLoadMore() {
        if (canLoadMore()) {
            onLoadMore();
        }
    }

    @Override
    public void onListFeedsStatusSuccessed(List<FeedsStatusObserver.FeedStatus> statusList) {
        if (!CollectionUtil.isEmpty(statusList)) {
            mAdapter.refreshFeedsStatus(statusList);
        }
    }

    @Override
    public void onRefreshUserLikePosts(UserLikePostsManager manager, List<PostBase> posts, String uid, int newCount, int errCode) {
        if (userLikePostsManager == manager) {
            if (errCode == ErrorCode.ERROR_SUCCESS) {
                mAdapter.addNewData(posts);
                mAdapter.clearFinishFlag();
                int size = CollectionUtil.getCount(posts);
                // 提醒用户刷新成功条数
                mView.setRefreshCount(newCount);
                mView.autoPlayVideo();
                if (size == 0 && mAdapter.getRealItemCount() == 0) {
                    mView.showEmptyView();
                } else {
                    mView.showContent();
                }
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
        PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_ME_LIKE);
        PostEventManager.INSTANCE.setType(mAdapter.getFeedSource());
        PostEventManager.INSTANCE.sendEventStrategy(FeedHelper.subPosts(posts, newCount));
        mPostViewTimeManager.setData(posts,mAdapter.hasHeader());
        mPostViewTimeManager.onDataLoaded();
    }

    @Override
    public void onReceiveHisUserLikePosts(UserLikePostsManager manager, List<PostBase> posts, String uid, boolean last, int errCode) {
        if (userLikePostsManager == manager) {
            if (errCode == ErrorCode.ERROR_SUCCESS) {
                mAdapter.addHisData(posts);
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
        PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_ME_LIKE);
        PostEventManager.INSTANCE.setType(mAdapter.getFeedSource());
        PostEventManager.INSTANCE.sendEventStrategy(posts);
        mPostViewTimeManager.updateData(posts);
    }

    @Override
    public void onCommentClick(PostBase postBase) {
        EventLog.Feed.report6(14, postBase == null ? "" : postBase.getPid(), PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
    }

    @Override
    public void onLikeClick(PostBase postBase, int exp) {
        exp = Math.min(exp, 100);
        EventLog.Feed.report5(postBase == null ? "" : postBase.getPid(),
                postBase == null ? 0 : postBase.getSuperLikeExp(), exp, 14, PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
    }

    @Override
    public void onForwardClick(PostBase postBase) {
        EventLog.Feed.report7(14, postBase == null ? "" : postBase.getPid(), PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
    }

    @Override
    public void onFollowClick(PostBase postBase) {

    }

    @Override
    public void onShareClick(PostBase postBase, int shareType) {
        ShareEventManager.INSTANCE.setShareType(shareType);
        ShareEventManager.INSTANCE.setFrompage(EventConstants.SHARE_FROM_PAGE_OTHER);
        ShareEventManager.INSTANCE.report3();
    }

    @Override
    public void onPostBodyClick(PostBase postBase) {
        ShareEventManager.INSTANCE.setChannel_page(EventConstants.SHARE_CHANNEL_PAGE_OTHER);
    }

    @Override
    public void onPostAreaClick(PostBase postBase, int clickArea) {

        if (postBase == null) return;

        EventLog.Feed.report8(EventConstants.FEED_SOURCE_ME_LIKES,
                postBase.getUid(), PostEventManager.getPostRootUid(postBase),
                postBase.getPid(), PostEventManager.getPostRootPid(postBase), clickArea,
                PostEventManager.getPostType(postBase), postBase.getStrategy(), postBase.getSequenceId());
    }

}
