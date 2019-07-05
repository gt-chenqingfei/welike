package com.redefine.welike.business.feeds.management;

import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.framework.BroadcastManagerBase;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.feeds.management.parser.PostsDataSourceParser;
import com.redefine.welike.business.feeds.management.request.FeedsStatusListenerRequest;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/1/29.
 */

public class FeedsStatusObserver extends BroadcastManagerBase implements RequestCallback {
    private final List<FeedsStatusListenerRequest> taskPool = new ArrayList<>();

    public static class FeedStatus {
        private String pid;
        private long likeCount;
        private int commentCount;
        private int forwardCount;
        private boolean following;
        private boolean like;
        private long superLikeExp;

        public String getPid() { return pid; }

        public void setPid(String pid) { this.pid = pid; }

        public long getLikeCount() { return likeCount; }

        public void setLikeCount(long likeCount) { this.likeCount = likeCount; }

        public int getCommentCount() { return commentCount; }

        public void setCommentCount(int commentCount) { this.commentCount = commentCount; }

        public int getForwardCount() { return forwardCount; }

        public void setForwardCount(int forwardCount) { this.forwardCount = forwardCount; }

        public boolean isFollowing() {
            return following;
        }

        public void setFollowing(boolean following) {
            this.following = following;
        }

        public boolean isLike() {
            return like;
        }

        public void setLike(boolean like) {
            this.like = like;
        }

        public long getSuperLikeExp() {
            return superLikeExp;
        }

        public void setSuperLikeExp(long superLikeExp) {
            this.superLikeExp = superLikeExp;
        }
    }

    public interface FeedsStatusObserverCallback {

        void onListFeedsStatusSuccessed(List<FeedStatus> statusList);

    }

    public interface OneFeedStatusCallback {

        void onOneFeedStatusSuccessed(FeedStatus status);

    }

    private static class FeedsStatusObserverHolder {
        public static FeedsStatusObserver instance = new FeedsStatusObserver();
    }

    private FeedsStatusObserver() {}

    public static FeedsStatusObserver getInstance() { return FeedsStatusObserverHolder.instance; }

    public void register(FeedsStatusObserverCallback listener) {
        super.register(listener);
    }

    public void unregister(FeedsStatusObserverCallback listener) {
        super.unregister(listener);
    }

    public void reqStatusList(List<String> pids) {
        FeedsStatusListenerRequest request = new FeedsStatusListenerRequest(pids, MyApplication.getAppContext());
        try {
            request.req(this);
            taskPool.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reqOneStatus(String pid, final OneFeedStatusCallback callback) {
        List<String> pids = new ArrayList<>();
        pids.add(pid);
        FeedsStatusListenerRequest request = new FeedsStatusListenerRequest(pids, MyApplication.getAppContext());
        try {
            request.req(new RequestCallback() {

                @Override
                public void onError(BaseRequest request, int errCode) {}

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    List<FeedStatus> feedStatusList = PostsDataSourceParser.parseFeedStatusList(result);
                    if (feedStatusList != null && feedStatusList.size() == 1) {
                        final FeedStatus status = feedStatusList.get(0);
                        if (status != null) {
                            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                                @Override
                                public void run() {
                                    if (callback != null) {
                                        callback.onOneFeedStatusSuccessed(status);
                                    }
                                }

                            });
                        }
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(BaseRequest request, int errCode) {}

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (request instanceof FeedsStatusListenerRequest) {
            FeedsStatusListenerRequest feedsStatusListenerRequest = (FeedsStatusListenerRequest)request;
            taskPool.remove(feedsStatusListenerRequest);

            List<FeedStatus> feedStatusList = PostsDataSourceParser.parseFeedStatusList(result);
            if (feedStatusList != null && feedStatusList.size() > 0) {
                broadcast(feedStatusList);
            }
        }
    }

    private void broadcast(final List<FeedStatus> statusList) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                synchronized (listenerRefList) {
                    for (int i = 0; i < listenerRefList.size(); i++) {
                        ListenerRefExt callbackRef = listenerRefList.get(i);
                        Object l = callbackRef.getListener();
                        if (l != null && l instanceof FeedsStatusObserverCallback) {
                            FeedsStatusObserverCallback listener = (FeedsStatusObserverCallback)l;
                            listener.onListFeedsStatusSuccessed(statusList);
                        }
                    }
                }
            }

        });
    }

}
