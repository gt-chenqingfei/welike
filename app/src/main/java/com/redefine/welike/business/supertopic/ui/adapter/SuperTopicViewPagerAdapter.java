package com.redefine.welike.business.supertopic.ui.adapter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.adapter.FragmentPageAdapter;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.BaseFragmentPage;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.supertopic.ui.page.SuperTopicInfoPage;
import com.redefine.welike.business.supertopic.ui.page.SuperTopicLatestPage;
import com.redefine.welike.business.supertopic.ui.page.SuperTopicTrendingPage;
import com.redefine.welike.business.topic.ui.constant.TopicConstant;
import com.redefine.welike.frameworkmvvm.BaseLifecyclePageStackManager;

public class SuperTopicViewPagerAdapter extends FragmentPageAdapter {
    private String slidingTitle[] = {ResourceTool.getString(ResourceTool.ResourceFileEnum.TOPIC, "super_topic_info_tab"),
            ResourceTool.getString(ResourceTool.ResourceFileEnum.TOPIC, "super_topic_trending_tab"),
            ResourceTool.getString(ResourceTool.ResourceFileEnum.TOPIC, "super_topic_latest_tab")};
    private static final int INFO_POSITION = 0;
    private static final int TRENDING_POSITION = 1;
    private static final int LATEST_POSITION = 2;

    private String topicId;
    private boolean isBrowse;
    private SuperTopicInfoPage infoPage;
    private SuperTopicTrendingPage trendingPage;
    private SuperTopicLatestPage latestPage;

    public SuperTopicViewPagerAdapter(IPageStackManager pm, String topicId, boolean isBrowse) {
        super(pm);
        this.topicId = topicId;
        this.isBrowse = isBrowse;
    }

    public SuperTopicInfoPage getInfoPage() {
        return infoPage;
    }

    @Override
    public BaseFragmentPage getItem(int position) {
        BaseFragmentPage fragment = null;
        Bundle bundle = new Bundle();
        bundle.putString("topicId", topicId);
        bundle.putBoolean(TopicConstant.BUNDLE_KEY_IS_BROWSE, isBrowse);
        if (LATEST_POSITION == position) {
            if (latestPage == null) {
                latestPage = new SuperTopicLatestPage((BaseLifecyclePageStackManager)mPageStackManager, new PageConfig.Builder(SuperTopicLatestPage.class).setPageBundle(bundle).build(), null);
            }
            fragment = latestPage;
        } else if (TRENDING_POSITION == position) {
            if (trendingPage == null) {
                trendingPage = new SuperTopicTrendingPage((BaseLifecyclePageStackManager)mPageStackManager, new PageConfig.Builder(SuperTopicTrendingPage.class).setPageBundle(bundle).build(), null);
            }
            fragment = trendingPage;
        } else if (INFO_POSITION == position) {
            if (infoPage == null) {
                infoPage = new SuperTopicInfoPage((BaseLifecyclePageStackManager)mPageStackManager, new PageConfig.Builder(SuperTopicInfoPage.class).setPageBundle(bundle).build(), null);
            }
            fragment = infoPage;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return slidingTitle.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return slidingTitle[position];
    }

    public void onActivityResume() {
        if (infoPage != null) {
            infoPage.onActivityResume();
        }
        if (trendingPage != null) {
            trendingPage.onActivityResume();
        }
        if (latestPage != null) {
            latestPage.onActivityResume();
        }
    }

    public void onActivityPause() {
        if (infoPage != null) {
            infoPage.onActivityPause();
        }
        if (trendingPage != null) {
            trendingPage.onActivityPause();
        }
        if (latestPage != null) {
            latestPage.onActivityPause();
        }
    }

    public void setUserVisibleHint(int position) {
        BaseFragmentPage item = getItem(position);
        if (item instanceof SuperTopicInfoPage) {
            ((SuperTopicInfoPage) item).setUserVisibleHint(true);
            if (trendingPage != null) {
                trendingPage.setUserVisibleHint(false);
            }
            if (latestPage != null) {
                latestPage.setUserVisibleHint(false);
            }
        } else if (item instanceof SuperTopicLatestPage) {
            ((SuperTopicLatestPage) item).setUserVisibleHint(true);
            if (infoPage != null) {
                infoPage.setUserVisibleHint(false);
            }
            if (trendingPage != null) {
                trendingPage.setUserVisibleHint(false);
            }
        } else if (item instanceof SuperTopicTrendingPage) {
            ((SuperTopicTrendingPage) item).setUserVisibleHint(true);
            if (infoPage != null) {
                infoPage.setUserVisibleHint(false);
            }
            if (latestPage != null) {
                latestPage.setUserVisibleHint(false);
            }
        }
    }

}
