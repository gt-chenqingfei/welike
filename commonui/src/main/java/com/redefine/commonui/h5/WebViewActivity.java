package com.redefine.commonui.h5;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.redefine.commonui.R;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.constant.WebViewConstant;
import com.redefine.foundation.framework.Event;
import com.redefine.welike.base.constant.EventIdConstant;

import org.greenrobot.eventbus.EventBus;

/**
 * @author gongguan
 * @time 2018/1/14 下午6:32
 */
public class WebViewActivity extends BaseActivity implements ProgressWebView.ISetTitleInterface, ProgressWebView.ILoadWebView {
    private ProgressWebView webView;
    private TextView mTvTitle;
    private ImageView iv_back;
//    private View mLlActionbar;
    private int actionBarColor = -1;
    private String title = "";
//    private WebViewErrorView mErrorView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String url = null;
        if (intent != null) {
            url = intent.getStringExtra(WebViewConstant.WEBVIEW_URL);
            if (intent.getIntExtra(WebViewConstant.WEBVIEW_ACTION_BAR_COLOR, -1) != -1) {
                actionBarColor = intent.getIntExtra(WebViewConstant.WEBVIEW_ACTION_BAR_COLOR, -1);
            }
            if (!TextUtils.isEmpty(intent.getStringExtra(WebViewConstant.KEY_TITLE_TEXT))) {
                title = intent.getStringExtra(WebViewConstant.KEY_TITLE_TEXT);
            }
        }
        setContentView(R.layout.activity_web_view);
        initViews();
        initEvents(url);
    }

    public void initViews() {
        webView = findViewById(R.id.wb_common);
        iv_back = findViewById(R.id.iv_common_back);
        mTvTitle = findViewById(R.id.tv_common_title);
//        mLlActionbar = findViewById(R.id.ll_common_layout);
//        mErrorView = findViewById(R.id.webview_error_view);
        mTvTitle.setText(title);
    }

    public void initEvents(final String url) {
        if (actionBarColor != -1) {
//            mLlActionbar.setBackgroundColor(getResources().getColor(actionBarColor));
        }
        if (TextUtils.isEmpty(url)) {
            webView.setVisibility(View.GONE);
//            mErrorView.setVisibility(View.VISIBLE);
        }

//        mErrorView.setOnErrorViewClickListener(new WebViewErrorView.IErrorViewClickListener() {
//            @Override
//            public void onErrorViewClick() {
//                mErrorView.setVisibility(View.GONE);
//                loadUrl(url);
//            }
//        });

        loadUrl(url);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadUrl(String url) {
        webView.loadUrl(url);
        webView.setColor(R.color.app_color);
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
    }

    public static void luanch(Context context, String url) {
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_WEB_VIEW, bundle));
//        Intent intent = new Intent();
//        intent.putExtra(WebViewConstant.WEBVIEW_URL, url);
//        intent.setClass(context, WebViewActivity.class);
//        context.startActivity(intent);
    }

    public static void luanch(Context context, String url, int color) {
        Intent intent = new Intent();
        intent.putExtra(WebViewConstant.WEBVIEW_URL, url);
        intent.putExtra(WebViewConstant.WEBVIEW_ACTION_BAR_COLOR, color);
        intent.setClass(context, WebViewActivity.class);
        context.startActivity(intent);
    }
    public static void luanch(Context context, String url, String title) {
        Intent intent = new Intent();
        intent.putExtra(WebViewConstant.WEBVIEW_URL, url);
        intent.putExtra(WebViewConstant.KEY_TITLE_TEXT, title);
        intent.setClass(context, WebViewActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    @Override
    public void loadSuccess(boolean isLoaded) {
        if (!isLoaded) {
            webView.setVisibility(View.GONE);
//            mErrorView.setVisibility(View.VISIBLE);
        } else {
            webView.setVisibility(View.VISIBLE);
//            mErrorView.setVisibility(View.GONE);
        }
    }

}
