package com.redefine.commonui.h5;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsPromptResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.redefine.commonui.R;
import com.redefine.commonui.dialog.CommonConfirmDialog;
import com.redefine.commonui.h5.protocol.IJsBridgeDispatcher;
import com.redefine.commonui.h5.protocol.IProtocolParser;
import com.redefine.commonui.share.ISharePackageManager;
import com.redefine.commonui.share.SharePackageFactory;
import com.redefine.commonui.share.activity.ShareActivity;
import com.redefine.commonui.share.system.SystemPackageManager;
import com.redefine.commonui.widget.BaseErrorView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.foundation.utils.VersionUtil;
import com.redefine.welike.base.io.WeLikeFileManager;
import com.redefine.welike.base.resource.ResourceTool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liwenbo on 2018/3/16.
 */

public class WebViewDelegate implements BaseErrorView.IErrorViewClickListener {

    private static final String WELIKE_REFRESH = "javascript:welike.reflash()";

    private final IProtocolParser mBuildInProtocol;
    private final ViewGroup mWebViewGroup;
    private final ISslErrorCancelProcess mSslCancelProcessListener;
    private String mFrom;
    private IReceivedTitleListener mListenter;
    private WebView mWebView;
    private ErrorView mErrorView;
    private LoadingView mLoadingView;
    private boolean isReceivedError = false;
    private String mLastUrl;
    private WeLikeJsBridge mJsBridge;
    private IJsBridgeDispatcher mMessageDispatcher;
    private UrlParamsExpander mParamsExpander;
    private Map<String, String> mToAddedParams;

    public WebViewDelegate(ViewGroup viewGroup, IProtocolParser parser, String from, ISslErrorCancelProcess cancelProcess) {
        mWebViewGroup = viewGroup;
        mBuildInProtocol = parser;
        mFrom = from;
        mSslCancelProcessListener = cancelProcess;

        mToAddedParams = new LinkedHashMap<>();
        mToAddedParams.put("from", mFrom);
        initWebView();
    }

    private void initWebView() {
        LayoutInflater.from(mWebViewGroup.getContext()).inflate(R.layout.web_view_layout, mWebViewGroup, true);
        mWebView = mWebViewGroup.findViewById(R.id.common_web_view);
        mErrorView = mWebViewGroup.findViewById(R.id.common_error_view);
        mLoadingView = mWebViewGroup.findViewById(R.id.common_loading_view);
        mJsBridge = new WeLikeJsBridge(mWebView.getContext());
        mMessageDispatcher = new JsBridgeMessageDispatcher(mJsBridge);
        mParamsExpander = new UrlParamsExpander();
        mErrorView.setOnErrorViewClickListener(this);
        WebSettings settings = mWebView.getSettings();
        settings.setDomStorageEnabled(true);
        if (shouldDisableHardwareRenderInLayer()) {
            // 修复三星4.3手机退出crash问题
            try {
                mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            } catch (Exception globalException) {
                globalException.printStackTrace();
            }
        }
        // 坑：部分网站通过UA来判断手机是属于Android机，所以，不能随意定制UA，下个版本统一采用公参展开来解决
//        settings.setUserAgentString(UserAgent.getDefaultUserAgent(mWebView.getContext()));
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAppCachePath(WeLikeFileManager.getWebViewCacheDir());
        settings.setAppCacheEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);

        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setJavaScriptEnabled(true);

