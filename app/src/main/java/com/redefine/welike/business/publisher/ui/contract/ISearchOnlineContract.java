package com.redefine.welike.business.publisher.ui.contract;

import android.support.v7.widget.RecyclerView;

import com.redefine.foundation.mvp.IBaseActivityPresenter;
import com.redefine.foundation.mvp.IBaseActivityView;
import com.redefine.welike.business.publisher.ui.presenter.SearchOnlinePresenter;
import com.redefine.welike.business.publisher.ui.view.SearchOnlineView;

/**
 * Created by gongguan on 2018/2/27.
 */

public interface ISearchOnlineContract {
    interface ISearchOnlinePresenter extends IBaseActivityPresenter {
        void onRefresh();

        boolean canLoadMore();

        void loadMore();

        void doSearch(String key);
    }

    interface ISearchOnlineView extends IBaseActivityView {
        void setPresenter(ISearchOnlinePresenter mPresenter);

        void setAdapter(RecyclerView.Adapter mAdapter);

        void showErrorView();

        void showEmptyView();

        void showContent();

        void showLoading();
    }

    class ISearchOnlineFactory {
//        public static ISearchOnlinePresenter createPresenter(String searchStr) {
//            return new SearchOnlinePresenter(searchStr);
//        }

        public static ISearchOnlinePresenter createPresenter(IContactListContract.OnContactChoiceListener listener) {
            return new SearchOnlinePresenter(listener);
        }

        public static ISearchOnlineView createView() {
            return new SearchOnlineView();
        }
    }

}
