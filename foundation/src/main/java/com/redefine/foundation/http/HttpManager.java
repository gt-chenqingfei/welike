package com.redefine.foundation.http;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * Created by liubin on 2018/1/6.
 */

public class HttpManager {
    private static final int HTTP_CONNECTION_TIMEOUT = 10;
    private static final int HTTP_READ_TIMEOUT = 20;
    private static final int HTTP_DOWNLOAD_CONNECTION_TIMEOUT = 10;
    private static final int HTTP_DOWNLOAD_READ_TIMEOUT = 20;
    private static final int HTTP_DOWNLOAD_WRITE_TIMEOUT = 30;
    private static final int HTTP_UPLOAD_OLD_CONNECTION_TIMEOUT = 15;
    private static final int HTTP_UPLOAD_OLD_READ_TIMEOUT = 60;
    private static final int HTTP_UPLOAD_OLD_WRITE_TIMEOUT = 60;
    private static final int HTTP_UPLOAD_CONNECTION_TIMEOUT = 15;
    private static final int HTTP_UPLOAD_READ_TIMEOUT = 60;
    private static final int HTTP_UPLOAD_WRITE_TIMEOUT = 60;
//    private OkHttpClient httpClient = new OkHttpClient.Builder()
//                                          .connectTimeout(HTTP_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
//                                          .readTimeout(HTTP_READ_TIMEOUT, TimeUnit.SECONDS).build();

    private boolean debug = true;

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    private OkHttpClient httpClient = debug ? new OkHttpClient.Builder()
            .connectTimeout(HTTP_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(HTTP_READ_TIMEOUT, TimeUnit.SECONDS).addNetworkInterceptor(new StethoInterceptor()).build() :
            new OkHttpClient.Builder()
                    .connectTimeout(HTTP_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(HTTP_READ_TIMEOUT, TimeUnit.SECONDS).build();
//    private OkHttpClient httpClient =  new OkHttpClient.Builder()
//            .connectTimeout(HTTP_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
//            .readTimeout(HTTP_READ_TIMEOUT, TimeUnit.SECONDS).addNetworkInterceptor(new StethoInterceptor()).build();


    private OkHttpClient httpDownloadClient = new OkHttpClient.Builder()
            .connectTimeout(HTTP_DOWNLOAD_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(HTTP_DOWNLOAD_READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(HTTP_DOWNLOAD_WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build();

    private OkHttpClient httpUploadOldClient = new OkHttpClient.Builder()
            .connectTimeout(HTTP_UPLOAD_OLD_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(HTTP_UPLOAD_OLD_READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(HTTP_UPLOAD_OLD_WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build();

    private OkHttpClient httpUploadClient;

    private static class HttpManagerHolder {
        public static HttpManager instance = new HttpManager();
    }

    private HttpManager() {
    }

    public static HttpManager getInstance() {
        return HttpManagerHolder.instance;
    }

    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    public OkHttpClient getHttpDownloadClient() {
        return httpDownloadClient;
    }

    public OkHttpClient getHttpUploadOldClient() {
        return httpUploadOldClient;
    }

    public OkHttpClient getHttpUploadClient(Interceptor requestInterceptor, Interceptor headerInterceptor) {
        if (httpUploadClient == null) {
            httpUploadClient = new OkHttpClient.Builder()
                    .connectTimeout(HTTP_UPLOAD_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(HTTP_UPLOAD_READ_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(HTTP_UPLOAD_WRITE_TIMEOUT, TimeUnit.SECONDS)
                    .addInterceptor(requestInterceptor)
                    .addInterceptor(headerInterceptor)
                    .build();
        }
        return httpUploadClient;
    }

    public void cancelAllHttpRequests() {
        httpClient.dispatcher().cancelAll();
    }

}
