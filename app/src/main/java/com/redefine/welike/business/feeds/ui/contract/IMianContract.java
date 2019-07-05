package com.redefine.welike.business.feeds.ui.contract;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import com.redefine.foundation.mvp.IBasePresenter;
import com.redefine.foundation.mvp.IBaseView;
import com.redefine.welike.business.common.GuideManager;
import com.redefine.welike.business.feeds.ui.presenter.MainPresenter;
import com.redefine.welike.business.feeds.ui.view.MainView;

/**
 * Created by liwb on 2018/1/7.
 */

public interface IMianContract {

    interface IMainPresenter extends IBasePresenter {

        void onCreate(Bundle arguments, Bundle savedInstanceState);

        void destroy();

        void onPageStateChanged(int oldPageState, int pageState);

        void onNewMessage(Message message);

        void onActivityPause();

        void onActivityResult(int requestCode, int resultCode, Intent data);

        void onActivityResume();

        void showPage();

        void onTabChange(boolean isDiscoverTab);
    }

    interface IMainView extends IBaseView {

        View getView();

        void updateRedPoint(boolean isShowRedPoint);

        void updateMyRedPoint(boolean show);

        void updateMyHomeTab(boolean showRefresh);

        void updateMyDistoryPoint(boolean show);

        void destroy();

        void onPageStateChanged(int oldPageState, int pageState);

        void switchToPageIndex(int fragmentDiscoverPosition);

        void switchToHomePageIndex(boolean reRefresh);

        void refreshUserTask(Message message);

        void onActivityPause();

        void onActivityResume();

        void onActivityResult(int requestCode, int resultCode, Intent data);

        void attach();
        void setPresenter(IMainPresenter presenter);

        void showGuide(@GuideManager.GuideType String guide);

        boolean isDiscoveryTab();
    }

    public static class MainPageFactory {
        public static IMainPresenter createPresenter(IMainView view) {
            return new MainPresenter(view);
        }

        public static IMainView createView(View view, Bundle mPageConfig, Bundle saveState, Activity activity) {
            return new MainView(view, mPageConfig, saveState,activity);
        }
    }
}
