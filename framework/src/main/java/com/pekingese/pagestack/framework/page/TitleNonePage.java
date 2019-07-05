package com.pekingese.pagestack.framework.page;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;

/**
 * Created by liwenbo on 2018/2/1.
 */
@PageName("TitleNonePage")
public class TitleNonePage extends BaseTitlePage {

    public TitleNonePage(IPageStackManager stackManager, PageConfig config, PageCache cache) {
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
