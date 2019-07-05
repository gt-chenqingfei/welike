package com.redefine.welike.business.search.ui.contract;

import android.os.Bundle;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.foundation.mvp.IBasePagePresenter;
import com.redefine.foundation.mvp.IBasePageView;
import com.redefine.welike.business.search.ui.adapter.SearchSugAdapter;
import com.redefine.welike.business.search.ui.presenter.SearchSugPresenter;
import com.redefine.welike.business.search.ui.view.SearchSugView;

/**
 * Created by liwenbo on 2018/3/13.
 */

public interface ISearchSugContract {

    interface ISearchSugPresenter extends IBasePagePresenter {

        void onSearch(String trim);

        void goSearchResultPage(String trim);

        void hideInputMethod();
    }

    interface ISearchSugView extends IBasePageView {

        void setAdapter(SearchSugAdapter mAdapter);

        void setPresenter(ISearchSugPresenter searchSugPresenter);

        void setText(String sug);

        void hideInputMethod();
    }

    class SearchSugFactory {
        public static ISearchSugPresenter createPresenter(IPageStackManager pageStackManager, Bundle bundle) {
            return new SearchSugPresenter(pageStackManager, bundle);
        }

        public static ISearchSugView createView() {
            return new SearchSugView();
        }
    }
}
