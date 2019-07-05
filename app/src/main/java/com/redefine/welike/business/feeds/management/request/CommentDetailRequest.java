package com.redefine.welike.business.feeds.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

/**
 * Created by liubin on 2018/1/19.
 */

public class CommentDetailRequest extends BaseRequest {
    public static final String COMMENTS_REQ_CID_KEY = "cid";
    private static final String COMMENTS_KEY_COUNT_ONE_PAGE = "count";
    private static final String COMMENTS_KEY_ORDER = "order";
    private static final String COMMENTS_KEY_CURSOR = "cursor";

    public CommentDetailRequest(Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);
    }

    public void refresh(String cid, String order, RequestCallback callback) throws Exception {
        setHost("feed/comment/" + cid + "/replies", true);
        setParam(COMMENTS_KEY_COUNT_ONE_PAGE, GlobalConfig.COMMENTS_NUM_ONE_PAGE);
        putUserInfo(COMMENTS_REQ_CID_KEY, cid);
        setParam(COMMENTS_KEY_ORDER, order);
        req(callback);
    }

    public void his(String cid, String order, String cursor, RequestCallback callback) throws Exception {
        setHost("feed/comment/" + cid + "/replies", true);
        setParam(COMMENTS_KEY_COUNT_ONE_PAGE, GlobalConfig.COMMENTS_NUM_ONE_PAGE);
        setParam(COMMENTS_KEY_CURSOR, cursor);
        putUserInfo(COMMENTS_REQ_CID_KEY, cid);
        setParam(COMMENTS_KEY_ORDER, order);
        req(callback);
    }

}
