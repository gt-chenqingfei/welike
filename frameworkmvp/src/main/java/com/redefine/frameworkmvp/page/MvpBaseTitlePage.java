package com.redefine.frameworkmvp.page;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.view.View;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.BaseTitlePage;
import com.redefine.foundation.mvp.IBasePagePresenter;

/**
 * Created by liwenbo on 2018/2/11.
 */

public abstract class MvpBaseTitlePage<T extends IBasePagePresenter> extends BaseTitlePage {

    protected final T mPresenter;

    public MvpBaseTitlePage(IPageStackManager stackManager, PageConfig config, PageCache cache) {
        super(stackManager, config, cache);
        mPresenter = createPresenter();
    }

    protected abstract T createPresenter();

    @Override
    protected View createPageView(ViewGroup container, Bundle saveState) {
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
