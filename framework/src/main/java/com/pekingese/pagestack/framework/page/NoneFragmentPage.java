package com.pekingese.pagestack.framework.page;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;

/**
 * Created by liwenbo on 2018/2/2.
 */

public class NoneFragmentPage extends BaseFragmentPage {

    public NoneFragmentPage(IPageStackManager stackManager, PageConfig config, PageCache cache) {
        super(stackManager, config, cache);
    }

    @Override
    protected void initView(ViewGroup container, Bundle saveState) {

    }

    @NonNull
    @Override
    protected View createView(ViewGroup container, Bundle saveState) {
        return new FrameLayout(container.getContext());
    }
}
