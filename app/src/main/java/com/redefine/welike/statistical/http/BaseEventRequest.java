package com.redefine.welike.statistical.http;

import android.text.TextUtils;

import com.redefine.welike.base.URLCenter;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.statistical.Config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;

/**
 * Created by nianguowang on 2018/5/3
 */
public class BaseEventRequest {

    protected Map<String, String> header;
    protected Map<String, Object> params;
    protected String host;
    protected String contentType = "application/json;charset=utf-8";

    public BaseEventRequest() {
        Account account = AccountManager.getInstance().getAccount();
        if (account != null) {
            appendHeader("idtoken", account.getUid());
        }
        appendHeader("Content-Encoding", "gzip");
        host = URLCenter.getHostLog() + Config.GATHER_API;
    }

    protected void appendHeader(String key, String value) {
        if(!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            if (header == null) {
                header = new HashMap<>();
            }
            header.put(key, value);
        }
    }

    protected void setParam(String key, Object value) {
        if (!TextUtils.isEmpty(key) && value != null) {
            if (params == null) {
                params = new HashMap<>();
            }
            params.put(key, value);
        }
    }

    protected static class Packer {
        public static byte[] pack(String string) throws Exception {
            if (!TextUtils.isEmpty(string)) {
                return string.getBytes("UTF-8");
            } else {
                return null;
            }
        }

        public static String unpack(byte[] body) throws Exception {
            if (body != null && body.length > 0) {
                return new String(body, "UTF-8");
            } else {
                return null;
            }
        }
    }

    protected RequestBody gzip(final RequestBody body) {

        return new RequestBody() {
            @Nullable
            @Override
            public MediaType contentType() {
                return body.contentType();
            }

            @Override
            public long contentLength() throws IOException {
                return super.contentLength();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
                body.writeTo(gzipSink);
                gzipSink.close();
            }
        };
    }

    protected RequestBody forceContentLength(final RequestBody requestBody) throws IOException {
        final Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return requestBody.contentType();
            }

            @Override
            public long contentLength() {
                return buffer.size();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.write(buffer.snapshot());
            }
        };
    }
}
