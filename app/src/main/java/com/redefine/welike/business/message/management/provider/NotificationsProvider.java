package com.redefine.welike.business.message.management.provider;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.utils.NetWorkUtil;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.message.management.bean.NotificationBase;
import com.redefine.welike.business.message.management.parser.NotificationParser;
import com.redefine.welike.business.message.management.request.NotificationsRequest;

import java.util.List;

/**
 * Created by MR on 2018/1/26.
 */

public class NotificationsProvider implements RequestCallback {
    public static final String GENERAL_TYPE = "GENERAL";
    public static final String MENTION_TYPE = "MENTION";
    public static final String COMMENT_TYPE = "COMMENT";
    public static final String LIKE_TYPE = "LIKE";
    private final String mType;
    private NotificationsRequest mNotificationsRequest;
    private String mCursor;
    private int actionState = GlobalConfig.LIST_ACTION_NONE;
    private INotifyCallback listener;

    public NotificationsProvider(String type) {
        mType = type;
    }

    public void tryRefreshNotification() {
        if (mNotificationsRequest != null) {
            return;
        }
        mCursor = null;
        actionState = GlobalConfig.LIST_ACTION_REFRESH;
        if (NetWorkUtil.isNetWorkConnected(MyApplication.getAppContext())) {
            mNotificationsRequest = new NotificationsRequest(mType, MyApplication.getAppContext());
            try {
                mNotificationsRequest.refresh(this);
            } catch (Exception e) {
                e.printStackTrace();
                mNotificationsRequest = null;
                if (listener != null) {
                    listener.onRefreshNotification(null, ErrorCode.networkExceptionToErrCode(e));
                }
            }
        } else {
            if (listener != null) {
                listener.onRefreshNotification(null, ErrorCode.ERROR_NETWORK_INVALID);
            }
        }
    }

    public void tryHisNotification() {
        if (mNotificationsRequest != null) {
            return;
        }
        actionState = GlobalConfig.LIST_ACTION_HIS;
        if (NetWorkUtil.isNetWorkConnected(MyApplication.getAppContext())) {
            if (!TextUtils.isEmpty(mCursor)) {
                mNotificationsRequest = new NotificationsRequest(mType, MyApplication.getAppContext());
                try {
                    mNotificationsRequest.his(mCursor, this);
                } catch (Exception e) {
                    e.printStackTrace();
                    mNotificationsRequest = null;
                    if (listener != null) {
                        listener.onReceiveHisNotification(null, false, ErrorCode.networkExceptionToErrCode(e));
                    }
                }
            } else {
                if (listener != null) {
                    listener.onReceiveHisNotification(null, true, ErrorCode.ERROR_SUCCESS);
                }
            }
        } else {
            if (listener != null) {
                listener.onReceiveHisNotification(null, false, ErrorCode.ERROR_NETWORK_INVALID);
            }
        }
    }

    public void setListener(INotifyCallback listener) {
        this.listener = listener;
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if (request == mNotificationsRequest) {
            mNotificationsRequest = null;
            if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                if (listener != null) {
                    listener.onReceiveHisNotification(null, false, errCode);
                }
            } else if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                if (listener != null) {
                    listener.onRefreshNotification(null, errCode);
                }
            }
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (request == mNotificationsRequest) {
            mNotificationsRequest = null;
            List<NotificationBase> notifications = NotificationParser.parseNotification(result);
            if (listener != null) {
                if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                    mCursor = NotificationParser.parseCursor(result);
                    listener.onRefreshNotification(notifications, ErrorCode.ERROR_SUCCESS);
                } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                    mCursor = NotificationParser.parseCursor(result);
                    listener.onReceiveHisNotification(notifications, TextUtils.isEmpty(mCursor), ErrorCode.ERROR_SUCCESS);
                }
            }
        }
    }
}
