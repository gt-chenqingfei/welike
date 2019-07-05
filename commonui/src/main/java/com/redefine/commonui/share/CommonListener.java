package com.redefine.commonui.share;

/**
 * Created by daining on 2018/4/10.
 */

public interface CommonListener<T> {
    void onFinish(T value);

    void onError(T value);
}
