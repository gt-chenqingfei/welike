package com.redefine.welike.commonui.share;

import android.content.Context;
import android.text.TextUtils;

import com.redefine.commonui.share.SharePackageFactory;
import com.redefine.commonui.share.sharemedel.ShareModel;
import com.redefine.commonui.share.sharemedel.SharePackageModel;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.multimedia.player.constant.PlayerConstant;
import com.redefine.richtext.RichItem;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.business.feeds.management.bean.ForwardPost;
import com.redefine.welike.business.feeds.management.bean.PicInfo;
import com.redefine.welike.business.feeds.management.bean.PicPost;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.bean.TopicCardInfo;
import com.redefine.welike.business.feeds.management.bean.VideoPost;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.commonui.event.model.EventModel;
import com.redefine.welike.commonui.share.model.ShareTitleModel;
import com.redefine.welike.commonui.share.model.ShortLinkModel;
import com.redefine.welike.commonui.share.model.WaterMarkModel;
import com.redefine.welike.business.videoplayer.management.bean.AttachmentBase;
import com.redefine.welike.business.videoplayer.management.bean.ImageAttachment;
import com.redefine.welike.statistical.manager.NewShareEventManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by nianguowang on 2018/5/16
 */
public class ShareHelper {

    private static final String SHARE_APP_IMAGE = "https://img.welike.in/whatsapp.jpg";
    private ShareHelper() {}

    /**
     * 获取Post中分享的图片，获取策略：
     * 1，当post中没有图片的时候，则使用用户的头像，作为图片分享出去。
     * 2，当post中有多张图片的时候，使用第一张图片分享出去。
     * @param postBase
     * @return
     */
    public static String getSharePostImage(PostBase postBase) {
        String imageUrl = "";
        if (postBase == null) {
            return imageUrl;
        }
        if (postBase instanceof PicPost) {
            PicInfo picInfo = ((PicPost) postBase).getPicInfo(0);
            imageUrl = picInfo.getPicUrl();
        } else if (postBase instanceof VideoPost) {
            imageUrl = ((VideoPost) postBase).getCoverUrl();
        } else if (postBase instanceof ForwardPost) {
            PostBase rootPost = ((ForwardPost) postBase).getRootPost();
            if (rootPost instanceof PicPost) {
                PicInfo picInfo = ((PicPost) rootPost).getPicInfo(0);
                imageUrl = picInfo.getPicUrl();
            } else if (rootPost instanceof VideoPost) {
                imageUrl = ((VideoPost) rootPost).getCoverUrl();
            }
        }
        if (TextUtils.isEmpty(imageUrl)) {
            imageUrl = postBase.getHeadUrl();
        }
        return imageUrl;
    }

    public static String getShareVideoUrl(PostBase post) {
        if (post == null) {
            return "";
        }
        if(post instanceof VideoPost) {
            String url = ((VideoPost) post).getDownloadUrl();
            if (!TextUtils.isEmpty(url)) {
                return url;
            }
            url = ((VideoPost) post).getVideoUrl();
            if (!TextUtils.equals(((VideoPost) post).getVideoSite(), PlayerConstant.VIDEO_SITE_YOUTUBE)) {
                return url;
            }
            return "";
        } else if (post instanceof ForwardPost) {
            PostBase rootPost = ((ForwardPost) post).getRootPost();
            if(rootPost instanceof VideoPost) {
                String url = ((VideoPost) rootPost).getDownloadUrl();
                if (!TextUtils.isEmpty(url)) {
                    return url;
                }
                url = ((VideoPost) rootPost).getVideoUrl();
                if (!TextUtils.equals(((VideoPost) rootPost).getVideoSite(), PlayerConstant.VIDEO_SITE_YOUTUBE)) {
                    return url;
                }
                return "";
            }
        }
        return "";
    }

    public static String getShareProfileImage(User user) {
        if(user == null) {
            return "";
        } else {
            return user.getHeadUrl();
        }
    }

    public static String getShareAppImage() {
        return SHARE_APP_IMAGE;
    }

    public static String getShareTopicImage() {
        return SHARE_APP_IMAGE;
    }

    public static String getShareTopicName(PostBase post) {
        TopicCardInfo info = post.getTopicCardInfo();
        if (info == null) {
            List<RichItem> richItemList = post.getRichItemList();
            if (CollectionUtil.isEmpty(richItemList)) {
                return null;
            }
            for (RichItem item : richItemList) {
                if (item.isTopicItem()) {
                    return TextUtils.isEmpty(item.display) ? item.source : item.display;
                }
            }
            return null;
        }
        return info.topicName;
    }

