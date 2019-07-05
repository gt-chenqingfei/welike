package com.redefine.welike.commonui.share.interceptor;

import android.net.Uri;
import android.text.TextUtils;

import com.redefine.commonui.share.SharePackageFactory;
import com.redefine.commonui.share.interceptor.AbstractInterceptor;
import com.redefine.commonui.share.sharemedel.ShareModel;
import com.redefine.foundation.language.LocalizationManager;
import com.redefine.welike.commonui.share.model.ShortLinkModel;

/**
 * 分享链接生成规则：
 * 根据参数判断对应路由：
 * af_adset:
 * 1: /postdetail/comment
 * 2: /download
 * 3: /download
 * 4: /profile
 * 5: /topic/hot
 * 6: /lbs/hot
 * 具体的路由拼接为
 *  https://m.welike.in/postdetail/comment/[af_sub2]?[app 端给的参数]
 *  https://m.welike.in/download?[app 端给的参数]
 *  https://m.welike.in/profile/[af_sub2]?[app 端给的参数]
 *  https://m.welike.in/topic/hot/[af_sub2]?[app 端给的参数]
 *  https://m.welike.in/lbs/hot/[af_sub2]?[app 端给的参数]
 *  af_sub2的值不需要关心是什么，他会根据 af_set的值变化。
 *
 * Created by nianguowang on 2018/7/10
 */
public class ShareLinkInterceptor extends AbstractInterceptor {

    private ShortLinkModel mModel;
    private static final String SCHEME = "https";
    private static final String HOST = "m.welike.in";

    private static final String PATH_POST = "postdetail/comment";
    private static final String PATH_APP = "download";
    private static final String PATH_PROFILE = "profile";
    private static final String PATH_TOPIC = "topic/hot";
    private static final String PATH_LBS = "lbs/hot";

    public ShareLinkInterceptor(ShortLinkModel model) {
        mModel = model;
    }

    @Override
    public void handle(ShareModel shareModel, SharePackageFactory.SharePackage channel) {
        if(mModel == null) {
            doNext(shareModel, channel);
            return;
        }

        String language = LocalizationManager.getInstance().getCurrentLanguage();
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
        .authority(HOST);
        switch (mModel.getFrom()) {
            case ShareModel.SHARE_MODEL_TYPE_POST:
                builder.path(PATH_POST + "/" + mModel.getShareId());
                break;
            case ShareModel.SHARE_MODEL_TYPE_APP:
                builder.path(PATH_APP);
                break;
            case ShareModel.SHARE_MODEL_TYPE_PROFILE:
                builder.path(PATH_PROFILE + "/" + mModel.getShareId());
                break;
            case ShareModel.SHARE_MODEL_TYPE_TOPIC:
                builder.path(PATH_TOPIC + "/" + mModel.getShareId());
                break;
            case ShareModel.SHARE_MODEL_TYPE_LBS:
                builder.path(PATH_LBS + "/" + mModel.getShareId());
                break;
        }
        builder.appendQueryParameter("pid", "share")
                .appendQueryParameter("af_adset", String.valueOf(mModel.getFrom()))
                .appendQueryParameter("af_sub1", "")
                .appendQueryParameter("af_sub2", mModel.getShareId())
                .appendQueryParameter("lang", (TextUtils.isEmpty(language) ? "" : language));
        shareModel.setH5Link(builder.build().toString());

        doNext(shareModel, channel);
    }
}
