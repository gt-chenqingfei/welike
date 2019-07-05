package com.redefine.welike.business.feeds.management;

import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.framework.SingleListenerManagerBase;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.constant.MessageIdConstant;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.browse.management.request.CommentsRequest;
import com.redefine.welike.business.browse.management.request.CommentsRequest2;
import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.feeds.management.parser.CommentsDataSourceParser;
import com.redefine.welike.business.feeds.management.request.DeleteCommentRequest;
import com.redefine.welike.business.feeds.management.request.DeleteReplyRequest;
import com.redefine.welike.business.feeds.management.request.DislikeCommentRequest;
import com.redefine.welike.business.feeds.management.request.DislikeReplyRequest;
import com.redefine.welike.business.feeds.management.request.LikeCommentRequest;
import com.redefine.welike.business.feeds.management.request.LikeReplyRequest;
import com.redefine.welike.business.feeds.ui.bean.FeedDetailCommentHeadBean;
import com.redefine.welike.business.startup.management.HalfLoginManager;
import com.redefine.welike.hive.AppsFlyerManager;
import com.redefine.welike.hive.CommonListener;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/1/13.
 */

public class CommentsManager extends SingleListenerManagerBase implements RequestCallback {
    private final FeedDetailCommentHeadBean.CommentSortType mSortType;
    private BaseRequest commentsRequest;
    private String cursor;
    private int actionState = GlobalConfig.LIST_ACTION_NONE;

    public CommentsManager(FeedDetailCommentHeadBean.CommentSortType sortType) {
        mSortType = sortType;
    }

    public interface CommentsProviderCallback {

        void onRefreshComments(CommentsManager manager, FeedDetailCommentHeadBean.CommentSortType sortType, List<Comment> comments, String fid, int errCode);

        void onReceiveHisComments(CommentsManager manager, FeedDetailCommentHeadBean.CommentSortType sortType, List<Comment> comments, String fid, boolean last, int errCode);
    }

    public void setListener(CommentsProviderCallback listener) {
        super.setListener(listener);
    }

