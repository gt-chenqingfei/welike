package com.redefine.welike.business.location.ui.contract;

import com.redefine.foundation.mvp.IBasePagePresenter;
import com.redefine.welike.business.location.management.bean.LBSNearInfo;
import com.redefine.welike.business.location.management.bean.LBSUser;
import com.redefine.welike.business.location.ui.adapter.LocationMixAdapter;
import com.redefine.welike.commonui.event.expose.ItemExposeManager;

import java.util.List;

/**
 * Created by liwenbo on 2018/3/20.
 */

public interface ILocationMixContract {

    interface ILocationMixPresenter extends IBasePagePresenter {

        void onRefresh();

        void onLoadMore();

        boolean canLoadMore();

        void goPublish();

        void goPasserByPage();
        void changTab(int tab);

        void onActivityResume();

        void onActivityPause();
    }

    interface ILocationMixView {

        void setPresenter(ILocationMixPresenter locationMixPresenter);

        void setAdapter(LocationMixAdapter mAdapter);

        void showLoading();

        void showErrorView();

        void setRefreshCount(int size);

        void autoPlayVideo();

        void showEmptyView();

        void showContentView();

        void finishRefresh(boolean isSuccess);

        void setRefreshEnable(boolean b);

        void dismissNearBy();

        void showNearBy(LBSNearInfo nearInfo, List<LBSUser> userList);

        boolean canScroll();

        void showChangeTab();

        void hideChangeTab();

        void onActivityResume();

        void onActivityPause();

        void setRecyclerViewManager(ItemExposeManager manager);

    }

//    class LocationPageFactory {
//        public static ILocationMixPresenter createPresenter(IPageStackManager pageStackManager, Bundle pageBundle) {
//            return new LocationMixPresenter(pageStackManager, pageBundle);
//        }
//
//        public static ILocationMixView createView() {
//            return new LocationMixView();
//        }
//    }
}
