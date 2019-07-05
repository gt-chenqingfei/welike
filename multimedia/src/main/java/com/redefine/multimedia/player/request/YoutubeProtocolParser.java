package com.redefine.multimedia.player.request;

import android.net.Uri;
import android.text.TextUtils;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.redefine.commonui.h5.protocol.IProtocolParser;

import java.net.URI;
import java.net.URISyntaxException;

public class YoutubeProtocolParser implements IProtocolParser {

    public static final String BUILD_IN_WELIKE_SCHEME = "welike";
    public static final String BUILD_IN_HTTP_SCHEME = "http";
    public static final String BUILD_IN_HTTPS_SCHEME = "https";

    @Override
    public boolean interceptUrl(WebView webView, String url) {
        if (!TextUtils.isEmpty(url)) {

            try {
                URI uri = new URI(url);
                String scheme = uri.getScheme();
                if (!BUILD_IN_HTTP_SCHEME.equalsIgnoreCase(scheme) && !BUILD_IN_HTTPS_SCHEME.equalsIgnoreCase(scheme)) {
                    return true;
                }
            } catch (URISyntaxException e) {
                // parse uri failed， filter it
                return true;
            }
        }
        return false;
    }

    @Override
    public void onFileChooserCallback(Uri[] results) {

    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, String[] acceptTypes, int modeOpen) {
        return false;
    }
}
