package com.redefine.welike.base.request;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;

/**
 * Created by liubin on 2018/2/8.
 */

public class NickNameCheckRequest2 extends BaseRequest {
    private static final String NICK_NAME_CHECK_KEY_NAME = "nickName";

    public NickNameCheckRequest2(String nickName, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);

        setHost("user/skip/validate-nick-name/v2", false);
        setParam(NICK_NAME_CHECK_KEY_NAME, nickName);
    }

}
