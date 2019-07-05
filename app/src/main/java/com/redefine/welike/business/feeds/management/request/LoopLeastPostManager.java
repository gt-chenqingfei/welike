package com.redefine.welike.business.feeds.management.request;

import com.redefine.foundation.framework.SingleListenerManagerBase;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.provider.ILoopLeastPostProvider;
import com.redefine.welike.business.feeds.management.provider.LoopPostsLeastProviderCallBack;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by mengnan on 2018/5/10.
 **/
public class LoopLeastPostManager extends SingleListenerManagerBase implements  LoopPostsLeastProviderCallBack {
    private static final long loopTime=1000*200;
    private static final long sleepTime=1000*20;
    private  boolean isLoop=true;
    public interface LoopLeastPostsCallback {

        void onLoopNewLeastPosts(LoopLeastPostManager manager, List<PostBase> posts,  boolean last, int errCode);

    }

    public LoopLeastPostManager() {

    }
    public void setIsLoop(boolean isLoop){
        this.isLoop=isLoop;
    }

    private ILoopLeastPostProvider dataSourceProvider;

    public void setDataSourceProvider(ILoopLeastPostProvider provider) {
        dataSourceProvider = provider;
    }

    public void setListener(LoopLeastPostsCallback listener) {
        super.setListener(listener);
        if (listener != null) {
            if (dataSourceProvider != null) {
                dataSourceProvider.setListener(this);
                dataSourceProvider.attachListener();
            }
        } else {
            if (dataSourceProvider != null) {
                dataSourceProvider.setListener(null);
                dataSourceProvider.detachListener();
            }
        }
    }


    public void getNewPosts() {
        if (dataSourceProvider != null) {
            dataSourceProvider.LoopGetNewLeastPost();
        }
    }

    public void loopFetch(){
        new LooperThread().start();
    }

    @Override
    public void onGetNewLeastPosts(final List<PostBase> posts,  final boolean last, final int errCode) {

        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                LoopLeastPostsCallback callback = getCallback();
                if (callback != null) {
                    callback.onLoopNewLeastPosts(LoopLeastPostManager.this, posts, last, errCode);
                }
            }

        });

    }
    private LoopLeastPostsCallback getCallback() {
        LoopLeastPostsCallback callback = null;
        Object l = getListener();
        if (l != null && l instanceof LoopLeastPostsCallback) {
            callback = (LoopLeastPostsCallback)l;
        }
        return callback;
    }

    public class LooperThread extends Thread {
        @Override
        public void run() {
            while (true) {
                if(isLoop){
                    try {
                        Thread.sleep(loopTime);
                        getNewPosts();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

}
