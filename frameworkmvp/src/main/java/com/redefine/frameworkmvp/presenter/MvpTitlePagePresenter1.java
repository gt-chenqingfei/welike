package com.redefine.frameworkmvp.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.view.View;

import com.redefine.foundation.mvp.IBasePagePresenter;
import com.redefine.foundation.mvp.IBasePageView;

/**
 * Created by liwenbo on 2018/2/11.
 */

public abstract class MvpTitlePagePresenter1<V extends IBasePageView> implements IBasePagePresenter {

    protected final V mView;
    protected final Activity mActivity;
    protected final Bundle mPageBundle;

    public MvpTitlePagePresenter1(Activity context, Bundle pageConfig) {
        mActivity = context;
        mPageBundle = pageConfig;
        mView = createPageView();
    }

    protected abstract V createPageView();

    @Override
    public View createView(Context context, Bundle savedInstanceState) {
        View view = mView.createView(context, savedInstanceState);
        initView(savedInstanceState);
        return view;
    }

    protected abstract void initView(Bundle savedInstanceState);

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

    @Override
    public void onBackPressed() {
        mActivity.finish();
    }
}
