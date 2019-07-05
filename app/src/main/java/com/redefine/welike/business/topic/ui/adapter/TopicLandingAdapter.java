package com.redefine.welike.business.topic.ui.adapter;

import android.text.TextUtils;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.loadmore.bean.BaseHeaderBean;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;
import com.redefine.welike.business.browse.ui.listener.IBrowseClickListener;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.business.feeds.ui.viewholder.BaseFeedViewHolder;
import com.redefine.welike.statistical.EventConstants;

/**
 * Created by liwenbo on 2018/3/21.
 */

public class TopicLandingAdapter extends FeedRecyclerViewAdapter<BaseHeaderBean> {
    public TopicLandingAdapter(IPageStackManager pageStackManager) {
        super(pageStackManager, EventConstants.FEED_PAGE_TOPIC_HOT);
    }


//    private GuideListener<BaseRecyclerViewHolder> listener;

//    public void setFirstShownListener(GuideListener<BaseRecyclerViewHolder> listener) {
//        this.listener = listener;
//    }

    @Override
    protected void onBindItemViewHolder(BaseRecyclerViewHolder holder, int position) {

        if (holder instanceof BaseFeedViewHolder) {
            if (TextUtils.equals(EventConstants.FEED_PAGE_TOPIC_HOT, mFeedSource)) {
                ((BaseFeedViewHolder) holder).getFeedHeadView().setTrending(true);
            }
        }

        super.onBindItemViewHolder(holder, position);

        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        marginLayoutParams.topMargin = 0;
        holder.itemView.setLayoutParams(marginLayoutParams);

        if (holder instanceof BaseFeedViewHolder) {

            ((BaseFeedViewHolder) holder).dismissTopicCard(true);

            if (iBrowseClickListener != null)
                ((BaseFeedViewHolder) holder).setBrowseClickListener(iBrowseClickListener);
        }
//        if (position == 0) {
//            if (listener != null) {
//                listener.onShow(holder, null);
//            }
//        }

    }

    @Override
    public BaseRecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
//        BaseRecyclerViewHolder holder = super.onCreateItemViewHolder(parent, viewType);
//        if (holder instanceof BaseFeedViewHolder) {
//            ((BaseFeedViewHolder) holder).getmCommonFeedBottomView().setLikeListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (listener != null) {
//                        //dismiss Guide.
//                        listener.onClick(v);
//                    }
//                }
//            });
//        }
        return super.onCreateItemViewHolder(parent, viewType);
    }


    @Override
    protected BaseRecyclerViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        return super.onCreateFooterViewHolder(parent, viewType);
    }

    private IBrowseClickListener iBrowseClickListener;

    public void setBrowseClickListener(IBrowseClickListener iBrowseClickListener) {
        this.iBrowseClickListener = iBrowseClickListener;
    }
}
