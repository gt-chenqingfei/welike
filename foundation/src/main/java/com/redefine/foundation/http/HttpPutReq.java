package com.redefine.foundation.http;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liubin on 2018/3/19.
 */

public class HttpPutReq extends BaseHttpReq {
    private Map<String, Object> urlExtParams;

    private class PutUrlParamsInterceptor implements UrlParamsInterceptor {

        @Override
        public String formatUrlParams() {
            if (urlExtParams != null) {
                return parserMapToFromFormat(urlExtParams);
            }
            return null;
        }
    }

    public HttpPutReq() {
        super(REQUEST_METHOD_PUT);
        setContentType(DEFAULT_CONTENT_TYPE);
        urlParamsInterceptor = new HttpPutReq.PutUrlParamsInterceptor();
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
