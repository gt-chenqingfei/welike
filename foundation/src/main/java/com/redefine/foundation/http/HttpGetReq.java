package com.redefine.foundation.http;

/**
 * Created by liubin on 2018/1/6.
 */

public class HttpGetReq extends BaseHttpReq {

    public HttpGetReq() {
        super(REQUEST_METHOD_GET);
        setContentType(DEFAULT_CONTENT_TYPE);
    }

}
