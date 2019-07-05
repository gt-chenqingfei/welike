package com.redefine.welike.business.videoplayer.ui.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.commonui.download.DownloadManager;
import com.redefine.commonui.download.MediaDownloadManager;
import com.redefine.commonui.enums.DownloadStateEnum;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.multimedia.player.constant.PlayerConstant;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.browse.management.bean.Interest;
import com.redefine.welike.business.browse.management.dao.InterestCallBack;
import com.redefine.welike.business.browse.management.dao.InterestEventStore;
import com.redefine.welike.business.browse.management.request.SimilarVideoRequest;
import com.redefine.welike.business.browse.management.request.SimilarVideoRequest1;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.bean.VideoPost;
import com.redefine.welike.business.feeds.management.parser.PostsDataSourceParser;
import com.redefine.welike.business.feeds.ui.util.FeedHelper;
import com.redefine.welike.business.videoplayer.management.VideoPlayerConstants;
import com.redefine.welike.business.videoplayer.management.manager.VideoPostsManager;
import com.redefine.welike.business.videoplayer.management.provider.IVideoPostsProvider;
import com.redefine.welike.business.videoplayer.management.provider.ProfileVideoPostProvider;
import com.redefine.welike.business.videoplayer.management.provider.SimilarVideoPostsProvider;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.manager.PostEventManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nianguowang on 2018/8/7
 */
public class VideoListPlayerViewModel extends AndroidViewModel implements DownloadManager.IDownloadListener, VideoPostsManager.VideoPostsCallback {

    private boolean mCanLoadMore = true;
    private int mLastProgress;
    private String mDownloadUrl;
    private boolean mAuth;

    private MutableLiveData<PageStatusEnum> mPageStatus = new MutableLiveData<>();
    private MutableLiveData<List<VideoPost>> mVideoPostLiveData = new MutableLiveData<>();
    private MutableLiveData<DownloadStateEnum> mDownloadStatus = new MediatorLiveData<>();
    private MutableLiveData<Integer> mDownloadProgress = new MediatorLiveData<>();


