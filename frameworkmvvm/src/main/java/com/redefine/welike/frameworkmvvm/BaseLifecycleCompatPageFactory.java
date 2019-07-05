package com.redefine.welike.frameworkmvvm;

import android.view.ViewGroup;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.BasePage;
import com.pekingese.pagestack.framework.page.BaseTitlePage;
import com.pekingese.pagestack.framework.view.IPageFactory;

import java.lang.reflect.Constructor;

public class BaseLifecycleCompatPageFactory implements IPageFactory {

    private final BaseLifecyclePageStackManager mPageStackManager;

    public BaseLifecycleCompatPageFactory(BaseLifecyclePageStackManager pageStackManager) {
        mPageStackManager = pageStackManager;
    }

    @Override
    public BasePage instantiatePage(ViewGroup container, PageConfig config, PageCache cache) {
        Class<? extends BasePage> clazz = config.pageClazz;
        BasePage page = null;
        if (clazz != null) {
            if (BaseLifecyclePage.class.isAssignableFrom(clazz)) {
                try {
                    Constructor<? extends BasePage> constructor = clazz.getDeclaredConstructor(BaseLifecyclePageStackManager.class,
                            PageConfig.class, PageCache.class);
                    page = constructor.newInstance(mPageStackManager, config, cache);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Constructor<? extends BasePage> constructor = clazz.getDeclaredConstructor(IPageStackManager.class,
                            PageConfig.class, PageCache.class);
                    page = constructor.newInstance(mPageStackManager, config, cache);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (page == null) {
            page = new BaseLifecycleNonePage(mPageStackManager, config, cache);
        }
        page.createAndAttach(container);
        return page;
    }
}
