package com.redefine.welike.business.feeds.management;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.framework.BroadcastManagerBase;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.base.track.AFGAEventManager;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.business.browse.management.request.BrowseLikeRequest;
import com.redefine.welike.business.common.LikeManager;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.parser.PostsDataSourceParser;
import com.redefine.welike.business.feeds.management.request.DeleteFeedRequest;
import com.redefine.welike.business.feeds.management.request.DislikePostRequest;
import com.redefine.welike.business.feeds.management.request.LikePostRequest;
import com.redefine.welike.business.feeds.management.request.PostDetailRequest;
import com.redefine.welike.business.feeds.management.request.SuperLikePostRequest;
import com.redefine.welike.business.startup.management.HalfLoginManager;
import com.redefine.welike.business.feeds.management.request.TopUserPostRequest;
import com.redefine.welike.business.feeds.management.request.UnTopUserPostRequest;
import com.redefine.welike.hive.AppsFlyerManager;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/3/2.
 */

public class SinglePostManager extends BroadcastManagerBase implements RequestCallback {
    private static final String DELETE_POST_ID = "pid";
    private DeleteFeedRequest deleteFeedRequest;

    public interface PostDeleteListener {

        void onPostDeleted(String pid);

    }

    public interface PostDetailCallback {

        void onPostDetailSuccessed(PostBase post);

        void onPostDetailFailed(int errCode);

    }

    public interface TopPostCallback {

        void onTopPostSuccessed(String info);

        void onTopPostFailed(int errCode);

    }

    public interface UnTopPostCallback {

        void unTopPostSuccessed(String info);

        void unTopDetailFailed(int errCode);

    }

    private static class SinglePostManagerHolder {
        public static SinglePostManager instance = new SinglePostManager();
    }

    private SinglePostManager() {
    }

    public static SinglePostManager getInstance() {
        return SinglePostManagerHolder.instance;
    }

    public void register(PostDeleteListener listener) {
        super.register(listener);
    }

    public void unregister(PostDeleteListener listener) {
        super.unregister(listener);
    }

