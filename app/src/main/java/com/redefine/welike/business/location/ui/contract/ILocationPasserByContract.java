package com.redefine.welike.business.location.ui.contract;

import android.app.Activity;
import android.os.Bundle;

import com.redefine.foundation.mvp.IBasePagePresenter;
import com.redefine.foundation.mvp.IBasePageView;
import com.redefine.welike.business.location.ui.adapter.LocationPasserByAdapter;
import com.redefine.welike.business.location.ui.presenter.LocationPasserByPresenter;
import com.redefine.welike.business.location.ui.view.LocationPasserByView;

/**
 * Created by liwenbo on 2018/3/21.
 */

public interface ILocationPasserByContract {

    interface ILocationPasserByPresenter extends IBasePagePresenter {

        boolean canLoadMore();

        void onLoadMore();

        void onRefresh();
    }

    interface ILocationPasserByView extends IBasePageView {

        void setPresenter(ILocationPasserByPresenter locationPasserByPresenter);

        void setAdapter(LocationPasserByAdapter mAdapter);

        void showLoading();

        void showContent();

        void showEmptyView();

        void showErrorView();
    }

    class LocationPasserByFactory {
        public static ILocationPasserByPresenter createPresenter(Activity pageStackManager, Bundle pageBundle) {
            return new LocationPasserByPresenter(pageStackManager, pageBundle);
        }

        public static ILocationPasserByView createView() {
            return new LocationPasserByView();
        }
    }
}
