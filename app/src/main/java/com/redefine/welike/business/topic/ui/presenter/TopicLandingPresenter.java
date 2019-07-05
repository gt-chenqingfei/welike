package com.redefine.welike.business.topic.ui.presenter;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.frameworkmvp.presenter.MvpTitlePagePresenter1;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean;
import com.redefine.welike.business.topic.management.bean.TopicInfo;
import com.redefine.welike.business.topic.management.manager.TopicInfoManager;
import com.redefine.welike.business.topic.ui.constant.TopicConstant;
import com.redefine.welike.business.topic.ui.contract.ITopicLandingContract;
import com.redefine.welike.business.topic.ui.page.TopicLandingActivity;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.statistical.EventLog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


/**
 * Created by liwenbo on 2018/3/20.
 */

public class TopicLandingPresenter extends MvpTitlePagePresenter1<ITopicLandingContract.ITopicLandingView> implements ITopicLandingContract.ITopicLandingPresenter
        , TopicInfoManager.TopicInfoGetterListener {

    private final TopicInfoManager mTopicInfoManager;
    private TopicSearchSugBean.TopicBean mTopic;
    private boolean isBrowse = false;

    private int mCurrentSelectTab = TopicLandingActivity.TAB_HOT;

    public TopicLandingPresenter(Activity activity, Bundle pageConfig) {
        super(activity, pageConfig);
        mTopicInfoManager = new TopicInfoManager();
        mTopicInfoManager.setListener(this);
    }

    @Override
    protected ITopicLandingContract.ITopicLandingView createPageView() {
        return ITopicLandingContract.TopicLandingFactory.createView(mActivity, mPageBundle);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        parseBundle(mPageBundle, savedInstanceState);
        parseRouteBundle(mPageBundle, savedInstanceState);

        isBrowse = !AccountManager.getInstance().isLogin();
        mView.setPresenter(this);
        if (isBrowse) {
            if (mTopic != null) {
                EventLog.UnLogin.report13(mTopic.name);
            }
        }
        if (mTopic == null || TextUtils.isEmpty(mTopic.id)) {
            mView.showEmptyView();
        } else {
            mView.showLoading();
            mTopicInfoManager.refresh(mTopic.id, isBrowse);
        }

        mView.initViewPager(mTopic.id);

    }

    private void parseBundle(Bundle mPageBundle, Bundle savedInstanceState) {
        mTopic = (TopicSearchSugBean.TopicBean) mPageBundle.getSerializable(TopicConstant.BUNDLE_KEY_TOPIC);
        if (mTopic == null && savedInstanceState != null) {
            mTopic = (TopicSearchSugBean.TopicBean) savedInstanceState.getSerializable(TopicConstant.BUNDLE_KEY_TOPIC);
        }

        isBrowse = !AccountManager.getInstance().isLogin();
    }

    private void parseRouteBundle(Bundle mPageBundle, Bundle savedInstanceState) {
        if (mTopic != null) {
            return;
        }
        String topicId = mPageBundle.getString(TopicConstant.BUNDLE_KEY_TOPIC_ID);
        String topicName = mPageBundle.getString(TopicConstant.BUNDLE_KEY_TOPIC_NAME);
        if ((TextUtils.isEmpty(topicId) || TextUtils.isEmpty(topicName)) && savedInstanceState != null) {
            topicId = savedInstanceState.getString(TopicConstant.BUNDLE_KEY_TOPIC_ID);
            topicName = savedInstanceState.getString(TopicConstant.BUNDLE_KEY_TOPIC_NAME);
        }
        if (!TextUtils.isEmpty(topicId) && !TextUtils.isEmpty(topicName)) {
            mTopic = new TopicSearchSugBean.TopicBean();
            mTopic.id = topicId;
            mTopic.name = topicName;
        }
    }


    @Override
    public void goPublish() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TopicConstant.BUNDLE_KEY_TOPIC, mTopic);
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_PUBLISH_PAGE, bundle));
    }

    @Override
    public void goPasserByPage() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TopicConstant.BUNDLE_KEY_TOPIC, mTopic);
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_TOPIC_USER_PAGE, bundle));
    }

    @Override
    public void changTab(int tab) {
        mCurrentSelectTab = tab;
        if (tab == TopicLandingActivity.TAB_HOT) {
            onHotTabSelect();
        } else if (tab == TopicLandingActivity.TAB_LATEST) {
            onLatestTabSelect();
        }

    }

    @Override
    public void onActivityResume() {
//        mView.onActivityResume();
    }

    @Override
    public void onActivityPause() {
//        mView.onActivityPause();
    }

    @Override
    public void destroy() {
        super.destroy();

    }

    private void onLatestTabSelect() {
//        List<PostBase> latestPostList = mDataSourceCache.getLatestPostList();
//        mAdapter.addNewData(latestPostList);
//        if (CollectionUtil.isEmpty(latestPostList)) {
//            mView.showLoading();
//            mPostModel.tryRefreshPosts();
//        } else {
//            mView.showContentView();
//            mAdapter.addNewData(latestPostList);
//            mView.showContentView();
//        }
//        mAdapter.clearFinishFlag();
    }

    private void onHotTabSelect() {
//        List<PostBase> hotPostList = mDataSourceCache.getHotPostList();
//        mAdapter.addNewData(hotPostList);
//        if (CollectionUtil.isEmpty(hotPostList)) {
//            mView.showLoading();
//            mHotPostModel.tryRefreshPosts();
//        } else {
//            mAdapter.addNewData(hotPostList);
//            mView.showContentView();
//        }
//        mAdapter.clearFinishFlag();
    }

    @Override
    public void onLoadMore() {
        mView.setRefreshEnable(false);
//        mAdapter.onLoadMore();
//        hisPost();
    }

    @Override
    public void onRefresh() {

    }


    @Override
    public void onTopicInfoGetterCompleted(TopicInfo topicInfo, List<User> userList) {
        if (topicInfo == null && CollectionUtil.isEmpty(userList)) {
            mView.dismissNearBy();
            return;
        }
        mView.shoTopicInfo(topicInfo, userList);
    }

    @Override
    public void onTopicInfoGetterFailed(int errCode) {
        if (errCode == ErrorCode.ERROR_NETWORK_OBJECT_NOT_FOUND) {
            // topic不合法
            mView.dismissNearBy();
            mView.showOfflineView();
        } else {
            mView.dismissNearBy();
        }
    }


}
