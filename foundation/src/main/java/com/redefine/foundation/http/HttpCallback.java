package com.redefine.foundation.http;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by liubin on 2018/1/6.
 */

public interface HttpCallback {

    void onFailure(BaseHttpReq request, Exception e);
    void onResponse(BaseHttpReq request, JSONObject result) throws Exception;

}
