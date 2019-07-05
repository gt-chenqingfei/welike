package com.redefine.welike.business.user.management.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by liubin on 2018/1/21.
 */

public class ContactsListRequest extends BaseRequest {
    private static final String CONTACTS_LIST_KEY_COUNT = "count";

    public ContactsListRequest(String uid, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);

        setHost("feed/user/" + uid + "/follow-users", true);
        setParam(CONTACTS_LIST_KEY_COUNT, 0);
    }

}
