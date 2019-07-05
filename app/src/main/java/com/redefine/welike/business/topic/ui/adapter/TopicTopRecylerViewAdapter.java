package com.redefine.welike.business.topic.ui.adapter;

import android.view.ViewGroup;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.commonui.share.sharemedel.SharePackageModel;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.business.feeds.ui.viewholder.BaseFeedViewHolder;
import com.redefine.welike.statistical.EventConstants;

import java.util.ArrayList;
import java.util.List;

import kotlin.jvm.functions.Function1;

/**
 * Created by mengnan on 2018/5/16.
 **/
public class TopicTopRecylerViewAdapter extends FeedRecyclerViewAdapter {

    public TopicTopRecylerViewAdapter(IPageStackManager pageStackManager) {
        super(pageStackManager, EventConstants.FEED_PAGE_TOPIC_HOT);
    }

    @Override
    protected List<SharePackageModel> initMenuItemList(PostBase postBase, Function1 menuInvoker) {
        return new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindItemViewHolder(BaseRecyclerViewHolder holder, int position) {

        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        marginLayoutParams.topMargin = 0;
        holder.itemView.setLayoutParams(marginLayoutParams);

        if (holder instanceof BaseFeedViewHolder) {
            ((BaseFeedViewHolder) holder).setForwardFeedClickEnable(true);
            ((BaseFeedViewHolder) holder).setShowContent(true);
        }

        super.onBindItemViewHolder(holder, position);
        if (holder instanceof BaseFeedViewHolder) {
            ((BaseFeedViewHolder) holder).dismissArrowBtn(true);
            ((BaseFeedViewHolder) holder).dismissDivider(false);
            ((BaseFeedViewHolder) holder).dismissFollowBtn(true);
            ((BaseFeedViewHolder) holder).showHotFlog(false);
            ((BaseFeedViewHolder) holder).showTopFlog(true);
            ((BaseFeedViewHolder) holder).dismissTopicCard(true);

            //((BaseFeedViewHolder) holder).setDismissBottomContent(true);
        }
    }

    @Override
    public void onPostDeleted(String pid) {
        // todo topic delete or not
    }
}
