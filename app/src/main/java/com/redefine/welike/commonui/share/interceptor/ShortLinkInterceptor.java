package com.redefine.welike.commonui.share.interceptor;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.commonui.share.SharePackageFactory;
import com.redefine.commonui.share.interceptor.AbstractInterceptor;
import com.redefine.commonui.share.request.ShortUrlRequest;
import com.redefine.commonui.share.sharemedel.ShareModel;
import com.redefine.foundation.language.LocalizationManager;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.commonui.share.model.ShortLinkModel;

/**
 * Created by nianguowang on 2018/6/20
 */
public class ShortLinkInterceptor extends AbstractInterceptor {

    private ShortLinkModel mModel;
    private Context mContext;

    public ShortLinkInterceptor(Context context, ShortLinkModel model) {
        mModel = model;
        mContext = context;
    }

    @Override
    public void handle(final ShareModel shareModel, final SharePackageFactory.SharePackage channel) {
        if(mModel == null) {
            doNext(shareModel, channel);
            return;
        }

        String language = LocalizationManager.getInstance().getCurrentLanguage();
        String param = "pid=share" + "&c=" + SharePackageFactory.getName2(channel) +
                "&af_adset=" + mModel.getFrom() +
                "&af_sub1=" + mModel.getUserId() +
                "&af_sub2=" + mModel.getShareId() +
                "&lang=" + (TextUtils.isEmpty(language) ? "" : language);
        ShortUrlRequest request = new ShortUrlRequest(mContext, param);
        try {
            request.req(new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {
                    doNext(shareModel,channel);
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    String link = result.getString("link");
                    shareModel.setH5Link(link);
                    doNext(shareModel, channel);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            doNext(shareModel, channel);
        }
    }
}
