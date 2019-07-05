package com.redefine.welike.business.topic.ui.contract;

import android.app.Activity;
import android.os.Bundle;

import com.redefine.foundation.mvp.IBasePagePresenter;
import com.redefine.foundation.mvp.IBasePageView;
import com.redefine.welike.business.topic.management.bean.TopicUser;
import com.redefine.welike.business.topic.ui.adapter.TopicUserAdapter;
import com.redefine.welike.business.topic.ui.presenter.TopicUserPresenter;
import com.redefine.welike.business.topic.ui.view.TopicUserView;

/**
 * Created by liwenbo on 2018/3/21.
 */

public interface ITopicUserContract {

    interface ITopicUserPresenter extends IBasePagePresenter {

        boolean canLoadMore();

        void onLoadMore();

        void onRefresh();
    }

    interface ITopicUserView extends IBasePageView {

        void setPresenter(ITopicUserPresenter presenter);

        void setAdapter(TopicUserAdapter mAdapter);

        void showLoading();

        void showContent();

        void showEmptyView();

        void showErrorView();

        void setData(TopicUser user);
    }

    class TopicUserFactory {
        public static ITopicUserPresenter createPresenter(Activity  activity, Bundle pageBundle) {
            return new TopicUserPresenter(activity, pageBundle);
        }

        public static ITopicUserView createView() {
            return new TopicUserView();
        }
    }
}
