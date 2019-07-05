package com.redefine.welike.business.search.ui.contract;

import android.os.Bundle;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.foundation.mvp.IBaseFragmentPagePresenter;
import com.redefine.foundation.mvp.IBaseFragmentPageView;
import com.redefine.welike.business.search.ui.adapter.SearchUserAdapter;
import com.redefine.welike.business.search.ui.presenter.SearchUserPresenter;
import com.redefine.welike.business.search.ui.view.SearchUserView;

/**
 * Created by liwenbo on 2018/2/11.
 */

public interface ISearchUserContract {

    interface ISearchUserPresenter extends IBaseFragmentPagePresenter, ILoadMoreDelegate {

        void onRefresh(String searchText);

        void retryRefresh();
    }

    interface ISearchUserView extends IBaseFragmentPageView {

        void setPresenter(ISearchUserPresenter searchUserPresenter);

        void showErrorView();

        void setAdapter(SearchUserAdapter mAdapter);

        void showLoading();

        void showEmptyView();

        void showContentView();
    }

    class SearchUserFactory {
        public static ISearchUserPresenter createPresenter(IPageStackManager pageStackManager, Bundle saveState) {
            return new SearchUserPresenter(pageStackManager, saveState);
        }

        public static ISearchUserView createView() {
            return new SearchUserView();
        }
    }
}
