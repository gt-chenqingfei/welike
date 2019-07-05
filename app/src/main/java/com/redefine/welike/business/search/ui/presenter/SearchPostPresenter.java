package com.redefine.welike.business.search.ui.presenter;

import android.os.Bundle;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.loadmore.adapter.OnClickRetryListener;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.frameworkmvp.presenter.MvpFragmentPagePresenter;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.browse.ui.listener.IBrowseClickListener;
import com.redefine.welike.business.common.mission.OnPostButtonClickListener;
import com.redefine.welike.business.feeds.management.SearchManager;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.commonui.event.expose.ItemExposeManager;
import com.redefine.welike.business.search.ui.adapter.SearchPostAdapter;
import com.redefine.welike.business.search.ui.contract.ISearchPostContract;
import com.redefine.welike.business.startup.management.HalfLoginManager;
import com.redefine.welike.business.startup.management.StartEventManager;
import com.redefine.welike.commonui.event.helper.LoginEventHelper;
import com.redefine.welike.commonui.event.expose.impl.ItemExposeCallbackFactory;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;
import com.redefine.welike.statistical.manager.PostEventManager;
import com.redefine.welike.statistical.manager.ShareEventManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwenbo on 2018/2/11.
 */

public class SearchPostPresenter extends MvpFragmentPagePresenter<ISearchPostContract.ISearchPostView>
        implements ISearchPostContract.ISearchPostPresenter
        , OnClickRetryListener, SearchManager.SearchManagerListener, OnPostButtonClickListener {


    private final SearchPostAdapter mAdapter;
    private final SearchManager mModel;
    private String mSearchQuery;
    private boolean mAuth;

    private ItemExposeManager mPostViewTimeManager = new ItemExposeManager(ItemExposeCallbackFactory.Companion.createPostExposeCallback(),
            ItemExposeCallbackFactory.Companion.createPostViewCallback(),
            ItemExposeCallbackFactory.Companion.createPostFollowBtnCallback());

    public SearchPostPresenter(IPageStackManager pageStackManager, Bundle pageConfig) {
        super(pageStackManager, pageConfig);
        mView.setPresenter(this);
        mAdapter = new SearchPostAdapter(pageStackManager);
        mAdapter.setRetryLoadMoreListener(this);
        mAdapter.setOnPostButtonClickListener(this);
        mModel = new SearchManager();
        mModel.setSearchType(SearchManager.SEARCH_MANAGER_TYPE_POSTS);
        mAuth = AccountManager.getInstance().isLogin();
    }

    @Override
    protected ISearchPostContract.ISearchPostView createFragmentPageView() {
        return ISearchPostContract.SearchPostFactory.createView();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mView.setAdapter(mAdapter);
        mView.setRecyclerViewManager(mPostViewTimeManager);
        mModel.register(this);
    }

    @Override
    public boolean canLoadMore() {
        return mAdapter.canLoadMore();
    }

    @Override
    public void destroy() {
        super.destroy();
        mModel.unregister(this);
        mAdapter.destroy();
        mPostViewTimeManager.onDestroy();
    }

    @Override
    public void attach() {
        super.attach();
        PostEventManager.INSTANCE.reset();
        PostEventManager.INSTANCE.setAction(EventConstants.AUTO_REFRESH);
    }

    @Override
    public void detach() {
        super.detach();
        mAdapter.addNewData(null);
    }

    @Override
    public void onLoadMore() {
        mAdapter.onLoadMore();
        mModel.loadMore(mAuth);
        PostEventManager.INSTANCE.setAction(EventConstants.PULL_UP_REFRESH);
    }

    @Override
    public void onRefresh(String searchText) {
        mSearchQuery = searchText;

        mModel.search(searchText, mAuth);
        mView.showLoading();
        if (!mAuth) {
            mAdapter.setBrowseClickListener(mBrowseClickListener);
        }
    }

    @Override
    public void retryRefresh() {
        onRefresh(mSearchQuery);
        PostEventManager.INSTANCE.setAction(EventConstants.CLICK_BUTTON_REFRESH);
    }

    @Override
    public void onActivityResume() {
        mView.onActivityResume();
        mPostViewTimeManager.onResume();
    }

    @Override
    public void onActivityPause() {
        mView.onActivityPause();
        mPostViewTimeManager.onPause();
    }

    @Override
    public void onPageStateChanged(int oldState, int newState) {
        mView.onPageStateChanged(oldState, newState);
    }

    @Override
    public void onRetryLoadMore() {
        if (canLoadMore()) {
            onLoadMore();
        }
    }

    @Override
    public void onNewSearchResult(List<Object> contents, int searchType, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            mAdapter.addNewData(fileType(contents));
            mAdapter.clearFinishFlag();
            if (CollectionUtil.isEmpty(contents)) {
                mView.showEmptyView();
            } else {
                mView.showContentView();
                mView.autoPlayVideo();
            }

            mPostViewTimeManager.setData(fileType(contents), mAdapter.hasHeader());

        } else {
            mView.showErrorView();
        }
        mPostViewTimeManager.onDataLoaded();
        PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_SEARCH_POSTS);
        PostEventManager.INSTANCE.setType(mAdapter.getFeedSource());
        PostEventManager.INSTANCE.sendEventStrategy(fileType(contents));
    }

    @Override
    public void onMoreSearchResult(List<Object> contents, int searchType, boolean last, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            mAdapter.addHisData(fileType(contents));
            if (last) {
                mAdapter.noMore();
            } else {
                mAdapter.finishLoadMore();
            }
            mPostViewTimeManager.updateData(fileType(contents));
        } else {
            mAdapter.loadError();
        }
        PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_SEARCH_POSTS);
        PostEventManager.INSTANCE.setType(mAdapter.getFeedSource());
        PostEventManager.INSTANCE.sendEventStrategy(fileType(contents));
    }

    private List<PostBase> fileType(List<Object> contents) {
        List<PostBase> postBases = new ArrayList<>();
        if (CollectionUtil.isEmpty(contents)) {
            return postBases;
        }
        for (Object o : contents) {
            if (o instanceof PostBase) {
                postBases.add((PostBase) o);
            }
        }
        return postBases;
    }

    @Override
    public void onCommentClick(PostBase postBase) {
        EventLog.Feed.report6(5, postBase == null ? "" : postBase.getPid(), PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
    }

    @Override
    public void onLikeClick(PostBase postBase, int exp) {
        exp = Math.min(exp, 100);
        EventLog.Feed.report5(postBase == null ? "" : postBase.getPid(),
                postBase == null ? 0 : postBase.getSuperLikeExp(), exp, 5, PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
    }

    @Override
    public void onForwardClick(PostBase postBase) {
        EventLog.Feed.report7(5, postBase == null ? "" : postBase.getPid(), PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
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

        if (mAdapter != null && mAdapter.indexOfPost(postBase) >= 0) {
            EventLog1.Search.report3(mSearchQuery, postBase.getUid(), postBase.getPid(), postBase.getLanguage(), postBase.getTags(), postBase.getSequenceId(), mAdapter.indexOfPost(postBase));
        }
    }

    @Override
    public void onPostAreaClick(PostBase postBase, int clickArea) {

        if (postBase == null) return;

        EventLog.Feed.report8(EventConstants.FEED_SOURCE_SEARCH_POSTS,
                postBase.getUid(), PostEventManager.getPostRootUid(postBase),
                postBase.getPid(), PostEventManager.getPostRootPid(postBase), clickArea,
                PostEventManager.getPostType(postBase), postBase.getStrategy(), postBase.getSequenceId());
    }

    private IBrowseClickListener mBrowseClickListener = new IBrowseClickListener() {
        @Override
        public void onBrowseClick(int tye, boolean isShowLogin, int showType) {
            StartEventManager.getInstance().setActionType(tye > 5 ? 6 : tye);
            StartEventManager.getInstance().setFrom_page(5);
            if (tye == BrowseConstant.TYPE_SHARE) {
                EventLog.UnLogin.report16(StartEventManager.getInstance().getFrom_page(), EventConstants.LABEL_OTHER);
            }
            if (isShowLogin) {
                HalfLoginManager.getInstancce().showLoginDialog(mPageStackManager.getContext(), new RegisterAndLoginModel(LoginEventHelper.convertTypeToPageSource(tye)));
//                RegisterActivity.Companion.show(mPageStackManager.getContext(),0, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.OTHER));}
            }
        }
    };
}
