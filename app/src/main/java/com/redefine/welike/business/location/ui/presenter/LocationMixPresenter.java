package com.redefine.welike.business.location.ui.presenter;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.redefine.commonui.loadmore.adapter.OnClickRetryListener;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.business.common.mission.OnPostButtonClickListener;
import com.redefine.welike.business.feeds.management.SinglePostsManager;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.util.FeedHelper;
import com.redefine.welike.business.location.management.LBSNearInfoGetter;
import com.redefine.welike.business.location.management.LocationDataSourceCache;
import com.redefine.welike.business.location.management.bean.LBSNearInfo;
import com.redefine.welike.business.location.management.bean.LBSUser;
import com.redefine.welike.business.location.management.bean.Location;
import com.redefine.welike.business.location.management.provider.LBSHotPostsProvider;
import com.redefine.welike.business.location.management.provider.LBSPostsProvider;
import com.redefine.welike.business.location.ui.adapter.LocationMixAdapter;
import com.redefine.welike.business.location.ui.constant.LocationConstant;
import com.redefine.welike.business.location.ui.contract.ILocationMixContract;
import com.redefine.welike.business.location.ui.page.LocationMixPage;
import com.redefine.welike.commonui.event.expose.ItemExposeManager;
import com.redefine.welike.commonui.event.expose.impl.ItemExposeCallbackFactory;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.manager.PostEventManager;
import com.redefine.welike.statistical.manager.ShareEventManager;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by liwenbo on 2018/3/20.
 */

