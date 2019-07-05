package com.redefine.multimedia.player;

import android.text.TextUtils;

/**
 * Created by nianguowang on 2018/8/2
 */
public class UrlFileTypeUtil {

    public static final String FILE_TYPE_M3U8 = ".m3u8";

    private UrlFileTypeUtil() {}

    /**
     * 根据url获取文件的后缀名，例如：URL：http://pre-file.welike.in/download/video-006c7287ae93415cba4de717a9219d37.mp4/  返回后缀名.mp4
     * 如果不满足该格式，则返回""，例如： https://events.appsflyer.com/api/v4/androidevent?buildnumber=4.8.13&app_id=com.redefine.welike  返回""
     * @param url
     * @return
     */
    public static String getFileType(String url) {
        try {
            String[] urlArray = url.split("/");
            String fileName = urlArray[urlArray.length - 1];
            int index = fileName.lastIndexOf(".");
            if (index > 0) {
                String imageType = fileName.substring(index, fileName.length());
                imageType = TextUtils.isEmpty(imageType) ? ".png" : imageType.toLowerCase();
                return imageType;
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 返回URL是否是m3u8格式的文件
     * @param url
     * @return
     */
    public static boolean isM3U8Type(String url) {
        String fileType = getFileType(url);
        return TextUtils.equals(fileType.toLowerCase(), FILE_TYPE_M3U8);
    }
}
