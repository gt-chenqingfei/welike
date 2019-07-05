package com.redefine.frameworkmvp.page;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.BaseFragmentPage;
import com.redefine.foundation.mvp.IBaseFragmentPagePresenter;

/**
 * Created by liwenbo on 2018/2/11.
 */

public abstract class MvpFragmentPage<T extends IBaseFragmentPagePresenter> extends BaseFragmentPage {

    protected final T mPresenter;

    public MvpFragmentPage(IPageStackManager stackManager, PageConfig config, PageCache cache) {
        super(stackManager, config, cache);
        mPresenter = createPresenter();
    }

    protected abstract T createPresenter();

    @NonNull
    @Override
    protected View createView(ViewGroup container, Bundle saveState) {
        return mPresenter.createView(container.getContext(), saveState);
    }

    @CallSuper
    @Override
    public void attach(ViewGroup container) {
        super.attach(container);
        mPresenter.attach();
    }

    @CallSuper
    @Override
    public void detach(ViewGroup container) {
        super.detach(container);
        mPresenter.detach();
    }

    @CallSuper
    @Override
    public void destroy(ViewGroup container) {
        super.destroy(container);
        mPresenter.destroy();
    }
}