public class LocationMixPresenter implements ILocationMixContract.ILocationMixPresenter
        , LBSNearInfoGetter.LBSNearInfoGetterListener, SinglePostsManager.PostsCallback, OnClickRetryListener, OnPostButtonClickListener {

    private final LBSNearInfoGetter mNearByManager;
    private final SinglePostsManager mPostModel;
    private final SinglePostsManager mHotPostModel;
    private LocationMixAdapter mAdapter;

    private Location mLocation;
    private int mCurrentSelectTab = LocationMixPage.TAB_HOT;
    private final LocationDataSourceCache mDataSourceCache;
    ILocationMixContract.ILocationMixView mView;

    private ItemExposeManager mPostViewTimeManager = new ItemExposeManager(ItemExposeCallbackFactory.Companion.createPostExposeCallback(),
            ItemExposeCallbackFactory.Companion.createPostViewCallback(),
            ItemExposeCallbackFactory.Companion.createPostFollowBtnCallback());

    public LocationMixPresenter(ILocationMixContract.ILocationMixView view) {
        this.mView = view;
        mNearByManager = new LBSNearInfoGetter();
        mNearByManager.setListener(this);
        mPostModel = new SinglePostsManager();
        mHotPostModel = new SinglePostsManager();
        mDataSourceCache = new LocationDataSourceCache();
    }

    public void initView(Bundle data, Bundle savedInstanceState) {
        parseBundle(data, savedInstanceState);
        parseRouteBundle(data, savedInstanceState);
        mAdapter = new LocationMixAdapter();
        mView.setAdapter(mAdapter);
        mView.setRecyclerViewManager(mPostViewTimeManager);
        mAdapter.setRetryLoadMoreListener(this);
        mAdapter.setOnPostButtonClickListener(this);
        if (mLocation == null) {
            mView.showEmptyView();
        } else {
            mView.showLoading();
            mPostModel.setDataSourceProvider(new LBSPostsProvider(mLocation.getPlaceId()));
            mHotPostModel.setDataSourceProvider(new LBSHotPostsProvider(mLocation.getPlaceId()));
            mNearByManager.refresh(mLocation.getPlaceId());
            mPostModel.setListener(this);
            mHotPostModel.setListener(this);
            refreshPost();
            PostEventManager.INSTANCE.setAction(EventConstants.AUTO_REFRESH);
        }
    }

    private void refreshPost() {
        if (mCurrentSelectTab == LocationMixPage.TAB_HOT) {
            mHotPostModel.tryRefreshPosts();
        } else {
            mPostModel.tryRefreshPosts();
        }
    }

    private void parseRouteBundle(Bundle mPageBundle, Bundle savedInstanceState) {
        if (mLocation != null) {
            return;
        }
        try {
            String placeId = mPageBundle.getString(LocationConstant.BUNDLE_KEY_LOCATION_PLACE_ID);
            if (TextUtils.isEmpty(placeId)) {
                if (savedInstanceState != null) {
                    placeId = savedInstanceState.getString(LocationConstant.BUNDLE_KEY_LOCATION_PLACE_ID);
                }
            }
            if (!TextUtils.isEmpty(placeId)) {
                mLocation = new Location();

                mLocation.setPlaceId(placeId);
            }
        } catch (Exception e) {
            // do nothing
        }

    }

    private void parseBundle(Bundle mPageBundle, Bundle savedInstanceState) {
        mLocation = (Location) mPageBundle.getSerializable(LocationConstant.BUNDLE_KEY_LOCATION);
        if (mLocation == null && savedInstanceState != null) {
            mLocation = (Location) savedInstanceState.getSerializable(LocationConstant.BUNDLE_KEY_LOCATION);
        }
    }

    @Override
    public View createView(Context context, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void attach() {
        PostEventManager.INSTANCE.reset();
        PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_LOCATION_LATEST);
        PostEventManager.INSTANCE.setType(mAdapter.getFeedSource());
    }

    @Override
    public void detach() {

    }

    @Override
    public void destroy() {
        mPostViewTimeManager.onDestroy();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean canLoadMore() {
        return mAdapter.canLoadMore();
    }

    @Override
    public void goPublish() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(LocationConstant.BUNDLE_KEY_LOCATION, mLocation);
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_PUBLISH_PAGE, bundle));
    }

    @Override
    public void goPasserByPage() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(LocationConstant.BUNDLE_KEY_LOCATION, mLocation);
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_PASSER_BY_PAGE, bundle));
    }

    @Override
    public void changTab(int tab) {
        mCurrentSelectTab = tab;
        mAdapter.setFeedSource(tab == LocationMixPage.TAB_HOT ? EventConstants.FEED_PAGE_LOCATION_HOT : EventConstants.FEED_PAGE_LOCATION_LATEST);
        if (tab == LocationMixPage.TAB_HOT) {
            onHotTabSelect();
        } else if (tab == LocationMixPage.TAB_LATEST) {
            onLatestTabSelect();
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
    public void onLoadMore() {
        mView.setRefreshEnable(false);
        mAdapter.onLoadMore();
        hisPost();
    }

    @Override
    public void onRetryLoadMore() {
        if (canLoadMore()) {
            onLoadMore();
        }
    }

    private void hisPost() {
        if (mCurrentSelectTab == LocationMixPage.TAB_HOT) {
            mHotPostModel.tryHisPosts();
        } else {
            mPostModel.tryHisPosts();
        }
    }

    @Override
    public void onLBSNearInfoGetterCompleted(LBSNearInfo nearInfo, List<LBSUser> userList) {
        if (nearInfo == null && CollectionUtil.isEmpty(userList)) {
            mView.dismissNearBy();
            return;
        }
        mView.showNearBy(nearInfo, userList);
    }

    @Override
    public void onLBSNearInfoGetterFailed(int errCode) {
        mView.dismissNearBy();
    }

    @Override
    public void onRefresh() {
        if (mAdapter.getRealItemCount() == 0) {
            mView.showLoading();
        }
        mAdapter.hideFooter();
        mNearByManager.refresh(mLocation.getPlaceId());
        refreshPost();
    }

    @Override
    public void onRefreshPosts(SinglePostsManager manager, List<PostBase> posts, int newCount, boolean last, int errCode) {
        if (mPostModel == manager) {
            onRefreshLatestPosts(posts, newCount, last, errCode);
            PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_LOCATION_LATEST);
        } else if (mHotPostModel == manager) {
            onRefreshHotPosts(posts, newCount, last, errCode);
            PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_LOCATION_HOT);
        }
        mPostViewTimeManager.setData(posts, mAdapter.hasHeader());
        mPostViewTimeManager.onDataLoaded();
        mAdapter.showFooter();
        PostEventManager.INSTANCE.setType(mAdapter.getFeedSource());
        PostEventManager.INSTANCE.sendEventStrategy(FeedHelper.subPosts(posts, newCount));

    }


    private void onRefreshLatestPosts(List<PostBase> posts, int newCount, boolean last, int errCode) {
        boolean isSuccess = errCode == ErrorCode.ERROR_SUCCESS;
        if (isSuccess) {
            mDataSourceCache.addLatestNewData(posts);
            if (mCurrentSelectTab != LocationMixPage.TAB_LATEST) {
                return;
            }
            mAdapter.addNewData(mDataSourceCache.getLatestPostList());
            mAdapter.clearFinishFlag();
            int size = CollectionUtil.getCount(posts);
            // 提醒用户刷新成功条数
            mView.setRefreshCount(newCount);
            mView.autoPlayVideo();
            if (size == 0 && mAdapter.getRealItemCount() == 0) {
                // 空页面
                mView.showEmptyView();
            } else {
                mView.showContentView();
            }
            if (last) {
                if (mView.canScroll()) {
                    mAdapter.noMore();
                } else {
                    mAdapter.clearFinishFlag();
                }
            } else {
                mAdapter.clearFinishFlag();
            }
        } else {
            // 网络失败给用户提醒
            if (errCode == ErrorCode.ERROR_NETWORK_OBJECT_NOT_FOUND) {
                mView.showErrorView();
            } else {
                if (mAdapter.getRealItemCount() == 0) {
                    // 空页面
                    mView.showErrorView();
                } else {
                    mView.showContentView();
                }
                mView.setRefreshCount(-1);
            }
        }
        mView.finishRefresh(isSuccess);

    }

    private void tryLatestTopics() {
        mView.hideChangeTab();
        mCurrentSelectTab = LocationMixPage.TAB_LATEST;
        refreshPost();
    }

    private void onRefreshHotPosts(List<PostBase> posts, int newCount, boolean last, int errCode) {
        boolean isSuccess = errCode == ErrorCode.ERROR_SUCCESS;
        if (isSuccess) {
            mDataSourceCache.addHotNewData(posts);
            if (mCurrentSelectTab != LocationMixPage.TAB_HOT) {
                return;
            }
            mAdapter.addNewData(mDataSourceCache.getHotPostList());
            mAdapter.clearFinishFlag();
            int size = CollectionUtil.getCount(posts);
            // 提醒用户刷新成功条数
            mView.setRefreshCount(newCount);
            mView.autoPlayVideo();
            if (size == 0 && mAdapter.getRealItemCount() == 0) {
                tryLatestTopics();
            } else {

                mView.showChangeTab();
                mView.showContentView();
            }
            if (last) {
                if (mView.canScroll()) {
                    mAdapter.noMore();
                } else {
                    mAdapter.clearFinishFlag();
                }
            } else {
                mAdapter.clearFinishFlag();
            }
        } else {
            // 网络失败给用户提醒
            if (errCode == ErrorCode.ERROR_NETWORK_OBJECT_NOT_FOUND) {
                // mView.hideChangeTab();
                tryLatestTopics();
            } else {
                if (mAdapter.getRealItemCount() == 0) {
                    // 空页面
                    tryLatestTopics();
                } else {
                    mView.showChangeTab();
                    mView.showContentView();
                }
                mView.setRefreshCount(-1);
            }
        }
        mView.finishRefresh(isSuccess);
    }


    @Override
    public void onReceiveHisPosts(SinglePostsManager manager, List<PostBase> posts, boolean last, int errCode) {
        if (mPostModel == manager) {
            onReceiveLatestHisPosts(posts, last, errCode);
            PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_LOCATION_LATEST);
        } else if (mHotPostModel == manager) {
            onReceiveHotHisPosts(posts, last, errCode);
            PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_LOCATION_HOT);
        }
        mView.setRefreshEnable(true);
        PostEventManager.INSTANCE.setType(mAdapter.getFeedSource());
        PostEventManager.INSTANCE.sendEventStrategy(posts);
        mPostViewTimeManager.updateData(posts);
    }


    private void onReceiveLatestHisPosts(List<PostBase> posts, boolean last, int errCode) {
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

    private void onReceiveHotHisPosts(List<PostBase> posts, boolean last, int errCode) {
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

    private void onLatestTabSelect() {
        List<PostBase> latestPostList = mDataSourceCache.getLatestPostList();
        mAdapter.addNewData(latestPostList);
        if (CollectionUtil.isEmpty(latestPostList)) {
            mView.showLoading();
            mPostModel.tryRefreshPosts();
        } else {
            mView.showContentView();
            mAdapter.addNewData(latestPostList);
            mView.showContentView();
        }
        mAdapter.clearFinishFlag();
        mAdapter.setFeedSource(EventConstants.FEED_PAGE_LOCATION_LATEST);
    }

    private void onHotTabSelect() {
        List<PostBase> hotPostList = mDataSourceCache.getHotPostList();
        mAdapter.addNewData(hotPostList);
        if (CollectionUtil.isEmpty(hotPostList)) {
            mView.showLoading();
            mHotPostModel.tryRefreshPosts();
        } else {
            mAdapter.addNewData(hotPostList);
            mView.showContentView();
        }
        mAdapter.clearFinishFlag();
        mAdapter.setFeedSource(EventConstants.FEED_PAGE_LOCATION_HOT);
    }

    @Override
    public void onCommentClick(PostBase postBase) {
        if (mCurrentSelectTab == LocationMixPage.TAB_HOT) {
            EventLog.Feed.report7(6, postBase == null ? "" : postBase.getPid(), PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
        } else {
            EventLog.Feed.report7(7, postBase == null ? "" : postBase.getPid(), PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
        }
    }

    @Override
    public void onLikeClick(PostBase postBase, int exp) {
        exp = Math.min(exp, 100);
        if (mCurrentSelectTab == LocationMixPage.TAB_HOT) {
            EventLog.Feed.report5(postBase == null ? "" : postBase.getPid(),
                    postBase == null ? 0 : postBase.getSuperLikeExp(),
                    exp, 6,
                    PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
        } else {
            EventLog.Feed.report5(postBase == null ? "" : postBase.getPid(),
                    postBase == null ? 0 : postBase.getSuperLikeExp(),
                    exp, 7, PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
        }
    }

    @Override
    public void onForwardClick(PostBase postBase) {
        if (mCurrentSelectTab == LocationMixPage.TAB_HOT) {
            EventLog.Feed.report6(6, postBase == null ? "" : postBase.getPid(), PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
        } else {
            EventLog.Feed.report6(7, postBase == null ? "" : postBase.getPid(), PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
        }
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

        EventLog.Feed.report8(mCurrentSelectTab == LocationMixPage.TAB_HOT ? EventConstants.FEED_SOURCE_LOCATION_HOT : EventConstants.FEED_SOURCE_LOCATION_LATEST,
                postBase.getUid(), PostEventManager.getPostRootUid(postBase),
                postBase.getPid(), PostEventManager.getPostRootPid(postBase), clickArea,
                PostEventManager.getPostType(postBase), postBase.getStrategy(), postBase.getSequenceId());
    }

}