    public static SharePackageFactory.SharePackage getSharePackage(String platform) {
        if(TextUtils.isEmpty(platform)) {
            return null;
        }

        final String PLAT_FROM_WHATSAPP = "whatsapp";
        final String PLAT_FROM_FACE_BOOK = "facebook";
        final String PLAT_FROM_INSTAGRAM = "instagram";
        final String PLAT_FROM_ALL = "all";
        if(platform.equalsIgnoreCase(PLAT_FROM_WHATSAPP)) {
            return SharePackageFactory.SharePackage.WHATS_APP;
        } else if(platform.equalsIgnoreCase(PLAT_FROM_FACE_BOOK)) {
            return SharePackageFactory.SharePackage.FACEBOOK;
        } else if(platform.equalsIgnoreCase(PLAT_FROM_INSTAGRAM)) {
            return SharePackageFactory.SharePackage.INSTAGTRAM;
        } else if(platform.equalsIgnoreCase(PLAT_FROM_ALL)) {
            return null;
        } else {
            return null;
        }
    }

    public static void sharePost(Context context, PostBase postBase, boolean video, EventModel eventModel) {
        sharePostToChannel(context, postBase, video, null, eventModel);
    }

    public static void sharePostWithCustomMenu(Context context, PostBase postBase, boolean video, EventModel eventModel, List<SharePackageModel> menuList) {
        sharePostToChannel(context, postBase, video, null, eventModel, menuList);
    }

    public static void sharePostToWhatsApp(Context context, PostBase postBase, boolean video, EventModel eventModel) {
        sharePostToChannel(context, postBase, video, SharePackageFactory.SharePackage.WHATS_APP, eventModel);
    }

    public static void sharePostToChannel(Context context, PostBase postBase, boolean video, SharePackageFactory.SharePackage sharePackage, EventModel eventModel) {
        sharePostToChannel(context, postBase, video, sharePackage, eventModel, null);
    }
    public static void sharePostToChannel(Context context, PostBase postBase, boolean video, SharePackageFactory.SharePackage sharePackage, EventModel eventModel, List<SharePackageModel> menuList) {
        ShareModel shareModel = new ShareModel();
        shareModel.setImageUrl(getSharePostImage(postBase));
        shareModel.setVideoUrl(getShareVideoUrl(postBase));
        shareModel.setShareModelType(ShareModel.SHARE_MODEL_TYPE_POST);
        ShareManagerWrapper.Builder builder = new ShareManagerWrapper.Builder();
        builder.with(context)
                .login(AccountManager.getInstance().isLoginComplete())
                .sharePackage(sharePackage)
                .shareModel(shareModel)
                .eventModel(eventModel)
                .shareVideo(video)
                .shortLinkModel(new ShortLinkModel(ShareModel.SHARE_MODEL_TYPE_POST, postBase.getPid()))
                .shareTitleModel(new ShareTitleModel(ShareModel.SHARE_MODEL_TYPE_POST, postBase.getNickName()))
                .waterMarkModel(new WaterMarkModel(postBase.getHeadUrl(), postBase.getNickName(), ShareHelper.getShareTopicName(postBase)))
                .addMenuList(menuList)
                .build().share();
        NewShareEventManager.INSTANCE.setPostType(postBase.getType());
    }

    public static void shareImageAttachment(Context context, ImageAttachment image, EventModel eventModel) {
        ShareModel shareModel = new ShareModel();
        shareModel.setImageUrl(image.getImageUrl());
        shareModel.setShareModelType(ShareModel.SHARE_MODEL_TYPE_POST);
        ShareManagerWrapper.Builder builder = new ShareManagerWrapper.Builder();
        builder.with(context)
                .login(AccountManager.getInstance().isLoginComplete())
                .shareModel(shareModel)
                .eventModel(eventModel)
                .shortLinkModel(new ShortLinkModel(ShareModel.SHARE_MODEL_TYPE_POST, image.getPid()))
                .shareTitleModel(new ShareTitleModel(ShareModel.SHARE_MODEL_TYPE_POST, image.getNickName()))
                .waterMarkModel(new WaterMarkModel(image.getHeadUrl(), image.getNickName(), null))
                .build().share();
    }

}
