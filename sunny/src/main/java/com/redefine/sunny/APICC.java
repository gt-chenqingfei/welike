package com.redefine.sunny;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.redefine.sunny.api.ApiRequest;
import com.redefine.sunny.task.OkTask;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


/**
 * Created by n.d on 2017/6/26.
 */

public class APICC {
    static final private int TASK_OK_HTTP = 1;

    private static int networkTask = TASK_OK_HTTP;

    private static OkHttpClient client ;

    private static Application app;

    public static ITask newTask(ApiRequest request, Type responseType) {
//        if (networkTask == TASK_OK_HTTP){
//        }
        return new OkTask(request, responseType, client);
    }

    public static OkHttpClient getClient(){
        return client;
    }
    public static Application getApp() {
        return app;
    }

    public APICC userOkHttp() {
        networkTask = TASK_OK_HTTP;
        client = new OkHttpClient.Builder().build();
        return this;
    }

    public APICC registerListener(){
        return this;
    }

    public static void init(Application application){
        app = application;
        Stetho.initializeWithDefaults(application);
        client = new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor()).readTimeout(30000, TimeUnit.MILLISECONDS).build();
    }

}
