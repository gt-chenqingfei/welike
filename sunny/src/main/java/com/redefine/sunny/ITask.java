package com.redefine.sunny;

/**
 * Created by ningdai on 16/7/21.
 */
public interface ITask<T> {
    void go(Callback<T> call);
}
