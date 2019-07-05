package com.redefine.commonui.h5;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.redefine.commonui.R;

/**
 * Created by gongguan on 2018/1/14.
 */

public class ProgressWebView extends WebView {
    private ProgressView progressBar;
    private Handler handler;
    private WebView mWebView;
    private ISetTitleInterface iSetTitleInterface;
    private ILoadWebView iLoadWebView;

    public ProgressWebView(Context context) {
        super(context, null);
    }

    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        iSetTitleInterface = (ISetTitleInterface) context;
        iLoadWebView = (ILoadWebView) context;
        initProgress(context);
        initSettings();
    }

    public ProgressWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initProgress(Context context) {
        progressBar = new ProgressView(context);
        ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.setLayoutParams(vl);
        progressBar.setVisibility(GONE);
        addView(progressBar);
        //初始化handle
        handler = new Handler();
        mWebView = this;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initSettings() {
        WebSettings mSettings = this.getSettings();

        mSettings.setJavaScriptEnabled(true);
        mSettings.setLoadWithOverviewMode(true);// 调整到适合webview大小
        mSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        mSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        mSettings.setBlockNetworkImage(true);

        setWebViewClient(new MyWebClient());
        setWebChromeClient(new MyWebChromeClient());
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressBar.setProgress(100);
                handler.postDelayed(runnable, 200);//0.2秒后隐藏进度条
            } else if (progressBar.getVisibility() == GONE) {
                progressBar.setVisibility(VISIBLE);
            }

            if (newProgress < 10) {
                newProgress = 10;
            }

            progressBar.setProgress(newProgress);
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            iSetTitleInterface.setTitle(title);
        }
    }

    private class MyWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mWebView.loadUrl(url);
            return true;
        }

        /**
         * 页面加载完成回调的方法
         *
         * @param view
         * @param url
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // 关闭图片加载阻塞
            view.getSettings().setBlockNetworkImage(false);
        }

        /**
         * 页面开始加载调用的方法
         *
         * @param view
         * @param url
         * @param favicon
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            iLoadWebView.loadSuccess(false);
        }

        @Override
        public void onScaleChanged(WebView view, float oldScale, float newScale) {
            super.onScaleChanged(view, oldScale, newScale);
            ProgressWebView.this.requestFocus();
            ProgressWebView.this.requestFocusFromTouch();
        }
    }

    interface ISetTitleInterface {
        void setTitle(String title);
    }

    interface ILoadWebView {
        void loadSuccess(boolean isLoaded);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            progressBar.setVisibility(View.GONE);
        }
    };


    public void setColor(int color) {
        progressBar.setColor(color);
    }

}