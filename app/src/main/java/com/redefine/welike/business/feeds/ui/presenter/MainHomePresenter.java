package com.redefine.welike.business.feeds.ui.presenter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.loadmore.adapter.OnClickRetryListener;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.SpManager;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.MessageIdConstant;
import com.redefine.welike.business.assignment.management.BannerManagement;
import com.redefine.welike.business.assignment.management.bean.Banner;
import com.redefine.welike.business.common.mission.MissionManager;
import com.redefine.welike.business.common.mission.MissionTask;
import com.redefine.welike.business.common.mission.MissionType;
import com.redefine.welike.business.common.mission.OnPostButtonClickListener;
import com.redefine.welike.business.feeds.management.FeedsStatusObserver;
import com.redefine.welike.business.feeds.management.RecommendUserManager;
import com.redefine.welike.business.feeds.management.SinglePostsManager;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.provider.HomePostsProvider2;
import com.redefine.welike.business.feeds.ui.adapter.HomeFeedRecyclerViewAdapter;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.business.feeds.ui.contract.IMainHomeContract;
import com.redefine.welike.business.feeds.ui.listener.IMenuBtnClickListener;
import com.redefine.welike.business.feeds.ui.listener.OnRequestPermissionCallback;
import com.redefine.welike.business.feeds.ui.util.FeedHelper;
import com.redefine.welike.business.user.management.UserModelFactory;
import com.redefine.welike.business.user.management.bean.RecommendSlideUserList;
import com.redefine.welike.commonui.event.expose.ItemExposeManager;
import com.redefine.welike.business.user.management.bean.FollowPost;
import com.redefine.welike.business.user.management.bean.RecommendSlideUser;
import com.redefine.welike.business.user.management.bean.RecommendUser;
import com.redefine.welike.common.ScoreManager;
import com.redefine.welike.common.abtest.ABKeys;
import com.redefine.welike.common.abtest.ABTest;
import com.redefine.welike.commonui.event.expose.impl.ItemExposeCallbackFactory;
import com.redefine.welike.hive.CommonListener;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.manager.GPScoreEventManager;
import com.redefine.welike.statistical.manager.InterestAndRecommendCardEventManager;
import com.redefine.welike.statistical.manager.PostEventManager;
import com.redefine.welike.statistical.manager.ShareEventManager;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 18/1/8
 * <p>
 * Description :
 * <p>
 * Creation    : 18/1/8
 * Author      : liwenbo0328@163.com
 * History     : Creation, 18/1/8, liwenbo, Create the file
 * ****************************************************************************
 */

