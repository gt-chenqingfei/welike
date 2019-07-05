package com.redefine.welike.business.mysetting.ui.contract;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.foundation.mvp.IBasePagePresenter;
import com.redefine.foundation.mvp.IBasePageView;
import com.redefine.welike.commonui.event.expose.ItemExposeManager;
import com.redefine.welike.business.mysetting.ui.presenter.MyLikePresenter;
import com.redefine.welike.business.mysetting.ui.view.MyLikeView;

/**
 * Created by gongguan on 2018/2/23.
 */

public interface IMyLikeContract {
    interface IMyLikePresenter extends IBasePagePresenter, ILoadMoreDelegate {

        void onRefresh();

        void onActivityResume();

        void onActivityPause();

    }

    interface IMyLikeView extends IBasePageView {
        void setPresenter(IMyLikePresenter mPresenter);

        void setAdapter(RecyclerView.Adapter mAdapter);

        void finishRefresh();

        void autoRefresh();

        void setRefreshEnable(boolean b);

        void showLoading();

        void showContent();

        void showEmptyView();

        void showErrorView();

        void setRefreshCount(int size);

        void autoPlayVideo();

        void onActivityResume();

        void onActivityPause();

        void setRecyclerViewManager(ItemExposeManager manager);


    }

    class IMyLikeFactory {
        public static IMyLikePresenter createPresenter(Activity pageStackManager, Bundle pageBundle) {
            return new MyLikePresenter(pageStackManager, pageBundle);
        }

        public static IMyLikeView createView() {
            return new MyLikeView();
        }
    }
}
