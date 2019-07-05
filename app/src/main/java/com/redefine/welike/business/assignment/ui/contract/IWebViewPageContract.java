package com.redefine.welike.business.assignment.ui.contract;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;

import com.redefine.foundation.mvp.IBasePagePresenter;
import com.redefine.foundation.mvp.IBasePageView;
import com.redefine.welike.business.assignment.ui.presenter.WebViewPagePresenter;
import com.redefine.welike.business.assignment.ui.view.WebViewPageView;

import java.util.List;

/**
 * Created by liwenbo on 2018/3/16.
 */

public interface IWebViewPageContract {

    interface IWebViewPagePresenter extends IBasePagePresenter {

        void onNewMessage(Message message);

        void onActivityResume();

        void onActivityPause();

        void onTitleBackPressed();

        boolean interceptGoBackPressed();

        void onWebFileChosenResult(List<Uri> result);
    }

    interface IWebViewPageView extends IBasePageView {

        void setPresenter(IWebViewPagePresenter webViewPagePresenter);

        void setTitleColor(String titleColor);

        void showTitle(boolean showTitle);

        void showBack(boolean showBack);

        void showClose(boolean showClose);

        void showShare(boolean showShare);

        void loadUrl(String mUrl);

        boolean onGoBack();

        void onActivityResume();

        void onActivityPause();

        void notifyRefreshWebView();

        void setKeepScreenOn(boolean keepScreenOn);

        void onWebFileChosenResult(List<Uri> result);
    }

    class WebViewPageFactory {
        public static IWebViewPagePresenter createPresenter(Activity pageStackManager, Bundle bundle) {
            return new WebViewPagePresenter(pageStackManager, bundle);
        }

        public static IWebViewPageView createView(String from) {
            return new WebViewPageView(from);
        }

    }
}
