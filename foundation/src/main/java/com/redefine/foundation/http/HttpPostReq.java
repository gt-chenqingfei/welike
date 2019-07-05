package com.redefine.foundation.http;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liubin on 2018/1/6.
 */

public class HttpPostReq extends BaseHttpReq {
    private Map<String, Object> urlExtParams;

    private class PostUrlParamsInterceptor implements UrlParamsInterceptor {

        @Override
        public String formatUrlParams() {
            if (urlExtParams != null) {
                return parserMapToFromFormat(urlExtParams);
            }
            return null;
        }
    }

    public HttpPostReq() {
        super(REQUEST_METHOD_POST);
        setContentType(DEFAULT_CONTENT_TYPE);
        urlParamsInterceptor = new PostUrlParamsInterceptor();
    }

    public void setUrlExtParam(String key, String value) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            if (urlExtParams == null) {
                urlExtParams = new HashMap<>();
            }
            urlExtParams.put(key, value);
        }
    }

}
