package com.redefine.welike.frameworkmvvm;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.BaseFragmentPage;

public abstract class BaseLifecycleFragmentPage extends BaseFragmentPage<BaseLifecyclePageStackManager> implements LifecycleOwner {

    private final LifecycleRegistry mLifecycleRegistry;
    private ViewModelStore mViewModelStore;

    public BaseLifecycleFragmentPage(BaseLifecyclePageStackManager stackManager, PageConfig config, PageCache cache) {
        super(stackManager, config, cache);
        mLifecycleRegistry = new LifecycleRegistry(this);
        mLifecycleRegistry.markState(Lifecycle.State.CREATED);
    }

    @Override
    public void attach(ViewGroup container) {
        super.attach(container);
        mLifecycleRegistry.markState(Lifecycle.State.STARTED);
    }

    public ViewModelStore getViewModelStore() {
        if (mViewModelStore == null) {
            mViewModelStore = new ViewModelStore();
        }
        return mViewModelStore;
    }

    @Override
    public void destroy(ViewGroup container) {
        super.destroy(container);
        mLifecycleRegistry.markState(Lifecycle.State.DESTROYED);
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
}
