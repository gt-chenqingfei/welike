package com.redefine.welike.business.search.ui.contract;

import android.os.Bundle;
import android.view.View;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.foundation.mvp.IBaseFragmentPagePresenter;
import com.redefine.foundation.mvp.IBaseFragmentPageView;
import com.redefine.welike.commonui.event.expose.ItemExposeManager;
import com.redefine.welike.business.search.ui.adapter.SearchLatestAdapter;
import com.redefine.welike.business.search.ui.presenter.SearchLatestPresenter;
import com.redefine.welike.business.search.ui.view.SearchLatestView;

/**
 * Created by liwenbo on 2018/3/5.
 */

public interface ISearchLatestContract {
    interface ISearchLatestPresenter extends IBaseFragmentPagePresenter, ILoadMoreDelegate {

        void onRefresh(String searchText);

        void setBasePresenter(ISearchPageContract.ISearchResultPagePresenter presenter);

        void retryRefresh();

        void onActivityResume();

        void onActivityPause();

        void onPageStateChanged(int oldState, int newState);
    }

    interface ISearchLatestView extends IBaseFragmentPageView {

        void setAdapter(SearchLatestAdapter mAdapter);

        void setPresenter(ISearchLatestPresenter searchMixPostPresenter);

        void showErrorView();

        void showLoading();

        void showEmptyView();

        void showContentView();

        void autoPlayVideo();

        View getView();

        void onActivityResume();

        void onActivityPause();

        void onPageStateChanged(int oldState, int newState);

        void setRecyclerViewManager(ItemExposeManager manager);

    }

    class SearchLatestFactory {
        public static ISearchLatestPresenter createPresenter(IPageStackManager pageStackManager, Bundle saveState) {
            return new SearchLatestPresenter(pageStackManager, saveState);
        }

        public static ISearchLatestView createView() {
            return new SearchLatestView();
        }
    }
}
