package com.redefine.welike.business.search.ui.bean;

import com.redefine.commonui.loadmore.bean.BaseFooterBean;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean;

import java.util.List;

/**
 * Created by nianguowang on 2018/8/1
 */
public class SearchFootBean extends BaseFooterBean {

    private boolean mShowMoreHistory;
    private String mTopicText = ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "trending_topic");
    private String mMoreHistoryText = ResourceTool.getString(ResourceTool.ResourceFileEnum.SEARCH, "search_all_history_text");
    private List<TopicSearchSugBean.TopicBean> mTopicBeans;

    public void setTopicBeans(List<TopicSearchSugBean.TopicBean> mTopicBeans) {
        this.mTopicBeans = mTopicBeans;
    }

    public List<TopicSearchSugBean.TopicBean> getTopicBeans() {
        return mTopicBeans;
    }

    public boolean isShowMoreHistory() {
        return mShowMoreHistory;
    }

    public void setShowMoreHistory(boolean mShowMoreHistory) {
        this.mShowMoreHistory = mShowMoreHistory;
    }

    public String getTopicText() {
        return mTopicText;
    }

    public String getMoreHistoryText() {
        return mMoreHistoryText;
    }
}
