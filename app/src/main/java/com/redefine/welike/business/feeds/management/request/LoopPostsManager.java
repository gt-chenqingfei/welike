package com.redefine.welike.business.feeds.management.request;

import com.redefine.foundation.framework.SingleListenerManagerBase;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.provider.ILoopPostProvider;
import com.redefine.welike.business.feeds.management.provider.LoopPostsProviderCallBack;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by mengnan on 2018/5/10.
 **/
public class LoopPostsManager extends SingleListenerManagerBase implements LoopPostsProviderCallBack {
     private static final long loopTime=1000*100;

    public interface LoopPostsCallback {

        void onLoopNewPosts(LoopPostsManager manager, List<PostBase> posts,  boolean last, int errCode);

    }

    public LoopPostsManager() {

    }

    private ILoopPostProvider dataSourceProvider;

    public void setDataSourceProvider(ILoopPostProvider provider) {
        dataSourceProvider = provider;
    }

    public void setListener(LoopPostsCallback listener) {
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
            dataSourceProvider.LoopGetNewPost();
        }
    }
    public void loopFetch(){
         new LooperThread().start();
    }

    @Override
    public void onGetNewPosts(final List<PostBase> posts,  final boolean last, final int errCode) {

        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                LoopPostsCallback callback = getCallback();
                if (callback != null) {
                    callback.onLoopNewPosts(LoopPostsManager.this, posts, last, errCode);
                }
            }

        });

    }
    private LoopPostsCallback getCallback() {
        LoopPostsCallback callback = null;
        Object l = getListener();
        if (l != null && l instanceof LoopPostsCallback) {
            callback = (LoopPostsCallback)l;
        }
        return callback;
    }

    public class LooperThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(loopTime);
                   getNewPosts();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
