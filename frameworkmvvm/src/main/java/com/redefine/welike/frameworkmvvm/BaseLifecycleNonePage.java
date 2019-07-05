package com.redefine.welike.frameworkmvvm;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;

public class BaseLifecycleNonePage extends BaseLifecyclePage {

    public BaseLifecycleNonePage(BaseLifecyclePageStackManager stackManager, PageConfig config, PageCache cache) {
        super(stackManager, config, cache);
    }

    @Override
    protected View createPageView(ViewGroup container, Bundle saveState) {
        return new FrameLayout(container.getContext());
    }


    @Override
    protected void initView(ViewGroup container, Bundle saveState) {
        getView().setBackgroundColor(Color.WHITE);
    }
}
