package com.redefine.welike.business.feeds.ui.bean;

import com.redefine.commonui.loadmore.bean.BaseHeaderBean;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.business.assignment.management.bean.Banner;
import com.redefine.welike.business.assignment.management.bean.TopUserShakeBean;
import com.redefine.welike.business.feeds.ui.viewholder.DiscoverHeaderViewHolder;
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwenbo on 2018/3/22.
 */

public class HotFeedHeaderBean extends BaseHeaderBean {

    public static final int STATE_SHOW_CONTENT = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_LOAD_ERROR = 2;
    public static final int STATE_LOAD_EMPTY = 3;

    private List<Banner> mBanner = new ArrayList<>();

    private TopUserShakeBean mTopUser;

    private List<UserBase.Intrest> mInterests = new ArrayList<>();

    private int mLoadingState = STATE_SHOW_CONTENT;
    private List<TopicSearchSugBean.TopicBean> mTopicBean;

    public int getCurrentTab() {
        return mCurrentTab;
    }

    public void setCurrentTab(int mCurrentTab) {
        this.mCurrentTab = mCurrentTab;
    }

    private int mCurrentTab = DiscoverHeaderViewHolder.TAB_HOT;

    public void setBanner(List<Banner> list) {
        mBanner.clear();
        if (!CollectionUtil.isEmpty(list)) {
            mBanner.addAll(list);
        }
    }


    public void setTopUser(TopUserShakeBean bean) {
        mTopUser = bean;
    }

    public void setInterests(List<UserBase.Intrest> list) {
        mInterests.clear();
        if(!CollectionUtil.isEmpty(list)) {
            mInterests.addAll(list);
        }
    }

    public TopUserShakeBean getTopUser() {
        return mTopUser;
    }


    public List<Banner> getBanner() {
        return mBanner;
    }

    public List<UserBase.Intrest> getInterests() {
        return mInterests;
    }

    public void setLoadingState(int loadingState) {
        mLoadingState = loadingState;
    }

    public int getLoadingState() {
        return mLoadingState;
    }

    public List<TopicSearchSugBean.TopicBean> getTopics() {
        return mTopicBean;
    }

    public void setTopics(List<TopicSearchSugBean.TopicBean> mTopicBean) {
        this.mTopicBean = mTopicBean;
    }
}
