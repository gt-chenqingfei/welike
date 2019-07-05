package com.redefine.foundation.http;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liubin on 2018/3/19.
 */

public class HttpDeleteReq extends BaseHttpReq {
    private Map<String, Object> urlExtParams;

    private class DeleteUrlParamsInterceptor implements UrlParamsInterceptor {

        @Override
        public String formatUrlParams() {
            if (urlExtParams != null) {
                return parserMapToFromFormat(urlExtParams);
            }
            return null;
        }
    }

    public HttpDeleteReq() {
        super(REQUEST_METHOD_DELETE);
        setContentType(DEFAULT_CONTENT_TYPE);
        urlParamsInterceptor = new HttpDeleteReq.DeleteUrlParamsInterceptor();
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
