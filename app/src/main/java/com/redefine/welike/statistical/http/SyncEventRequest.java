package com.redefine.welike.statistical.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.redefine.foundation.http.HttpManager;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.welike.statistical.Config;
import com.redefine.welike.statistical.FieldGenerator;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by nianguowang on 2018/5/3
 */
public class SyncEventRequest extends BaseEventRequest {

    public SyncEventRequest() {
        super();
    }

    public boolean sendEvent(JSONArray data) throws Exception {
        if(data != null) {
            setParam(Config.REQUEST_PARAM_COMMON, FieldGenerator.generateCommon());
            setParam(Config.REQUEST_PARAM_DATA, data);
            return send();
        }
        return false;
    }

    private boolean send() throws Exception {
        okhttp3.Request request;
        Headers headerBuild = null;
        if (header != null) {
            headerBuild = Headers.of(header);
        }

        RequestBody body;
        if (params != null) {
            String json = JSON.toJSONString(params);
            LogUtil.d("wng", "SyncEventRequest" + json);
            byte[] data = Packer.pack(json);
            if (data == null) {
                body = RequestBody.create(MediaType.parse(contentType), "");
            } else {
                body = RequestBody.create(MediaType.parse(contentType), data);
            }
        } else {
            body = RequestBody.create(MediaType.parse(contentType), "");
        }
        StringBuilder url = new StringBuilder(host);
        RequestBody requestBody = forceContentLength(gzip(body));
        appendHeader("Content-Length", String.valueOf(requestBody.contentLength()));
        LogUtil.d("wng_", "Event content Length : " + requestBody.contentLength());
        if (headerBuild != null) {
            request = new okhttp3.Request.Builder().post(requestBody).url(url.toString()).headers(headerBuild).build();
        } else {
            request = new okhttp3.Request.Builder().post(requestBody).url(url.toString()).build();
        }

        Call call = HttpManager.getInstance().getHttpClient().newCall(request);
        Response response = null;
        try {
            response = call.execute();
            if(response != null) {
                return response.isSuccessful();
            }
            return false;
        } catch (IOException e) {
//            e.printStackTrace();
            return false;
        }

    }
}
