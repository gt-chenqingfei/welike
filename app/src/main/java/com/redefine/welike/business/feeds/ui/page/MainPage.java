package com.redefine.welike.business.feeds.ui.page;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.PageName;
import com.redefine.welike.R;
import com.redefine.welike.business.common.ToastBusinessManager;
import com.redefine.welike.business.feeds.ui.contract.IMianContract;
import com.redefine.welike.frameworkmvvm.BaseLifecyclePage;
import com.redefine.welike.frameworkmvvm.BaseLifecyclePageStackManager;

/**
 * Created by liwenbo on 2018/2/1.
 */
@PageName("MainPage")
public class MainPage extends BaseLifecyclePage {

    private IMianContract.IMainView mMainView;
    private IMianContract.IMainPresenter mMainPresenter;
    private ToastBusinessManager mToastManager;

    public MainPage(BaseLifecyclePageStackManager stackManager, PageConfig config, PageCache cache) {
        super(stackManager, config, cache);

    }

    @Override
    protected View createPageView(ViewGroup container, Bundle saveState) {
        View view = LayoutInflater.from(mPageStackManager.getContext()).inflate(R.layout.activity_main2, null);
        mMainView = IMianContract.MainPageFactory.createView(view, mPageConfig.pageBundle, saveState, mPageStackManager.getActivity());
        mMainPresenter = IMianContract.MainPageFactory.createPresenter(mMainView);
        mToastManager = new ToastBusinessManager();
        mMainPresenter.onCreate(mPageConfig.pageBundle, saveState);
        return view;
    }

    @Override
    protected void initView(ViewGroup container, Bundle saveState) {
        super.initView(container, saveState);
        mToastManager.onBind();
    }

    @Override
    public void attach(ViewGroup container) {
        super.attach(container);
        mMainView.attach();
        mMainPresenter.showPage();
    }

    @Override
    public void detach(ViewGroup container) {
        super.detach(container);
    }

    @Override
    public void destroy(ViewGroup container) {
        super.destroy(container);
        mMainPresenter.destroy();
        mToastManager.onUnBind();
    }

    @Override
    public void onActivityPause() {
        super.onActivityPause();
        mMainPresenter.onActivityPause();
    }

    @Override
    public void onActivityResume() {
        super.onActivityResume();
        mMainPresenter.onActivityResume();
    }

    @Override
    public void onPageStateChanged(int oldPageState, int pageState) {
        super.onPageStateChanged(oldPageState, pageState);
        mMainPresenter.onPageStateChanged(oldPageState, pageState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mMainPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onActivityDestroy() {
        super.onActivityDestroy();
    }

    @Override
    public void onNewMessage(Message message) {
        super.onNewMessage(message);
        mMainPresenter.onNewMessage(message);
    }
}
