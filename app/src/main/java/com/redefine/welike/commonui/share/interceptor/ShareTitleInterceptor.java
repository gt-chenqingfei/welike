package com.redefine.welike.commonui.share.interceptor;

import android.text.TextUtils;

import com.redefine.commonui.share.SharePackageFactory;
import com.redefine.commonui.share.interceptor.AbstractInterceptor;
import com.redefine.commonui.share.sharemedel.ShareModel;
import com.redefine.foundation.language.LocalizationManager;
import com.redefine.welike.MyApplication;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.commonui.share.model.ShareTitleModel;

/**
 * Created by nianguowang on 2018/6/20
 */
public class ShareTitleInterceptor extends AbstractInterceptor {

    private ShareTitleModel mModel;

    public ShareTitleInterceptor(ShareTitleModel model) {
        mModel = model;
    }

    @Override
    public void handle(ShareModel shareModel, SharePackageFactory.SharePackage channel) {
        if (mModel == null) {
            doNext(shareModel, channel);
            return;
        }
        String title;
        if (channel.equals(SharePackageFactory.SharePackage.WHATS_APP)) {
            title = getTitleForWhatsapp(mModel.getFrom(), mModel.getNickName());
        } else {
            title = getTitle(mModel.getFrom(), mModel.getNickName());
        }
        shareModel.setTitle(title);

        doNext(shareModel, channel);
    }

    /**
     * 1博文页 2分享APP 3任务页,4 个人详情,5 Topic
     */
    public String getTitle(int from, String nickName) {
        String title = "";
        String currentLanguage = LocalizationManager.getInstance().getCurrentLanguage();
        if (from == 1) {
            String titlePost = ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "share_title_post");
            title = String.format(titlePost, nickName);
        } else if (from == 2) {
            title = ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "share_title_app");
        } else if (from == 3) {
            title = ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "share_title_app");
        } else if (from == 4) {
            String titlePost = ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "share_title_profile");
            title = String.format(titlePost, nickName);
        } else if (from == 5) {
            String titleTopic = ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "share_title_topic");
            if (TextUtils.equals(currentLanguage, LocalizationManager.LANGUAGE_TYPE_ENG)) {
                title = String.format(titleTopic, nickName);
            } else {
                title = String.format(titleTopic, nickName, nickName);
            }
        }
        return title;
    }

    public String getTitleForWhatsapp(int from, String nickName) {
        String title = "";
        String currentLanguage = LocalizationManager.getInstance().getCurrentLanguage();
        if (from == 1) {
            String titlePost = MyApplication.getApp().getString(R.string.share_title_post_whatsapp);
            title = String.format(titlePost, nickName);
//            ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "share_title_post_whatsapp");
//            if (TextUtils.equals(currentLanguage, LocalizationManager.LANGUAGE_TYPE_ENG)) {
//            } else {
//                title = String.format(titlePost, nickName, nickName);
//            }
        } else if (from == 2) {
            title = ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "share_title_app");
        } else if (from == 3) {
            title = ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "share_title_app");
        } else if (from == 4) {
            title = ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "share_title_profile_whatsapp");
        } else if (from == 5) {
            String topicTitle = ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "share_title_topic_whatsapp");
            if (TextUtils.equals(currentLanguage, LocalizationManager.LANGUAGE_TYPE_ENG)) {
                title = String.format(topicTitle, nickName);
            } else {
                title = String.format(topicTitle, nickName, nickName);
            }
        }
        return title;
    }

}
