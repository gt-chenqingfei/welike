package com.redefine.im.service.request;

import android.content.Context;
import android.text.TextUtils;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by liubin on 2018/2/3.
 */

public class PullOfflineMessagesRequest extends BaseRequest {
    private static final String PULL_MESSAGES_KEY_COUNT = "count";
    private static final String PULL_MESSAGES_KEY_CURSOR = "lv";

    public PullOfflineMessagesRequest(String lv, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);

        Account account = AccountManager.getInstance().getAccount();
        if (account != null) {
            setHost("im/message/offline/sync", true);
            setParam(PULL_MESSAGES_KEY_COUNT, GlobalConfig.IM_MESSAGES_ONE_PAGE);
            if (!TextUtils.isEmpty(lv)) {
                setParam(PULL_MESSAGES_KEY_CURSOR, lv);
            }
        }
    }

}
