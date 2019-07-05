package com.redefine.multimedia.picturelooker.config;


import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.multimedia.photoselector.entity.Item;

import java.io.File;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.lib.config
 * email：893855882@qq.com
 * data：2017/5/24
 */

public final class PictureMimeType {
    /**
     * 是否是gif
     *
     * @param pictureType
     * @return
     */
    public static boolean isGif(String pictureType) {
        switch (pictureType) {
            case "image/gif":
            case "image/GIF":
                return true;
        }
        return false;
    }


    /**
     * 是否是网络图片
     *
     * @param path
     * @return
     */
    public static boolean isHttp(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (path.startsWith("http")
                    || path.startsWith("https")) {
                return true;
            }
        }
        return false;
    }


    /**
     * 是否是长图
     *
     * @param width
     * @param height
     * @return true 是 or false 不是
     */
    public static boolean isLongImg(int width, int height, Context context) {

        int sWidth = ScreenUtils.getSreenWidth(context);
        int sHeight = ScreenUtils.getScreenHeight(context);

        return height * sWidth > sHeight * width;
    }

    /**
     * 获取图片后缀
     *
     * @param path
     * @return
     */
    public static String getLastImgType(String path) {
        try {
            String[] urlArray = path.split("/");
            String fileName = urlArray[urlArray.length - 1];
            int index = fileName.lastIndexOf(".");
            if (index > 0) {
                String imageType = fileName.substring(index, fileName.length());
                imageType = TextUtils.isEmpty(imageType) ? ".png" : imageType.toLowerCase();
                switch (imageType) {
                    case ".png":
                    case ".jpg":
                    case ".jpeg":
                    case ".bmp":
                    case ".gif":
                    case ".webp":
                        return imageType;
                    default:
                        return ".png";
                }
            } else {
                return ".png";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ".png";
        }
    }

}
