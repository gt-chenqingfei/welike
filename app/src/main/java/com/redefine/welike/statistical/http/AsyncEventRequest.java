package com.redefine.welike.statistical.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.redefine.foundation.http.HttpManager;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.welike.statistical.Config;
import com.redefine.welike.statistical.FieldGenerator;
import com.redefine.welike.statistical.bean.EventBean;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AsyncEventRequest extends BaseEventRequest implements Callback {

    private EventCallback mCallback;
    private EventBean mTag;

    public void setTag(EventBean bean) {
        mTag = bean;
    }

    public EventBean getTag() {
        return mTag;
    }

    public interface EventCallback {
        void onFailure(BaseEventRequest request, Exception e);
        void onResponse(BaseEventRequest request, Response result) throws Exception;
    }

    public void asyncSend(JSONArray data, EventCallback callback) throws Exception {
        mCallback = callback;
        if(data != null) {
            LogUtil.d("wng", "AsyncEventRequest" + data);
            setParam(Config.REQUEST_PARAM_DATA, data);
            setParam(Config.REQUEST_PARAM_COMMON, FieldGenerator.generateCommon());
            send();
        }
    }

    private void send() throws Exception {
        okhttp3.Request request;
        Headers headerBuild = null;

        RequestBody body;
        if (params != null) {
            String json = JSON.toJSONString(params);
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
        if (header != null) {
            headerBuild = Headers.of(header);
        }

        if (headerBuild != null) {
            request = new okhttp3.Request.Builder().post(requestBody).url(url.toString()).headers(headerBuild).build();
        } else {
            request = new okhttp3.Request.Builder().post(requestBody).url(url.toString()).build();
        }

        Call call = HttpManager.getInstance().getHttpClient().newCall(request);
        call.enqueue(this);
    }

    @Override
    public void onFailure(Call call, IOException e) {
        if(mCallback != null) {
            mCallback.onFailure(this, e);
        }
    }

    @Override
    public void onResponse(Call call, Response response) {
        if(mCallback != null) {
            try {
                mCallback.onResponse(this, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
