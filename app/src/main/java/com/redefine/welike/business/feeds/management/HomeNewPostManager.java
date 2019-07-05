package com.redefine.welike.business.feeds.management;

import com.redefine.foundation.framework.BroadcastManagerBase;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by mengnan on 2018/5/10.
 **/
public class HomeNewPostManager extends BroadcastManagerBase {


    public interface NewPostListener {

        void onNewPostListener();

    }

    public interface NewHomePostListener {

        void onNewHomePostListener();

    }

    private static class HomeNewPostManagerHolder {
        public static HomeNewPostManager instance = new HomeNewPostManager();
    }


    public static HomeNewPostManager getInstance() {
        return HomeNewPostManagerHolder.instance;
    }

    public void register(NewPostListener listener) {
        super.register(listener);
    }

    public void unregister(NewPostListener listener) {
        super.unregister(listener);
    }


    public void registerHome(NewHomePostListener listener) {
        super.register(listener);
    }

    public void unregisterHome(NewHomePostListener listener) {
        super.unregister(listener);
    }

    public void startHomeLooper() {
        new LooperThread().start();
    }

    public class LooperThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(100000);
                    startLoop();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void startLoop() {
        synchronized (listenerRefList) {
            for (int i = 0; i < listenerRefList.size(); i++) {
                ListenerRefExt callbackRef = listenerRefList.get(i);
                Object l = callbackRef.getListener();
                if (l != null && l instanceof NewHomePostListener) {
                    NewHomePostListener listener = (NewHomePostListener) l;
                    listener.onNewHomePostListener();
                }
            }
        }
    }

    private void broadcast() {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                synchronized (listenerRefList) {
                    for (int i = 0; i < listenerRefList.size(); i++) {
                        ListenerRefExt callbackRef = listenerRefList.get(i);
                        Object l = callbackRef.getListener();
                        if (l != null && l instanceof NewPostListener) {
                            NewPostListener listener = (NewPostListener) l;
                            listener.onNewPostListener();


                        }
                    }
                }
            }

        });
    }

}
