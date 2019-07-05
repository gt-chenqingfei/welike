package com.redefine.welike.business.supertopic.ui.adapter;

import android.view.ViewGroup;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.commonui.share.sharemedel.SharePackageModel;
import com.redefine.welike.R;
import com.redefine.welike.business.browse.ui.listener.IBrowseClickListener;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.business.feeds.ui.viewholder.BaseFeedViewHolder;
import com.redefine.welike.business.supertopic.management.bean.SuperTopicInfoBean;
import com.redefine.welike.business.supertopic.ui.view.SuperTopicInfoViewHolder;
import com.redefine.welike.commonui.share.ShareMenu;
import com.redefine.welike.statistical.EventConstants;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class SuperTopicTopRecylerViewAdapter extends FeedRecyclerViewAdapter<SuperTopicInfoBean> {
    private IBrowseClickListener iBrowseClickListener;

    public SuperTopicTopRecylerViewAdapter(IPageStackManager pageStackManager) {
        super(pageStackManager, EventConstants.FEED_PAGE_SUPER_TOPIC_INFO);
    }

    public void setData(String info, List<PostBase> postBases) {
        setHeader(new SuperTopicInfoBean(info));
        addNewData(postBases);
    }

    public void setBrowseClickListener(IBrowseClickListener iBrowseClickListener) {
        this.iBrowseClickListener = iBrowseClickListener;
    }

    @Override
    protected BaseRecyclerViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        return new SuperTopicInfoViewHolder(mInflater.inflate(R.layout.super_topic_info_layout, parent, false));
    }

    @Override
    protected void onBindHeaderViewHolder(BaseRecyclerViewHolder holder, int position) {
        holder.bindViews(this, getHeader());
    }

    @Override
    protected List<SharePackageModel> initMenuItemList(PostBase postBase, Function1<ShareMenu, Unit> menuInvoker) {
        return new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindItemViewHolder(BaseRecyclerViewHolder holder, int position) {
        super.onBindItemViewHolder(holder, position);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        marginLayoutParams.topMargin = 0;
        holder.itemView.setLayoutParams(marginLayoutParams);
        if (holder instanceof BaseFeedViewHolder) {
            ((BaseFeedViewHolder) holder).setForwardFeedClickEnable(true);
            ((BaseFeedViewHolder) holder).setShowContent(true);
        }

        if (holder instanceof BaseFeedViewHolder) {
            ((BaseFeedViewHolder) holder).dismissArrowBtn(true);
            ((BaseFeedViewHolder) holder).dismissDivider(false);
            ((BaseFeedViewHolder) holder).dismissFollowBtn(true);
            ((BaseFeedViewHolder) holder).showHotFlog(false);
            ((BaseFeedViewHolder) holder).showTopFlog(true);
            ((BaseFeedViewHolder) holder).dismissTopicCard(true);

            if (iBrowseClickListener != null) {
                ((BaseFeedViewHolder) holder).setBrowseClickListener(iBrowseClickListener);
            }
        }
    }

    @Override
    public void onPostDeleted(String pid) {
        // todo topic delete or not
    }

}
