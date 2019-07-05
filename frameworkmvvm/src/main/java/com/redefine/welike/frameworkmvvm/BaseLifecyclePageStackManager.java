package com.redefine.welike.frameworkmvvm;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.content.Context;
import android.support.annotation.NonNull;

import com.pekingese.pagestack.framework.manager.BasePageStackManager;
import com.pekingese.pagestack.framework.view.PageStack;
import com.pekingese.pagestack.framework.view.TitlePageFactory;
import com.pekingese.pagestack.framework.view.TitlePageStack;

public abstract class BaseLifecyclePageStackManager extends BasePageStackManager implements LifecycleOwner {

    private LifecycleRegistry mLifecycleRegistry;
    private ViewModelStore mViewModelStore;

    public BaseLifecyclePageStackManager(Activity activity) {
        super(activity);
        mLifecycleRegistry = new LifecycleRegistry(this);
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
    }

    @Override
    protected final TitlePageStack createPageStack(Context context) {
        TitlePageStack pageStack = new TitlePageStack(context, this, new BaseLifecycleCompatPageFactory(this));
        initPageStack(pageStack);
        return pageStack;
    }

    protected void initPageStack(PageStack pageStack) {

    }

    @Override
    public void onActivityDestroy() {
        super.onActivityDestroy();
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        if (mViewModelStore != null) {
            mViewModelStore.clear();
        }
    }

    @Override
    public void onActivityPause() {
        super.onActivityPause();
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
    }

    @Override
    public void onActivityResume() {
        super.onActivityResume();
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
    }

    @Override
    public void onActivityStart() {
        super.onActivityStart();
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    @Override
    public void onActivityStop() {
        super.onActivityStop();
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }

    public ViewModelStore getViewModelStore() {
        if (mViewModelStore == null) {
            mViewModelStore = new ViewModelStore();
        }
        return mViewModelStore;
    }
}
