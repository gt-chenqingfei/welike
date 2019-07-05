package com.redefine.foundation.http;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by liubin on 2018/1/6.
 */

public class BaseHttpReq {
    public static final String DEFAULT_CONTENT_TYPE = "application/json;charset=utf-8";
    public static final int REQUEST_METHOD_GET = 1;
    public static final int REQUEST_METHOD_POST = 2;
    public static final int REQUEST_METHOD_PUT = 3;
    public static final int REQUEST_METHOD_DELETE = 4;

    public Map<String, Object> userInfo = new HashMap<>();
    public String host;
    protected int method;
    public Map<String, Object> params;
    public String bodyData;
    public Map<String, String> header;
    public UrlParamsInterceptor urlParamsInterceptor;
    private HttpCallback callback;
    private String contentType;
    private Call call;

    protected interface UrlParamsInterceptor {

        String formatUrlParams();

    }

    private static class Packer {
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

    protected BaseHttpReq(int method) {
        this.method = method;
    }

    public void setContentType(String value) {
        if (!TextUtils.isEmpty(value)) {
            appendHeader("Content-Type", value);
            contentType = value;
        }
    }

    public void appendHeader(String key, String value) {

        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            if (header == null) {
                header = new HashMap<>();
            }
            header.put(key, value);
        }
    }

    public void setParam(String key, Object value) {
        if (!TextUtils.isEmpty(key) && value != null) {
            if (params == null) {
                params = new HashMap<>();
            }
            params.put(key, value);
        }
    }