    public VideoListPlayerViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<VideoPost>> getVideoPostLiveData() {
        return mVideoPostLiveData;
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

    public void init(List<VideoPost> videoPosts, PostBase postBase, boolean auth, String videoListType) {
        if (postBase == null || !(postBase instanceof VideoPost)) {
            mPageStatus.postValue(PageStatusEnum.ERROR);
            return;
        }

        mAuth = auth;
        IVideoPostsProvider provider = null;
        if (TextUtils.equals(VideoPlayerConstants.VIDEO_LIST_TYPE_SIMILAR, videoListType)) {
            provider = new SimilarVideoPostsProvider();
        } else if (TextUtils.equals(VideoPlayerConstants.VIDEO_LIST_TYPE_PROFILE, videoListType)) {
            provider = new ProfileVideoPostProvider();
        }

        if (provider != null) {
            provider.init(postBase, auth);
            VideoPostsManager.getInstance().setVideoPostsProvider(provider);
            VideoPostsManager.getInstance().setListener(this);
        }

        List<VideoPost> value = mVideoPostLiveData.getValue();
        if (value == null) {
            value = new ArrayList<>();
        }
        value.clear();
        value.addAll(videoPosts);
        mVideoPostLiveData.postValue(value);
    }

    public void refresh() {
        VideoPostsManager.getInstance().refresh();
    }

    public void loadMore() {
        if (mCanLoadMore) {
            VideoPostsManager.getInstance().hisPosts();
        }
    }

    @Override
    public void onRefreshVideoPosts(VideoPostsManager manager, List<PostBase> postBases, int newCount, boolean last, int errCode) {
        mCanLoadMore = !last;
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            ArrayList<VideoPost> videoPosts = filterVideoPost(postBases);
            if (!CollectionUtil.isEmpty(videoPosts)) {
                List<VideoPost> value = mVideoPostLiveData.getValue();
                if (value == null) {
                    value = new ArrayList<>();
                }
                value.addAll(videoPosts);
                mVideoPostLiveData.postValue(value);
            }
        } else {
            mPageStatus.postValue(PageStatusEnum.ERROR);
        }
        if (mAuth) {
            PostEventManager.INSTANCE.setType(EventConstants.FEED_PAGE_DISCOVER_VIDEO_AFTER);
            PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_DISCOVER_VIDEO_AFTER);
        } else {
            PostEventManager.INSTANCE.setType(EventConstants.FEED_PAGE_UNLOGIN_VIDEO_AFTER);
            PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_UNLOGIN_VIDEO_AFTER);
        }
        PostEventManager.INSTANCE.setAction(EventConstants.AUTO_REFRESH);
        PostEventManager.INSTANCE.sendEventStrategy(FeedHelper.subPosts(postBases, newCount));

    }

    @Override
    public void onHisVideoPosts(VideoPostsManager manager, List<PostBase> postBases, boolean last, int errCode) {
        mCanLoadMore = !last;
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            ArrayList<VideoPost> videoPosts = filterVideoPost(postBases);
            if (!CollectionUtil.isEmpty(videoPosts)) {
                List<VideoPost> value = mVideoPostLiveData.getValue();
                if (value == null) {
                    value = new ArrayList<>();
                }
                value.addAll(videoPosts);
                mVideoPostLiveData.postValue(value);
            }
        } else {
            mPageStatus.postValue(PageStatusEnum.ERROR);
        }
        if (mAuth) {
            PostEventManager.INSTANCE.setType(EventConstants.FEED_PAGE_DISCOVER_VIDEO_AFTER);
            PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_DISCOVER_VIDEO_AFTER);
        } else {
            PostEventManager.INSTANCE.setType(EventConstants.FEED_PAGE_UNLOGIN_VIDEO_AFTER);
            PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_UNLOGIN_VIDEO_AFTER);
        }
        PostEventManager.INSTANCE.setAction(EventConstants.PULL_UP_REFRESH);
        PostEventManager.INSTANCE.sendEventStrategy(postBases);
    }

    /**
     * 过滤条件：
     *  1，是{@link VideoPost}类型的post；
     *  2，没有被删除；
     *  3，不是YouTube的视频。
     * @param postBases
     * @return
     */
    private ArrayList<VideoPost> filterVideoPost(List<PostBase> postBases) {
        if (CollectionUtil.isEmpty(postBases)) {
            return null;
        }
        ArrayList<VideoPost> videoPosts = new ArrayList<>();
        for (PostBase postBase : postBases) {
            if (postBase instanceof VideoPost &&
                    !postBase.isDeleted() &&
                    !TextUtils.equals(((VideoPost)postBase).getVideoSite(), PlayerConstant.VIDEO_SITE_YOUTUBE)) {
                videoPosts.add((VideoPost) postBase);
            }
        }
        return videoPosts;
    }

    public void downloadVideo(String url, String userNick) {
        mDownloadUrl = url;
        boolean isSuccess = MediaDownloadManager.getInstance().download(getApplication(), url, userNick, this);
        if (!isSuccess) {
            mDownloadStatus.postValue(DownloadStateEnum.ERROR);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        MediaDownloadManager.getInstance().unRegisterListener(mDownloadUrl, this);
        VideoPostsManager.getInstance().setListener(null);
    }

    @Override
    public void onDownloadStart(String url, String filePath) {
        mLastProgress = 0;
        mDownloadStatus.postValue(DownloadStateEnum.START);
    }

    @Override
    public void onDownloadSuccess(String url, String filePath) {
        mDownloadStatus.postValue(DownloadStateEnum.SUCCESS);
        notifySystemMedia(getApplication(), new File(filePath));
        EventLog.VIDEO.report4();
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

    private void notifySystemMedia(final Context applicationContext, final File file) {
        try {
            MediaScannerConnection.scanFile(applicationContext, new String[]{file.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {
                    getApplication().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file.getPath()))));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
