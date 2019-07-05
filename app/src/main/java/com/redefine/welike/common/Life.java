package com.redefine.welike.common;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * App 业务逻辑的生命周期。后期用LiveData代替。
 * Created by daining on 2018/4/17.
 */

public enum Life {

    INSTANCE;

    public final static int LIFE_LOGIN = 1;
    public final static int LIFE_LOGOUT = LIFE_LOGIN << 1;
    public final static int LIFE_REGISTER_FINISH = LIFE_LOGOUT << 1;

    @IntDef({LIFE_LOGIN, LIFE_LOGOUT, LIFE_REGISTER_FINISH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LifeEvent {
    }

    public static void login() {
        notifyListener(LIFE_LOGIN);
    }

    public static void logout() {
        notifyListener(LIFE_LOGOUT);
    }

    public static void registerFinish() {
        notifyListener(LIFE_REGISTER_FINISH);
    }

    private static ArrayList<LifeListener> lifeListeners = new ArrayList<>();

    private static ReentrantLock lock = new ReentrantLock();

    private static void notifyListener(final int id) {
        new Thread() {
            @Override
            public void run() {
                lock.lock();
                try {
                    for (LifeListener listener : lifeListeners) {
                        try {
                            listener.onFire(id);
                        } catch (Exception ignored) {
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }
        }.start();
    }


    public static void regListener(LifeListener listener) {
        lock.lock();
        try {
            if (!lifeListeners.contains(listener)) {
                lifeListeners.add(listener);
            }
        } finally {
            lock.unlock();
        }
    }

    public static void unregListener(LifeListener listener) {
        lock.lock();
        try {
            lifeListeners.remove(listener);
        } finally {
            lock.unlock();
        }
    }


}
