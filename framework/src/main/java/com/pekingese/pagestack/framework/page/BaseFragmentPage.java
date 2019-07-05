package com.pekingese.pagestack.framework.page;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;

/**
 * Created by liwenbo on 2018/2/2.
 */

public abstract class BaseFragmentPage<T extends IPageStackManager> extends BasePage<T> {

    public BaseFragmentPage(T stackManager, PageConfig config, PageCache cache) {
        super(stackManager, config, cache);
        config.isFitSystemWindow = false;
    }

    @Override
    protected void initView(ViewGroup container, Bundle saveState) {

    }

    public void onBasePageStateChanged(int oldPageState, int pageState) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
