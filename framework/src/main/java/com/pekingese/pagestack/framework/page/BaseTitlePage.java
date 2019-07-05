package com.pekingese.pagestack.framework.page;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.R;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.config.StatusBarConfig;
import com.pekingese.pagestack.framework.constant.CommonConstant;
import com.pekingese.pagestack.framework.titlebar.PageTitleActionPack;
import com.pekingese.pagestack.framework.titlebar.TitleAction;
import com.pekingese.pagestack.framework.titlestrip.IPageTitleManager;
import com.pekingese.pagestack.framework.titlestrip.PageTitleBarStrip;

/**
 * Created by liwenbo on 2018/2/1.
 */

public abstract class BaseTitlePage<T extends IPageStackManager> extends BaseSubPage<T> {

    protected IPageTitleManager mTitleStrip;

    public BaseTitlePage(T stackManager, PageConfig config, PageCache cache) {
        super(stackManager, config, cache);
    }

    @NonNull
    @Override
    protected final View createView(ViewGroup container, Bundle saveState) {
        LinearLayout layout = new LinearLayout(container.getContext());
        mTitleStrip = createPageTitleStrip(container.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        // 暂时屏蔽掉
//        layout.addView(mTitleStrip.getView(), createPageTitleStripLayoutParams(container.getContext()));
        View contentView = createPageView(container, saveState);
        layout.addView(contentView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        return layout;
    }

    protected abstract View createPageView(ViewGroup container, Bundle saveState);

    private IPageTitleManager createPageTitleStrip(Context context) {
        PageTitleBarStrip strip = new PageTitleBarStrip(context);
        strip.setTitleActionObserver(this);
        return strip;
    }

    private LinearLayout.LayoutParams createPageTitleStripLayoutParams(Context context) {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                context.getResources().getDimensionPixelOffset(R.dimen.title_strip_height));
    }

    @Override
    protected void initView(ViewGroup container, Bundle saveState) {
        getView().setBackgroundColor(Color.WHITE);
        mTitleStrip.applyPageTitleActions(getTitleAction());
    }

    @Override
    protected StatusBarConfig initStatusBarConfig() {
        return new StatusBarConfig.Builder(true, true).build();
    }

    @Override
    protected PageTitleActionPack initTitleActions() {
        return new PageTitleActionPack.Builder().addCenterAction(new TitleAction.Builder(CommonConstant.INVALID_ID).build()).build();
    }
}
