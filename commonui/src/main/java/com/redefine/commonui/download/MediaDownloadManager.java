package com.redefine.commonui.download;

import android.content.Context;
import android.text.TextUtils;

import com.liulishuo.okdownload.OkDownload;
import com.redefine.commonui.share.CommonListener;
import com.redefine.foundation.utils.CommonHelper;
import com.redefine.foundation.utils.MD5Helper;
import com.redefine.welike.base.io.WeLikeFileManager;

import java.io.File;

public class MediaDownloadManager {

    private final DownloadManager mDownloadManager;

    private MediaDownloadManager() {
        mDownloadManager = new DownloadManager();
    }

    public void unRegisterListener(String url, DownloadManager.IDownloadListener iDownloadListener) {
        mDownloadManager.unRegisterListener(url, iDownloadListener);
    }

    private static class MediaDownloadManagerHolder {

        private static MediaDownloadManager INSTANCE = new MediaDownloadManager();

        private MediaDownloadManagerHolder() {

        }
    }

    public static MediaDownloadManager getInstance() {
        return MediaDownloadManagerHolder.INSTANCE;
    }

    public boolean download(final Context applicationContext, final String url, final String userNick, final DownloadManager.IDownloadListener downloadListener) {

        final File file = getDownloadFilePath(url);
        String filePath = file == null ? "" : file.getAbsolutePath();
        mDownloadManager.download(applicationContext, url, filePath, new IDownloadIntercept() {
            @Override
            public void intercept(String url, final String filePath, final CommonListener<String> commonListener) {
//                boolean isSuccess = WaterMarkUtil.markVideo(applicationContext, filePath, userNick, new IWaterProcessor.IProcessorCallback() {
//                    @Override
//                    public void ProcessCallback(boolean b, String s, int i, int i1, long l) {
//                        if (b) {
//                            new File(filePath).deleteOnExit();
//                            new File(s).renameTo(new File(filePath));
//                        } else {
//                            new File(s).deleteOnExit();
//                        }
//                        WeLikeFileManager.refreshGallery(applicationContext, filePath);
//                        commonListener.onFinish(filePath);
//                    }
//                });
//
//                if (!isSuccess) {
//                    commonListener.onFinish(filePath);
//                }
                commonListener.onFinish(filePath);
            }
        }, downloadListener);
        return true;
    }


    public File getDownloadFilePath(String url) {
        String fileName = OkDownload.with().breakpointStore().getResponseFilename(url);
        final File file;
        if (!TextUtils.isEmpty(fileName)) {
            file = WeLikeFileManager.getPhotoSaveFile(fileName);
        } else {
            file = WeLikeFileManager.getPhotoSaveFile(getDownloadFileName(url));
        }
        return file;
    }

    public DownloadState getDownloadState(String url) {
        final File file = getDownloadFilePath(url);
        String filePath = file == null ? "" : file.getAbsolutePath();
        return mDownloadManager.getDownloadState(url, filePath);
    }

    private String getDownloadFileName(String url) {
        String fileName;
        String suffix = getLastVideoType(url);
        try {
            fileName = WeLikeFileManager.WELIKE_PREFFIX + MD5Helper.md5(url) + suffix;
            return fileName;
        } catch (Exception e) {
            // do nothing
        }
        return CommonHelper.generateUUID() + suffix;
    }

    public void retryBindDownloadTask(String url, DownloadManager.IDownloadListener downloadListener) {
        File file = getDownloadFilePath(url);
        String filePath = file == null ? "" : file.getAbsolutePath();
        mDownloadManager.retryBindDownloadTask(url, filePath, downloadListener);
    }

    /**
     * 获取图片后缀
     *
     * @param path
     * @return
     */
    public static String getLastVideoType(String path) {
        try {
            String[] urlArray = path.split("/");
            String fileName = urlArray[urlArray.length - 1];
            int index = fileName.lastIndexOf(".");
            if (index > 0) {
                String imageType = fileName.substring(index, fileName.length());
                imageType = TextUtils.isEmpty(imageType) ? ".mp4" : imageType.toLowerCase();
                switch (imageType) {
                    case ".mp4":
                    case ".mpeg":
                    case ".mpg":
                    case ".m4v":
                    case ".mov":
                    case ".3gp":
                    case ".3gpp":
                    case ".3g2":
                    case ".3gpp2":
                    case ".mkv":
                    case ".webm":
                    case ".ts":
                    case ".avi":
                        return imageType;
                    default:
                        return ".mp4";
                }
            } else {
                return ".mp4";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ".mp4";
        }
    }
}
