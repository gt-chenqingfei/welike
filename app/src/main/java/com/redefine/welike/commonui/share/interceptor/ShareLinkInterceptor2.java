package com.redefine.welike.commonui.share.interceptor;

import android.net.Uri;

import com.redefine.commonui.share.SharePackageFactory;
import com.redefine.commonui.share.interceptor.AbstractInterceptor;
import com.redefine.commonui.share.sharemedel.ShareModel;
import com.redefine.welike.base.BuildConfig;
import com.redefine.welike.commonui.share.model.ShortLinkModel;

/**
 * 目前只支持post链接：
 * https://s.welike.in/p/fd0bbf56-921f-4bbf-8192-f40a0c5593c1
 *
 * Created by nianguowang on 2018/7/10
 */
public class ShareLinkInterceptor2 extends AbstractInterceptor {

    private ShortLinkModel mModel;
    private static final String SCHEME = "https";
    private static final String HOST_POST = "welike.in";
    private static final String HOST_POST_PRE = "pre-www.welike.in";

    private static final String PATH_POST = "p";
    private static final String PATH_APP = "download";
    private static final String PATH_PROFILE = "";
    private static final String PATH_TOPIC = "topic";
    private static final String PATH_LBS = "lbs";

    public ShareLinkInterceptor2(ShortLinkModel model) {
        mModel = model;
    }

    @Override
    public void handle(ShareModel shareModel, SharePackageFactory.SharePackage channel) {
        if(mModel == null) {
            doNext(shareModel, channel);
            return;
        }
        String postHost = HOST_POST;
        if (BuildConfig.DEBUG) {
            postHost = HOST_POST_PRE;
        }

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME).authority(postHost);
        switch (mModel.getFrom()) {
            case ShareModel.SHARE_MODEL_TYPE_POST:
                builder.authority(postHost).path(PATH_POST + '/' + mModel.getShareId());
                break;
            case ShareModel.SHARE_MODEL_TYPE_APP:
                builder.path(PATH_APP);
                break;
            case ShareModel.SHARE_MODEL_TYPE_PROFILE:
                builder.encodedPath(mModel.getShareId());
                break;
            case ShareModel.SHARE_MODEL_TYPE_TOPIC:
                builder.encodedPath(PATH_TOPIC + "/" + mModel.getShareId());
                break;
            case ShareModel.SHARE_MODEL_TYPE_LBS:
                builder.path(PATH_LBS + "/" + mModel.getShareId());
                break;
        }

        shareModel.setH5Link(builder.build().toString());

        doNext(shareModel, channel);
    }
}
