package com.redefine.sunny.task;

import com.redefine.sunny.Callback;
import com.redefine.sunny.Result;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;

/**
 * Created by n.d on 2017/6/26.
 */

public class ApiProgressInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        Response response = chain.proceed(request);
        return response.newBuilder()
                .body(new DownloadProgressResponseBody(response.body(), new Callback.ProgressCallback() {
                    @Override
                    public void onFinish(Object o, Result result) {

                    }

                    @Override
                    public void onError(Result result) {

                    }

                    @Override
                    public void onNetworkError(boolean timeout) {

                    }

                    @Override
                    public void onFinal() {
                        
                    }

                    @Override
                    public void onProgress(int percent) {

                    }
                }))
                .build();
    }

    private static class DownloadProgressResponseBody extends ResponseBody {
        private final ResponseBody responseBody;
        Callback.ProgressCallback callback;

        public DownloadProgressResponseBody(ResponseBody responseBody, Callback.ProgressCallback callback) {
            this.responseBody = responseBody;
            this.callback = callback;
        }

        @Override
        public MediaType contentType() {
            return null;
        }

        @Override
        public long contentLength() {
            return 0;
        }

        @Override
        public BufferedSource source() {
            return null;
        }
    }
}
