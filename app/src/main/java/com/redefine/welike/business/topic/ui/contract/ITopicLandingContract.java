package com.redefine.welike.business.topic.ui.contract;

import android.app.Activity;
import android.os.Bundle;

import com.redefine.foundation.mvp.IBasePagePresenter;
import com.redefine.foundation.mvp.IBasePageView;
import com.redefine.welike.business.topic.management.bean.TopicInfo;
import com.redefine.welike.business.topic.ui.presenter.TopicLandingPresenter;
import com.redefine.welike.business.topic.ui.view.TopicLandingView;
import com.redefine.welike.business.user.management.bean.User;

import java.util.List;

/**
 * Created by liwenbo on 2018/3/20.
 */

public interface ITopicLandingContract {

    interface ITopicLandingPresenter extends IBasePagePresenter {

        void onRefresh();

        void onLoadMore();

//        boolean canLoadMore();

        void goPublish();

        void goPasserByPage();

        void changTab(int tab);

        void onActivityResume();

        void onActivityPause();

    }

    interface ITopicLandingView extends IBasePageView {

        void setPresenter(ITopicLandingPresenter locationMixPresenter);

        void showLoading();

        void showErrorView();

        void setRefreshCount(int size);

        void showEmptyView();

        void showContentView();

        void finishRefresh(boolean isSuccess);

        void autoRefresh();

        void setRefreshEnable(boolean b);

        void dismissNearBy();

        void shoTopicInfo(TopicInfo nearInfo, List<User> userList);

        void showOfflineView();

        void showChangeTab();

        void hideChangeTab();

        void goPublish();

        void onFeedFollowChange();

        void onBrowseTopicClick(int tye,boolean isShowLogin,int showType);

        void initViewPager(String topicId);

    }

    class TopicLandingFactory {
        public static ITopicLandingPresenter createPresenter(Activity activity, Bundle pageBundle) {
            return new TopicLandingPresenter(activity, pageBundle);
        }

        public static ITopicLandingView createView(Activity activity,Bundle pageBundle) {
            return new TopicLandingView(activity,pageBundle);
        }
    }
}