    public void tryRefresh(final String pid, boolean isAuth) {
        if (commentsRequest != null) return;

        cursor = null;
        actionState = GlobalConfig.LIST_ACTION_REFRESH;
        try {
            if (isAuth) {
                CommentsRequest request = new CommentsRequest(pid, null, mSortType, MyApplication.getAppContext());
                request.req(this);
                commentsRequest = request;
            } else {
                CommentsRequest2 request = new CommentsRequest2(pid, null, mSortType, MyApplication.getAppContext());
                request.req(this);
                commentsRequest = request;
            }
        } catch (Exception e) {
            e.printStackTrace();
            commentsRequest = null;
            final int errCode = ErrorCode.networkExceptionToErrCode(e);
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    CommentsProviderCallback callback = getCallback();
                    if (callback != null) {
                        callback.onRefreshComments(CommentsManager.this, mSortType, null, pid, errCode);
                    }
                }

            });
        }
    }

    public void tryHis(final String pid, boolean isAuth) {
        if (commentsRequest != null) return;

        actionState = GlobalConfig.LIST_ACTION_HIS;
        if (!TextUtils.isEmpty(cursor)) {
            try {
                if (isAuth) {
                    CommentsRequest request = new CommentsRequest(pid, cursor, mSortType, MyApplication.getAppContext());
                    request.req(this);
                    commentsRequest = request;
                } else {
                    CommentsRequest2 request = new CommentsRequest2(pid, cursor, mSortType, MyApplication.getAppContext());
                    request.req(this);
                    commentsRequest = request;
                }
            } catch (Exception e) {
                e.printStackTrace();
                commentsRequest = null;
                callReceiveHisComments(null, pid, ErrorCode.networkExceptionToErrCode(e));
            }
        } else {
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    CommentsProviderCallback callback = getCallback();
                    if (callback != null) {
                        callback.onReceiveHisComments(CommentsManager.this, mSortType, null, pid, true, ErrorCode.ERROR_SUCCESS);
                    }
                }

            });
        }
    }

    public static void likeComment(String cid) {
        AppsFlyerManager.addEvent(AppsFlyerManager.EVENT_LIKE);
        LikeCommentRequest request = new LikeCommentRequest(cid, MyApplication.getAppContext());
        try {
            request.req(null);

            HalfLoginManager.updateClickCount(new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.LIKE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dislikeComment(String cid) {
        DislikeCommentRequest request = new DislikeCommentRequest(cid, MyApplication.getAppContext());
        try {
            request.req(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void likeReply(String replyId) {
        AppsFlyerManager.addEvent(AppsFlyerManager.EVENT_LIKE);
        LikeReplyRequest request = new LikeReplyRequest(replyId, MyApplication.getAppContext());
        try {
            request.req(new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {
                    int a = errCode;
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    int a = 0;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dislikeReply(String replyId) {
        DislikeReplyRequest request = new DislikeReplyRequest(replyId, MyApplication.getAppContext());
        try {
            request.req(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if (request == commentsRequest) {
            final int error = errCode;
            final String pid = getPidFromRequest(commentsRequest);
            commentsRequest = null;
            if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                    @Override
                    public void run() {
                        CommentsProviderCallback callback = getCallback();
                        if (callback != null) {
                            callback.onRefreshComments(CommentsManager.this, mSortType, null, pid, error);
                        }
                    }

                });
            } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                callReceiveHisComments(null, pid, error);
            }
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (request == commentsRequest) {
            final String pid = getPidFromRequest(commentsRequest);
            commentsRequest = null;
            final List<Comment> comments = CommentsDataSourceParser.parseComments(result);
            cursor = CommentsDataSourceParser.parseCursor(result);
            if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                    @Override
                    public void run() {
                        CommentsProviderCallback callback = getCallback();
                        if (callback != null) {
                            callback.onRefreshComments(CommentsManager.this, mSortType, comments, pid, ErrorCode.ERROR_SUCCESS);
                        }
                    }

                });
            } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                callReceiveHisComments(comments, pid, ErrorCode.ERROR_SUCCESS);
            }
        }
    }

    private void callReceiveHisComments(final List<Comment> comments, final String pid, final int errCode) {
        final boolean last = TextUtils.isEmpty(cursor);
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                CommentsProviderCallback callback = getCallback();
                if (callback != null) {
                    callback.onReceiveHisComments(CommentsManager.this, mSortType, comments, pid, last, errCode);
                }
            }

        });
    }

    private String getPidFromRequest(BaseRequest request) {
        String pid = null;
        Object o = request.getUserInfo("pid");
        if (o != null && o instanceof String) {
            pid = (String) o;
        }
        return pid;
    }

    private CommentsProviderCallback getCallback() {
        CommentsProviderCallback callback = null;
        Object l = getListener();
        if (l != null && l instanceof CommentsProviderCallback) {
            callback = (CommentsProviderCallback) l;
        }
        return callback;
    }

    public static void deleteComment(String id) {


        Message message = Message.obtain();
        message.what = MessageIdConstant.MESSAGE_DELETE_COMMENT;
        message.obj = id;
        EventBus.getDefault().post(message);
        try {
            new DeleteCommentRequest(id, MyApplication.getAppContext()).req(new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {

                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyDeleteListener(id);
    }

    public static void deleteReply(String id) {

        try {
            new DeleteReplyRequest(id, MyApplication.getAppContext()).req(new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {

                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyDeleteListener(id);
    }


    private static ArrayList<CommonListener<String>> deleteListeners = new ArrayList<>();
    private static ReentrantLock lock = new ReentrantLock();

    private static void notifyDeleteListener(String id) {
        lock.lock();
        try {
            for (CommonListener<String> listener : deleteListeners) {
                listener.onFinish(id);
            }
        } finally {
            lock.unlock();
        }
    }

    public static void regDeleteListener(CommonListener<String> listener) {
        lock.lock();
        try {
            if (!deleteListeners.contains(listener)) {
                deleteListeners.add(listener);
            }
        } finally {
            lock.unlock();
        }
    }

    public static void unregDeleteListener(CommonListener<String> listener) {
        lock.lock();
        try {
            deleteListeners.remove(listener);
        } finally {
            lock.unlock();
        }
    }
}
