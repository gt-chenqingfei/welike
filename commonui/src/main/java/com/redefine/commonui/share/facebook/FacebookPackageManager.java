package com.redefine.commonui.share.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.redefine.commonui.share.AbsSharePackageManager;
import com.redefine.commonui.share.CommonListener;
import com.redefine.commonui.share.sharemedel.ShareModel;
import com.redefine.commonui.share.SharePackageFactory;
import com.redefine.welike.base.resource.ResourceTool;


/**
 * Created by gongguan on 2018/3/20.
 */

public class FacebookPackageManager extends AbsSharePackageManager {
    private CallbackManager callbackManager;

    public FacebookPackageManager(SharePackageFactory.SharePackage sharePackage) {
        super(sharePackage);
    }

    @Override
    public boolean isInstall(Context context) {
        String facebookPkg = "com.facebook.katana";
        return isPkgInstalled(context, facebookPkg);
    }

    @Override
    public SharePackageFactory.SharePackage getSharePackage() {
        return null;
    }

    @Override
    public boolean supportImage() {
        return false;
    }

    @Override
    public void shareTo(Context context, ShareModel shareModel, final CommonListener<String> listener) {
        callbackManager = CallbackManager.Factory.create();
        ShareDialog shareDialog = new ShareDialog((Activity) context);

        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                if (!TextUtils.isEmpty(result.getPostId())) {
                    Log.i("success", result.getPostId());
                }
                if(listener != null) {
                    listener.onFinish("Success");
                }
            }

            @Override
            public void onCancel() {
                if(listener != null) {
                    listener.onError("Canceled");
                }
            }

            @Override
            public void onError(FacebookException error) {
                if (!TextUtils.isEmpty(error.toString())) {
                    Log.i("error", error.getMessage());
                }
                if(listener != null) {
                    listener.onFinish("Error");
                }
            }
        });

        if (!TextUtils.isEmpty(shareModel.getH5Link())) {
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(shareModel.getH5Link()))
                    .build();
            shareDialog.show(content);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        callbackManager.onActivityResult(requestCode, responseCode, intent);
    }

    @Override
    public void onDestroy() {
    }

}
