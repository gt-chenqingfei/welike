package com.redefine.sunny.task;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.redefine.sunny.ITask;
import com.redefine.sunny.Callback;
import com.redefine.sunny.Result;
import com.redefine.sunny.api.ApiRequest;
import com.redefine.sunny.api.HttpMethod;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Task for OK HTTP
 * Created by n.d on 2017/6/26.
 */

public class OkTask<T> implements ITask<T> {

    public static final MediaType MediaType_JSON
            = MediaType.parse("application/json; charset=utf-8");
//    public static int test = 0;
    private Type responseType;
    private ApiRequest apiRequest;
    private OkHttpClient client;
    private Request request;
    private okhttp3.Callback okCallback;
    private int retry = 0;

    public OkTask(ApiRequest apiRequest, Type responseType, OkHttpClient client) {
        this.responseType = responseType;
        this.apiRequest = apiRequest;
        this.client = client;
    }

    public static RequestBody getRequestBody(HashMap<String, String> params) {
        //表单提交，没有文件
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        Set<Map.Entry<String, String>> entrySet = params.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            bodyBuilder.add(entry.getKey(), entry.getValue());
        }
        return bodyBuilder.build();
    }

    private static String getURL(String action) {
        return "https://api.qdingnet.com/property-api" + action;
    }

    private static JSONObject addCommonParams(JSONObject jsonObject) {
        JSONObject appDevice = new JSONObject();
        JSONObject appUser = new JSONObject();
        try {
            appUser.put("projectId", "");
            appUser.put("curMemberId", "");
            appDevice.put("qdPlatform", "IOS");
            appDevice.put("qdDevice", "iphone6");
            appDevice.put("qdVersion", "1.0.0");
            jsonObject.put("appDevice", appDevice);
            jsonObject.put("appUser", appUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private static Request getRequest(ApiRequest apiRequest) {
//        Request request;
        if (apiRequest.method== HttpMethod.PUT){
//            request = new Request.Builder().put(getRequestBody(apiRequest.params));
        }
        Set<Map.Entry<String, Object>> entrySet = apiRequest.params.entrySet();
        JSONObject jsonObject = new JSONObject();
        try {
            for (Map.Entry<String, Object> entry : entrySet) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonString = addCommonParams(jsonObject).toString();
        HashMap<String, String> params = new HashMap<>();
        params.put("body", jsonString);
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));

        Request request = new Request.Builder()
                .url(getURL(apiRequest.action))
                .post(getRequestBody(params))
                .build();
        return request;
    }

    /**
     * handle retry change.
     *
     * @return true means finish. stop retry.-
     */
    private boolean handleRetry(final Callback<T> callback) {
        if (callback instanceof Callback.RetryCallback) {
            if (((Callback.RetryCallback) callback).canRetry(++retry)) {
                client.newCall(request).enqueue(okCallback);
                return false;
            }
        }
        return true;
    }

    @Override
    public void go(final Callback<T> callback) {
        final Handler handler = new Handler(Looper.getMainLooper());
//        final Callback uiCallback = (Callback) Proxy.newProxyInstance(call.getClass().getClassLoader(), call
//                .getClass().getInterfaces(), new UICallbackProxy(call, handler));
        // create request
        request = getRequest(apiRequest);
        okCallback = new okhttp3.Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                if (handleRetry(callback)) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onNetworkError(e instanceof SocketTimeoutException);
                            callback.onFinal();
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseString = response.body().string();
                    try {
//                        JSONObject responseJson = new JSONObject(responseString);
//                        if (test == 1) {
//                            Log.e("DDAI", "MAKE ERROR");
//                            throw new JSONException("");
//                        }
                        JSONObject responseJson = JSON.parseObject(responseString);
                        JSONObject dataJson = responseJson.getJSONObject("data");
                        final Result result = new Result(responseJson.getIntValue("code"), dataJson.getString("toast"), dataJson.getString("message"));
                        if (result.code != 200) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onError(result);
                                }
                            });
                        } else {
//                        Object result = new Gson().fromJson(dataJson.toString(), responseType);
                            final Object object = JSON.parseObject(dataJson.toString(), responseType);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onFinish((T) object, result);
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (handleRetry(callback)) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onNetworkError(false);
                                }
                            });
                        }
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                    if (handleRetry(callback)) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onNetworkError(e instanceof SocketTimeoutException);
                            }
                        });
                    }
                }
                if (handleRetry(callback)) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFinal();
                        }
                    });
                }
            }
        };
        client.newCall(request).enqueue(okCallback);
//            response.body().string();
    }
}
