package com.redefine.welike.commonui.event.helper;

import com.redefine.commonui.share.SharePackageFactory;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.commonui.event.commonenums.PostType;
import com.redefine.welike.commonui.event.commonenums.ShareChannel;

/**
 * Created by nianguowang on 2018/11/3
 */
public class ShareEventHelper {

    public static ShareChannel convertSharePackage(SharePackageFactory.SharePackage shareChannel) {
        ShareChannel channel;
        if (shareChannel == SharePackageFactory.SharePackage.WHATS_APP) {
            channel = ShareChannel.WHATS_APP;
        } else if (shareChannel == SharePackageFactory.SharePackage.FACEBOOK) {
            channel = ShareChannel.FACEBOOK;
        } else if (shareChannel == SharePackageFactory.SharePackage.SYSYTEM) {
            channel = ShareChannel.OTHER;
        } else if (shareChannel == SharePackageFactory.SharePackage.INSTAGTRAM) {
            channel = ShareChannel.INSTAGRAM;
        } else if (shareChannel == SharePackageFactory.SharePackage.COPY) {
            channel = ShareChannel.COPY_LINK;
        } else if (shareChannel == SharePackageFactory.SharePackage.SHARE_APK) {
            channel = ShareChannel.SHARE_APK_BUTTON_CIRCLE;
        } else if (shareChannel == SharePackageFactory.SharePackage.SHARE_APK_LONG) {
            channel = ShareChannel.SHARE_APK_BUTTON_LONG;
        } else {
            channel = ShareChannel.OTHER;
        }
        return channel;
    }

    public static PostType convertPostType(PostBase postBase) {
        if (postBase == null) {
            return PostType.OTHER;
        }

        PostType postType;
        if (postBase.getType() == PostBase.POST_TYPE_TEXT) {
            postType = PostType.TEXT;
        } else if (postBase.getType() == PostBase.POST_TYPE_PIC) {
            postType = PostType.IMAGE;
        } else if (postBase.getType() == PostBase.POST_TYPE_VIDEO) {
            postType = PostType.VIDEO;
        } else if (postBase.getType() == PostBase.POST_TYPE_POLL) {
            postType = PostType.POLL;
        } else if (postBase.getType() == PostBase.POST_TYPE_ART) {
            postType = PostType.ARTICLE;
        } else {
            postType = PostType.OTHER;
        }
        return postType;
    }
}
