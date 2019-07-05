package com.redefine.welike.business.search.ui.contract;

import android.os.Bundle;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.foundation.mvp.IBasePagePresenter;
import com.redefine.foundation.mvp.IBasePageView;
import com.redefine.welike.business.search.ui.bean.SearchMovieBean;
import com.redefine.welike.business.search.ui.presenter.SearchResultPagePresenter;
import com.redefine.welike.business.search.ui.view.SearchResultPageView;

/**
 * Created by liwenbo on 2018/2/11.
 */

public interface ISearchPageContract {

    interface ISearchResultPagePresenter extends IBasePagePresenter {

        void onPageScrollStart();

        void switchToPeoplePage();

        void onPageStateChanged(int oldPageState, int pageState);

        void goSugPage(String mSearchQuery);

        void getMovie(String mSearchQuery);

        void onActivityResume();

        void onActivityPause();

    }

    interface ISearchResultPageView extends IBasePageView {

        void hideInputMethod();

        void switchToPeoplePage();
        void initMovieCard(SearchMovieBean bean);

        void onActivityResume();

        void onActivityPause();
    }

    class SearchResultPageFactory {
        public static ISearchResultPagePresenter createPresenter(IPageStackManager pageStackManager, Bundle pageBundle) {
            return new SearchResultPagePresenter(pageStackManager, pageBundle);
        }

        public static ISearchResultPageView createView(IPageStackManager pageStackManager, ISearchResultPagePresenter presenter, Bundle pageBundle) {
            return new SearchResultPageView(pageStackManager, presenter, pageBundle);
        }
    }
}
