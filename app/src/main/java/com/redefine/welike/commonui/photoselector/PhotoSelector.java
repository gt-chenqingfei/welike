package com.redefine.welike.commonui.photoselector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.multimedia.photoselector.activity.PhotoPreviewActivity;
import com.redefine.multimedia.photoselector.activity.PhotoSelectorActivity;
import com.redefine.multimedia.photoselector.config.ImagePickConfig;
import com.redefine.multimedia.photoselector.constant.ImagePickConstant;
import com.redefine.multimedia.photoselector.entity.Item;
import com.redefine.multimedia.photoselector.entity.MimeType;
import com.redefine.multimedia.photoselector.model.SelectedItemCollection;
import com.redefine.multimedia.picturelooker.config.PictureConfig;
import com.redefine.multimedia.player.VideoPlayerActivity;
import com.redefine.multimedia.player.constant.PlayerConstant;
import com.redefine.welike.base.constant.CommonRequestCode;
import com.redefine.welike.business.feeds.management.bean.PicInfo;
import com.redefine.welike.event.protocol.BuildInProtocolParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MR on 2018/1/17.
 */

public class PhotoSelector {

    public static void launchPhotoSelectorForPublishPost(Activity context, ArrayList<Item> list) {
        Intent intent = new Intent();
        intent.setClass(context, PhotoSelectorActivity.class);
        intent.putExtra(ImagePickConstant.EXTRA_IMAGE_PICK_CONFIG, ImagePickConfig.get().setMimeTypeSet(MimeType.ofAll())
                .setShowSingleMediaType(false).setIsCutPhoto(false).setCapture(true).setCountable(true));
        intent.putParcelableArrayListExtra(SelectedItemCollection.STATE_SELECTION, list);
        context.startActivityForResult(intent, CommonRequestCode.EDITOR_CHOOSE_PIC_CODE);
    }

    public static void launchPhotoSelectorForPoll(Activity context, Bundle bundle) {

        Intent intent = new Intent();
        intent.setClass(context, PhotoSelectorActivity.class);
        intent.putExtras(bundle);
        intent.putExtra(ImagePickConstant.EXTRA_IMAGE_PICK_CONFIG, ImagePickConfig.get().setCountable(true).setCapture(true)
                .setIsCutPhoto(true).setMaxImageSelectable(1).setShowSingleMediaType(true).setMimeTypeSet(MimeType.ofStaticImage()).setAspectRatioX(4).setAspectRatioY(3));
        context.startActivityForResult(intent, CommonRequestCode.EDITOR_POLL_CHOOSE_PIC_CODE);
    }

    public static void launchPhotoSelectorForPoll1(Activity context) {

        Intent intent = new Intent();
        intent.setClass(context, PhotoSelectorActivity.class);
//        intent.putExtras(bundle);
        intent.putExtra(ImagePickConstant.EXTRA_IMAGE_PICK_CONFIG, ImagePickConfig.get().setCountable(true).setCapture(true)
                .setIsCutPhoto(false).setMaxImageSelectable(1).setShowSingleMediaType(true).setMimeTypeSet(MimeType.ofStaticImage()).setAspectRatioX(4).setAspectRatioY(3));
        context.startActivityForResult(intent, CommonRequestCode.EDITOR_POLL_CHOOSE_PIC_CODE);
    }

    public static void launchRegisterChooseUserHeaderSelect(Activity context) {
        Intent intent = new Intent();
        intent.setClass(context, PhotoSelectorActivity.class);
        intent.putExtra(ImagePickConstant.EXTRA_IMAGE_PICK_CONFIG, ImagePickConfig.get()
                .setShowSingleMediaType(true).setMimeTypeSet(MimeType.ofStaticImage())
                .setMaxImageSelectable(1).setIsCutPhoto(true).setCapture(true).setCountable(true));
        context.startActivityForResult(intent, CommonRequestCode.REGISTER_CHOOSE_HEADER_PIC_CODE);
    }

    public static void launchChooseUserHeaderSelect(Activity context) {
        Intent intent = new Intent();
        intent.setClass(context, PhotoSelectorActivity.class);
        intent.putExtra(ImagePickConstant.EXTRA_IMAGE_PICK_CONFIG, ImagePickConfig.get()
                .setShowSingleMediaType(true).setMimeTypeSet(MimeType.ofStaticImage())
                .setMaxImageSelectable(1).setIsCutPhoto(true).setCapture(true).setCountable(true));
        context.startActivityForResult(intent, CommonRequestCode.USER_HOST_CHOOSE_PIC_CODE);
    }

    public static void launchChooseReportPicSelect(Activity context, ArrayList<Item> list) {
        Intent intent = new Intent();
        intent.setClass(context, PhotoSelectorActivity.class);
        intent.putExtra(ImagePickConstant.EXTRA_IMAGE_PICK_CONFIG, ImagePickConfig.get()
                .setShowSingleMediaType(true).setMimeTypeSet(MimeType.ofStaticImage())
                .setMaxImageSelectable(4).setIsCutPhoto(false).setCapture(true).setCountable(true));
        intent.putParcelableArrayListExtra(SelectedItemCollection.STATE_SELECTION, list);
        context.startActivityForResult(intent, CommonRequestCode.CHOSEN_REPORT_PIC_CODE);
    }

    public static void launchPlayPostVideo(Activity activity, String url) {
        launchPlayPostVideo(activity, url, PlayerConstant.VIDEO_SITE_DEFAULT);
    }

