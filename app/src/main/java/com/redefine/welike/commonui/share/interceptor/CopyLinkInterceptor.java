package com.redefine.welike.commonui.share.interceptor;

import android.content.Context;

import com.redefine.commonui.share.ISharePackageManager;
import com.redefine.commonui.share.SharePackageFactory;
import com.redefine.commonui.share.interceptor.AbstractInterceptor;
import com.redefine.commonui.share.sharemedel.ShareModel;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by nianguowang on 2018/7/10
 */
public class CopyLinkInterceptor extends AbstractInterceptor {

    private Context mContext;
    public CopyLinkInterceptor(Context context) {
       mContext = context;
    }

    @Override
    public void handle(final ShareModel shareModel, final SharePackageFactory.SharePackage channel) {
        if(shareModel == null) {
            doNext(shareModel, channel);
            return;
        }

        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                ISharePackageManager copySharePackage = SharePackageFactory.getSharePackages().get(SharePackageFactory.SharePackage.COPY);
                if(copySharePackage != null) {
                    copySharePackage.shareTo(mContext, shareModel, null);
                }
                doNext(shareModel, channel);
            }
        });
    }
}
