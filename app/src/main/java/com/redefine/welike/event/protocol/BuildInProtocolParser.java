package com.redefine.welike.event.protocol;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.facebook.share.model.ShareMessengerMediaTemplateContent;
import com.redefine.commonui.h5.WhiteListManager;
import com.redefine.commonui.h5.protocol.IProtocolParser;
import com.redefine.foundation.utils.VersionUtil;
import com.redefine.multimedia.photoselector.config.ImagePickConfig;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.commonui.photoselector.PhotoSelector;
import com.redefine.welike.event.AvoidRouteDispatcher;
import com.redefine.welike.event.RouteDispatcher;

import java.util.List;

/**
 * Created by liwenbo on 2018/3/16.
 */

public class BuildInProtocolParser implements IProtocolParser {

    public static final String BUILD_IN_WELIKE_SCHEME = "welike";
    public static final String BUILD_IN_HTTP_SCHEME = "http";
    public static final String BUILD_IN_HTTPS_SCHEME = "https";
    public static final String BUILD_IN_INTENT_SCHEME = "intent";
    private final WhiteListManager mWhiteList;
    private ValueCallback<Uri[]> mFilePathCallback;

    public BuildInProtocolParser() {
        mWhiteList = new WhiteListManager();
    }

    @Override
    public boolean interceptUrl(WebView webView, String url) {
        if (!TextUtils.isEmpty(url)) {

            try {
                Uri uri = Uri.parse(url);
                String scheme = uri.getScheme();
                if (TextUtils.equals(scheme, BUILD_IN_WELIKE_SCHEME)) {
                    boolean validUri = RouteDispatcher.validUri(uri);
                    Account account = AccountManager.getInstance().getAccount();
                    if (account != null &&
                            account.getCompleteLevel() >= Account.PROFILE_COMPLETE_LEVEL_MAIN_DONE &&
                            account.isLogin()) {
                        if (validUri) {
//                            ARouter.getInstance().build(uri).navigation(webView.getContext());
                            RouteDispatcher.routeDispatcher(url, webView.getContext());
                        }
                    } else {
                        if (validUri) {
                            try {
//                                uri = Uri.parse(uri.toString().replace(RouteConstant.MAIN_ROUTE_PATH, RouteConstant.AVOID_ROUTE_PATH));
//                                ARouter.getInstance().build(uri).navigation(webView.getContext());

                                AvoidRouteDispatcher.avoidRouteDispatcher(url);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    //Uri rawUri = Uri.parse(url);
                    //ARouter.getInstance().build(rawUri).navigation(webView.getContext());
                    return true;
                }
                if (BUILD_IN_INTENT_SCHEME.equalsIgnoreCase(scheme)) {
//                    MyApplication.getApp().startActivity(new Intent(uri));
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        PackageManager packageManager = MyApplication.getApp().getPackageManager();
                        ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
                        if (info != null) {
                            MyApplication.getApp().startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                if (!BUILD_IN_HTTP_SCHEME.equalsIgnoreCase(scheme) && !BUILD_IN_HTTPS_SCHEME.equalsIgnoreCase(scheme)) {
                    return true;
                }
            } catch (Exception e) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, String[] acceptTypes, int modeOpen) {
        String url = webView.getUrl();
        Uri uri = Uri.parse(url);
        String host = uri.getHost();
        if (!mWhiteList.inWhiteList(host)) {
            return false;
        }

        boolean isCrop = false;
        MediaType mediaType = MediaType.ALL;
        if (acceptTypes != null) {
            for (String acceptType: acceptTypes) {
                if (acceptType.equalsIgnoreCase("image/*")) {
                    mediaType = MediaType.IMAGE;
                } else if (acceptType.equalsIgnoreCase("video/*")) {
                    mediaType = MediaType.VIDEO;
                }

                if (acceptType.equalsIgnoreCase("welike/crop")) {
                    isCrop = true;
                }
            }
        }

        if (webView.getContext() instanceof Activity) {
            mFilePathCallback = filePathCallback;
            int count = 1;
            if (modeOpen == WebChromeClient.FileChooserParams.MODE_OPEN_MULTIPLE) {
                count = ImagePickConfig.MAX_IMAGE_SELECT_COUNT;
            }
            PhotoSelector.launchWebFileChooser((Activity) webView.getContext(), mediaType, count, isCrop);
            return true;
        }
        return false;
    }

    @Override
    public void onFileChooserCallback(Uri[] results) {
        if (mFilePathCallback != null){
            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        }
    }

    public static enum  MediaType {
        ALL, IMAGE, VIDEO
    }

}
