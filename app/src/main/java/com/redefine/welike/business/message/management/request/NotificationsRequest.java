package com.redefine.welike.business.message.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

/**
 * Created by liubin on 2018/1/25.
 */

public class NotificationsRequest extends BaseRequest {
    private static final String NOTIFICATIONS_KEY_TYPE = "type";
    private static final String NOTIFICATIONS_KEY_CURSOR = "cursor";
    private final String mNotificationType;

    public NotificationsRequest(String notificationType, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);
        mNotificationType = notificationType;
    }

    public void refresh(RequestCallback callback) throws Exception {
        Account account = AccountManager.getInstance().getAccount();
        if (account != null) {
            setHost("im/user/" + account.getUid() + "/notifications", true);
            setParam(NOTIFICATIONS_KEY_TYPE, mNotificationType);
        }
        req(callback);
    }

    public void his(String cursor, RequestCallback callback) throws Exception {
        Account account = AccountManager.getInstance().getAccount();
        if (account != null) {
            setHost("im/user/" + account.getUid() + "/notifications", true);
            setParam(NOTIFICATIONS_KEY_TYPE, mNotificationType);
            setParam(NOTIFICATIONS_KEY_CURSOR, cursor);
        }
        req(callback);
    }
}
