package com.redefine.sunny;

/**
 * Created by ningdai on 16/7/21.
 */
public interface Callback<T> {

    void onFinish(T t, Result result);

    void onError(Result result);

    void onNetworkError(boolean timeout);

    void onFinal();

    interface PrepareCallback<T> extends Callback<T> {
        /**
         * @param t the response, model from api.
         * @return false can interrupt next callback, like {@link #onFinish(Object, Result)}
         */
        boolean prepare(String jsonString, T t);
    }

    interface CacheCallback<T> extends Callback<T> {
        /**
         * @param t the response from cache, model from api.
         */
        void fromCache(T t);
    }

    interface ProgressCallback<T> extends Callback<T> {

        void onProgress(int percent);
    }

    interface RetryCallback<T> extends Callback<T> {
        /**
         * Whether to allow retry
         *
         * @param i current retry time, first time i = 1.
         * @return true means allow
         */
        boolean canRetry(int i);
    }
}
