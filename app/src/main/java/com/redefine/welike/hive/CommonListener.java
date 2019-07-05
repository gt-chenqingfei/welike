package com.redefine.welike.hive;

/**
 * Created by daining on 2018/4/10.
 */

public interface CommonListener<T> {
    void onFinish(T value);

    void onError(T value);
}
