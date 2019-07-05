package com.redefine.welike.business.feeds.ui.adapter;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.loadmore.bean.BaseHeaderBean;
import com.redefine.welike.statistical.EventConstants;

/**
 * Created by liwenbo on 2018/3/24.
 */

public class CommonFeedListAdapter extends FeedRecyclerViewAdapter<BaseHeaderBean> {

    public CommonFeedListAdapter(IPageStackManager pageStackManager) {
        super(pageStackManager, EventConstants.FEED_PAGE_HOT_24_ASSIGNMENT);
    }
}
