package com.redefine.welike.commonui.share.interceptor;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.redefine.commonui.fresco.loader.ShareImageLoader;
import com.redefine.commonui.share.SharePackageFactory;
import com.redefine.commonui.share.interceptor.AbstractInterceptor;
import com.redefine.commonui.share.sharemedel.ShareModel;
import com.redefine.commonui.watermark.WaterMarkUtil;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.commonui.share.model.WaterMarkModel;

/**
 * Created by nianguowang on 2018/6/20
 */
public class WaterMarkInterceptor extends AbstractInterceptor {

    private WaterMarkModel mModel;
    private Context mContext;
    public WaterMarkInterceptor(Context context, WaterMarkModel model) {
        mModel = model;
        mContext = context;
    }

    @Override
    public void handle(final ShareModel shareModel, final SharePackageFactory.SharePackage channel) {
        if(mModel == null || TextUtils.isEmpty(shareModel.getImagePath())) {
            doNext(shareModel, channel);
            return;
        }

        if (WaterMarkUtil.isGif(shareModel.getImageUrl()) || WaterMarkUtil.isWebP(shareModel.getImageUrl())) {
            doNext(shareModel, channel);
            return;
        }

        String userHeader = mModel.getUserHeader();
        final String userNick = mModel.getUserNick();
        if(!TextUtils.isEmpty(userHeader)) {
            ShareImageLoader.getInstance().loadSharePic(mContext, userHeader, new ShareImageLoader.OnImageBitmapLoadListener() {
                @Override
                public void onImageLoad(boolean success, Bitmap bitmap) {
                    if (success) {
                        success = WaterMarkUtil.markWithHeader(mContext, bitmap, GlobalConfig.AT + userNick, shareModel.getImagePath(), null)
                                || WaterMarkUtil.markWithHeader(mContext, bitmap, GlobalConfig.AT + userNick, shareModel.getImagePath(), null);
                    } else {
                        success = WaterMarkUtil.markWithNick(mContext, GlobalConfig.AT + userNick, shareModel.getImagePath(), null)
                                || WaterMarkUtil.markWithNick(mContext, GlobalConfig.AT + userNick, shareModel.getImagePath(), null);
                    }
                    doNext(shareModel, channel);
                }

            });
        } else {
            if(!TextUtils.isEmpty(userNick)) {
                boolean success = WaterMarkUtil.markWithNick(mContext, GlobalConfig.AT + userNick, shareModel.getImagePath(), null)
                        || WaterMarkUtil.markWithNick(mContext, GlobalConfig.AT + userNick, shareModel.getImagePath(), null);
                doNext(shareModel, channel);
            } else {
                doNext(shareModel, channel);
            }
        }
    }
}
