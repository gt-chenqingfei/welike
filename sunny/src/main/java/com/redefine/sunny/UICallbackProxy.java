package com.redefine.sunny;

import android.os.Handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by ningdai on 16/8/5.
 */
public class UICallbackProxy implements InvocationHandler {
    private Object target;
    private Handler handler;

    public UICallbackProxy(Object object, Handler handler) {
        this.target = object;
        this.handler = handler;
    }

    @Override
    public Object invoke(Object proxy, final Method method, final Object[] args) throws
            Throwable {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    method.invoke(target, args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return new Object();
    }
}