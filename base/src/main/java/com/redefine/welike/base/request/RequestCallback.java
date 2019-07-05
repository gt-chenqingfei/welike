package com.redefine.welike.base.request;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by liubin on 2018/1/6.
 */

public interface RequestCallback {

    void onError(BaseRequest request, int errCode);
    void onSuccess(BaseRequest request, JSONObject result) throws Exception;

}