public class MainHomePresenter implements IMainHomeContract.IMainHomePresenter,
        SinglePostsManager.PostsCallback, IMenuBtnClickListener,
        OnClickRetryListener, BannerManagement.BannerManagementCallback,
        FeedsStatusObserver.FeedsStatusObserverCallback,
        CommonListener<MissionTask>, OnPostButtonClickListener {

    private final IMainHomeContract.IMainHomeView mView;
    private final SinglePostsManager mPostsModel;
    private RecommendUserManager recommendUserManager;
    private final HomeFeedRecyclerViewAdapter mAdapter;
    private boolean isFirstCreate = true;
    private final BannerManagement mBannerManager;
    private OnRequestPermissionCallback mPermissionCallback;

    private ItemExposeManager mPostViewTimeManager = new ItemExposeManager(ItemExposeCallbackFactory.Companion.createPostExposeCallback(),
            ItemExposeCallbackFactory.Companion.createPostViewCallback(),
            ItemExposeCallbackFactory.Companion.createPostFollowBtnCallback());
    private int refreshHisTimes = 0;

    private boolean mShown = true;
    private boolean isAttach = true;

    public MainHomePresenter(IPageStackManager pageStackManager) {
        mView = IMainHomeContract.IMainHomeFactory.createView();
        mPostsModel = new SinglePostsManager();
        recommendUserManager = new RecommendUserManager();
        mBannerManager = new BannerManagement();
        mPostsModel.setDataSourceProvider(new HomePostsProvider2());
        mView.setPresenter(this);
        mAdapter = new HomeFeedRecyclerViewAdapter(pageStackManager);
        mAdapter.setFirstTopMargin(0);
        mAdapter.setFeedMenuBtnClickCallback(this);
        mAdapter.setRetryLoadMoreListener(this);
        mAdapter.setOnPostButtonClickListener(this);
    }

    @Override
    public void onCreate(Bundle arguments, Bundle savedInstanceState) {
        mPostsModel.setListener(this);
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = mView.createView(inflater, container, savedInstanceState);
        mView.setAdapter(mAdapter);
        mView.setRecyclerViewManager(mPostViewTimeManager);
        if (isFirstCreate) {
            isFirstCreate = false;
            onRefresh();
            mView.showLoading();
            PostEventManager.INSTANCE.setAction(EventConstants.AUTO_REFRESH);
        }
        FeedsStatusObserver.getInstance().register(this);
        mBannerManager.loadHomeBanner(this);
        return view;
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onRefreshPosts(SinglePostsManager manager, List<PostBase> posts, int newCount, boolean last, int errCode) {
        if (mPostsModel == manager) {
            boolean isSuccess = errCode == ErrorCode.ERROR_SUCCESS;
            if (isSuccess) {
                if (null != posts && !posts.isEmpty() && null != posts.get(0)) {
                    SpManager.LoopPostIdSp.setLooperPostId(posts.get(0).getPid(), MyApplication.getAppContext());
                }
                mAdapter.addNewData(posts);
                mPostViewTimeManager.setData(posts, mAdapter.hasHeader());
                //todo

                mAdapter.clearFinishFlag();
                int size = CollectionUtil.getCount(posts);
                // 提醒用户刷新成功条数
                mView.setRefreshCount(newCount);
                mView.autoPlayVideo();

                if (size == 0 && mAdapter.getRealItemCount() == 0) {
                    // 空页面
//                    EventBus.getDefault().post(new Event(EventIdConstant.CLEAR_STACK_LAUNCH_MAIN_HOME));
                    mView.showEmptyView();
                } else {
                    if (mShown) {
                        ScoreManager.setRefreshCount(MyApplication.getAppContext());
                        if (ScoreManager.canShowScoreView(MyApplication.getAppContext())) {
                            GPScoreEventManager.INSTANCE.setAction_type(EventConstants.GP_ACTION_TYPE_REFRESH);
                            mAdapter.addScoreData(0);
                        }
                    }
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
                if (mAdapter.getRealItemCount() == 0) {
                    // 空页面
                    mView.showErrorView();
                } else {
                    mView.showContentView();
                }
                mView.setRefreshCount(-1);
            }
            mView.finishRefresh(isSuccess);
            PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_HOME);
            PostEventManager.INSTANCE.setType(mAdapter.getFeedSource());
            PostEventManager.INSTANCE.sendEventStrategy(FeedHelper.subPosts(posts, newCount));
            mPostViewTimeManager.onDataLoaded();
        }

        recommendUserManager.tryRefresh(MyApplication.getAppContext(), new RecommendUserManager.OnRecommendUserListener() {
            @Override
            public void onSuccess(final RecommendSlideUserList ls) {
                if (ls == null || CollectionUtil.isEmpty(ls.getList())) return;
                final List<RecommendSlideUser> users = ls.getList();
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        if (!EasyPermissions.hasPermissions(mView.getContext(), Manifest.permission.READ_CONTACTS)) {
                            users.add(0, UserModelFactory.Companion.createRecommendContactUser(mView.getContext()));
                        }

                        FollowPost followPost = new FollowPost();
                        followPost.setList(users);
                        mAdapter.setPermissionCallback(mPermissionCallback);
                        mAdapter.addFollowData(followPost);
                        InterestAndRecommendCardEventManager.INSTANCE.report6();
                    }
                });
            }

            @Override
            public void onFail(int errCode) {

            }
        });
        mAdapter.showFooter();
    }

    private void sendMsgToMainPage() {
        Message message;
        message = Message.obtain();
        message.what = MessageIdConstant.MESSAGE_SYNC_NEW_HOME_FEED;
        EventBus.getDefault().post(message);

    }

    private void sendMsgToMainPageShowRefresh() {
        Message message;
        message = Message.obtain();
        message.what = MessageIdConstant.MESSAGE_SHOW_HOME_REFRESH;
        EventBus.getDefault().post(message);

    }

    private void sendMsgToMainPageHideRefresh() {
        Message message;
        message = Message.obtain();
        message.what = MessageIdConstant.MESSAGE_HIDE_HOME_REFRESH;
        EventBus.getDefault().post(message);

    }

    @Override
    public void onReceiveHisPosts(SinglePostsManager manager, List<PostBase> posts, boolean last, int errCode) {
        if (mPostsModel == manager) {
            if (errCode == ErrorCode.ERROR_SUCCESS) {
                mAdapter.addHisData(posts);
                mPostViewTimeManager.updateData(posts);
                if (mShown) {
                    ScoreManager.setRefreshCount(MyApplication.getAppContext());

                    if (ScoreManager.canShowScoreView(MyApplication.getAppContext())) {
                        GPScoreEventManager.INSTANCE.setAction_type(EventConstants.GP_ACTION_TYPE_REFRESH);
                        mAdapter.addScoreData(0);
                    }
                }

                if (last) {
                    mAdapter.noMore();
                } else {
                    refreshHisTimes++;
//                    if (refreshHisTimes > 0) {
//                        sendMsgToMainPageShowRefresh();
//                    }
                    mAdapter.finishLoadMore();
                }
            } else {
                mAdapter.loadError();
            }
            PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_HOME);
            PostEventManager.INSTANCE.setType(mAdapter.getFeedSource());
            PostEventManager.INSTANCE.sendEventStrategy(posts);
        }
        mView.setRefreshEnable(true);
    }

    @Override
    public void onRefresh() {
        refreshHisTimes = 0;
        sendMsgToMainPageHideRefresh();
        if (mAdapter.getRealItemCount() == 0) {
            mView.showLoading();
        }
        sendMsgToMainPage();
        mAdapter.hideFooter();
        mPostsModel.tryRefreshPosts();
        MissionManager.INSTANCE.loadMission(this);
    }

    @Override
    public void attach() {
        isAttach = true;
        if (refreshHisTimes > 0) {
            sendMsgToMainPageShowRefresh();
        }
        PostEventManager.INSTANCE.reset();
        MissionManager.INSTANCE.loadMission(this);
    }

    @Override
    public void detach() {
        isAttach = false;
    }

    @Override
    public void destroy() {
        FeedsStatusObserver.getInstance().unregister(this);
        mAdapter.destroy();
        mPostsModel.setListener(null);
        mView.destroy();
    }

    @Override
    public void toDiscover() {
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_DISCOVER_PAGE, null));
    }

    @Override
    public void autoRefresh() {
        if (mAdapter.getRealItemCount() == 0) {
            mView.showLoading();
        } else {
            mView.scrollToTop();

        }
        mAdapter.hideFooter();
//        mPostsModel.tryRefreshPosts();
        mView.autoRefresh();
    }

    @Override
    public void hide() {
        mView.hideGuide(false);
    }

    @Override
    public void show() {
        mAdapter.onActivityResume();
        MissionManager.INSTANCE.loadMission(this);
    }

    @Override
    public void hideRefresh() {
        refreshHisTimes = 0;
        sendMsgToMainPageHideRefresh();
    }

    @Override
    public void showRefresh() {
        sendMsgToMainPageShowRefresh();
    }

    @Override
    public void setRequestPermissionCallback(OnRequestPermissionCallback callback) {
        mPermissionCallback = callback;
    }

    @Override
    public boolean canLoadMore() {
        return mAdapter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        mView.setRefreshEnable(false);
        mAdapter.onLoadMore();
        mPostsModel.tryHisPosts();
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
    public void onMenuBtnClick(Context context, PostBase postBase) {
        if (mAdapter.getRealItemCount() == 0) {
            // 空页面
            mView.showEmptyView();
        }
    }

    @Override
    public void onBannerManagementEnd(List<Banner> bannerList, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            mAdapter.updateBanner(bannerList);
            EventLog1.Banner.report1(EventLog1.Banner.PageName.HOME);
        }
    }

    @Override
    public void onActivityResume() {
        mAdapter.onActivityResume();
        MissionManager.INSTANCE.loadMission(this);
        mView.onActivityResume();
        mPostViewTimeManager.onResume();
    }

    @Override
    public void onActivityPause() {
        mAdapter.onActivityPause();
        mView.onActivityPause();
        mPostViewTimeManager.onPause();
    }

    @Override
    public void onFragmentShow() {
        mShown = true;
        mView.onFragmentShow();
        mPostViewTimeManager.onShow();
    }

    @Override
    public void onFragmentHide() {
        mShown = false;
        mView.onFragmentHide();
        mPostViewTimeManager.onHide();
    }


    @Override
    public void onFinish(final MissionTask value) {
    }

    @Override
    public void onError(final MissionTask value) {
    }


    @Override
    public void onCommentClick(PostBase postBase) {
        EventLog.Feed.report6(1, postBase == null ? "" : postBase.getPid(), PostEventManager.getPostType(postBase),
                postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
    }

    @Override
    public void onLikeClick(PostBase postBase, int exp) {
        exp = Math.min(exp, 100);
        EventLog.Feed.report5(postBase == null ? "" : postBase.getPid(),
                postBase == null ? 0 : postBase.getSuperLikeExp(), exp, 1,
                PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());

        MissionManager.INSTANCE.notifyEvent(MissionType.LIKE, new CommonListener<MissionTask>() {
            @Override
            public void onFinish(final MissionTask value) {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }

            @Override
            public void onError(MissionTask value) {

            }
        });
    }

    @Override
    public void onForwardClick(PostBase postBase) {
        EventLog.Feed.report7(1, postBase == null ? "" : postBase.getPid(), PostEventManager.getPostType(postBase),
                postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
    }

    @Override
    public void onFollowClick(PostBase postBase) {

    }

    @Override
    public void onShareClick(PostBase postBase, int shareType) {
        ShareEventManager.INSTANCE.setShareType(shareType);
        ShareEventManager.INSTANCE.setFrompage(EventConstants.SHARE_FROM_PAGE_HOME);
        ShareEventManager.INSTANCE.report3();

        if (ScoreManager.canShowScoreView(MyApplication.getAppContext())) {
            GPScoreEventManager.INSTANCE.setAction_type(EventConstants.GP_ACTION_TYPE_REFRESH);
            mAdapter.addScoreData(postBase.getPosition());
        }

    }

    @Override
    public void onPostBodyClick(PostBase postBase) {
        ShareEventManager.INSTANCE.setChannel_page(EventConstants.SHARE_CHANNEL_PAGE_HOME);
    }

    @Override
    public void onPostAreaClick(PostBase postBase, int clickArea) {

        if (postBase == null) return;

        EventLog.Feed.report8(EventConstants.FEED_SOURCE_HOME,
                postBase.getUid(), PostEventManager.getPostRootUid(postBase),
                postBase.getPid(), PostEventManager.getPostRootPid(postBase), clickArea,
                PostEventManager.getPostType(postBase), postBase.getStrategy(), postBase.getSequenceId());
    }
}
