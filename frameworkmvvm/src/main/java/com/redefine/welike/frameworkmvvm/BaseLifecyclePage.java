package com.redefine.welike.frameworkmvvm;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.BaseTitlePage;

public abstract class BaseLifecyclePage extends BaseTitlePage<BaseLifecyclePageStackManager> implements LifecycleOwner {

    private final LifecycleRegistry mLifecycleRegistry;
    private ViewModelStore mViewModelStore;

    public BaseLifecyclePage(BaseLifecyclePageStackManager stackManager, PageConfig config, PageCache cache) {
        super(stackManager, config, cache);
        mLifecycleRegistry = new LifecycleRegistry(this);
    }

    @Override
    public void create(ViewGroup container) {
        super.create(container);
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
    }

    @Override
    public void attach(ViewGroup container) {
        super.attach(container);
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
    }

    @Override
    public void detach(ViewGroup container) {
        super.detach(container);
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
    }

    @Override
    public void destroy(ViewGroup container) {
        super.destroy(container);
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        if (mViewModelStore != null) {
            mViewModelStore.clear();
        }
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }

    public BaseLifecyclePageStackManager getPageStackManager() {
        return mPageStackManager;
    }

    public ViewModelStore getViewModelStore() {
        if (mViewModelStore == null) {
            mViewModelStore = new ViewModelStore();
        }
        return mViewModelStore;
    }
}
