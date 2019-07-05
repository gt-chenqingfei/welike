package com.redefine.sunny.task;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by n.d on 2017/6/26.
 */

public class ApiInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = new Request.Builder();
        Response response = chain.proceed(request);
        return null;
    }
}
