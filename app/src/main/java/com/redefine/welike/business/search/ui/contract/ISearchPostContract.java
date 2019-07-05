package com.redefine.welike.business.search.ui.contract;

import android.os.Bundle;
import android.view.View;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.foundation.mvp.IBaseFragmentPagePresenter;
import com.redefine.foundation.mvp.IBaseFragmentPageView;
import com.redefine.welike.commonui.event.expose.ItemExposeManager;
import com.redefine.welike.business.search.ui.adapter.SearchPostAdapter;
import com.redefine.welike.business.search.ui.presenter.SearchPostPresenter;
import com.redefine.welike.business.search.ui.view.SearchPostView;

/**
 * Created by liwenbo on 2018/2/11.
 */

public interface ISearchPostContract {


    interface ISearchPostPresenter extends IBaseFragmentPagePresenter, ILoadMoreDelegate {

        void onRefresh(String searchText);

        void retryRefresh();

        void onActivityResume();

        void onActivityPause();

        void onPageStateChanged(int oldState, int newState);
    }

    interface ISearchPostView extends IBaseFragmentPageView {

        void setAdapter(SearchPostAdapter mAdapter);

        void setPresenter(ISearchPostPresenter searchMixPostPresenter);

        View getView();

        void showErrorView();

        void showLoading();

        void showEmptyView();

        void showContentView();

        void autoPlayVideo();

        void onActivityResume();

        void onActivityPause();

        void onPageStateChanged(int oldState, int newState);

        void setRecyclerViewManager(ItemExposeManager manager);

    }

    class SearchPostFactory {
        public static ISearchPostPresenter createPresenter(IPageStackManager pageStackManager, Bundle saveState) {
            return new SearchPostPresenter(pageStackManager, saveState);
        }

        public static ISearchPostView createView() {
            return new SearchPostView();
        }
    }
}
