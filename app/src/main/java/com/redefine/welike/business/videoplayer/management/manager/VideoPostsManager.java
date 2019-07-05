package com.redefine.welike.business.videoplayer.management.manager;

import com.redefine.foundation.framework.SingleListenerManagerBase;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.provider.SinglePostsProviderCallback;
import com.redefine.welike.business.videoplayer.management.provider.IVideoPostsProvider;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by nianguowang on 2018/9/22
 */
public class VideoPostsManager extends SingleListenerManagerBase implements SinglePostsProviderCallback {

    private IVideoPostsProvider videoPostsProvider;

    private VideoPostsManager() {}

    private static class VideoListManagerHolder {
        private static VideoPostsManager sInstance = new VideoPostsManager();
    }

    public static VideoPostsManager getInstance() {
        return VideoListManagerHolder.sInstance;
    }

    public interface VideoPostsCallback {

        void onRefreshVideoPosts(VideoPostsManager manager, List<PostBase> videoPosts, int newCount, boolean last, int errCode);

        void onHisVideoPosts(VideoPostsManager manager, List<PostBase> videoPosts, boolean last, int errCode);
    }

    public void setVideoPostsProvider(IVideoPostsProvider provider) {
        videoPostsProvider = provider;
    }

    public void setListener(VideoPostsCallback listener) {
        super.setListener(listener);
        if (listener != null) {
            if (videoPostsProvider != null) {
                videoPostsProvider.setListener(this);
            }
        } else {
            if (videoPostsProvider != null) {
                videoPostsProvider.setListener(null);
            }
        }
    }

    public void refresh() {
        if (videoPostsProvider != null) {
            videoPostsProvider.tryRefreshPosts();
        }
    }

    public void hisPosts() {
        if (videoPostsProvider != null) {
            videoPostsProvider.tryHisPosts();
        }
    }

    @Override
    public void onRefreshPosts(final List<PostBase> posts, final int newCount, final boolean last, final int errCode) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                VideoPostsCallback callback = getCallback();
                if (callback != null) {
                    callback.onRefreshVideoPosts(VideoPostsManager.this, posts, newCount, last, errCode);
                }
            }
        });
    }

    @Override
    public void onReceiveHisPosts(final List<PostBase> posts, final boolean last, final int errCode) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                VideoPostsCallback callback = getCallback();
                if (callback != null) {
                    callback.onHisVideoPosts(VideoPostsManager.this, posts, last, errCode);
                }
            }
        });
    }

    private VideoPostsCallback getCallback() {
        VideoPostsCallback callback = null;
        Object l = getListener();
        if (l != null && l instanceof VideoPostsCallback) {
            callback = (VideoPostsCallback)l;
        }
        return callback;
    }

}
