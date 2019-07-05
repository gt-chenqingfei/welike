package com.redefine.multimedia.player;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.redefine.commonui.download.DownloadManager;
import com.redefine.commonui.enums.DownloadStateEnum;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.commonui.download.MediaDownloadManager;
import com.redefine.multimedia.player.base.VideoViewType;
import com.redefine.multimedia.player.constant.PlayerConstant;
import com.redefine.multimedia.player.request.YoutubeApiManager;
import com.redefine.multimedia.player.request.YoutubeVideoInfo;
import com.redefine.multimedia.player.request.YoutubeVideoItem;
import com.redefine.multimedia.player.youtube.YouTubeUrlParser;
import com.redefine.welike.base.ErrorCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VideoPlayerViewModel extends AndroidViewModel implements DownloadManager.IDownloadListener {

    private MutableLiveData<VideoViewType> mViewType = new MediatorLiveData<>();
    private MutableLiveData<String> mVideoUrl = new MediatorLiveData<>();
    private MutableLiveData<PageStatusEnum> mPageStatus = new MediatorLiveData<>();
    private MutableLiveData<DownloadStateEnum> mDownloadStatus = new MediatorLiveData<>();
    private MutableLiveData<Integer> mDownloadProgress = new MediatorLiveData<>();

    public final static String[] YOUTUBE_QUALITY_HEIGHT = {"22", "84"};
    public final static String[] YOUTUBE_QUALITY_MEDIUM = {"18", "82", "83"};
    public final static String[] YOUTUBE_QUALITY_LOW = {"17", "36"};
    private int mLastProgress;

    public VideoPlayerViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<VideoViewType> getViewType() {
        return mViewType;
    }

    public MutableLiveData<String> getVideoUrl() {
        return mVideoUrl;
    }

    public MutableLiveData<PageStatusEnum> getPageStatus() {
        return mPageStatus;
    }

    public MutableLiveData<DownloadStateEnum> getDownloadStatus() {
        return mDownloadStatus;
    }

    public MutableLiveData<Integer> getDownloadProgress() {
        return mDownloadProgress;
    }

    public void setVideoUrlAndSource(final String videoPath, String videoSource) {

        if (TextUtils.equals(videoSource, PlayerConstant.VIDEO_SITE_DEFAULT)) {
            // 默认链接直接播放
            mPageStatus.postValue(PageStatusEnum.CONTENT);
            mViewType.postValue(VideoViewType.APOLLO);
            mVideoUrl.postValue(videoPath);
            MediaDownloadManager.getInstance().retryBindDownloadTask(videoPath, this);
        } else if (TextUtils.equals(videoSource, PlayerConstant.VIDEO_SITE_YOUTUBE)) {
            // 做youtube链接解析和
            final String youtubeId = YouTubeUrlParser.getVideoId(videoPath);
            mPageStatus.postValue(PageStatusEnum.LOADING);
            YoutubeApiManager.getYoutubeUrl(getApplication(), YouTubeUrlParser.getVideoId(videoPath), new YoutubeApiManager.IRequestCallback() {
                @Override
                public void onError(int errCode) {
                    if (errCode == ErrorCode.ERROR_NETWORK_INVALID) {
                        mPageStatus.postValue(PageStatusEnum.ERROR);
                    } else {
                        mPageStatus.postValue(PageStatusEnum.CONTENT);
                        mViewType.postValue(VideoViewType.WEB);
                        mVideoUrl.postValue("https://www.youtube.com/watch?v=" + youtubeId);
                    }
                }

                @Override
                public void onSuccess(YoutubeVideoInfo result) {
                    if (CollectionUtil.isEmpty(result.files)) {
                        mPageStatus.postValue(PageStatusEnum.CONTENT);
                        mViewType.postValue(VideoViewType.WEB);
                        mVideoUrl.postValue("https://www.youtube.com/watch?v=" + youtubeId);
                    } else {
                        YoutubeVideoItem item = findVideoItemByQuality(result.files, YOUTUBE_QUALITY_MEDIUM);
                        if (item == null) {
                            item = findVideoItemByQuality(result.files, YOUTUBE_QUALITY_LOW);
                        }
                        if (item == null) {
                            item = findVideoItemByQuality(result.files, YOUTUBE_QUALITY_HEIGHT);
                        }
                        if (item != null) {
                            mPageStatus.postValue(PageStatusEnum.CONTENT);
                            mViewType.postValue(VideoViewType.APOLLO);
                            mVideoUrl.postValue(item.url);
                            MediaDownloadManager.getInstance().retryBindDownloadTask(item.url,VideoPlayerViewModel.this);
                        } else {
                            mPageStatus.postValue(PageStatusEnum.CONTENT);
                            mViewType.postValue(VideoViewType.WEB);
                            mVideoUrl.postValue("https://www.youtube.com/watch?v=" + youtubeId);
                        }
                    }
                }
            });
        }
    }


    YoutubeVideoItem findVideoItemByQuality(List<YoutubeVideoItem> list, String[] quality) {
        Set<String> stringSet = new HashSet<>(Arrays.asList(quality));
        List<YoutubeVideoItem> result = new ArrayList<>();
        for (YoutubeVideoItem item : list) {
            if (stringSet.contains(String.valueOf(item.quality))) {
                result.add(item);
            }
        }

        if (!CollectionUtil.isEmpty(result)) {
            // find mp4
            for (YoutubeVideoItem item : result) {
                if (TextUtils.equals(item.format, "MP4")) {
                    return item;
                }
            }
            for (YoutubeVideoItem item : result) {
                if (TextUtils.equals(item.format, "3GP")) {
                    return item;
                }
            }
            return result.get(0);
        }
        return null;
    }

    public void downloadVideo(String url, String userNick) {
        boolean isSuccess = MediaDownloadManager.getInstance().download(getApplication(), url, userNick, this);
        if (!isSuccess) {
            mDownloadStatus.postValue(DownloadStateEnum.ERROR);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        MediaDownloadManager.getInstance().unRegisterListener(mVideoUrl.getValue(), this);
    }

    @Override
    public void onDownloadStart(String url, String filePath) {
        mLastProgress = 0;
        mDownloadStatus.postValue(DownloadStateEnum.START);
    }

    @Override
    public void onDownloadSuccess(String url, String filePath) {
        mDownloadStatus.postValue(DownloadStateEnum.SUCCESS);
    }

    @Override
    public void onDownloadError(String url, String filePath) {
        mDownloadStatus.postValue(DownloadStateEnum.ERROR);

    }

    @Override
    public void onDownloadCancel(String url, String filePath) {
        mDownloadStatus.postValue(DownloadStateEnum.CANCELED);

    }

    @Override
    public void onDownloadProgress(String url, String filePath, long currentOffset, long totalLength) {
        if (totalLength == 0) {
            return ;
        }
        int progress = (int) (currentOffset * 100 / totalLength);

        mLastProgress = Math.max(mLastProgress, progress);
        mDownloadProgress.postValue(mLastProgress);
    }
}
