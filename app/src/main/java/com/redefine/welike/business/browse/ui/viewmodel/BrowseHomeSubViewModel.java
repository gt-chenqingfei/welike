package com.redefine.welike.business.browse.ui.viewmodel;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.redefine.commonui.enums.PageLoadMoreStatusEnum;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.browse.management.bean.Interest;
import com.redefine.welike.business.browse.management.bean.InterestPost;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.browse.management.dao.InterestCallBack;
import com.redefine.welike.business.browse.management.dao.InterestEventStore;
import com.redefine.welike.business.browse.management.request.FeedsRequest;
import com.redefine.welike.business.browse.management.request.Hot24hFeedRequest;
import com.redefine.welike.business.browse.management.request.Interest24hFeedRequest;
import com.redefine.welike.business.browse.management.request.InterestRequest2;
import com.redefine.welike.business.browse.management.request.RefreshPostCacheRequest;
import com.redefine.welike.business.browse.management.request.VideoFeedRequest;
import com.redefine.welike.business.feeds.management.DiscoverDataSourceCache;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.parser.PostsDataSourceParser;
import com.redefine.welike.business.user.management.bean.InterestRequestBean;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.manager.PostEventManager;
import com.redefine.welike.statistical.utils.GoogleUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by honglin on 2018/7/9.
 * <p>
 * viewmodel
 */
public class BrowseHomeSubViewModel extends AndroidViewModel {

    private String mCursor = null;
    private boolean isShowInterest = false;
    private volatile boolean isLoading = false;
    private String mFeedSource;

    private DiscoverDataSourceCache mDiscoverDataSourceCache = new DiscoverDataSourceCache();

    private MutableLiveData<List<PostBase>> mPosts = new MutableLiveData<>();

    private MutableLiveData<List<PostBase>> mMorePosts = new MutableLiveData<>();

    private MutableLiveData<PageStatusEnum> mPageStatus = new MutableLiveData<>();

    private MutableLiveData<PageLoadMoreStatusEnum> mLoadMoreStatus = new MutableLiveData<>();

    private MutableLiveData<Integer> mPageRefreshCount = new MutableLiveData<>();

    private MutableLiveData<InterestPost> interestPost = new MutableLiveData<>();

    private ArrayList<String> interestIds = new ArrayList<>();

    public BrowseHomeSubViewModel(@NonNull Application application) {
        super(application);
    }

    public void refresh(String interestId, String feedSource) {

        mFeedSource = feedSource;
        mCursor = null;
        if (interestId.equals(BrowseConstant.INTEREST_ALL)) {
            loadHotAndInterest();
        } else if (interestId.equals(BrowseConstant.INTEREST_VIDEO)) {
            loadVideoData();
//            loadHotAndInterest();

        } else if (interestId.equals(BrowseConstant.INTEREST_LATEST)) {
            loadLatestData();
        } else {
            loadInterestPostData(interestId);
        }
    }

    private void loadHotAndInterest() {
        InterestEventStore.INSTANCE.getInterestList(new InterestCallBack() {

            @Override
            public void onLoadEntity(@Nullable List<? extends Interest> interests) {
                if (interests == null || interests.isEmpty()) {
                    loadHotData();
                    if (!isShowInterest) {
                        getInterestlist("");
                    }
                } else {
                    interestIds.clear();
                    for (Interest interest : interests) {
                        interestIds.add(interest.getId());
                    }
                    loadHotData();
                }
            }
        });
    }

    public void onHisLoad(String interestId) {
        EventLog.UnLogin.report9(interestId);
        if (interestId.equals(BrowseConstant.INTEREST_ALL)) {
            loadMoreHotData();
        } else if (interestId.equals(BrowseConstant.INTEREST_VIDEO)) {
            loadMoreVideoData();
        } else if (interestId.equals(BrowseConstant.INTEREST_LATEST)) {
            loadMoreLatestData();
        } else {
            loadInterestMorePostData(interestId);
        }
    }

