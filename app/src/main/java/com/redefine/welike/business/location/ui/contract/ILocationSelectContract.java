package com.redefine.welike.business.location.ui.contract;

import android.location.Location;

import com.redefine.foundation.mvp.IBaseActivityPresenter;
import com.redefine.foundation.mvp.IBaseActivityView;
import com.redefine.welike.business.location.management.bean.PoiInfo;

import java.util.List;

/**
 * Created by liwenbo on 2018/3/20.
 */

public interface ILocationSelectContract {

    interface ILocationSelectPresenter extends IBaseActivityPresenter {

//        void onBackPressed();

        void onTextChange(String s);

//        boolean canLoadMore();

        void onLoadMore();

        void onClickError();

        void doLocation(Location location);

//        void showEmptyView();
    }

    interface ILocationSelectView extends IBaseActivityView {

        void showLoading();

        void showEmptyView();

        void showContent();

        void showErrorView();

        void stopLoadmore();

        void loadError();

        void showNew(List<PoiInfo> data);

        void showLoadMore(List<PoiInfo> data);

        void showNoMore();

        void finishLoadMore();
    }

}