    public void send(HttpCallback respCallback) throws Exception {
        callback = respCallback;
        okhttp3.Request request;
        Headers headerBuild = null;
        if (header != null) {
            headerBuild = Headers.of(header);
        }
        if (method == REQUEST_METHOD_POST) {
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
                if (!TextUtils.isEmpty(bodyData)) {
                    body = RequestBody.create(MediaType.parse(contentType), bodyData);
                } else {
                    body = RequestBody.create(MediaType.parse(contentType), "");
                }
            }
            StringBuilder url = new StringBuilder(host);
            if (urlParamsInterceptor != null) {
                String m = urlParamsInterceptor.formatUrlParams();
                if (m != null && m.length() > 0) {
                    url = url.append("?").append(m);
                }
            }
            if (headerBuild != null) {
                request = new okhttp3.Request.Builder().post(body).url(url.toString()).headers(headerBuild).build();
            } else {
                request = new okhttp3.Request.Builder().post(body).url(url.toString()).build();
            }
        } else if (method == REQUEST_METHOD_PUT) {
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
                if (!TextUtils.isEmpty(bodyData)) {
                    body = RequestBody.create(MediaType.parse(contentType), bodyData);
                } else {
                    body = RequestBody.create(MediaType.parse(contentType), "");
                }
            }
            StringBuilder url = new StringBuilder(host);
            if (urlParamsInterceptor != null) {
                String m = urlParamsInterceptor.formatUrlParams();
                if (m != null && m.length() > 0) {
                    url = url.append("?").append(m);
                }
            }
            if (headerBuild != null) {
                request = new okhttp3.Request.Builder().put(body).url(url.toString()).headers(headerBuild).build();
            } else {
                request = new okhttp3.Request.Builder().put(body).url(url.toString()).build();
            }
        } else if (method == REQUEST_METHOD_DELETE) {
            RequestBody body = null;
            if (params != null) {
                String json = JSON.toJSONString(params);
                byte[] data = Packer.pack(json);
                if (data == null) {
                    body = RequestBody.create(MediaType.parse(contentType), "");
                } else {
                    body = RequestBody.create(MediaType.parse(contentType), data);
                }
            } else if (!TextUtils.isEmpty(bodyData)) {
                body = RequestBody.create(MediaType.parse(contentType), bodyData);
            }
            StringBuilder url = new StringBuilder(host);
            if (urlParamsInterceptor != null) {
                String m = urlParamsInterceptor.formatUrlParams();
                if (m != null && m.length() > 0) {
                    url = url.append("?").append(m);
                }
            }
            if (headerBuild != null) {
                if (body != null) {
                    request = new okhttp3.Request.Builder().delete(body).url(url.toString()).headers(headerBuild).build();
                } else {
                    request = new okhttp3.Request.Builder().delete().url(url.toString()).headers(headerBuild).build();
                }
            } else {
                if (body != null) {
                    request = new okhttp3.Request.Builder().delete(body).url(url.toString()).build();
                } else {
                    request = new okhttp3.Request.Builder().delete().url(url.toString()).build();
                }
            }
        } else {
            StringBuilder url = new StringBuilder(host);
            if (params != null) {
                String m;
                url = url.append("?");
                if (urlParamsInterceptor != null) {
                    m = urlParamsInterceptor.formatUrlParams();
                    url.append(m);
                }
                else
                {
                    m = parserMapToFromFormat(params);
                    url.append(m);
                }
            }
            if (headerBuild != null) {
                request = new okhttp3.Request.Builder().get().url(url.toString()).headers(headerBuild).build();
            } else {
                request = new okhttp3.Request.Builder().get().url(url.toString()).build();
            }
        }

        call = HttpManager.getInstance().getHttpClient().newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handleOkHttpFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleOkHttpResponse(response);
            }
        });
    }

    public JSONObject send() throws Exception {
        okhttp3.Request request;
        Headers headerBuild = null;
        if (header != null) {
            headerBuild = Headers.of(header);
        }
        if (method == REQUEST_METHOD_POST) {
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
                if (!TextUtils.isEmpty(bodyData)) {
                    body = RequestBody.create(MediaType.parse(contentType), bodyData);
                } else {
                    body = RequestBody.create(MediaType.parse(contentType), "");
                }
            }
            StringBuilder url = new StringBuilder(host);
            if (urlParamsInterceptor != null) {
                String m = urlParamsInterceptor.formatUrlParams();
                if (m != null && m.length() > 0) {
                    url = url.append("?").append(m);
                }
            }
            if (headerBuild != null) {
                request = new okhttp3.Request.Builder().post(body).url(url.toString()).headers(headerBuild).build();
            } else {
                request = new okhttp3.Request.Builder().post(body).url(url.toString()).build();
            }
        } else if (method == REQUEST_METHOD_PUT) {
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
                if (!TextUtils.isEmpty(bodyData)) {
                    body = RequestBody.create(MediaType.parse(contentType), bodyData);
                } else {
                    body = RequestBody.create(MediaType.parse(contentType), "");
                }
            }
            StringBuilder url = new StringBuilder(host);
            if (urlParamsInterceptor != null) {
                String m = urlParamsInterceptor.formatUrlParams();
                if (m != null && m.length() > 0) {
                    url = url.append("?").append(m);
                }
            }
            if (headerBuild != null) {
                request = new okhttp3.Request.Builder().put(body).url(url.toString()).headers(headerBuild).build();
            } else {
                request = new okhttp3.Request.Builder().put(body).url(url.toString()).build();
            }
        } else if (method == REQUEST_METHOD_DELETE) {
            RequestBody body = null;
            if (params != null) {
                String json = JSON.toJSONString(params);
                byte[] data = Packer.pack(json);
                if (data == null) {
                    body = RequestBody.create(MediaType.parse(contentType), "");
                } else {
                    body = RequestBody.create(MediaType.parse(contentType), data);
                }
            } else if (!TextUtils.isEmpty(bodyData)) {
                body = RequestBody.create(MediaType.parse(contentType), bodyData);
            }
            StringBuilder url = new StringBuilder(host);
            if (urlParamsInterceptor != null) {
                String m = urlParamsInterceptor.formatUrlParams();
                if (m != null && m.length() > 0) {
                    url = url.append("?").append(m);
                }
            }
            if (headerBuild != null) {
                if (body != null) {
                    request = new okhttp3.Request.Builder().delete(body).url(url.toString()).headers(headerBuild).build();
                } else {
                    request = new okhttp3.Request.Builder().delete().url(url.toString()).headers(headerBuild).build();
                }
            } else {
                if (body != null) {
                    request = new okhttp3.Request.Builder().delete(body).url(url.toString()).build();
                } else {
                    request = new okhttp3.Request.Builder().delete().url(url.toString()).build();
                }
            }
        } else {
            StringBuilder url = new StringBuilder(host);
            if (params != null) {
                String m;
                url = url.append("?");
                if (urlParamsInterceptor != null) {
                    m = urlParamsInterceptor.formatUrlParams();
                    url.append(m);
                }
                else
                {
                    m = parserMapToFromFormat(params);
                    url.append(m);
                }
            }
            if (headerBuild != null) {
                request = new okhttp3.Request.Builder().get().url(url.toString()).headers(headerBuild).build();
            } else {
                request = new okhttp3.Request.Builder().get().url(url.toString()).build();
            }
        }

        call = HttpManager.getInstance().getHttpClient().newCall(request);
        Response response = call.execute();
        call = null;
        byte[] body = null;
        try {
            ResponseBody respBody = response.body();
            if (respBody != null) {
                body = respBody.bytes();
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
        if (body != null) {
            String json;
            try {
                json = Packer.unpack(body);
            } catch (Exception e) {
                throw new Exception(e);
            }
            if (json != null) {
                JSONObject jsonObj;
                try {
                    jsonObj = JSON.parseObject(json);
                } catch (Exception e) {
                    throw new Exception(e);
                }
                if (jsonObj != null) {
                    return jsonObj;
                } else {
                    throw new HttpRespFormatException("invalid json format");
                }
            } else {
                throw new HttpRespFormatException("invalid json format");
            }
        } else {
            throw new IOException("response body is null");
        }
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
        }
        call = null;
        callback = null;
    }

    protected static String parserMapToFromFormat(Map<String, Object> m) {
        StringBuilder mm = new StringBuilder();
        Iterator<Map.Entry<String, Object>> entries = m.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Object> entry = entries.next();
            String val = null;
            try {
                val = URLEncoder.encode(entry.getValue().toString(), "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (val != null) {
                mm = mm.append(entry.getKey()).append("=").append(val).append("&");
            }
        }
        return mm.substring(0, mm.length() - 1);
    }

    private void handleOkHttpFailure(IOException e) {
        e.printStackTrace();
        if (callback != null) {
            callback.onFailure(this, e);
        }
        call = null;
        callback = null;
    }

    private void handleOkHttpResponse(Response response) {
        byte[] body = null;
        try {
            ResponseBody respBody = response.body();
            if (respBody != null) {
                body = respBody.bytes();
            }
        } catch (Exception e) {
            e.printStackTrace();
            body = null;
            if (callback != null) {
                callback.onFailure(this, e);
            }
        }
        if (body != null) {
            String json;
            try {
                json = Packer.unpack(body);
            } catch (Exception e) {
                e.printStackTrace();
                json = null;
                if (callback != null) {
                    callback.onFailure(this, e);
                }
            }
            if (json != null) {
                JSONObject jsonObj;
                try {
                    jsonObj = JSON.parseObject(json);
                } catch (Exception e) {
                    e.printStackTrace();
                    jsonObj = null;
                    if (callback != null) {
                        callback.onFailure(this, e);
                    }
                }
                if (jsonObj != null) {
                    try {
                        if (callback != null) {
                            callback.onResponse(this, jsonObj);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (callback != null) {
                            callback.onFailure(this, e);
                        }
                    }
                } else {
                    if (callback != null) {
                        callback.onFailure(this, new HttpRespFormatException("invalid json format"));
                    }
                }
            } else {
                if (callback != null) {
                    callback.onFailure(this, new HttpRespFormatException("invalid json format"));
                }
            }
        } else {
            if (callback != null) {
                callback.onFailure(this, new IOException("response body is null"));
            }
        }
        call = null;
        callback = null;
    }

}
