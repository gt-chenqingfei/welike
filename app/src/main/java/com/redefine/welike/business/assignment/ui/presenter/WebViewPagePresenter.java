package com.redefine.welike.business.assignment.ui.presenter;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;

import com.redefine.commonui.constant.WebViewConstant;
import com.redefine.frameworkmvp.presenter.MvpTitlePagePresenter1;
import com.redefine.welike.base.constant.MessageIdConstant;
import com.redefine.welike.business.assignment.ui.contract.IWebViewPageContract;

import java.util.List;

/**
 * Created by liwenbo on 2018/3/16.
 */

public class WebViewPagePresenter extends MvpTitlePagePresenter1<IWebViewPageContract.IWebViewPageView> implements IWebViewPageContract.IWebViewPagePresenter {

    private static final int MENU_SHOW_BACK = 1;
    private static final int MENU_SHOW_CLOSE = 2;
    private static final int MENU_SHOW_SHARE = 4;
    private static final int MENU_DEFAULT_VALUE = 3;

    private static final int HIDE_TITLE_VALUE = 0;
    private static final int SHOW_TITLE_VALUE = 1;

    private String mFrom;
    private String mUrl;
    private String mTitleColor;
    private int mShowTitle;
    private int mMenuItems;
    private boolean mKeepScreenOn = false;

    public WebViewPagePresenter(Activity activity, Bundle pageConfig) {
        super(activity, pageConfig);
    }

    @Override
    protected IWebViewPageContract.IWebViewPageView createPageView() {
        parseBundle();
        return IWebViewPageContract.WebViewPageFactory.createView(mFrom);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        setUpView();
        mView.setPresenter(this);

        mView.loadUrl(mUrl);
        mView.setKeepScreenOn(mKeepScreenOn);
    }

    private void parseBundle() {
        if (mPageBundle != null) {
            mFrom = mPageBundle.getString(WebViewConstant.KEY_FROM);
            mKeepScreenOn = mPageBundle.getBoolean(WebViewConstant.KEEP_SCREEN_ON, false);
            mUrl = mPageBundle.getString(WebViewConstant.KEY_URL);
            mTitleColor = mPageBundle.getString(WebViewConstant.KEY_TITLE_COLOR, "");
            String showTitle = mPageBundle.getString(WebViewConstant.KEY_SHOW_TITLE, String.valueOf(SHOW_TITLE_VALUE));
            try {
                mShowTitle = Integer.valueOf(showTitle);
            } catch (Throwable e) {
                mShowTitle = SHOW_TITLE_VALUE;
            }
            String menuItems = mPageBundle.getString(WebViewConstant.KEY_TITLE_MENU, String.valueOf(MENU_DEFAULT_VALUE));
            try {
                mMenuItems = Integer.valueOf(menuItems);
            } catch (Throwable e) {
                mMenuItems = MENU_DEFAULT_VALUE;
            }
        }
    }

    private void setUpView() {
        mView.setTitleColor(mTitleColor);
        mView.showTitle(mShowTitle > 0);

        boolean showBack = (mMenuItems & MENU_SHOW_BACK) == MENU_SHOW_BACK;
        mView.showBack(showBack);

        boolean showClose = (mMenuItems & MENU_SHOW_CLOSE) == MENU_SHOW_CLOSE;
        mView.showClose(showClose);

        boolean showShare = (mMenuItems & MENU_SHOW_SHARE) == MENU_SHOW_SHARE;
        mView.showShare(showShare);
    }

    @Override
    public void onNewMessage(Message message) {
        if (message.what == MessageIdConstant.MESSAGE_NOTIFY_ASSIGNMENT) {
            mView.notifyRefreshWebView();
        }
    }

    @Override
    public void onActivityResume() {
        mView.onActivityResume();
    }

    @Override
    public void onActivityPause() {
        mView.onActivityPause();
    }

    @Override
    public void onTitleBackPressed() {
        mActivity.onBackPressed();
    }

    @Override
    public boolean interceptGoBackPressed() {
        return mView.onGoBack();
    }

    @Override
    public void onWebFileChosenResult(List<Uri> result) {
        mView.onWebFileChosenResult(result);
    }

    @Override
    public void onBackPressed() {
        mActivity.finish();
    }
}
