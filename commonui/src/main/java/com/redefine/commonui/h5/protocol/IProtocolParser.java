package com.redefine.commonui.h5.protocol;

import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebView;

/**
 * Created by liwenbo on 2018/3/16.
 */

public interface IProtocolParser {

    boolean interceptUrl(WebView mWebView, String url);

    void onFileChooserCallback(Uri[] results);

    boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, String[] acceptTypes, int modeOpen);
}