    public static void like(String pid) {
        AppsFlyerManager.addEvent(AppsFlyerManager.EVENT_LIKE);
        LikePostRequest request = new LikePostRequest(pid, MyApplication.getAppContext());
        try {
            request.req(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        HomeCache.getInstance().markLikeState(pid, true);
//        HotCache.getInstance().markLikeState(pid, true);
    }

    public static void superLike( PostBase post, long exp, boolean isBrowse) {
        if (exp < 0) exp = 0;
        if (exp > 99) exp = 100;
        long preExp = post.getSuperLikeExp();
        post.setSuperLikeExp(Math.max(preExp, exp));
        int level = LikeManager.convertSuperLikeLevel(exp);
        int preLevel = LikeManager.convertSuperLikeLevel(preExp);
        if (preLevel != level) {
            long count = likeCountBySuperLikeLevel(level) + post.getLikeCount();
            post.setLikeCount(count);
        }
        long ex = exp - preExp;
        if (ex == 0) return;

        try {
            if (!isBrowse) {
                SuperLikePostRequest request = new SuperLikePostRequest(post.getPid(), ex, MyApplication.getAppContext());
                request.req(null);
                HalfLoginManager.updateClickCount(new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.LIKE));
            } else {
                browseLike(post);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void browseLike(final PostBase post) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new BrowseLikeRequest(MyApplication.getAppContext()).request(post.getPid());
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public static void disSuperLike(PostBase post) {
        int level = LikeManager.convertSuperLikeLevel(post.getSuperLikeExp());
        long count = likeCountBySuperLikeLevel(level);
        long lastLikeCount = post.getLikeCount() - count;
        if (lastLikeCount < 0) lastLikeCount = 0;
        post.setSuperLikeExp(0);
        post.setLikeCount(lastLikeCount);
        post.setLike(false);
        dislike(post.getPid());
    }

    public static void dislike(String pid) {
        DislikePostRequest request = new DislikePostRequest(pid, MyApplication.getAppContext());
        try {
            request.req(null);

            HalfLoginManager.updateClickCount(new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.LIKE));

        } catch (Exception e) {
            e.printStackTrace();
        }
//        HomeCache.getInstance().markLikeState(pid, false);
//        HotCache.getInstance().markLikeState(pid, false);
    }

    public void delete(PostBase post) {
        Account account = AccountManager.getInstance().getAccount();
        if (TextUtils.equals(post.getUid(), account == null ? "" : account.getUid())) {
            if (deleteFeedRequest == null) {
                deleteFeedRequest = new DeleteFeedRequest(post.getPid(), MyApplication.getAppContext());
                try {
                    deleteFeedRequest.req(this);
                    deleteFeedRequest.putUserInfo(DELETE_POST_ID, post.getPid());
                } catch (Exception e) {
                    e.printStackTrace();
                    deleteFeedRequest = null;
                }
            }
        }
    }

    public void topPost(PostBase post,final TopPostCallback callback) {
        Account account = AccountManager.getInstance().getAccount();
        if (TextUtils.equals(post.getUid(), account == null ? "" : account.getUid())) {
            TopUserPostRequest  topUserPostRequest = new TopUserPostRequest(post.getPid(), MyApplication.getAppContext());
                try {
                    topUserPostRequest.req(new RequestCallback(){

                        @Override
                        public void onError(BaseRequest request, int errCode) {
                            int realErrCode = errCode;
                            if (errCode == ErrorCode.ERROR_NETWORK_OBJECT_NOT_FOUND) {
                                realErrCode = ErrorCode.ERROR_POST_NOT_FOUND;
                            }
                            final int resultCode = realErrCode;
                            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                                @Override
                                public void run() {
                                    if (callback != null) {
                                        callback.onTopPostFailed(resultCode);
                                    }
                                }
                            });

                        }

                        @Override
                        public void onSuccess(BaseRequest request, JSONObject result)  {
                            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                                @Override
                                public void run() {
                                    if (callback != null) {
                                        callback.onTopPostSuccessed("true");
                                    }
                                }
                            });

                        }
                    });
                } catch (final Exception e) {
                    e.printStackTrace();
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onTopPostFailed(ErrorCode.networkExceptionToErrCode(e));
                            }
                        }
                    });
                }
            }
    }

    public void unPostPost(PostBase post,final UnTopPostCallback callback) {
        Account account = AccountManager.getInstance().getAccount();
        if (TextUtils.equals(post.getUid(), account == null ? "" : account.getUid())) {
            UnTopUserPostRequest  unTopUserPostRequest = new UnTopUserPostRequest(post.getPid(), MyApplication.getAppContext());
                try {
                    unTopUserPostRequest.req(new RequestCallback() {
                        @Override
                        public void onError(BaseRequest request, int errCode) {
                            int realErrCode = errCode;
                            if (errCode == ErrorCode.ERROR_NETWORK_OBJECT_NOT_FOUND) {
                                realErrCode = ErrorCode.ERROR_POST_NOT_FOUND;
                            }
                            final int resultCode = realErrCode;
                            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                                @Override
                                public void run() {
                                    if (callback != null) {
                                        callback.unTopDetailFailed(resultCode);
                                    }
                                }
                            });

                        }

                        @Override
                        public void onSuccess(BaseRequest request, JSONObject result)  {
                            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                                @Override
                                public void run() {
                                    if (callback != null) {
                                        callback.unTopPostSuccessed("true");
                                    }
                                }
                            });

                        }
                    });
                } catch (final Exception e) {
                    e.printStackTrace();
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.unTopDetailFailed(ErrorCode.networkExceptionToErrCode(e));
                            }
                        }
                    });
                }
            }
    }

    public void reqPostDetail(String pid, final PostDetailCallback callback) {
        PostDetailRequest postDetailRequest = new PostDetailRequest(pid, MyApplication.getAppContext());
        try {
            postDetailRequest.req(new RequestCallback() {

                @Override
                public void onError(BaseRequest request, int errCode) {
                    int realErrCode = errCode;
                    if (errCode == ErrorCode.ERROR_NETWORK_OBJECT_NOT_FOUND) {
                        realErrCode = ErrorCode.ERROR_POST_NOT_FOUND;
                    }
                    final int resultCode = realErrCode;
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onPostDetailFailed(resultCode);
                            }
                        }
                    });

                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    final PostBase post = PostsDataSourceParser.parsePostBase(result);

                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onPostDetailSuccessed(post);
                            }
                        }
                    });
                }

            });
        } catch (final Exception e) {
            e.printStackTrace();
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    if (callback != null) {
                        callback.onPostDetailFailed(ErrorCode.networkExceptionToErrCode(e));
                    }
                }
            });
        }
    }


    public void reqPostDetail4H5(String pid, final PostDetailCallback callback) {
        PostDetailRequest postDetailRequest = new PostDetailRequest(pid, false, MyApplication.getAppContext());
        try {
            postDetailRequest.req(new RequestCallback() {

                @Override
                public void onError(BaseRequest request, int errCode) {
                    int realErrCode = errCode;
                    if (errCode == ErrorCode.ERROR_NETWORK_OBJECT_NOT_FOUND) {
                        realErrCode = ErrorCode.ERROR_POST_NOT_FOUND;
                    }
                    final int resultCode = realErrCode;
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onPostDetailFailed(resultCode);
                            }
                        }
                    });

                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    final PostBase post = PostsDataSourceParser.parsePostBase(result);

                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onPostDetailSuccessed(post);
                            }
                        }
                    });
                }

            });
        } catch (final Exception e) {
            e.printStackTrace();
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    if (callback != null) {
                        callback.onPostDetailFailed(ErrorCode.networkExceptionToErrCode(e));
                    }
                }
            });
        }
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if (deleteFeedRequest == request) {
            deleteFeedRequest = null;
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (deleteFeedRequest == request) {
            String pid = (String) deleteFeedRequest.getUserInfo(DELETE_POST_ID);
            if (!TextUtils.isEmpty(pid)) {
//                HomeCache.getInstance().removePost(pid);
//                HotCache.getInstance().removePosts(pid);
                deleteBroadcast(pid);
            }
            deleteFeedRequest = null;
        }
    }

    private void deleteBroadcast(final String pid) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                synchronized (listenerRefList) {
                    for (int i = 0; i < listenerRefList.size(); i++) {
                        ListenerRefExt callbackRef = listenerRefList.get(i);
                        Object l = callbackRef.getListener();
                        if (l != null && l instanceof PostDeleteListener) {
                            PostDeleteListener listener = (PostDeleteListener) l;
                            listener.onPostDeleted(pid);
                        }
                    }
                }
            }

        });
    }

    private static int likeCountBySuperLikeLevel(int level) {
        if (level == 2) return 1;
        if (level == 3) return 2;
        if (level == 4) return 2;
        if (level == 5) return 5;
        return 0;
    }

}
