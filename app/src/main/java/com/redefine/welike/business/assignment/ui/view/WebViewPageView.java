package com.redefine.welike.business.assignment.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.redefine.commonui.h5.WebViewDelegate;
import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.business.assignment.management.HtmlParser;
import com.redefine.welike.business.assignment.ui.contract.IWebViewPageContract;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.event.protocol.BuildInProtocolParser;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liwenbo on 2018/3/16.
 */

public class WebViewPageView implements IWebViewPageContract.IWebViewPageView, View.OnClickListener
        , WebViewDelegate.IReceivedTitleListener, WebViewDelegate.ISslErrorCancelProcess {
    private final String mFrom;
    private WebViewDelegate mDelegateWebView;
    private RelativeLayout mTitleContainer;
    private TextView mTitleView;
    private View mBackBtn, mCloseBtn, mMoreBtn;
    private IWebViewPageContract.IWebViewPagePresenter mPresenter;
    private LoadingDlg mLoadingDlg;

    public WebViewPageView(String from) {
        mFrom = from;
    }

    @Override
    public View createView(Context context, Bundle savedInstanceState) {
        View view = LayoutInflater.from(context).inflate(R.layout.webview_page_layout, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        ViewGroup webViewGroup = view.findViewById(R.id.web_rootView);
        mTitleContainer = view.findViewById(R.id.assignment_title_view);
        mTitleView = view.findViewById(R.id.common_title_view);
        mBackBtn = view.findViewById(R.id.common_back_btn);
        mCloseBtn = view.findViewById(R.id.common_close_btn);
        mMoreBtn = view.findViewById(R.id.common_more_btn);

        mBackBtn.setOnClickListener(this);
        mCloseBtn.setOnClickListener(this);
        mMoreBtn.setOnClickListener(this);
        mDelegateWebView = new WebViewDelegate(webViewGroup, new BuildInProtocolParser(), mFrom, this);
        mDelegateWebView.setReceivedTitleListener(this);
    }

    @Override
    public void attach() {
    }

    @Override
    public void detach() {

    }

    @Override
    public void destroy() {
        mDelegateWebView.destroy();
        if(mLoadingDlg != null) {
            mLoadingDlg.dismiss();
        }
    }

    @Override
    public void onClick(final View v) {
        if (v == mBackBtn) {
            mPresenter.onTitleBackPressed();
        } else if (v == mCloseBtn) {
            mPresenter.onBackPressed();
        } else if (v == mMoreBtn) {
            doShare(v.getContext());
        }
    }

    private void doShare(final Context context) {
        if(mLoadingDlg == null) {
            mLoadingDlg = new LoadingDlg((Activity) context);
        }
        mLoadingDlg.show();
        new HtmlParser().parseHtml(mDelegateWebView.getCurrentUrl(), new HtmlParser.OnParseCompleteCallback() {
            @Override
            public void onComplete(boolean success, String title, String url, String summary, String imagePath) {
                mLoadingDlg.dismiss();
                if(success) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, title + "\t" + url);
                    context.startActivity(Intent.createChooser(intent,"Share using?"));
                } else {
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showLong(ErrorCode.showErrCodeText(ErrorCode.ERROR_NETWORK_INVALID));
                        }
                    });
                }
            }
        });
    }

    @Override
    public void setPresenter(IWebViewPageContract.IWebViewPagePresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setTitleColor(String titleColor) {
        try {
            int color = Color.parseColor(titleColor);
            mTitleContainer.setBackgroundColor(color);
        } catch (Exception e) {
            mTitleContainer.setBackgroundResource(R.color.white);
        }
    }

    @Override
    public void showTitle(boolean showTitle) {
        mTitleContainer.setVisibility(showTitle ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showBack(boolean showBack) {
        mBackBtn.setVisibility(showBack ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showClose(boolean showClose) {
        mCloseBtn.setVisibility(showClose ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showShare(boolean showShare) {
        mMoreBtn.setVisibility(showShare ? View.VISIBLE : View.GONE);
    }

    @Override
    public void loadUrl(String mUrl) {
        mDelegateWebView.loadUrl(mUrl);
    }

    @Override
    public boolean onGoBack() {
        if (mDelegateWebView.canGoBack()) {
            mDelegateWebView.goBack();
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResume() {
        mDelegateWebView.onActivityResume();
    }

    @Override
    public void onActivityPause() {
        mDelegateWebView.onActivityPause();
    }

    @Override
    public void notifyRefreshWebView() {
        mDelegateWebView.notifyRefreshWebView();
    }

    @Override
    public void setKeepScreenOn(boolean keepScreenOn) {
        mDelegateWebView.setKeepScreenOn(keepScreenOn);
    }

    @Override
    public void onWebFileChosenResult(List<Uri> result) {
        mDelegateWebView.onWebFileChosenResult(result);

    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        int padding;
        if(mCloseBtn.getVisibility() == View.VISIBLE && mBackBtn.getVisibility() == View.VISIBLE) {
            padding = ScreenUtils.dip2Px(100);
        } else {
            padding = ScreenUtils.dip2Px(50);
        }
        mTitleView.setPadding(padding, 0, padding, 0);
        mTitleView.setText(title);
    }

    @Override
    public void onCancelProcess() {
        mPresenter.onBackPressed();
    }



}
