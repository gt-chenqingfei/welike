package com.redefine.welike.commonui.share.interceptor;

import android.content.Context;
import android.text.TextUtils;

import com.redefine.commonui.fresco.loader.ImageDownloader;
import com.redefine.commonui.share.SharePackageFactory;
import com.redefine.commonui.share.interceptor.AbstractInterceptor;
import com.redefine.commonui.share.sharemedel.ShareModel;

/**
 * Created by nianguowang on 2018/6/20
 */
public class ImageDownLoadInterceptor extends AbstractInterceptor {

    private Context mContext;
    public ImageDownLoadInterceptor(Context context) {
        mContext = context;
    }

    @Override
    public void handle(final ShareModel shareModel, final SharePackageFactory.SharePackage channel) {
        if(shareModel == null || TextUtils.isEmpty(shareModel.getImageUrl())) {
            doNext(shareModel, channel);
            return;
        }
        ImageDownloader.getInstance().download(mContext, shareModel.getImageUrl(), new ImageDownloader.ImageDownloadListener() {
            @Override
            public void onSuccess(String filePath) {
                shareModel.setImagePath(filePath);
                doNext(shareModel, channel);
            }

            @Override
            public void onFail(int errorCode) {
                doNext(shareModel, channel);
            }
        });
    }
}
