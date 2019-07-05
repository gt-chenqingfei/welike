package com.redefine.welike.commonui.share.interceptor;

import android.content.Context;
import android.text.TextUtils;

import com.redefine.commonui.download.DownloadManager;
import com.redefine.commonui.download.WhatsAppMediaDownloadManager;
import com.redefine.commonui.share.SharePackageFactory;
import com.redefine.commonui.share.interceptor.AbstractInterceptor;
import com.redefine.commonui.share.sharemedel.ShareModel;

import io.reactivex.schedulers.Schedulers;

public class VideoDownloadInterceptor extends AbstractInterceptor {

    private final Context mContext;
//    private final WaterMarkModel mWaterMarkModel;
    private String mUrl;

    public VideoDownloadInterceptor(Context context/*, WaterMarkModel waterMarkModel*/) {
        mContext = context;
//        mWaterMarkModel = waterMarkModel;
    }

    @Override
    public void handle(final ShareModel shareModel, final SharePackageFactory.SharePackage channel) {
        if (/*mWaterMarkModel == null || */shareModel == null || TextUtils.isEmpty(shareModel.getVideoUrl())) {
            doNext(shareModel, channel);
            return ;
        }
        mUrl = shareModel.getVideoUrl();
        boolean isSuccess = WhatsAppMediaDownloadManager.getInstance().download(mContext, mUrl, /*mWaterMarkModel.getUserNick(),*/ new DownloadManager.IDownloadListener() {
            @Override
            public void onDownloadStart(String url, String filePath) {
                if (mCallback != null) {
                    mCallback.showDialog();
                }
            }

            @Override
            public void onDownloadSuccess(String url, final String filePath) {
                Schedulers.newThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        shareModel.setVideoPath(filePath);
                        shareModel.setImageUrl(null);
                        doNext(shareModel, channel);
                    }
                });
                if (mCallback != null) {
                    mCallback.dismissDialog();
                }
            }

            @Override
            public void onDownloadError(String url, String filePath) {
                Schedulers.newThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        doNext(shareModel, channel);
                    }
                });
                if (mCallback != null) {
                    mCallback.dismissDialog();
                }
            }

            @Override
            public void onDownloadCancel(String url, String filePath) {
                Schedulers.newThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        doNext(shareModel, channel);
                    }
                });
                if (mCallback != null) {
                    mCallback.dismissDialog();
                }
            }

            @Override
            public void onDownloadProgress(String url, String filePath, long currentOffset, long totalLength) {
                if (mCallback != null) {
                    mCallback.updateProgress((int) (currentOffset * 100 / totalLength));
                }
            }
        });

        if (!isSuccess) {
            doNext(shareModel, channel);
        }
    }

    @Override
    public void cancel() {
        super.cancel();
        WhatsAppMediaDownloadManager.getInstance().cancelDownloadTask(mUrl);
    }
}
