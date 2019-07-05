package com.redefine.welike.commonui.event.expose.base;

/**
 * Created by nianguowang on 2019/1/9
 */
public interface IDataProvider<T> {

    T getData();

    String getSource();

    boolean hasHeader();
}