        if (VersionUtil.isUpperOrEquql4_2()) {
            mWebView.addJavascriptInterface(mJsBridge, "WeLikeJsBridge");
        }
//        if (BuildConfig.DEBUG && !VersionUtil.isLower4_4()) {
//            WebView.setWebContentsDebuggingEnabled(true);
//        }

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                if (uri.getHost().equals("chat.whatsapp.com")) {
                    PackageInfo packageInfo = null;
                    try {
                        packageInfo = view.getContext().getPackageManager().getPackageInfo("com.whatsapp", 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        packageInfo = null;
                    }
                    if (packageInfo == null) {
                        String toastMsg = String.format(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_share_no_app"), "Whatsapp");
                        Toast.makeText(view.getContext(), toastMsg, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    try {
                        // 跳转whatsapp聊天群
                        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        view.getContext().startActivity(intent);
                    } catch (Exception e) {
                        // 未安装
                        e.printStackTrace();
                    }
                    return true;
                }
                if (mBuildInProtocol.interceptUrl(mWebView, url)) {
                    return true;
                }
                mWebView.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                CommonConfirmDialog.showConfirmDialog(view.getContext()
                        , ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_error_ssl_cert_invalid")
                        , ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_confirm")
                        , ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_cancel")
                        , new CommonConfirmDialog.IConfirmDialogListener() {
                            @Override
                            public void onClickCancel() {
                                handler.proceed();
                            }

                            @Override
                            public void onClickConfirm() {
                                handler.cancel();
                                if (mSslCancelProcessListener != null) {
                                    mSslCancelProcessListener.onCancelProcess();
                                }
                            }
                        });
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                isReceivedError = true;
                mErrorView.setVisibility(View.VISIBLE);
//                mLoadingView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                view.getSettings().setBlockNetworkImage(false);
                if (!isReceivedError) {
//                    mLoadingView.setVisibility(View.INVISIBLE);
                    mErrorView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                isReceivedError = false;
//                view.getSettings().setBlockNetworkImage(true);
                mErrorView.setVisibility(View.INVISIBLE);
//                mLoadingView.setVisibility(View.VISIBLE);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (mListenter != null) {
                    mListenter.onReceivedTitle(view, title);
                }
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                if (VersionUtil.isUpperOrEquql4_2()) {
                    return super.onJsPrompt(view, url, message, defaultValue, result);
                }
                mMessageDispatcher.dispatchMessage(view.getContext(), message);
                result.confirm();
                return true;
            }

            public void openFileChooser(final ValueCallback<Uri> filePathCallback, String acceptType, String capture) {
                String[] acceptTypes = new String[]{""};
                if (acceptType != null) {
                    acceptTypes = acceptType.split(",");
                }
                mBuildInProtocol.onShowFileChooser(mWebView, new ValueCallback<Uri[]>() {
                    @Override
                    public void onReceiveValue(Uri[] value) {
                        if (value == null || value.length == 0) {
                            filePathCallback.onReceiveValue(null);
                        } else {
                            filePathCallback.onReceiveValue(value[0]);
                        }
                    }
                }, acceptTypes, FileChooserParams.MODE_OPEN);

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                String[] fileAcceptTypes = fileChooserParams.getAcceptTypes();
                List<String> acceptTypes = new ArrayList<>();
                if (fileAcceptTypes != null && fileAcceptTypes.length != 0) {
                    for (String acceptType: fileAcceptTypes) {
                        String[] tempAcceptTypes;
                        if (acceptType != null) {
                            tempAcceptTypes = acceptType.split(",");
                            if (tempAcceptTypes.length != 0) {
                                Collections.addAll(acceptTypes, tempAcceptTypes);
                            }
                        }
                    }
                }
                String[] acceptT = new String[acceptTypes.size()];
                return mBuildInProtocol.onShowFileChooser(webView, filePathCallback, acceptTypes.toArray(acceptT), fileChooserParams.getMode());
            }
        });
    }

    public void setWebViewBgColor(int color) {
        mWebView.setBackgroundColor(color);
    }

    public void loadUrl(String url) {
        LogUtil.d("wng_url", "orig=" + url);
        mLastUrl = mParamsExpander.expandParams(url);
        mLastUrl = new UrlParamAdder(mToAddedParams).addParam(mLastUrl);
        mWebView.loadUrl(mLastUrl);
        LogUtil.d("wng_url", "lastUrl=" + mLastUrl);
    }

    public void setReceivedTitleListener(IReceivedTitleListener listener) {
        mListenter = listener;
    }

    @Override
    public void onErrorViewClick() {
        if (!TextUtils.isEmpty(mLastUrl)) {
            loadUrl(mLastUrl);
        }
    }

    public void notifyRefreshWebView() {
        mJsBridge.callWebView(mWebView, WELIKE_REFRESH);
//        if (mWebView != null) {
//            mWebView.reload();
//        }
    }

    public boolean canGoBack() {
        return mWebView.canGoBack();
    }

    public void goBack() {
        mWebView.goBack();
    }

    public String getCurrentUrl() {
        return mWebView.getUrl();
    }

    public void onActivityResume() {
        try {
            mWebView.onResume();
        } catch (Throwable e) {
            // do nothing
        }
    }

    public void onActivityPause() {
        try {
            mWebView.onPause();
        } catch (Throwable e) {
            // do nothing
        }
    }

    public void setKeepScreenOn(boolean keepScreenOn) {
        if (mWebView != null) {
            mWebView.setKeepScreenOn(keepScreenOn);
        }
    }

    public void onWebFileChosenResult(List<Uri> result) {
        Uri[] uris = new Uri[result.size()];
        mBuildInProtocol.onFileChooserCallback(result.toArray(uris));
    }

    public static interface IReceivedTitleListener {
        void onReceivedTitle(WebView view, String title);
    }

    public interface OnGetShareParamsListener {
        void onGetShareParams(String title, String shareUrl, String summary, String icon);
    }

    public void destroy() {
        if (mWebView != null) {
            try {
                mWebView.stopLoading();
                ((ViewGroup) mWebView.getParent()).removeView(mWebView);
                mWebView.destroy();
            } catch (Throwable e) {
                // do nothing
            }
        }
    }

    public static boolean shouldDisableHardwareRenderInLayer() {
        // case 1: samsung GS4 on android 4.3 is know to cause crashes at libPowerStretch.so:0x2d4c
        // use GT-I95xx to match more GS4 series devices though GT-I9500 is the typical device
        final boolean isSamsungGs4 =
                android.os.Build.MODEL != null &&
                        android.os.Build.MODEL.contains("GT-I9") &&
                        android.os.Build.MANUFACTURER != null &&
                        android.os.Build.MANUFACTURER.equals("samsung");
        final boolean isLow4_4 = Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT;
        return isSamsungGs4 || isLow4_4;
    }

    public static interface ISslErrorCancelProcess {
        void onCancelProcess();
    }

}
