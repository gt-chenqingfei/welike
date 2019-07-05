package com.redefine.frameworkmvp.presenter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.view.View;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.foundation.mvp.IBaseFragmentPagePresenter;
import com.redefine.foundation.mvp.IBaseFragmentPageView;

/**
 * Created by liwenbo on 2018/2/11.
 */

public abstract class MvpFragmentPagePresenter<V extends IBaseFragmentPageView> implements IBaseFragmentPagePresenter {

    protected final V mView;
    protected final IPageStackManager mPageStackManager;
    protected final Bundle mPageConfig;

    public MvpFragmentPagePresenter(IPageStackManager pageStackManager, Bundle pageConfig) {
        mPageStackManager = pageStackManager;
        mPageConfig = pageConfig;
        mView = createFragmentPageView();
    }

    protected abstract V createFragmentPageView();

    @Override
    public View createView(Context context, Bundle savedInstanceState) {
        View view = mView.createView(context, savedInstanceState);
        initViews(savedInstanceState);
        return view;
    }

    protected abstract void initViews(Bundle savedInstanceState);

    @CallSuper
    @Override
    public void attach() {
        mView.attach();
    }

    @CallSuper
    @Override
    public void detach() {
        mView.detach();
    }

    @CallSuper
    @Override
    public void destroy() {
        mView.destroy();
    }
}