    private void loadVideoData() {
        if (isLoading) return;
        VideoFeedRequest feedsRequest = new VideoFeedRequest(MyApplication.getAppContext());
        try {
            isLoading = true;
//            if (CollectionUtil.isEmpty(mDiscoverDataSourceCache.getHotPostList())) {
            mPageStatus.postValue(PageStatusEnum.LOADING);
//            }
            feedsRequest.request(mCursor, interestIds, new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {
                    isLoading = false;
                    mPageRefreshCount.postValue(-1);
                    mPageStatus.postValue(PageStatusEnum.ERROR);
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) {
                    isLoading = false;
                    List<PostBase> posts = PostsDataSourceParser.parsePosts(result);
                    mCursor = PostsDataSourceParser.parseCursor(result);
                    if (mCursor == null) {
                        mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE);
                    } else mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NONE);
                    if (!CollectionUtil.isEmpty(posts)) {
//                        mDiscoverDataSourceCache.cacheOnePage(posts, BrowseConstant.INTEREST_VIDEO);
//                        int size = mDiscoverDataSourceCache.refreshNewCount(posts, mDiscoverDataSourceCache.getHotPostList());
//                        mDiscoverDataSourceCache.addHotNewData(posts);
//                        mDiscoverDataSourceCache.addNewInterestsPost(BrowseConstant.INTEREST_VIDEO, posts);
//                        mPageRefreshCount.postValue(size);
                        mPosts.postValue(posts);
                        mPageStatus.postValue(PageStatusEnum.CONTENT);
                    } else {
                        mPageStatus.postValue(PageStatusEnum.EMPTY);
                        mPageRefreshCount.postValue(0);
                    }
                    PostEventManager.INSTANCE.setType(mFeedSource);
                    PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_UNLOGIN_VIDEO);
                    PostEventManager.INSTANCE.sendEventStrategy(posts);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            isLoading = false;
            mPageRefreshCount.postValue(-1);
            mPageStatus.postValue(PageStatusEnum.ERROR);
        }

    }


    private void loadMoreVideoData() {
        if (isLoading) return;

        if (TextUtils.isEmpty(mCursor)) {
            mPageStatus.postValue(PageStatusEnum.CONTENT);
            mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE);
        } else {
            VideoFeedRequest feedsRequest = new VideoFeedRequest(MyApplication.getAppContext());
            try {
                isLoading = true;
                feedsRequest.request(mCursor, interestIds, new RequestCallback() {
                    @Override
                    public void onError(BaseRequest request, int errCode) {
                        isLoading = false;
                        mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.LOAD_ERROR);
                    }

                    @Override
                    public void onSuccess(BaseRequest request, JSONObject result) {
                        isLoading = false;
                        List<PostBase> temPosts = PostsDataSourceParser.parsePosts(result);
//                        List<PostBase> posts = mDiscoverDataSourceCache.filterPosts(temPosts, BrowseConstant.INTEREST_VIDEO);
//                        mCursor = PostsDataSourceParser.parseCursor(result);
//                        mDiscoverDataSourceCache.addHotHisData(posts);
//                        mDiscoverDataSourceCache.addHistoryInterestsPost(BrowseConstant.INTEREST_VIDEO, posts);
                        mMorePosts.postValue(temPosts);
                        mPageStatus.postValue(PageStatusEnum.CONTENT);

                        if (TextUtils.isEmpty(mCursor) || temPosts.isEmpty()) {
                            mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE);
                        } else {
                            mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.FINISH);
                        }
                        PostEventManager.INSTANCE.setType(mFeedSource);
                        PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_UNLOGIN_VIDEO);
                        PostEventManager.INSTANCE.sendEventStrategy(temPosts);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                isLoading = false;
                mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.LOAD_ERROR);
            }
        }

    }


    private void loadLatestData() {
        if (isLoading) return;
        FeedsRequest feedsRequest = new FeedsRequest(MyApplication.getAppContext());
        try {
            isLoading = true;
//            if (CollectionUtil.isEmpty(mDiscoverDataSourceCache.getHotPostList())) {
            mPageStatus.postValue(PageStatusEnum.LOADING);
//            }
            feedsRequest.tryFeeds(mCursor, interestIds, new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {
                    isLoading = false;
                    mPageRefreshCount.postValue(-1);
                    mPageStatus.postValue(PageStatusEnum.ERROR);
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) {
                    isLoading = false;
                    List<PostBase> posts = PostsDataSourceParser.parsePosts(result);
                    mCursor = PostsDataSourceParser.parseCursor(result);
                    if (mCursor == null) {
                        mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE);
                    } else mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NONE);
                    if (!CollectionUtil.isEmpty(posts)) {
//                        mDiscoverDataSourceCache.cacheOnePage(posts, BrowseConstant.INTEREST_LATEST);
//                        int size = mDiscoverDataSourceCache.refreshNewCount(posts, mDiscoverDataSourceCache.getHotPostList());
//                        mDiscoverDataSourceCache.addHotNewData(posts);
//                        mDiscoverDataSourceCache.addNewInterestsPost(BrowseConstant.INTEREST_LATEST, posts);
//                        mPageRefreshCount.postValue(size);
                        mPosts.postValue(posts);
                        mPageStatus.postValue(PageStatusEnum.CONTENT);
                    } else {
                        mPageStatus.postValue(PageStatusEnum.EMPTY);
                        mPageRefreshCount.postValue(0);
                    }
                    PostEventManager.INSTANCE.setType(mFeedSource);
                    PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_UNLOGIN_LATEST);
                    PostEventManager.INSTANCE.sendEventStrategy(posts);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            isLoading = false;
            mPageRefreshCount.postValue(-1);
            mPageStatus.postValue(PageStatusEnum.ERROR);
        }

    }


    private void loadMoreLatestData() {
        if (isLoading) return;

        if (TextUtils.isEmpty(mCursor)) {
            mPageStatus.postValue(PageStatusEnum.CONTENT);
            mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE);
        } else {
            FeedsRequest feedsRequest = new FeedsRequest(MyApplication.getAppContext());
            try {
                isLoading = true;
                feedsRequest.tryFeeds(mCursor, interestIds, new RequestCallback() {
                    @Override
                    public void onError(BaseRequest request, int errCode) {
                        isLoading = false;
                        mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.LOAD_ERROR);
                    }

                    @Override
                    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                        isLoading = false;
                        List<PostBase> temPosts = PostsDataSourceParser.parsePosts(result);
                        List<PostBase> posts = mDiscoverDataSourceCache.filterPosts(temPosts, BrowseConstant.INTEREST_LATEST);
                        mCursor = PostsDataSourceParser.parseCursor(result);
                        mDiscoverDataSourceCache.addHotHisData(posts);
                        mDiscoverDataSourceCache.addHistoryInterestsPost(BrowseConstant.INTEREST_LATEST, posts);
                        mPosts.postValue(mDiscoverDataSourceCache.getHotPostList());
                        mPageStatus.postValue(PageStatusEnum.CONTENT);

                        if (TextUtils.isEmpty(mCursor) || posts.isEmpty()) {
                            mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE);
                        } else {
                            mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.FINISH);
                        }
                        PostEventManager.INSTANCE.setType(mFeedSource);
                        PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_UNLOGIN_LATEST);
                        PostEventManager.INSTANCE.sendEventStrategy(temPosts);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                isLoading = false;
                mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.LOAD_ERROR);
            }
        }
    }

    private void loadHotData() {
        if (isLoading) return;
        Hot24hFeedRequest feedsRequest = new Hot24hFeedRequest(MyApplication.getAppContext());
        try {
            isLoading = true;
//            if (CollectionUtil.isEmpty(mDiscoverDataSourceCache.getHotPostList())) {
            mPageStatus.postValue(PageStatusEnum.LOADING);
//            }
            feedsRequest.request("", interestIds, new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {
                    isLoading = false;
                    mPageRefreshCount.postValue(-1);
                    mPageStatus.postValue(PageStatusEnum.ERROR);
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    isLoading = false;
                    List<PostBase> posts = PostsDataSourceParser.parsePosts(result);
                    mCursor = PostsDataSourceParser.parseCursor(result);
                    if (mCursor == null) {
                        mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE);
                    } else mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NONE);
                    if (!CollectionUtil.isEmpty(posts)) {
//                        mDiscoverDataSourceCache.cacheOnePage(posts, BrowseConstant.INTEREST_ALL);
//                        int size = mDiscoverDataSourceCache.refreshNewCount(posts, mDiscoverDataSourceCache.getHotPostList());
//                        mDiscoverDataSourceCache.addHotNewData(posts);
//                        mDiscoverDataSourceCache.addNewInterestsPost(BrowseConstant.INTEREST_ALL, posts);
//                        mPageRefreshCount.postValue(size);
                        mPosts.postValue(posts);
                        mPageStatus.postValue(PageStatusEnum.CONTENT);
                    } else {
                        mPageStatus.postValue(PageStatusEnum.EMPTY);
                        mPageRefreshCount.postValue(0);
                    }
                    PostEventManager.INSTANCE.setType(mFeedSource);
                    PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_UNLOGIN_FOR_YOU);
                    PostEventManager.INSTANCE.sendEventStrategy(posts);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            isLoading = false;
            mPageRefreshCount.postValue(-1);
            mPageStatus.postValue(PageStatusEnum.ERROR);
        }

    }

    private void loadMoreHotData() {
        if (isLoading) return;

        if (TextUtils.isEmpty(mCursor)) {
            mPageStatus.postValue(PageStatusEnum.CONTENT);
            mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE);
        } else {
            Hot24hFeedRequest feedsRequest = new Hot24hFeedRequest(MyApplication.getAppContext());
            try {
                isLoading = true;
                feedsRequest.request(mCursor, interestIds, new RequestCallback() {
                    @Override
                    public void onError(BaseRequest request, int errCode) {
                        isLoading = false;
                        mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.LOAD_ERROR);
                    }

                    @Override
                    public void onSuccess(BaseRequest request, JSONObject result) {
                        isLoading = false;
                        List<PostBase> temPosts = PostsDataSourceParser.parsePosts(result);
//                        List<PostBase> posts = mDiscoverDataSourceCache.filterPosts(temPosts, BrowseConstant.INTEREST_ALL);
//                        mCursor = PostsDataSourceParser.parseCursor(result);
//                        mDiscoverDataSourceCache.addHotHisData(posts);
//                        mDiscoverDataSourceCache.addHistoryInterestsPost(BrowseConstant.INTEREST_ALL, posts);

                        mMorePosts.postValue(temPosts);

                        mPageStatus.postValue(PageStatusEnum.CONTENT);

                        if (TextUtils.isEmpty(mCursor) || temPosts.isEmpty()) {
                            mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE);
                        } else {
                            mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.FINISH);
                        }
                        PostEventManager.INSTANCE.setType(mFeedSource);
                        PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_UNLOGIN_FOR_YOU);
                        PostEventManager.INSTANCE.sendEventStrategy(temPosts);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                isLoading = false;
                mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.LOAD_ERROR);
            }
        }

    }


    private void loadInterestPostData(final String id) {
        if (isLoading) return;
        Interest24hFeedRequest feedsRequest = new Interest24hFeedRequest(MyApplication.getAppContext());
        try {
            isLoading = true;
            mPageStatus.postValue(PageStatusEnum.LOADING);
//            }
            feedsRequest.request(mCursor, id, new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {
                    isLoading = false;
                    mPageRefreshCount.postValue(-1);
                    mPageStatus.postValue(PageStatusEnum.ERROR);
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    isLoading = false;
                    List<PostBase> posts = PostsDataSourceParser.parsePosts(result);
                    mCursor = PostsDataSourceParser.parseCursor(result);
                    if (mCursor == null) {
                        mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE);
                    } else mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NONE);
                    if (!CollectionUtil.isEmpty(posts)) {
                        mPosts.postValue(posts);
                        mPageStatus.postValue(PageStatusEnum.CONTENT);
                    } else {
                        mPageStatus.postValue(PageStatusEnum.EMPTY);
                        mPageRefreshCount.postValue(0);
                    }
                    PostEventManager.INSTANCE.setType(mFeedSource);
                    PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_UNLOGIN + id);
                    PostEventManager.INSTANCE.sendEventStrategy(posts);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            isLoading = false;
            mPageRefreshCount.postValue(-1);
            mPageStatus.postValue(PageStatusEnum.ERROR);
        }

    }


    private void loadInterestMorePostData(final String id) {
        if (isLoading) return;

        if (TextUtils.isEmpty(mCursor)) {
            mPageStatus.postValue(PageStatusEnum.CONTENT);
            mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE);
        } else {
            Interest24hFeedRequest feedsRequest = new Interest24hFeedRequest(MyApplication.getAppContext());
            try {
                isLoading = true;
                feedsRequest.request(mCursor, id, new RequestCallback() {
                    @Override
                    public void onError(BaseRequest request, int errCode) {
                        isLoading = false;
                        mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.LOAD_ERROR);
                    }

                    @Override
                    public void onSuccess(BaseRequest request, JSONObject result) {
                        isLoading = false;
                        List<PostBase> temPosts = PostsDataSourceParser.parsePosts(result);

                        mMorePosts.postValue(temPosts);

                        mPageStatus.postValue(PageStatusEnum.CONTENT);

                        if (TextUtils.isEmpty(mCursor) || temPosts.isEmpty()) {
                            mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE);
                        } else {
                            mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.FINISH);
                        }
                        PostEventManager.INSTANCE.setType(mFeedSource);
                        PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_UNLOGIN + id);
                        PostEventManager.INSTANCE.sendEventStrategy(temPosts);
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
                isLoading = false;
                mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.LOAD_ERROR);
            }
        }

    }


    public MutableLiveData<List<PostBase>> getHotPosts() {
        return mPosts;
    }

    public MutableLiveData<List<PostBase>> getVideoPosts() {
        return mPosts;
    }


    public MutableLiveData<List<PostBase>> getmMorePosts() {
        return mMorePosts;
    }

    public MutableLiveData<PageStatusEnum> getPageStatus() {
        return mPageStatus;
    }

    public MutableLiveData<PageLoadMoreStatusEnum> getLoadMoreStatus() {
        return mLoadMoreStatus;
    }

    public MutableLiveData<InterestPost> getInterestPost() {
        return interestPost;
    }

    private void getInterestlist(String referrerId) {

        try {
            new InterestRequest2(MyApplication.getAppContext(), referrerId).req(new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {

                    // TODO: 2018/7/20 nothing

                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {

                    Gson gson = new Gson();
                    InterestRequestBean bean = gson.fromJson(result.toJSONString(), InterestRequestBean.class);
                    // TODO: 2018/7/20
                    if (bean != null && bean.getList() != null && bean.getList().size() > 0) {
                        InterestPost post = new InterestPost();
                        post.setList(bean.getList());
                        interestPost.postValue(post);
                        isShowInterest = true;
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertInterest2DB(List<? extends Interest> list) {
        InterestEventStore.INSTANCE.insert(list);
        // TODO: 2018/7/20
        mPageStatus.postValue(PageStatusEnum.LOADING);
        for (Interest interest : list) {
            interestIds.add(interest.getId());
        }

        try {
            new RefreshPostCacheRequest(MyApplication.getAppContext(), GoogleUtil.INSTANCE.getGaid()).req();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mCursor = null;
        loadHotData();
    }

}
