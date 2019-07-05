package com.redefine.welike.business.feeds.management;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.framework.SingleListenerManagerBase;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.feeds.management.parser.CommentsDataSourceParser;
import com.redefine.welike.business.feeds.management.request.CommentDetailRequest;
import com.redefine.welike.business.feeds.ui.bean.FeedDetailCommentHeadBean;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/1/13.
 */

public class CommentDetailManager extends SingleListenerManagerBase implements RequestCallback {
    private CommentDetailRequest commentDetailRequest;
    private String cursor;
    private int actionState = GlobalConfig.LIST_ACTION_NONE;
    private final FeedDetailCommentHeadBean.CommentSortType mSortType;
    private String order = "hot";

    public interface CommentDetailCallback {

        void onRefreshCommentDetail(CommentDetailManager manager, List<Comment> replies, String cid, int errCode);

        void onReceiveCommentDetailHis(CommentDetailManager manager, List<Comment> replies, FeedDetailCommentHeadBean.CommentSortType sortType, String cid, boolean last, int errCode);

    }

    //    public CommentDetailManager() {
//    }
    public CommentDetailManager(FeedDetailCommentHeadBean.CommentSortType sortType) {
        mSortType = sortType;

        order = mSortType == FeedDetailCommentHeadBean.CommentSortType.CREATED ? "created" : "hot";

    }

    public void setListener(CommentDetailCallback listener) {
        super.setListener(listener);
    }

    public void tryRefresh(final String mainCid) {
        if (commentDetailRequest != null) return;

        cursor = null;
        actionState = GlobalConfig.LIST_ACTION_REFRESH;
        commentDetailRequest = new CommentDetailRequest(MyApplication.getAppContext());
        try {
            commentDetailRequest.refresh(mainCid, order, this);
        } catch (Exception e) {
            e.printStackTrace();
            commentDetailRequest = null;
            final int errCode = ErrorCode.networkExceptionToErrCode(e);
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    CommentDetailCallback callback = getCallback();
                    if (callback != null) {
                        callback.onRefreshCommentDetail(CommentDetailManager.this, null, mainCid, errCode);
                    }
                }

            });
        }
    }

    public void tryHis(final String mainCid) {
        if (commentDetailRequest != null) return;

        actionState = GlobalConfig.LIST_ACTION_HIS;
        if (!TextUtils.isEmpty(cursor)) {
            commentDetailRequest = new CommentDetailRequest(MyApplication.getAppContext());
            try {
                commentDetailRequest.his(mainCid, order, cursor, this);
            } catch (Exception e) {
                e.printStackTrace();
                commentDetailRequest = null;
                callReceiveCommentDetailHis(null, mainCid, ErrorCode.networkExceptionToErrCode(e));
            }
        } else {
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    CommentDetailCallback callback = getCallback();
                    if (callback != null) {
                        callback.onReceiveCommentDetailHis(CommentDetailManager.this, null, mSortType, mainCid, true, ErrorCode.ERROR_SUCCESS);
                    }
                }

            });
        }
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if (request == commentDetailRequest) {
            final int error = errCode;
            final String cid = getCidFromRequest(commentDetailRequest);
            commentDetailRequest = null;
            if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                    @Override
                    public void run() {
                        CommentDetailCallback callback = getCallback();
                        if (callback != null) {
                            callback.onRefreshCommentDetail(CommentDetailManager.this, null, cid, error);
                        }
                    }

                });
            } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                callReceiveCommentDetailHis(null, cid, error);
            }
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (request == commentDetailRequest) {
            final String cid = getCidFromRequest(commentDetailRequest);
            commentDetailRequest = null;
            final List<Comment> replies = CommentsDataSourceParser.parseComments(result);
            cursor = CommentsDataSourceParser.parseCursor(result);
            if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                    @Override
                    public void run() {
                        CommentDetailCallback callback = getCallback();
                        if (callback != null) {
                            callback.onRefreshCommentDetail(CommentDetailManager.this, replies, cid, ErrorCode.ERROR_SUCCESS);
                        }
                    }

                });
            } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                callReceiveCommentDetailHis(replies, cid, ErrorCode.ERROR_SUCCESS);
            }
        }
    }

    private void callReceiveCommentDetailHis(final List<Comment> replies, final String cid, final int errCode) {
        final boolean last = TextUtils.isEmpty(cursor);
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                CommentDetailCallback callback = getCallback();
                if (callback != null) {
                    callback.onReceiveCommentDetailHis(CommentDetailManager.this, replies, mSortType, cid, last, errCode);
                }
            }

        });
    }

    private String getCidFromRequest(CommentDetailRequest request) {
        String cid = null;
        Object o = request.getUserInfo(CommentDetailRequest.COMMENTS_REQ_CID_KEY);
        if (o != null && o instanceof String) {
            cid = (String) o;
        }
        return cid;
    }

    private CommentDetailCallback getCallback() {
        CommentDetailCallback callback = null;
        Object l = getListener();
        if (l != null && l instanceof CommentDetailCallback) {
            callback = (CommentDetailCallback) l;
        }
        return callback;
    }

}