    public static void launchPlayPostVideo(Activity activity, String url, String videoSite) {
        Intent intent = new Intent(activity, VideoPlayerActivity.class);
        intent.putExtra(PlayerConstant.MEDIA_PLAYER_VIDEO_PATH, url);
        intent.putExtra(PlayerConstant.MEDIA_PLAYER_VIDEO_SOURCE, videoSite);
        activity.overridePendingTransition(com.redefine.commonui.R.anim.sliding_right_in, com.redefine.commonui.R.anim.sliding_to_left_out);
        activity.startActivity(intent);
    }

    public static void launchPlayPostVideo(Activity mContext, String nick, String videoCover, String videoSource, String videoUrl, String postId, String postUid, String rootPostId, String rootPostUid, String playSource, String userHeader, String userNick) {
        VideoPlayerActivity.launch(mContext, nick, videoCover, videoSource, videoUrl, postId, postUid, rootPostId, rootPostUid, playSource, userHeader, userNick);
//        Intent intent = new Intent(mActivity, VideoPlayerActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putString(PlayerConstant.MEDIA_PLAYER_VIDEO_PATH, videoUrl);
//        bundle.putString(PlayerConstant.MEDIA_PLAYER_VIDEO_SOURCE, videoSource);
//        bundle.putString(PlayerConstant.MEDIA_PLAYER_VIDEO_NICK, nick);
//
//        bundle.putString(PlayerConstant.POST_ID, postId);
//        bundle.putString(PlayerConstant.POST_UID, postUid);
//        bundle.putString(PlayerConstant.POST_USER_HEADER, userHeader);
//        bundle.putString(PlayerConstant.POST_USER_NICK, userNick);
//        bundle.putString(PlayerConstant.ROOT_POST_ID, rootPostId);
//        bundle.putString(PlayerConstant.ROOT_POST_UID, rootPostUid);
//        bundle.putString(PlayerConstant.PLAY_SOURCE, playSource);
//        bundle.putString(PlayerConstant.MEDIA_PLAYER_VIDEO_COVER, videoCover);
//        intent.putExtras(bundle);
//        mActivity.startActivity(intent);
    }

    public static void previewPics(Context context, String nickName, int position, List<PicInfo> lists) {
        Activity activity = null;
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
        if (activity == null) {
            return;
        }
        if (CollectionUtil.isEmpty(lists)) {
            return;
        }
        ArrayList<Item> items = new ArrayList<>();
        Item media;
        for (PicInfo p : lists) {
            media = new Item(Uri.parse(p.getPicUrl()), p.getPicUrl(), MimeType.JPEG.toString(), 0, 0, p.getWidth(), p.getHeight());
            items.add(media);
        }
//        PictureSelector.create(activity).externalPicturePreview(position, localMedias);
        launchPreview(nickName, position, items, activity);
    }

    public static void previewSinglePic(Context context, String nickName, String headUrl) {
        Activity activity = null;
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
        if (activity == null) {
            return;
        }
        if (TextUtils.isEmpty(headUrl)) {
            return;
        }
        ArrayList<Item> items = new ArrayList<>();
        Item item = new Item(Uri.parse(headUrl), headUrl, MimeType.JPEG.toString(), 0, 0, 0, 0);
        items.add(item);
        launchPreview(nickName, 0, items, activity);
    }

    public static void launchPreview(String nickName, int position, ArrayList<Item> items, Activity context) {
        launchPreview(true, nickName, position, items, context);

    }

    public static void launchPreview(boolean isSavePhoto, String nickName, int position, ArrayList<Item> items, Activity context) {
        Bundle bundle = new Bundle();

        bundle.putInt(PictureConfig.EXTRA_POSITION, position);
        bundle.putString(PictureConfig.EXTRA_NICK_NAME, nickName);
        bundle.putBoolean(PictureConfig.EXTRA_ENABLE_SAVE_PHOTO, isSavePhoto);

        bundle.putParcelableArrayList(PictureConfig.EXTRA_PREVIEW_LIST, items);
        PhotoPreviewActivity.launcher(context, bundle);
    }

    /**
     *  @param context
     * @param mediaType 0 all， 1图片，2视频
     * @param maxSelectCount
     * @param isCrop
     */
    public static void launchWebFileChooser(Activity context, BuildInProtocolParser.MediaType mediaType, int maxSelectCount, boolean isCrop) {
        ImagePickConfig config = ImagePickConfig.get().setCountable(true).setCapture(true)
                .setIsCutPhoto(isCrop).setMaxImageSelectable(maxSelectCount);
        if (mediaType == BuildInProtocolParser.MediaType.ALL) {
            config.setShowSingleMediaType(false).setMimeTypeSet(MimeType.ofAll()).setAspectRatioX(1).setAspectRatioY(1);
        } else if (mediaType == BuildInProtocolParser.MediaType.IMAGE) {
            config.setShowSingleMediaType(true).setMimeTypeSet(MimeType.ofStaticImage()).setAspectRatioX(1).setAspectRatioY(1);
        } else {
            config.setShowSingleMediaType(true).setMimeTypeSet(MimeType.ofVideo()).setAspectRatioX(1).setAspectRatioY(1);
        }
        Intent intent = new Intent();
        intent.setClass(context, PhotoSelectorActivity.class);
        intent.putExtra(ImagePickConstant.EXTRA_IMAGE_PICK_CONFIG, config);
        context.startActivityForResult(intent, CommonRequestCode.WEB_CHOOSE_PIC_CODE);
    }

}
